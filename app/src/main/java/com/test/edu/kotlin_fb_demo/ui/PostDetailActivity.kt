package com.test.edu.kotlin_fb_demo.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.test.edu.kotlin_fb_demo.R
import com.test.edu.kotlin_fb_demo.models.Comment
import com.test.edu.kotlin_fb_demo.models.Post
import com.test.edu.kotlin_fb_demo.models.User
import com.test.edu.kotlin_fb_demo.viewholder.CommentViewHdoler
import com.test.edu.kotlin_fb_demo.viewholder.PostViewHolder
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.content_new_post.*
import kotlinx.android.synthetic.main.item_author.*
import kotlinx.android.synthetic.main.item_text.*

class PostDetailActivity : RootActivity() {
    private var mPostRef:DatabaseReference?=null
    private var mCommentRef:DatabaseReference?=null
    private var mPostListener:ValueEventListener?=null
    private var mAdapter:FirebaseRecyclerAdapter<Comment, CommentViewHdoler>?=null

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

        // 댓글 리스트 뿌리기
        // 퀴리
        var query = mCommentRef!!.limitToFirst(100)
        // 퀴리를 기반으로 옵션
        var options = FirebaseRecyclerOptions.Builder<Comment>().setQuery(query, Comment::class.java).build()
        // 아답터
        // 뷰홀더 CommentViewHolder

        // 댓글 하나를 담는 그릇 ok
        // xml
        mAdapter = object : FirebaseRecyclerAdapter<Comment, CommentViewHdoler>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHdoler {
                return CommentViewHdoler( layoutInflater.inflate(R.layout.item_comment,parent,false) )
            }

            override fun onBindViewHolder(holder: CommentViewHdoler, position: Int, model: Comment) {
                holder.bindToComment(model)

            }
        }
        // 리사이클러뷰에 매니저 등록, 아답터 연결
        comment_list.adapter = mAdapter
        comment_list.layoutManager = LinearLayoutManager(this)
        // 아답터의 리스닝 등록, 해제
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
        mAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        // ValueEventListener 해제한다
        if (mPostListener != null)
            mPostRef?.removeEventListener(mPostListener!!)
        mAdapter?.stopListening()
    }

    fun onCommentSend(view: View){
        // 댓글입력
        // 유효성 검사
        if (!validForm(comment_text, "댓글이 없습니다.")) return
        // 로딩
        showProgressDialog("-- 글 업로드 중---")
        // 사용자정보 확인 -> 글정보 준비
        val mDataBase = FirebaseDatabase.getInstance().getReference()
        val userID = userID()
        mDataBase.child("users").child(userID!!).addListenerForSingleValueEvent(
                object: ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        hideProgressDialog()
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val user = p0.getValue<User>(User::class.java)
                        if (user == null) {
                            Toast.makeText(this@PostDetailActivity, "회원이 아닙니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            // 회원임을 검증하였으므로, 글을 업로드 한다.
                            // 경로: /post-comments/글번호/해쉬키/열매(Comment)
                            // 경로 맞춰서 입력
                            val comment = Comment(userID, user.email, comment_text.text.toString())
                            // 댓글 입력
                            mCommentRef!!.push().setValue(comment).addOnCompleteListener {
                                task -> if(task.isSuccessful) Toast.makeText(this@PostDetailActivity, "등록되었습니다.", Toast.LENGTH_SHORT).show()
                            }
                            // 입력창 초기화
                            comment_text.text.clear()
                        }
                        hideProgressDialog()
                    }
                }
        )

    }

    private fun validForm(target:EditText, msg:String):Boolean {
        // 비웠는지 체크 각각 -> 에러메시지 처리 및 초기화
        var result:Boolean = true
        if(TextUtils.isEmpty(target.text.toString())) {
            result = false
            target.setError(msg)
        } else {
            target.setError(null)
        }

        return result
    }
}
