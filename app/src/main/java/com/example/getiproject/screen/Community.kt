package com.example.getiproject.screen

import FirebaseDataManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.getiproject.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommunityHome(navController: NavController) {
    val firebaseDataManager = FirebaseDataManager()

    // State to hold the list of posts
    var postsState by remember { mutableStateOf<List<Post>>(emptyList()) }

    // State to track the number of posts to fetch
    var numberOfPostsToFetch by remember { mutableStateOf(10) }

    // Use Effect to fetch the initial posts when the composable is first created
    LaunchedEffect(true) {
        firebaseDataManager.postsRef
            .limitToLast(numberOfPostsToFetch)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val postList = snapshot.children.mapNotNull { it.getValue(Post::class.java) }
                    // Update the state with the retrieved posts
                    postsState = postList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error if needed
                    Log.e("CommunityHome", "Error fetching posts: ${error.message}")
                }
            })
    }

    Column {
        LazyColumn {
            items(postsState.reversed()) { post ->
                // Display each post in the LazyColumn in reverse order
                PostItem(post = post) {
                    // Navigate to the PostDetail screen when a post is clicked
                    navController.navigate(Screen.PostDetail.route + "/${post.postId}")
                }
            }

            // "더보기" (more) button
            item {
                Button(onClick = {
                    // Increase the number of posts to fetch
                    numberOfPostsToFetch += 10
                    firebaseDataManager.postsRef
                        .limitToLast(numberOfPostsToFetch)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val postList = snapshot.children.mapNotNull { it.getValue(Post::class.java) }
                                // Update the state with the retrieved posts
                                postsState = postList
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle error if needed
                                Log.e("CommunityHome", "Error fetching posts: ${error.message}")
                            }
                        })
                }) {
                    Text("더보기")
                }
            }
        }        // "게시글 작성" (create post) button
        Button(onClick = {
            navController.navigate(Screen.CreatePostScreen.route)
        }) {
            Text("게시글 작성")
        }
    }
}

@Composable
fun PostItem(post: Post, onPostClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onPostClick.invoke() }
            .padding(bottom = 5.dp)
    ) {
        Text(text = post.title)
        Text(text = post.author)
        Text(text = post.timestamp)
    }
}

@Composable
fun PostDetail(navController: NavController, postId: String, firebaseDataManager: FirebaseDataManager) {
    var post by remember { mutableStateOf<Post?>(null) }

    LaunchedEffect(postId) {
        // Fetch the post details using the provided postId
        firebaseDataManager.getPost(postId).addOnSuccessListener { snapshot ->
            val retrievedPost = snapshot.getValue(Post::class.java)
            post = retrievedPost
        }.addOnFailureListener { e ->
            // Handle failure if needed
            Log.e("PostDetail", "Error fetching post details: ${e.message}")
        }
    }

    post?.let { actualPost ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(text = actualPost.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = actualPost.author)
            Text(text = actualPost.timestamp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = actualPost.content)
        }
    }
    Button(onClick = { /*TODO*/ }) {
        Text(text = "수정")
    }
    Button(onClick = { /*TODO*/ }) {
        Text(text = "삭제")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    // FirebaseDataManager().createPost(post)를 사용하여 새로운 게시글 생성
    val firebaseDataManager = FirebaseDataManager()

    // TODO: 구글 로그인을 통해 사용자 정보를 가져오는 로직이 필요할 수 있습니다.
    // 예를 들어, FirebaseAuth.getInstance().currentUser를 사용하여 현재 로그인한 사용자 정보를 가져올 수 있습니다.
    val currentUser = FirebaseAuth.getInstance().currentUser
    val author = currentUser?.displayName ?: "Unknown"

    // 현재 시간을 가져와 timestamp로 사용
    val formattedTimestamp = SimpleDateFormat(
        "yyyy-MM-dd HH:mm",
        Locale.getDefault()
    ).format(System.currentTimeMillis())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 제목 입력칸
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("제목") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // 내용 입력칸
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("내용") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(200.dp)
        )

        // 저장 버튼
        Button(
            onClick = {
//                 새로운 게시글 생성
                val newPost = Post(
                    postId = "", // postId는 Firebase에서 생성될 것이므로 일단 빈 문자열로 둡니다.
                    title = title,
                    content = content,
                    author = author,
                    imageUrl = null, // 이미지 URL은 여기서는 null로 처리했습니다.
                    timestamp = formattedTimestamp
                )

                firebaseDataManager.createPost(newPost)
//                navController.navigate(Screen.CommunityHome.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "저장")
        }
    }
}


data class Post(
    var postId: String = "",
    val title: String = "",
    val content: String = "",
    val author: String = "", // 사용자의 displayName 또는 UID 등으로 대체
    val imageUrl: String? = null, // 이미지의 Firebase Storage URL
    val timestamp: String = ""
)