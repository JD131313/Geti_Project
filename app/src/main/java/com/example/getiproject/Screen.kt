package com.example.getiproject

sealed class Screen(val route : String) {
    object SuccessLogin : Screen("successLogin")
    object LoginScreen : Screen("login")
    object CommunityScreen : Screen("community")
    object PostDetailScreen : Screen("postDetailScreen")
    object CreatePostScreen : Screen("createPostScreen")

}
