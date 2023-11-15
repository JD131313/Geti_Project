package com.example.getiproject.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.getiproject.Screen
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@Composable
fun SuccessLogin(navController: NavController, onSignOutClicked: () -> Unit) {
    Column {
        Text(text = "로그인 성공")
        Button(onClick = { onSignOutClicked() }) {
            Text(text = "로그 아웃")
        }
        Button(onClick = { navController.navigate(Screen.CommunityHome.route)
        }) {
            Text(text = "커뮤니티")
        }
        Button(onClick = { navController.navigate(Screen.Asd.route)
        }) {
            Text(text = "커뮤니티")
        }
    }
}