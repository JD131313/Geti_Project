package com.example.getiproject.screen

import FirebaseDataManager
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.DisposableEffect
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
import com.example.getiproject.Screen
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale
import coil.compose.rememberImagePainter
import com.example.getiproject.data.Comment
import com.example.getiproject.data.Post


// PostDetail 화면에서 댓글을 수정하고 등록할 수 있도록 수정
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetail(
    navController: NavController,
    postId: String,
    firebaseDataManager: FirebaseDataManager
) {
    var post by remember { mutableStateOf<Post?>(null) }
    var newComment by remember { mutableStateOf("") }

    DisposableEffect(postId) {
        val postReference = firebaseDataManager.postsRef.child(postId)

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val retrievedPost = snapshot.getValue(Post::class.java)
                post = retrievedPost
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
                Log.e("PostDetail", "Error fetching post details: ${error.message}")
            }
        }

        // Fetch the initial post details
        postReference.addValueEventListener(postListener)

        // Remove the listener when the composable is disposed
        onDispose {
            postReference.removeEventListener(postListener)
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
            actualPost.imageUrls?.let { imageUrl ->
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

            // Display comments
            Text("댓글", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            CommentSection(comments = actualPost.comments)

            // Comment input field
            TextField(
                value = newComment,
                onValueChange = { newComment = it },
                label = { Text("댓글 추가") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // Button to add a new comment
            Button(
                onClick = {
                    val updatedComments = actualPost.comments.toMutableList()
                    val timestamp = SimpleDateFormat(
                        "yyyy-MM-dd HH:mm",
                        Locale.getDefault()
                    ).format(System.currentTimeMillis())

                    updatedComments.add(
                        Comment(
                            author = "댓글 작성자", // 작성자를 설정하거나, 현재 사용자 정보를 사용
                            content = newComment,
                            timestamp = timestamp
                        )
                    )

                    // Update the post with the new comment
                    val updatedPost = Post(
                        postId = postId,
                        title = actualPost.title,
                        content = actualPost.content,
                        author = actualPost.author,
                        imageUrls = actualPost.imageUrls,
                        timestamp = actualPost.timestamp,
                        hits = actualPost.hits,
                        comments = updatedComments
                    )

                    // Use the FirebaseDataManager to update the post
                    firebaseDataManager.updatePost(updatedPost)

                    // Clear the new comment field
                    newComment = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(text = "댓글 등록")
            }

            Button(onClick = {
                // Navigate to the EditPostScreen when the Edit button is clicked
                navController.navigate(Screen.EditPostScreen.route + "/$postId")
            }) {
                Text(text = "수정")
            }
        }
    }
}

// 댓글 섹션을 표시하는 컴포저블 추가
@Composable
fun CommentSection(comments: List<Comment>) {
    LazyColumn {
        items(comments) { comment ->
            CommentItem(comment = comment)
        }
    }
}

// 댓글 항목을 표시하는 컴포저블 추가
@Composable
fun CommentItem(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = comment.author, fontWeight = FontWeight.Bold)
        Text(text = comment.content)
        Text(text = comment.timestamp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPostScreen(
    navController: NavController,
    postId: String,
    firebaseDataManager: FirebaseDataManager
) {

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
                        imageUrls = retrievedPost?.imageUrls, // Keep the original imageUrl
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


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CreatePostScreen(navController: NavController) {
//    var title by remember { mutableStateOf("") }
//    var content by remember { mutableStateOf("") }
//    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//
//    // FirebaseDataManager().createPost(post)를 사용하여 새로운 게시글 생성
//    val firebaseDataManager = FirebaseDataManager()
//
//    // TODO: 구글 로그인을 통해 사용자 정보를 가져오는 로직이 필요할 수 있습니다.
//    // 예를 들어, FirebaseAuth.getInstance().currentUser를 사용하여 현재 로그인한 사용자 정보를 가져올 수 있습니다.
//    val currentUser = FirebaseAuth.getInstance().currentUser
//    val author = currentUser?.displayName ?: "Unknown"
//
//    // 현재 시간을 가져와 timestamp로 사용
//    val formattedTimestamp = SimpleDateFormat(
//        "yyyy-MM-dd HH:mm",
//        Locale.getDefault()
//    ).format(System.currentTimeMillis())
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        // 제목 입력칸
//        TextField(
//            value = title,
//            onValueChange = { title = it },
//            label = { Text("제목") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        )
//
//        // 내용 입력칸
//        TextField(
//            value = content,
//            onValueChange = { content = it },
//            label = { Text("내용") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//                .height(200.dp)
//        )
//        val getContent =
//            rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
//                uri?.let { selectedImageUri = it }
//            }
//        // 이미지 선택 버튼
//        Button(
//            onClick = {
//                // Use the activity result API to get content (image) from the gallery
//
//                getContent.launch("image/*")
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            Text(text = "이미지 선택")
//        }
//
//        // Display the selected image if available
//        selectedImageUri?.let { uri ->
//            Image(
//                painter = rememberImagePainter(data = uri),
//                contentDescription = null,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//                    .padding(8.dp)
//                    .clip(shape = RoundedCornerShape(4.dp))
//            )
//        }
//        Button(
//            onClick = {
//                // Your save logic here, whether an image is selected or not
//                val imageName = "${UUID.randomUUID()}.jpg"
//                val storageRef =
//                    FirebaseStorage.getInstance().reference.child("images/$imageName")
//
//                // Upload the image if available
//                if (selectedImageUri != null) {
//                    storageRef.putFile(selectedImageUri!!)
//                        .addOnSuccessListener {
//                            // Get the download URL for the image
//                            storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
//                                // Create a new post with the image URL
//                                val newPost = Post(
//                                    postId = "",
//                                    title = title,
//                                    content = content,
//                                    author = author,
//                                    imageUrl = imageUrl.toString(),
//                                    timestamp = formattedTimestamp
//                                )
//
//                                // Create the post in Firebase
//                                firebaseDataManager.createPost(newPost)
//                                navController.navigate(Screen.CommunityHome.route)
//                            }
//                        }
//                        .addOnFailureListener { exception ->
//                            // Handle errors
//                            // You might want to show a Snackbar or Toast with an error message
//                        }
//                } else {
//                    // Create a new post without an image
//                    val newPost = Post(
//                        postId = "",
//                        title = title,
//                        content = content,
//                        author = author,
//                        imageUrl = "", // You can set this to an empty string or handle it accordingly
//                        timestamp = formattedTimestamp
//                    )
//
//                    // Create the post in Firebase
//                    firebaseDataManager.createPost(newPost)
////                    navController.navigate(Screen.CommunityHome.route)
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            Text(text = "저장")
//        }
//
//    }
//
//    // 저장 버튼
//}


