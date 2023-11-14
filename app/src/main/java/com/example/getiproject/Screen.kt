package com.example.getiproject

sealed class Screen(val route : String) {
    object SuccessLogin : Screen("successLogin")
    object Login : Screen("login")
    object CommunityHome : Screen("community")
    object PostDetail : Screen("postDetailScreen")
    object CreatePostScreen : Screen("createPostScreen")
    object UpdatePostScreen : Screen("updatepostscreen")


}
