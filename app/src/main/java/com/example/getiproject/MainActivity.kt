package com.example.getiproject

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.getiproject.database.FirebaseAuthenticationManager
import com.example.getiproject.screen.Login
import com.example.getiproject.screen.SuccessLogin
import com.example.getiproject.ui.theme.GetiProjectTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    // 파이어베이스 로그인
    private lateinit var auth: FirebaseAuth
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 파이어베이스 로그인
        mAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // default_web_client_id 에러 시 rebuild
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            GetiProjectTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController() // navigation

                    val user: FirebaseUser? = mAuth.currentUser
                    val startDestination = remember {
                        if (user == null) {
                            ScreenRoute.Login.route
                        } else {
                            ScreenRoute.SuccessLogin.route
                        }
                    }
                    val signInIntent = googleSignInClient.signInIntent

                    val launcher =
                        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
                            val data = result.data
                            // result returned from launching the intent from GoogleSignInApi.getSignInIntent()
                            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                            val exception = task.exception
                            if (task.isSuccessful) {
                                try {
                                    // Google SignIn was successful, authenticate with firebase
                                    val account = task.getResult(ApiException::class.java)!!
                                    firebaseAuthWithGoogle(
                                        account.idToken!!,
                                        mAuth,
                                        navController = navController
                                    )
                                } catch (e: Exception) {
                                    // Google SignIn failed
                                    Log.d("SignIn", "로그인 실패")
                                }
                            } else {
                                Log.d("SignIn", exception.toString())
                            }
                        }

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable(ScreenRoute.Login.route) {
                            Login {
                                launcher.launch(signInIntent)
                            }
                        }
                        composable(ScreenRoute.SuccessLogin.route) { SuccessLogin(navController, onSignOutClicked = {signOut(navController)}) }
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
                        navController.popBackStack()
                    navController.navigate(ScreenRoute.SuccessLogin.route)
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
            docRef?.delete()
                ?.addOnSuccessListener {
                    Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener { e ->
                    // 삭제 실패 시 동작
                    Log.e("Firestore", "Error deleting document", e)
                }
            navController.navigate(ScreenRoute.Login.route)
        }.addOnFailureListener {
            Toast.makeText(this, "로그아웃 실패", Toast.LENGTH_SHORT).show()
        }
    }

}

