package com.example.parserapp.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parserapp.ui.theme.DisabledButtonContentCustom
import com.example.parserapp.ui.theme.DisabledButtonCustom
import com.example.parserapp.ui.theme.LightBlueCustom
import com.example.parserapp.ui.theme.LightGrayCustom
import com.example.parserapp.viewmodel.MainIntent
import com.example.parserapp.viewmodel.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavController,
    context: Context,
    viewModel: MainViewModel
) {
    val focusManager = LocalFocusManager.current
    val state by viewModel.state.collectAsState()

    var qrResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<String>("qrResult")

    val launcherPhone = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.updatePhonePermissionGranted(isGranted)
            }
        })

    val launcherPhoneState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.updatePhoneStatePermissionGranted(isGranted)
            }
        })

    val launcherSms = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.updateSmsPermissionGranted(isGranted)
            }
        })
    val launcherCamera = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.updateCameraPermissionGranted(isGranted);
                navController.navigate("addKey")
            }
        })
    val launcherNetworkState = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                viewModel.updateNetworkPermissionGranted(isGranted);
            }
        })
    val launcherPush = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { isGranted ->
            if (viewModel.permissionManager.isPushPermissionGranted()) {
                viewModel.updatePushPermissionGranted(true)
            }
        })
    val launcherBattery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { isGranted ->
            if (viewModel.permissionManager.isIgnoringBatteryOptimizations()) {
                viewModel.updateBatteryOptimizationIgnored(true);
            }
        })

    viewModel.initializePermissionLaunchers(
        launcherPhoneState,
        launcherPhone,
        launcherSms,
        launcherCamera,
        launcherNetworkState,
        launcherPush,
        launcherBattery
    )

    LaunchedEffect(qrResult) {
        if (qrResult != null) {
            viewModel.handleIntent(MainIntent.EnterApiKey(qrResult!!))
            qrResult = null
            viewModel.resetCursorToStart()
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                        viewModel.resetCursorToStart()
                    })
                }
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isSmsPermissionGranted && state.isPushPermissionGranted &&
                state.isBatteryOptimizationIgnored && state.isPhonePermissionGranted &&
                state.isPhoneStatePermissionGranted
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedTextField(
                            value = state.apiKey,
                            onValueChange = { viewModel.handleIntent(MainIntent.EnterApiKey(it.text)) },
                            placeholder = {
                                Text(
                                    text = "Ввести ключ аутентификации",
                                    style = TextStyle(color = LightGrayCustom)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.87f)
                                .padding(end = 16.dp)
                                .height(48.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                errorBorderColor = Color.Red,
                                focusedBorderColor = Color.White,
                                unfocusedTextColor = LightGrayCustom,
                                focusedTextColor = LightGrayCustom
                            ),
                            isError = !state.apiKey.text.isEmpty() && !state.isApiKeyFormatValid
                        )

                        IconButton(
                            onClick = { viewModel.handleIntent(MainIntent. RequestCameraPermission) },
                            modifier = Modifier
                                .background(
                                    color = LightBlueCustom,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .height(48.dp)
                                .width(48.dp)
                                .fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = "Камера",
                                tint = Color.White
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.handleIntent(MainIntent.ValidateKey(context)) },
                        enabled = !state.isValidating && state.apiKey.text.isNotEmpty() && state.isApiKeyFormatValid,
                        modifier = Modifier
                            .padding(horizontal = 12.dp, vertical = 20.dp)
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            disabledContainerColor = DisabledButtonCustom,
                            disabledContentColor = DisabledButtonContentCustom
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (state.isValidating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Авторизоваться")
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = "Войти"
                            )
                        }
                    }
                    state.validationSuccess?.let {
                        if (!it) {
                            Text(
                                text = "Ошибка при проверке ключа",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Red,
                                modifier = Modifier.padding(top = 5.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 90.dp)
                        .padding(horizontal = 16.dp)
                ) {
                    if (!state.isPhonePermissionGranted) {
                        AskPermissionRow("Получите разрешение на доступ к SIM") {
                            viewModel.handleIntent(MainIntent.RequestPhonePermission)
                        }
                    }
                    if (!state.isPhoneStatePermissionGranted) {
                        AskPermissionRow("Получите разрешение на доступ к статусу устройства") {
                            viewModel.handleIntent(MainIntent.RequestPhoneStatePermission)
                        }
                    }
                    if (!state.isSmsPermissionGranted) {
                        AskPermissionRow("Получите разрешение на доступ к СМС") {
                            viewModel.handleIntent(MainIntent.RequestSmsPermission)
                        }
                    }
                    if (!state.isPushPermissionGranted) {
                        AskPermissionRow("Получите разрешение на доступ к PUSH") {
                            viewModel.handleIntent(MainIntent.RequestPushPermission)
                        }
                    }
                    if (!state.isBatteryOptimizationIgnored) {
                        AskPermissionRow("Необходимо выключить оптимизацию батареи") {
                            viewModel.handleIntent(MainIntent.RequestBatteryOptimization)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AskPermissionRow(text: String, clickAction: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .background(LightBlueCustom, RoundedCornerShape(16.dp))
            .padding(5.dp)
            .clickable(onClick = clickAction),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Outlined.Info,
            tint = Color.White,
            contentDescription = "Выдайте разрешение",
            modifier = Modifier
                .padding(start = 10.dp)
                .width(20.dp)
                .height(20.dp)
        )
        Text(
            text,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(10.dp, 7.dp)
        )
    }
}

fun Modifier.shadowWithClipIntersect(
    elevation: Dp,
    shape: Shape = RectangleShape,
    clip: Boolean = elevation > 0.dp,
    ambientColor: Color = DefaultShadowColor,
    spotColor: Color = DefaultShadowColor,
): Modifier = this
    .drawWithCache {
        val bottomOffsetPx = elevation.toPx() * 2.2f
        val adjustedSize = Size(size.width, size.height + bottomOffsetPx)
        val outline = shape.createOutline(adjustedSize, layoutDirection, this)
        val path = Path().apply { addOutline(outline) }
        onDrawWithContent {
            clipPath(path, ClipOp.Intersect) {
                this@onDrawWithContent.drawContent()
            }
        }
    }
    .shadow(elevation, shape, clip, ambientColor, spotColor)