import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
//import android.net.Uri
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.DisposableEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavController
//import coil.annotation.ExperimentalCoilApi
//import coil.compose.rememberImagePainter
//import coil.transform.CircleCropTransformation
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.UserProfileChangeRequest
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.SetOptions
//import com.google.firebase.firestore.firestore
//import com.google.firebase.storage.storage
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
//@Composable
//fun UserInfo(
//    navController: NavController,
//    mAuth: FirebaseAuth,
//    currentUser: FirebaseUser?,
//    firestore: FirebaseFirestore
//) {
//    var displayName by remember { mutableStateOf(currentUser?.displayName ?: "") }
//    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
//    var profileImageUri by remember { mutableStateOf(Uri.parse((currentUser?.photoUrl ?: "").toString())) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        // Profile Picture
//        Image(
//            painter = rememberImagePainter(
//                data = profileImageUri,
//                builder = {
//                    // Add a timestamp to the end of the URL to force Coil to reload the image
//                    transformations(CircleCropTransformation())
//                }
//            ),
//            contentDescription = "Profile Picture",
//            modifier = Modifier
//                .size(120.dp)
//                .clip(CircleShape)
//                .background(MaterialTheme.colorScheme.primary)
//        )
//
//        // Display Name
//        OutlinedTextField(
//            value = displayName,
//            onValueChange = { displayName = it },
//            label = { Text("Display Name") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        )
//
//        // Profile Picture Picker
//        ImagePicker(
//            onImageSelected = { uri -> selectedImageUri = uri },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        )
//
//        // Save Button
//        Button(
//            onClick = {
//                // Update display name
//                currentUser?.updateProfile(
//                    UserProfileChangeRequest.Builder()
//                        .setDisplayName(displayName)
//                        .build()
//                )?.addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        // Update profile picture in Firebase Storage
//                        selectedImageUri?.let { uri ->
//                            val storageRef = Firebase.storage.reference.child("Users/${currentUser.uid}.jpg")
//                            storageRef.putFile(uri).addOnCompleteListener { storageTask ->
//                                if (storageTask.isSuccessful) {
//                                    // Get the download URL and update Firestore document
//                                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
//                                        // Add a timestamp to the end of the URL to force Coil to reload the image
//                                        profileImageUri = downloadUri.buildUpon()
//                                            .appendQueryParameter("timestamp", System.currentTimeMillis().toString())
//                                            .build()
//                                        updateFirestoreUser(currentUser.uid, displayName, downloadUri.toString())
//                                    }
//                                } else {
//                                    // Handle failure
//                                }
//                            }
//                        } ?: run {
//                            // Update Firestore document without changing the profile picture
//                            updateFirestoreUser(currentUser.uid, displayName, null)
//                        }
//
//                        // Navigate back
//                        navController.popBackStack()
//                    } else {
//                        // Handle failure
//                    }
//                }
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            Text("Save Changes")
//        }
//    }
//}
//
//private fun updateFirestoreUser(uid: String, displayName: String, photoUrl: String?) {
//    val db = Firebase.firestore
//    val userRef = db.collection("users").document(uid)
//
//    val user = hashMapOf(
//        "displayName" to displayName,
//        "photoUrl" to photoUrl
//        // Add other fields as needed
//    )
//
//    userRef.set(user, SetOptions.merge())
//        .addOnSuccessListener {
//            Log.d("Firestore", "User document successfully updated!")
//        }
//        .addOnFailureListener { e ->
//            Log.e("Firestore", "Error updating document", e)
//        }
//}
//
//@Composable
//fun ImagePicker(
//    onImageSelected: (Uri) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    // Create an activity result launcher for picking images
//    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
//        uri?.let { onImageSelected(it) }
//    }
//
//    // Button to launch the image picker
//    Button(
//        onClick = {
//            launcher.launch("image/*")
//        },
//        modifier = modifier
//    ) {
//        Text("Pick Image from Gallery")
//    }
//}
//

//composable(Screen.UserInfo.route) {
//    UserInfo(
//        navController = navController,
//        mAuth = mAuth,
//        currentUser = mAuth.currentUser,
//        firestore = Firebase.firestore
//    )
//}

// 구글 사용자 프로필 이름 바꾸기
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun UserInfo(navController: NavController) {
//    // Firebase Authentication 인스턴스 가져오기
//    val auth = Firebase.auth
//    // 현재 인증된 사용자 가져오기
//    val user = auth.currentUser
//
//    // 사용자 이름을 저장할 상태(State) 정의
//    var userName by remember { mutableStateOf(user?.displayName ?: "") }
//
//    // 사용자 이름 수정 함수 정의
//    fun updateUserName() {
//        user?.updateProfile(
//            UserProfileChangeRequest.Builder()
//                .setDisplayName(userName)
//                .build()
//        )
//    }
//
//    // Composable UI 작성
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // 사용자 이름을 입력하는 TextField
//        TextField(
//            value = userName,
//            onValueChange = { userName = it },
//            label = { Text("사용자 이름") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        )
//
//        // 사용자 이름 수정 버튼
//        Button(
//            onClick = { updateUserName() },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(8.dp)
//        ) {
//            Text("사용자 이름 수정")
//        }
//    }
//}
