package com.example.parserapp.ui.screens

import android.annotation.SuppressLint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.parserapp.ui.theme.BackgroundDarkGrayCustom
import com.example.parserapp.ui.theme.ParserAppTheme
import com.example.parserapp.viewmodel.AddKeyIntent
import com.example.parserapp.viewmodel.AddKeyViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddKeyScreen(
    navController: NavController,
    viewModel: AddKeyViewModel = AddKeyViewModel(),
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val executor = remember { Executors.newSingleThreadExecutor() }
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(Unit) {
        onDispose {
            cameraProviderFuture.get().unbindAll()
        }
    }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.qrCodeResult) {
        state.qrCodeResult?.let { qrCode ->
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("qrResult", qrCode)

            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            (TopAppBar(
                title = { Text("Отсканируйте QR код") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Назад",
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundDarkGrayCustom
                )
            ))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            CameraPreviewView(
                onQRCodeScanned = { qrCode ->
                    viewModel.handleIntent(AddKeyIntent.ScanQRCode(qrCode))
                },
                lifecycleOwner = lifecycleOwner,
                executor = executor
            )
        }
    }
}

@Composable
fun CameraPreviewView(
    onQRCodeScanned: (String) -> Unit,
    lifecycleOwner: LifecycleOwner,
    executor: ExecutorService
) {
    val context = LocalContext.current
    var cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val previewView = remember {
            PreviewView(context).apply {
                scaleType = PreviewView.ScaleType.FILL_START
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }

        AndroidView(factory = { previewView }) { view ->
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                cameraProvider.unbindAll()

                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(view.surfaceProvider)
                }

                val barcodeAnalyzer = BarcodeAnalyzer { barcode ->
                    barcode?.let {
                        onQRCodeScanned(it)

                        cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    }
                }

                val analysisUseCase = androidx.camera.core.ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(executor, barcodeAnalyzer)
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    analysisUseCase
                )
            }, ContextCompat.getMainExecutor(context))
        }
    }
}

class BarcodeAnalyzer(
    private val onBarcodeScanned: (String?) -> Unit
) : ImageAnalysis.Analyzer {

    private val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return

        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
                onBarcodeScanned(barcode?.rawValue)
            }
            .addOnFailureListener {
                onBarcodeScanned(null)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}

@Preview(showSystemUi = true)
@Composable
fun previewAddKey() {
    ParserAppTheme {
        AddKeyScreen(
            navController = rememberNavController()
        )
    }
}
