package com.example.getiproject

sealed class Screen(val route: String) {
    object SuccessLogin : Screen("successLogin")
    object Login : Screen("login")
    object CommunityHome : Screen("communityhome")
    object PostDetail : Screen("postDetailScreen")
    object CreatePostScreen : Screen("createPostScreen")
    object EditPostScreen : Screen("updatepostscreen")
    object UserInfo : Screen("userinfo")


}
