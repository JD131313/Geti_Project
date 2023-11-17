package com.example.getiproject.screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.getiproject.R
import com.example.getiproject.Screen
import com.example.getiproject.data.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@Composable
fun Asd(navController: NavController) {
    Image(
        painter = painterResource(id = R.drawable.vector4),
        contentDescription = "상단 배경",
        contentScale = ContentScale.FillBounds, // 또는 다른 ContentScale 값 사용
        modifier = Modifier
            .padding(1.dp)
            .fillMaxWidth()
            .height(200.dp)
    )
}

//@Composable
//fun UserInfo(navController: NavController) {
//
//}

//CircleImage(
//modifier = Modifier
//.padding(8.dp)
//.align(Alignment.CenterHorizontally),
//contentDescription = "Profile Picture",
//user = currentUser
//)
//
//@Composable
//fun CircleImage(
//    modifier: Modifier = Modifier,
//    contentDescription: String? = null,
//    user: FirebaseUser?,
//) {
//    val placeholderColor = Color.Gray
//
//    // Use the Coil library to load and display the image from the URL
//    DisposableEffect(user?.photoUrl) {
//        onDispose {} // No-op
//
//        // Additional cleanup logic can be added here if needed
//    }
//
//    Image(
//        painter = rememberImagePainter(
//            data = user?.photoUrl,
//            builder = {
//                // You can add additional parameters for image loading here
//                crossfade(true)
//            }
//        ),
//        contentDescription = contentDescription,
//        modifier = modifier
//            .size(120.dp)
//            .clip(CircleShape)
//            .background(MaterialTheme.colorScheme.primary)
//            .border(2.dp, MaterialTheme.colorScheme.onPrimary, CircleShape),
//        contentScale = ContentScale.Crop
//    )
//}

//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun CommunityHome(navController: NavController) {
//    val firebaseDataManager = FirebaseDataManager()
//
//    // State to hold the list of posts
//    var postsState by remember { mutableStateOf<List<Post>>(emptyList()) }
//
//    // State to track the number of posts to fetch
//    var numberOfPostsToFetch by remember { mutableStateOf(10) }
//
//    // State to hold the search query
//    var searchQuery by remember { mutableStateOf("") }
//
//    // State to track the selected search type
//    var searchType by remember { mutableStateOf(SearchType.TITLE) }
//
//    var isTitleSelected by remember { mutableStateOf(true) }
//
//    // Use Effect to fetch the initial posts when the composable is first created
//    LaunchedEffect(searchQuery, searchType) {
//        firebaseDataManager.postsRef
//            .limitToLast(numberOfPostsToFetch)
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val postList = snapshot.children.mapNotNull { it.getValue(Post::class.java) }
//
//                    // Filter posts based on search criteria locally
//                    postsState = postList.filter { post ->
//                        when (searchType) {
//                            SearchType.TITLE -> post.title.contains(searchQuery, ignoreCase = true)
//                            SearchType.CONTENT -> post.content.contains(
//                                searchQuery,
//                                ignoreCase = true
//                            )
//
//                            SearchType.BOTH ->
//                                post.title.contains(
//                                    searchQuery,
//                                    ignoreCase = true
//                                ) || post.content.contains(searchQuery, ignoreCase = true)
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle error if needed
//                    Log.e("CommunityHome", "Error fetching posts: ${error.message}")
//                }
//            })
//    }
//    Image(
//        painter = painterResource(id = R.drawable.vector4),
//        contentDescription = "상단 배경",
//        contentScale = ContentScale.FillBounds, // 또는 다른 ContentScale 값 사용
//        modifier = Modifier
//            .padding(1.dp)
//            .fillMaxWidth()
//            .height(200.dp)
//    )
//    Column {
//
//        // Search input field
//        OutlinedTextField(
//            value = searchQuery,
//            onValueChange = { searchQuery = it },
//            label = { Text("검색어를 입력하세요") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 70.dp, start = 30.dp, end = 30.dp)
//                .background(color = Color.White, shape = RoundedCornerShape(25.dp))
//                .clip(RoundedCornerShape(25.dp)),
//            colors = TextFieldDefaults.outlinedTextFieldColors(
//                focusedBorderColor = Color.White, // 포커스된 경우의 테두리 색상
//                unfocusedBorderColor = Color.White, // 포커스가 해제된 경우의 테두리 색상
//            ),
//            trailingIcon = {
//                Icon(
//                    painter = painterResource(id = R.drawable.baseline_search_24),
//                    contentDescription = "검색",
//                    modifier = Modifier
//                        .clickable {
//                            val queryRef = when (searchType) {
//                                SearchType.TITLE -> firebaseDataManager.postsRef.orderByChild("title")
//                                SearchType.CONTENT -> firebaseDataManager.postsRef.orderByChild("content")
//                                SearchType.BOTH -> firebaseDataManager.postsRef
//                                    .orderByChild("title_content") // Create a composite index in Firebase for both title and content
//                            }
//
//                            queryRef
//                                .startAt(searchQuery)
//                                .endAt(searchQuery + "\uf8ff") // This is to get all results starting with the searchQuery
//                                .addListenerForSingleValueEvent(object : ValueEventListener {
//                                    override fun onDataChange(snapshot: DataSnapshot) {
//                                        val postList =
//                                            snapshot.children.mapNotNull { it.getValue(Post::class.java) }
//                                        // Update the state with the retrieved posts
//                                        postsState = postList
//                                    }
//
//                                    override fun onCancelled(error: DatabaseError) {
//                                        // Handle error if needed
//                                        Log.e(
//                                            "CommunityHome",
//                                            "Error fetching posts: ${error.message}"
//                                        )
//                                    }
//                                })
//                        }
//                        .size(24.dp) // 아이콘 크기 조절
//
//                )
//            }
//        )
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            RadioGroupOptions(
//                options = listOf("제목", "내용", "제목 및 내용"),
//                selectedOption = if (isTitleSelected) "제목" else "내용", // Update selectedOption based on the state
//                onOptionSelected = { selectedOption ->
//                    searchType = when (selectedOption) {
//                        "제목" -> {
//                            isTitleSelected = true
//                            SearchType.TITLE
//                        }
//
//                        "내용" -> {
//                            isTitleSelected = false
//                            SearchType.CONTENT
//                        }
//
//                        else -> SearchType.BOTH
//                    }
//                }
//            )
//        }
//
//        LazyColumn {
//            items(postsState.filter {
//                when (searchType) {
//                    SearchType.TITLE -> it.title.contains(searchQuery, ignoreCase = true)
//                    SearchType.CONTENT -> it.content.contains(searchQuery, ignoreCase = true)
//                    SearchType.BOTH ->
//                        it.title.contains(searchQuery, ignoreCase = true) ||
//                                it.content.contains(searchQuery, ignoreCase = true)
//                }
//            }.reversed()) { post ->
//                // Display each post in the LazyColumn in reverse order
//                PostItem(post = post, firebaseDataManager) {
//                    // Navigate to the PostDetail screen when a post is clicked
//                    navController.navigate(Screen.PostDetail.route + "/${post.postId}")
//                }
//            }
//
//            // "더보기" (more) button
//            item {
//                Button(onClick = {
//                    // Increase the number of posts to fetch
//                    numberOfPostsToFetch += 10
//                    // Fetch additional posts from Firebase
//                    firebaseDataManager.postsRef
//                        .limitToLast(numberOfPostsToFetch)
//                        .addListenerForSingleValueEvent(object : ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                val postList =
//                                    snapshot.children.mapNotNull { it.getValue(Post::class.java) }
//                                // Update the state with the new list of posts
//                                postsState = postList
//                            }
//
//                            override fun onCancelled(error: DatabaseError) {
//                                // Handle error if needed
//                                Log.e(
//                                    "CommunityHome",
//                                    "Error fetching additional posts: ${error.message}"
//                                )
//                            }
//                        })
//                }) {
//                    Text("더보기")
//                }
//            }
//        }
//    }
//
//    Box(
//        contentAlignment = Alignment.BottomEnd,
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(30.dp, 80.dp)
//    ) {
//        Button(
//            onClick = {
//                navController.navigate(Screen.CreatePostScreen.route)
//            },
//            modifier = Modifier
//                .size(56.dp) // 버튼 크기를 설정
//                .clip(CircleShape) // 버튼을 완전한 원형으로 만듦
//                .background(Color.Blue)
//                .padding(0.dp) // 버튼 배경색을 원하는 색상으로 설정
//        ) {
//            Icon(
//                imageVector = Icons.Default.Add, // 플러스 아이콘 사용
//                contentDescription = "게시글 추가", // contentDescription을 비워줌
//                tint = Color.White, // 아이콘 색상을 원하는 색상으로 설정
//                modifier = Modifier.size(50.dp) // 아이콘 크기를 설정
//            )
//        }
//    }
//}
//
//@Composable
//fun RadioGroupOptions(
//    options: List<String>,
//    selectedOption: String,
//    onOptionSelected: (String) -> Unit
//) {
//    val radioGroup = remember { mutableStateOf(selectedOption) }
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .horizontalScroll(rememberScrollState())
//    ) {
//        options.forEach { option ->
//            Row(
//                modifier = Modifier
//                    .padding(end = 16.dp)
//                    .clickable {
//                        radioGroup.value = option
//                        onOptionSelected(option)
//                    }
//            ) {
//                RadioButton(
//                    selected = option == radioGroup.value,
//                    onClick = null
//                )
//                Spacer(modifier = Modifier.width(4.dp))
//                Text(text = option)
//            }
//        }
//    }
//}
//
//enum class SearchType {
//    TITLE, CONTENT, BOTH
//}
