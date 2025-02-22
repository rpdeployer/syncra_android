package com.example.parserapp.ui.screens

import android.app.Application
import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.parserapp.R
import com.example.parserapp.data.model.LogUploadRequest
import com.example.parserapp.data.repository.MessageRepository
import com.example.parserapp.data.repository.UpdateRepository
import com.example.parserapp.data.store.DataStoreManager
import com.example.parserapp.ui.factories.LogViewModelFactory
import com.example.parserapp.ui.theme.AlertCustom
import com.example.parserapp.ui.theme.DarkGrayCustom
import com.example.parserapp.ui.theme.GreenDarkCustom
import com.example.parserapp.ui.theme.LightBlueCustom
import com.example.parserapp.ui.theme.LightGrayCustom
import com.example.parserapp.ui.theme.UpdateButtonCustom
import com.example.parserapp.utils.NetworkMonitor
import com.example.parserapp.viewmodel.AuthorizedIntent
import com.example.parserapp.viewmodel.AuthorizedViewModel
import com.example.parserapp.viewmodel.LogViewModel
import com.example.parserapp.viewmodel.SmsQueueViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

@Composable
fun AuthorizedScreen(
    navController: NavController,
    context: Context,
    dataStoreManager: DataStoreManager = DataStoreManager(context),
    viewModel: AuthorizedViewModel = AuthorizedViewModel(context, DataStoreManager(context), UpdateRepository(context)),
    logViewModel: LogViewModel = viewModel(factory = LogViewModelFactory(context.applicationContext as Application)),
    queueViewModel: SmsQueueViewModel = SmsQueueViewModel(context)
) {
    val state by viewModel.state.collectAsState()
    val queueSize by queueViewModel.queueSize.collectAsState()
    val logs by logViewModel.logs.collectAsState()
    val networkMonitor = remember { NetworkMonitor(context) }
    val isConnected by networkMonitor.isConnected.collectAsState()
    val name by viewModel.name.collectAsState()
    var showNoInternetDialog by remember { mutableStateOf(false) }
    val isButtonEnabled by viewModel.isButtonEnabled.collectAsState()
    var showUploadDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val version by  viewModel.currentVersion.collectAsState()
    val remainingTime by viewModel.remainingTime.collectAsState()

    LaunchedEffect(isConnected) {
        showNoInternetDialog = !isConnected
    }

    LaunchedEffect(Unit) {
        queueViewModel.updateQueueSize()
    }

    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            (TopAppBar(
                title = {},
                navigationIcon = {},
                actions = {
                    IconButton(
                        onClick = { navController.navigate("exit") },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = LightGrayCustom
                        )
                    ) {
                        Icon(
                            ImageVector.vectorResource(R.drawable.exit),
                            contentDescription = "Выйти",
                            modifier = Modifier
                                .height(42.dp)
                        )
                    }
                },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .height(100.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            ))
        }
    ) {
        Box(
            modifier = Modifier.padding(it)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 25.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            name!!,
                            style = TextStyle(
                                color = Color.White,
                                fontSize = 24.sp,
                                letterSpacing = 0.25.sp,
                                fontFamily = FontFamily(
                                    Font(R.font.roboto_bold, FontWeight.W600, FontStyle.Normal)
                                ),
                                lineHeight = 28.sp,
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(top = 25.dp)
                            .defaultMinSize(190.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                modifier = Modifier,
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.wifi),
                                    "Интернет",
                                    tint = if (isConnected) GreenDarkCustom else Color.Red,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = if (isConnected) "Интернет подключен" else "Нет интернета",
                                    color = if (isConnected) GreenDarkCustom else Color.Red,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        letterSpacing = 0.25.sp,
                                        fontFamily = FontFamily(
                                            Font(
                                                R.font.roboto_regular,
                                                FontWeight.W400,
                                                FontStyle.Normal
                                            )
                                        ),
                                        lineHeight = 20.sp,
                                    )
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .padding(top = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Сообщений в очереди: ",
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        letterSpacing = 0.25.sp,
                                        fontFamily = FontFamily(
                                            Font(
                                                R.font.roboto_regular,
                                                FontWeight.W400,
                                                FontStyle.Normal
                                            )
                                        ),
                                        lineHeight = 20.sp,
                                    )
                                )
                                Text(
                                    queueSize.toString(),
                                    color = Color.White,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        letterSpacing = 0.25.sp,
                                        fontFamily = FontFamily(
                                            Font(
                                                R.font.roboto_regular,
                                                FontWeight.W400,
                                                FontStyle.Normal
                                            )
                                        ),
                                        lineHeight = 20.sp,
                                    ),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 49.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            Text(
                                "История действий",
                                color = Color.White,
                                style = TextStyle(
                                    fontSize = 18.sp,
                                    letterSpacing = 0.25.sp,
                                    fontFamily = FontFamily(
                                        Font(
                                            R.font.roboto_regular,
                                            FontWeight.W400,
                                            FontStyle.Normal
                                        )
                                    ),
                                    lineHeight = 21.sp,
                                ),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isButtonEnabled) UpdateButtonCustom else Color.Gray)
                                .width(32.dp)
                                .height(32.dp),
                        ) {
                            IconButton(
                                onClick = {
                                    viewModel.onUploadLogsClick(dataStoreManager)
                                    coroutineScope.launch {
                                        val logs = collectLogs()
                                        if (logs.isNotEmpty()) {
                                            val isUploadSuccessful = uploadLogsInChunks(context, logs)
                                            if (isUploadSuccessful) {
                                                showUploadDialog = true
                                            } else {
                                                Toast.makeText(context, "Ошибка при загрузке логов!", Toast.LENGTH_SHORT)
                                                    .apply { setGravity(Gravity.CENTER, 0, 0) }
                                                    .show()
                                            }
                                        } else {
                                            Toast.makeText(context, "Нет логов для загрузки!", Toast.LENGTH_SHORT)
                                                .apply { setGravity(Gravity.CENTER, 0, 0) }
                                                .show()
                                        }
                                    }
                                },
                                enabled = isButtonEnabled,
                                colors = IconButtonDefaults.iconButtonColors(disabledContainerColor = Color.Gray),
                                modifier = Modifier.fillMaxSize()
                                    .align(Alignment.Center)
                            ) {
                                if (isButtonEnabled) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.download),
                                        contentDescription = "Сохранить логи",
                                        tint = LightGrayCustom
                                    )
                                } else {
                                    Text(
                                        text = "$remainingTime",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxHeight(0.63f)
                            .fillMaxWidth()
                            .border(1.dp, DarkGrayCustom, RoundedCornerShape(16.dp))
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            items(logs.toList()) { log ->
                                Row(
                                    modifier = Modifier.padding(bottom = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "[ ",
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        log.dateTime,
                                        fontSize = 16.sp,
                                        color = if (log.isSuccess) GreenDarkCustom else Color.Red
                                    )
                                    Text(
                                        " ] ",
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        log.message,
                                        fontSize = 16.sp,
                                        letterSpacing = 0.25.sp,
                                        fontFamily = FontFamily(
                                            Font(
                                                R.font.roboto_regular,
                                                FontWeight.W400,
                                                FontStyle.Normal
                                            )
                                        ),
                                        lineHeight = 20.sp,
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 40.dp)
                    ) {
                        UpdateButton(viewModel, context)

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Текущая версия: $version",
                            style = MaterialTheme.typography.bodySmall,
                            color = DarkGrayCustom
                        )
                    }
                }
            }
            if (showNoInternetDialog) {
                Dialog(
                    onDismiss = { showNoInternetDialog = false },
                    title = "Внимание!",
                    text = "Нет подключения к интернету",
                    icon = ImageVector.vectorResource(R.drawable.attention_icon)
                )
            }
            if (showUploadDialog) {
                Dialog(
                    onDismiss = { showUploadDialog = false },
                    title = "Успешно!",
                    text = "Файл с логами отправлен на сервер",
                    icon = ImageVector.vectorResource(R.drawable.check_icon)
                )
            }
        }
    }
}

@Composable
fun UpdateButton(viewModel: AuthorizedViewModel, context: Context) {
    val isLoading by viewModel.isUpdating.collectAsState()
    val rotation = rememberInfiniteTransition(label = "Update animation").animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing)
        ), label = "Update animation"
    )

    Button(
        onClick = { viewModel.handleIntent(AuthorizedIntent.CheckUpdate(context)) },
        modifier = Modifier
            .width(229.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(28.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = UpdateButtonCustom,
            contentColor = LightGrayCustom
        ),
        enabled = !isLoading
    ) {
        Text(
            text = if (isLoading) "Обновление..." else "Обновить приложение",
            style = TextStyle(color = LightGrayCustom)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = ImageVector.vectorResource(R.drawable.refresh),
            contentDescription = "Обновить",
            modifier = Modifier
                .graphicsLayer { rotationZ = if (isLoading) rotation.value else 0f }
        )
    }
}



@Composable
fun Dialog(onDismiss: () -> Unit, title: String, text: String, icon: ImageVector) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = { onDismiss() }) {
                    Text(
                        "OK",
                        fontSize = 20.sp,
                        letterSpacing = 0.25.sp,
                        fontFamily = FontFamily(
                            Font(
                                R.font.roboto_regular,
                                FontWeight.W400,
                                FontStyle.Normal
                            )
                        ),
                        lineHeight = 20.sp,
                        color = LightBlueCustom,
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .background(
                            color = DarkGrayCustom,
                            shape = RoundedCornerShape(61.dp)
                        )
                        .height(90.dp)
                        .width(90.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Информация",
                        tint = LightGrayCustom,
                        modifier = Modifier
                            .width(46.dp)
                            .height(46.dp)
                    )
                }
            }
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        title,
                        fontSize = 20.sp,
                        letterSpacing = 0.25.sp,
                        fontFamily = FontFamily(
                            Font(
                                R.font.roboto_regular,
                                FontWeight.W400,
                                FontStyle.Normal
                            )
                        ),
                        lineHeight = 23.4.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text,
                        fontSize = 16.sp,
                        letterSpacing = 0.25.sp,
                        fontFamily = FontFamily(
                            Font(
                                R.font.roboto_regular,
                                FontWeight.W400,
                                FontStyle.Normal
                            )
                        ),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        color = LightGrayCustom,
                    )
                }
            }
        },
        modifier = Modifier
            .clip(RoundedCornerShape(23.dp))
            .defaultMinSize(380.dp, 289.dp)
            .background(AlertCustom)
    )
}

fun collectLogs(): List<String> {
    val process = Runtime.getRuntime().exec("logcat -d")
    val reader = BufferedReader(InputStreamReader(process.inputStream))
    val logs = mutableListOf<String>()

    reader.useLines { lines ->
        lines.forEach { line ->
            logs.add(line)
        }
    }

    Runtime.getRuntime().exec("logcat -c")

    val maxPartSize = 1_000_000
    return if (logs.size == 0) {
        logs
    } else {
        logs.chunked(maxPartSize / logs[0].toByteArray().size).map { chunk ->
            chunk.joinToString("\n")
        }
    }
}

suspend fun uploadLogsInChunks(context: Context, logs: List<String>): Boolean {

    val chunkSize = 100
    val apiService = MessageRepository(context)

    return try {
        logs.chunked(chunkSize).forEach { chunk ->
            val result = apiService.sendLog(LogUploadRequest(chunk))
            if (!result) {
                return false
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}
