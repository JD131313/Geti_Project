package com.example.getiproject

sealed class ScreenRoute(val route : String) {
    object SuccessLogin : ScreenRoute("successLogin")
    object Login : ScreenRoute("login")

}
