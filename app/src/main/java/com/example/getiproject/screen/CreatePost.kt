package com.example.getiproject.screen

import FirebaseDataManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.getiproject.Screen
import com.example.getiproject.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Composable
fun CreatePostScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

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

    // Use registerForActivityResult to get content (image) from the gallery
    val getMultipleContents =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
            uris?.let {
                // Replace the selected image URIs with the new list
                selectedImageUris = it
            }
        }

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

        // 이미지 선택 버튼
        Button(
            onClick = {
                // Use the activity result API to get content (images) from the gallery
                getMultipleContents.launch("image/*")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "이미지 선택")
        }
        Box(modifier = Modifier.height(300.dp)) {
            LazyColumn {
                item {
                    selectedImageUris.forEach { uri ->
                        Image(
                            painter = rememberImagePainter(data = uri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(8.dp)
                                .clip(shape = RoundedCornerShape(4.dp))
                        )
                    }

                }
            }
        }

        // Display the selected images if available

        Button(
            onClick = {
                // Your save logic here, whether images are selected or not
                val imageUrls = mutableListOf<String>()

                // Upload each selected image
                selectedImageUris.forEachIndexed { index, uri ->
                    val imageName = "${UUID.randomUUID()}_$index.jpg"
                    val storageRef =
                        FirebaseStorage.getInstance().reference.child("images/$imageName")

                    storageRef.putFile(uri)
                        .addOnSuccessListener {
                            // Get the download URL for the image
                            storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                // Add the image URL to the list
                                imageUrls.add(imageUrl.toString())

                                // Check if all images are uploaded
                                if (imageUrls.size == selectedImageUris.size) {
                                    // Create a new post with the list of image URLs
                                    val newPost = Post(
                                        postId = "",
                                        title = title,
                                        content = content,
                                        author = author,
                                        imageUrls = imageUrls,
                                        timestamp = formattedTimestamp
                                    )

                                    // Create the post in Firebase
                                    firebaseDataManager.createPost(newPost)
                                    navController.navigate(Screen.CommunityHome.route)
                                }
                            }
                        }
                        .addOnFailureListener { exception ->
                            // Handle errors
                            // You might want to show a Snackbar or Toast with an error message
                        }
                }

                // If no images are selected, create a new post without image URLs
                if (selectedImageUris.isEmpty()) {
                    val newPost = Post(
                        postId = "",
                        title = title,
                        content = content,
                        author = author,
                        imageUrls = emptyList(),
                        timestamp = formattedTimestamp
                    )

                    // Create the post in Firebase
                    firebaseDataManager.createPost(newPost)
                }
                navController.navigate(Screen.CommunityHome.route)

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "저장")
        }
    }
}
