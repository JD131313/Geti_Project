package com.example.getiproject.screen

import FirebaseDataManager
import android.util.Log
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.getiproject.R
import com.example.getiproject.Screen
import com.example.getiproject.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

@Composable
fun Practice(navController: NavController) {

    val firebaseDataManager = FirebaseDataManager()

    // State to hold the list of posts
    var postsState by remember { mutableStateOf<List<Post>>(emptyList()) }

    // State to track the number of posts to fetch
    var numberOfPostsToFetch by remember { mutableStateOf(10) }

    // State to hold the search query
    var searchQuery by remember { mutableStateOf("") }

    // State to track the selected search type
    var searchType by remember { mutableStateOf(SearchType.TITLE) }

    var isTitleSelected by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
    ) {
        LazyColumn {
            items(postsState.filter {
                when (searchType) {
                    SearchType.TITLE -> it.title.contains(searchQuery, ignoreCase = true)
                    SearchType.CONTENT -> it.content.contains(
                        searchQuery,
                        ignoreCase = true
                    )

                    SearchType.BOTH ->
                        it.title.contains(searchQuery, ignoreCase = true) ||
                                it.content.contains(searchQuery, ignoreCase = true)
                }
            }.reversed()) { post ->
                // Display each post in the LazyColumn in reverse order
                PostItem(post = post, firebaseDataManager) {
                    // Navigate to the PostDetail screen when a post is clicked
                    navController.navigate(Screen.PostDetail.route + "/${post.postId}")
                }
            }

            // "더보기" (more) button
            item {
                // Display the "더보기" button at the bottom of the LazyColumn
                Button(
                    onClick = {
                        // Increase the number of posts to fetch
                        numberOfPostsToFetch += 10
                        // Fetch additional posts from Firebase
                        firebaseDataManager.postsRef
                            .limitToLast(numberOfPostsToFetch)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val postList =
                                        snapshot.children.mapNotNull { it.getValue(Post::class.java) }
                                    // Update the state with the new list of posts
                                    postsState = postList
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error if needed
                                    Log.e(
                                        "CommunityHome",
                                        "Error fetching additional posts: ${error.message}"
                                    )
                                }
                            })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 아래 화살표 아이콘
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_downward_24),
                            contentDescription = "더보기",
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // "더보기" 버튼 텍스트
                        Text("더보기")
                    }
                }
            }
        }
    }
}

