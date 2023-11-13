package com.example.getiproject.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.getiproject.Screen

// CommunityApp.kt
@Composable
fun CommunityApp(navController: NavController) {
    var currentScreen by remember { mutableStateOf(Screen.CommunityScreen) }

    when (currentScreen) {
        is Screen.CommunityScreen -> CommunityScreen(
            navController = navController,
            onPostClick = { postId ->
                currentScreen = Screen.PostDetail(postId)
            },
            onCreatePostClick = {
                currentScreen = Screen.CreatePost
            }
        )
        is Screen.PostDetailScreen -> {
            val postId = (currentScreen as Screen.PostDetail).postId
            PostDetailScreen(
                postId = postId,
                onAction = { action ->
                    when (action) {
                        is PostDetailAction.Back -> currentScreen = Screen.CommunityScreen
                        is PostDetailAction.Edit -> {
                            // TODO: Move to the edit screen
                        }
                        is PostDetailAction.Delete -> {
                            // TODO: Implement the logic for deleting a post
                        }
                        is PostDetailAction.AddComment -> {
                            // TODO: Implement the logic for adding a comment
                        }
                    }
                }
            )
        }
        Screen.CreatePostScreen -> {
            CreatePostScreen(onPostCreated = {
                // TODO: Implement what should happen when a post is created
            })
        }
    }
}

@Composable
fun CommunityScreen(
    navController: NavController,
    onPostClick: (String) -> Unit,
    onCreatePostClick: () -> Unit
) {
    var recentPosts by remember { mutableStateOf<List<Post>>(emptyList()) }

    // Firebase에서 최근 게시글을 가져와서 UI에 표시
    FirebaseDataManager().getRecentPosts { posts ->
        recentPosts = posts
    }

    Column {
        // TODO: recentPosts를 UI에 표시
        for (post in recentPosts) {
            // 각 게시글을 UI에 표시
            // onPostClick(post.postId)를 사용하여 게시글 상세 화면으로 이동
        }

        // "Create Post" 버튼
        Button(onClick = onCreatePostClick) {
            Text("게시글 작성")
        }
    }
}

@Composable
fun PostDetailScreen(postId: String, onAction: (PostDetailAction) -> Unit) {
    var post by remember { mutableStateOf<Post?>(null) }

    // Firebase에서 특정 게시글을 가져와서 UI에 표시
    FirebaseDataManager().getPost(postId) { retrievedPost ->
        post = retrievedPost
    }

    if (post != null) {
        // TODO: 게시글의 상세 내용을 UI에 표시
        // onAction을 사용하여 다양한 동작 처리
    } else {
        // TODO: 게시글이 없는 경우에 대한 처리
    }
}

@Composable
fun CreatePostScreen(onPostCreated: () -> Unit) {
    // TODO: 새로운 게시글을 작성하는 UI 구현
    // FirebaseDataManager().createPost(post)를 사용하여 새로운 게시글 생성
}

data class Post(
    var postId: String,
    val title: String,
    val content: String,
    val author: String, // 사용자의 displayName 또는 UID 등으로 대체
    val imageUrl: String? // 이미지의 Firebase Storage URL
)