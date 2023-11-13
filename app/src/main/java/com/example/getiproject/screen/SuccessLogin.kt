package com.example.getiproject.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun SuccessLogin(navController: NavController,onSignOutClicked: () -> Unit) {
    Column {
        Text(text = "로그인 성공")
        Button(onClick = {onSignOutClicked()}) {

        }
    }
}