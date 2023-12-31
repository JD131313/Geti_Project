package com.example.getiproject

import com.example.getiproject.database.FirebaseDataManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.getiproject.database.FirebaseAuthenticationManager
import com.example.getiproject.screen.CommunityHome
import com.example.getiproject.screen.CreatePostScreen
import com.example.getiproject.screen.EditPostScreen
import com.example.getiproject.screen.Login
import com.example.getiproject.screen.PostDetail
import com.example.getiproject.screen.SuccessLogin
import com.example.getiproject.screen.UserInfo
import com.example.getiproject.ui.theme.GetiProjectTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    // 파이어베이스 로그인
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseDataManager: FirebaseDataManager // Add this line

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize com.example.getiproject.database.FirebaseDataManager
        firebaseDataManager = FirebaseDataManager()


        setContent {
            GetiProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()

                    val user: FirebaseUser? = mAuth.currentUser
                    val startDestination = remember {
                        if (user == null) {
                            Screen.Login.route
                        } else {
                            Screen.SuccessLogin.route
                        }
                    }
                    val signInIntent = googleSignInClient.signInIntent

                    val launcher =
                        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                            val data = result.data
                            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                            val exception = task.exception
                            if (task.isSuccessful) {
                                try {
                                    val account = task.getResult(ApiException::class.java)!!
                                    firebaseAuthWithGoogle(
                                        account.idToken!!,
                                        mAuth,
                                        navController = navController
                                    )
                                } catch (e: Exception) {
                                    Log.d("SignIn", "로그인 실패")
                                }
                            } else {
                                Log.d("SignIn", exception.toString())
                            }
                        }

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable(Screen.Login.route) {
                            Login {
                                launcher.launch(signInIntent)
                            }
                        }
                        composable(Screen.SuccessLogin.route) {
                            SuccessLogin(
                                navController,
                                onSignOutClicked = { signOut(navController) })
                        }
                        composable(Screen.CommunityHome.route) { CommunityHome(navController) }
                        composable(route = "${Screen.PostDetail.route}/{postId}") { backStackEntry ->
                            val postId = backStackEntry.arguments?.getString("postId")
                            if (::firebaseDataManager.isInitialized) { // Check if initialized
                                PostDetail(navController, postId ?: "", firebaseDataManager)
                            } else {
                                // Handle the case where firebaseDataManager is not initialized yet
                                Log.e("MainActivity", "com.example.getiproject.database.FirebaseDataManager is not initialized")
                            }
                        }
                        composable(Screen.CreatePostScreen.route) { CreatePostScreen(navController) }
                        composable(Screen.EditPostScreen.route + "/{postId}") { backStackEntry ->
                            val postId = backStackEntry.arguments?.getString("postId")
                            val firebaseDataManager =
                                FirebaseDataManager() // 또는 사용자 정의된 로직으로 FirebaseDataManager를 초기화

                            if (postId != null) {
                                EditPostScreen(navController, postId, firebaseDataManager)
                            } else {
                                Log.e("MainActivity", "postId is null")
                            }
                        }
                        composable(Screen.UserInfo.route) { UserInfo(navController)}

                    }
                }
            }
        }


    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        mAuth: FirebaseAuth,
        navController: NavController
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // SignIn Successful
                    val currentUser = mAuth.currentUser
                    currentUser?.let {
                        // Check if the user document already exists in Firestore
                        val db = Firebase.firestore
                        val uid = it.uid
                        val userRef = db.collection("users").document(uid)

                        userRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    // User document already exists, no need to save again
                                    Log.d("Firestore", "User document already exists")
                                } else {
                                    // User document doesn't exist, save the data
                                    val googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this)
                                    val profileImageUri = googleSignInAccount?.photoUrl

                                    val user = hashMapOf(
                                        "email" to it.email,
                                        "displayName" to it.displayName,
                                        "photoUrl" to profileImageUri.toString(), // Add the profile image URL
                                        // Add other fields as needed
                                    )

                                    userRef.set(user)
                                        .addOnSuccessListener {
                                            Log.d("Firestore", "User document successfully written!")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("Firestore", "Error writing document", e)
                                        }
                                }

                                navController.popBackStack()
                                navController.navigate(Screen.SuccessLogin.route)
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error checking document existence", e)
                            }
                    }
                    Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signOut(navController: NavController) {
        val db = Firebase.firestore
        val authManager = FirebaseAuthenticationManager()
        val currentUser = authManager.getCurrentUser()
        val uid = currentUser?.uid
        val docRef = uid?.let { db.collection("users").document(it) }

        // get the google account
        val googleSignInClient: GoogleSignInClient

        // configure Google SignIn
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Sign Out of all accounts
        mAuth.signOut()
        googleSignInClient.signOut().addOnSuccessListener {
            navController.navigate(Screen.Login.route)
        }.addOnFailureListener {
            Toast.makeText(this, "로그아웃 실패", Toast.LENGTH_SHORT).show()
        }
    }
}