package com.example.parserapp.ui.screens

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.parserapp.R
import com.example.parserapp.ui.factories.ExitViewModelFactory
import com.example.parserapp.ui.theme.DarkGrayCustom
import com.example.parserapp.ui.theme.ExitButtonCustom
import com.example.parserapp.ui.theme.LightGrayCustom
import com.example.parserapp.ui.theme.UpdateButtonCustom
import com.example.parserapp.viewmodel.ExitIntent
import com.example.parserapp.viewmodel.ExitViewModel
import com.example.parserapp.viewmodel.MainViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExitScreen(
    navController: NavController,
    context: Context,
    viewModel: ExitViewModel = viewModel(factory = ExitViewModelFactory(context.applicationContext as Application )),
    mainViewModel: MainViewModel
) {

    val state by viewModel.state.collectAsState()
    val apiKey by mainViewModel.apiKey.collectAsState()

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .background(
                                color = UpdateButtonCustom,
                                shape = RoundedCornerShape(49.dp)
                            )
                            .height(90.dp)
                            .width(90.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.plus),
                            contentDescription = "Выход",
                            tint = LightGrayCustom
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(top = 40.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Выход",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 24.sp,
                            letterSpacing = 0.25.sp,
                            fontFamily = FontFamily(
                                Font(R.font.roboto_bold, FontWeight.Bold, FontStyle.Normal)
                            ),
                            lineHeight = 28.sp,
                        ),
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Вы действительно хотите выйти?",
                        style = TextStyle(
                            color = LightGrayCustom,
                            fontSize = 16.sp,
                            letterSpacing = 0.25.sp,
                            fontFamily = FontFamily(
                                Font(R.font.roboto_regular, FontWeight.Bold, FontStyle.Normal)
                            ),
                            lineHeight = 20.sp,
                        ),
                    )
                }
                if (state.isValidating) {
                    Row(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .width(328.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .padding(top = 40.dp)
                            .width(328.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { viewModel.handleIntent(ExitIntent.Exit(apiKey.toString(), context)) },
                            modifier = Modifier
                                .width(160.dp)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = ExitButtonCustom
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Выйти")
                        }
                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .width(160.dp)
                                .height(48.dp)
                                .border(width = 1.dp, DarkGrayCustom, RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Отмена")
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

}