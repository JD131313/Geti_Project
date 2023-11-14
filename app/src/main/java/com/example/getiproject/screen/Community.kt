package com.example.getiproject.screen

import FirebaseDataManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
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
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID
import androidx.compose.foundation.Image
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter


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
                                val postList =
                                    snapshot.children.mapNotNull { it.getValue(Post::class.java) }
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
    Box(
        modifier = Modifier
            .clickable { onPostClick.invoke() }
    ) {
        Column {
            Text(text = post.title)
            Row {
                Text(text = post.author)
                Text(text = post.timestamp)
            }
        }

    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun PostDetail(
    navController: NavController,
    postId: String,
    firebaseDataManager: FirebaseDataManager
) {
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

            // Display the image if imageUrl is not null
            actualPost.imageUrl?.let { imageUrl ->
                val imageModifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(8.dp)
                    .clip(shape = RoundedCornerShape(4.dp))

                Image(
                    painter = rememberImagePainter(data = imageUrl),
                    contentDescription = null,
                    modifier = imageModifier
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = actualPost.content)

            Button(onClick = {
                // Navigate to the EditPostScreen when the Edit button is clicked
                navController.navigate(Screen.EditPostScreen.route + "/$postId")
            }) {
                Text(text = "수정")
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    navController: NavController,
    postId: String,
    firebaseDataManager: FirebaseDataManager
) {
    // Similar to CreatePostScreen, you can use the provided postId to fetch the post details
    // and populate the fields for editing.
    // You can use a similar approach as in PostDetail composable to fetch the post details.

    // Use a similar approach as in CreatePostScreen to fetch the post details using postId
    // and populate the title and content fields for editing.

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var retrievedPost: Post? by remember { mutableStateOf(null) }

    // Fetch post details using postId and populate the fields
    LaunchedEffect(postId) {
        firebaseDataManager.getPost(postId).addOnSuccessListener { snapshot ->
            retrievedPost = snapshot.getValue(Post::class.java)
            if (retrievedPost != null) {
                title = retrievedPost!!.title
                content = retrievedPost!!.content
            }
        }.addOnFailureListener { e ->
            // Handle failure if needed
            Log.e("EditPostScreen", "Error fetching post details: ${e.message}")
        }
    }

    // The rest of the code for EditPostScreen is similar to CreatePostScreen,
    // where you can have TextFields for title and content, and a Button to save the changes.

    // Use the same logic as CreatePostScreen with some modifications for updating the post.

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title input field
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("제목") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )

        // Content input field
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("내용") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(200.dp)
        )
        Row {
            Button(
                onClick = {
                    // Delete the existing post
                    firebaseDataManager.deletePost(postId)

                    // Navigate back to the previous screen after deleting
                    navController.navigate(Screen.CommunityHome.route)
                },
            ) {
                Text(text = "삭제")
            }
            Button(
                onClick = {
                    // Update the existing post with the new title and content
                    val updatedPost = Post(
                        postId = postId,
                        title = title,
                        content = content,
                        author = retrievedPost?.author
                            ?: "", // Keep the original author or set a default value
                        imageUrl = retrievedPost?.imageUrl, // Keep the original imageUrl
                        timestamp = retrievedPost?.timestamp
                            ?: "" // Keep the original timestamp or set a default value
                    )


                    // Use the FirebaseDataManager to update the post
                    firebaseDataManager.updatePost(updatedPost)

                    // Navigate back to the post detail screen after updating
                    navController.popBackStack()
                },
            ) {
                Text(text = "저장")
            }

        }
        // Save changes button
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

        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

// Activity Result API for getting content (image) from the gallery
        val getContent =
            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                uri?.let { selectedImageUri = it }
            }
        Column {
            Button(
                onClick = {
                    getContent.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "이미지 선택")
            }

            // 저장 버튼
            selectedImageUri?.let { uri ->
                // Display the selected image
                Image(
                    painter = rememberImagePainter(data = uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(8.dp)
                        .clip(shape = RoundedCornerShape(4.dp))
                )

                // Add the selected image to Firebase Storage when the user clicks the save button
                Button(
                    onClick = {
                        val imageName = "${UUID.randomUUID()}.jpg"
                        val storageRef =
                            FirebaseStorage.getInstance().reference.child("images/$imageName")

                        // Upload the image
                        storageRef.putFile(uri)
                            .addOnSuccessListener {
                                // Get the download URL for the image
                                storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                    // Create a new post with the image URL
                                    val newPost = Post(
                                        postId = "",
                                        title = title,
                                        content = content,
                                        author = author,
                                        imageUrl = imageUrl.toString(),
                                        timestamp = formattedTimestamp
                                    )

                                    // Create the post in Firebase
                                    firebaseDataManager.createPost(newPost)
                                    navController.navigate(Screen.CommunityHome.route)
                                }
                            }
                            .addOnFailureListener { exception ->
                                // Handle errors
                                // You might want to show a Snackbar or Toast with an error message
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "저장")
                }
            }
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