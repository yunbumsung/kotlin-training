package com.test.edu.kotlin_fb_demo.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.*
import com.test.edu.kotlin_fb_demo.R
import com.test.edu.kotlin_fb_demo.models.Post
import kotlinx.android.synthetic.main.item_author.*
import kotlinx.android.synthetic.main.item_text.*

class PostDetailActivity : AppCompatActivity() {
    private var mPostRef:DatabaseReference?=null
    private var mCommentRef:DatabaseReference?=null
    private var mPostListener:ValueEventListener?=null

    // static 대용
    companion object {
        val EXTRA_KEY = "post_key"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)
        // 전달받은 키를 획득
        val postKey = intent.getStringExtra(EXTRA_KEY)
        if (postKey == null) {
            finish()
            return
        }
        // 키가 존재한다 -> 데이터 획득 -> 해당 포스트글을 획득
        // 본글, 본글에 해당되는 댓글
        mPostRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postKey)
        mCommentRef = FirebaseDatabase.getInstance().getReference().child("post-comments").child(postKey)
    }

    override fun onStart() {
        super.onStart()
        // 본 글을 가져온다.
        // valueEventListener 한번 붙어있으면 변화가 오거나 할경우 계속 이벤트가 살아있어서 오동작
        mPostListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@PostDetailActivity, "데이터 획득 실패", Toast.LENGTH_SHORT);
            }

            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(Post::class.java)
                post_author_name.text = post?.author
                post_title_view.text = post?.title
                post_body_view.text = post?.body
            }
        }
        mPostRef?.addValueEventListener(mPostListener!!)
        // 댓글을 가져온다.
    }

    override fun onStop() {
        super.onStop()
        // ValueEventListener 해제한다
        if (mPostListener != null)
            mPostRef?.removeEventListener(mPostListener!!)

    }

    fun onCommentSend(view: View){
        // 댓글입력

    }
}
