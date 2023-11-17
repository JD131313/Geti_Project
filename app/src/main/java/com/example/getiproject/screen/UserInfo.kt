package com.example.getiproject.screen


import FirebaseDataManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.example.getiproject.Screen
import com.example.getiproject.data.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserInfo(navController: NavController) {
    // Firebase Authentication 인스턴스 가져오기
    val auth = Firebase.auth
    // 현재 인증된 사용자 가져오기
    val user = auth.currentUser

    // 사용자 이름을 저장할 상태(State) 정의
    var userName by remember { mutableStateOf(user?.displayName ?: "") }

    // 사용자 UID를 가져오기
    val userUid = user?.uid

    // State to hold the list of user-specific posts
    var userPosts by remember { mutableStateOf<List<Post>>(emptyList()) }
    val firebaseDataManager = FirebaseDataManager()
    // Use Effect to fetch the user-specific posts when the composable is first created
    LaunchedEffect(userUid) {
        userUid?.let { uid ->
            firebaseDataManager.postsRef.orderByChild("author").equalTo(uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val postList =
                            snapshot.children.mapNotNull { it.getValue(Post::class.java) }
                        Log.d("UserInfo", "User Posts: $postList")
                        userPosts = postList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error if needed
                        Log.e("UserInfo", "Error fetching user posts: ${error.message}")
                    }
                })
        }
    }

    // 사용자 이름 수정 함수 정의
    fun updateUserName() {
        user?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setDisplayName(userName)
                .build()
        )
    }

    // Composable UI 작성
    Column(
        modifier = Modifier
//            .fillMaxSize()
            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 사용자 이름을 입력하는 TextField
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("사용자 이름") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // 사용자 이름 수정 버튼
        Button(
            onClick = { updateUserName() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("사용자 이름 수정")
        }

        // Display the list of user-specific posts
        LazyColumn {
            items(userPosts) { post ->
                // Display each user-specific post
                DisplayPostItem(post, firebaseDataManager) {
                    // Navigate to the PostDetail screen when a post is clicked
                    navController.navigate(Screen.PostDetail.route + "/${post.postId}")
                }
            }
        }
    }
}

@Composable
fun DisplayPostItem(post: Post, firebaseDataManager: FirebaseDataManager, onPostClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clickable {
                // Increment hits when a post is clicked
                val updatedHits = post.hits + 1
                firebaseDataManager.updatePostHits(post.postId, updatedHits)
                onPostClick.invoke()
            }
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp)
    ) {
        Column {
            // Display post content
            Text(
                modifier = Modifier.padding(start = 10.dp),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                text = post.title
            )

            LazyRow {
                items(post.imageUrls.orEmpty()) { imageUrl ->
                    // Display post images
                    Image(
                        painter = rememberImagePainter(
                            data = imageUrl,
                            builder = {
                                transformations(RoundedCornersTransformation(4f))
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp) // Adjust the size as needed
                            .padding(4.dp) // Add padding between images
                            .clip(shape = RoundedCornerShape(4.dp))
                    )
                }
            }

            // Display truncated post content
            Text(
                text = post.content.take(30),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .heightIn(min = 30.dp) // Limit the height to show only one line
            )

            // Display post details
            Row {
                Spacer(modifier = Modifier.padding(start = 10.dp))
                Text(fontSize = 13.sp, text = post.author)
                // ... (other post details)
            }
        }
        Divider(modifier = Modifier.fillMaxWidth())
    }
}

