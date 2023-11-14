import android.util.Log
import com.example.getiproject.screen.Post
//import com.example.getiproject.screen.Post
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// FirebaseDataManager.kt
class FirebaseDataManager {

    val db = FirebaseDatabase.getInstance("https://geti-project-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val postsRef = db.getReference("posts")

    fun getPost(postId: String): Task<DataSnapshot> {
        // Assuming postsRef is your DatabaseReference to the "posts" node
        return postsRef.child(postId).get()
    }

    fun createPost(post: Post) {
        // 새로운 게시글을 생성하는 로직
        val postId = postsRef.push().key
//        newPosttRef.setValue(post)
        postId?.let {
            post.postId = it
            postsRef.child(it).setValue(post).addOnCompleteListener {}
        }
    }

    fun updatePost(post: Post) {
        // Firebase에서 게시글을 업데이트하는 로직
        postsRef.child(post.postId)
            .setValue(post)
    }

    fun deletePost(postId: String) {
        // Firebase에서 게시글을 삭제하는 로직
        postsRef.child(postId)
            .removeValue()
    }
}
