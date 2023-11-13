import android.util.Log
import com.example.getiproject.screen.Post
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

// FirebaseDataManager.kt
class FirebaseDataManager {

    private val db = FirebaseDatabase.getInstance()
    private val postsRef = db.getReference("posts")

    fun getRecentPosts(callback: (List<Post>) -> Unit) {
        // 최근 게시글을 가져오는 로직
        postsRef.orderByChild("timestamp")
            .limitToLast(10)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val posts = snapshot.children.mapNotNull { it.getValue(Post::class.java) }
                    callback(posts)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseDataManager", "Error getting recent posts", error.toException())
                    callback(emptyList())
                }
            })
    }

    fun getPost(postId: String, callback: (Post?) -> Unit) {
        // 특정 게시글을 가져오는 로직
        postsRef.child(postId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val post = snapshot.getValue(Post::class.java)
                    callback(post)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("FirebaseDataManager", "Error getting post $postId", error.toException())
                    callback(null)
                }
            })
    }

    fun createPost(post: Post, callback: () -> Unit) {
        // 새로운 게시글을 생성하는 로직
        val postId = postsRef.push().key
        postId?.let {
            post.postId = it
            postsRef.child(it).setValue(post).addOnCompleteListener { callback() }
        }
    }

    fun updatePost(post: Post, callback: () -> Unit) {
        // Firebase에서 게시글을 업데이트하는 로직
        postsRef.child(post.postId)
            .setValue(post)
            .addOnCompleteListener { callback() }
    }

    fun deletePost(postId: String, callback: () -> Unit) {
        // Firebase에서 게시글을 삭제하는 로직
        postsRef.child(postId)
            .removeValue()
            .addOnCompleteListener { callback() }
    }
}
