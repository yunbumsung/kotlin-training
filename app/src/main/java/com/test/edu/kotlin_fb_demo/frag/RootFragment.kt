package com.test.edu.kotlin_fb_demo.frag


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.test.edu.kotlin_fb_demo.R
import com.test.edu.kotlin_fb_demo.models.Post
import com.test.edu.kotlin_fb_demo.ui.PostDetailActivity
import com.test.edu.kotlin_fb_demo.viewholder.PostViewHolder
import kotlinx.android.synthetic.main.fragment_root.*

class RootFragment : Fragment() {
    private val mDatabase: DatabaseReference
    private var mAdapter: FirebaseRecyclerAdapter<Post, PostViewHolder>?=null
    private val TAG:String = "SignInActivity"

    init {
        // 디비의 시작점 경로 : /
        mDatabase = FirebaseDatabase.getInstance().getReference()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_root, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 뷰들을 접근하여 프로세스 진행
        // 리사이클러뷰의 방향성 및 모양
        val mgr:LinearLayoutManager = LinearLayoutManager(context)
        mgr.reverseLayout = true
        mgr.stackFromEnd = true
        post_list.layoutManager = mgr
        post_list.setHasFixedSize(true)

        // 전체 글가져오기(리미트)
        val postQuery = getQuery(mDatabase)

        // 뷰홀더
        // 데이터담는 그릇
        // 리사이클러뷰와 결합한 구조 처리 -> 아답터
        var options = FirebaseRecyclerOptions
                .Builder<Post>()
                .setQuery(postQuery, Post::class.java)
                .build()
        mAdapter = object : FirebaseRecyclerAdapter<Post, PostViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
                // xml -> View를 만드는 작업 : layoutInflater
                val view = LayoutInflater.from(context).inflate(R.layout.cell_post, parent, false)
                return PostViewHolder(view)
            }

            override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
                // 해당글의 고유키를 획득
                val postRef = getRef(position)
                val key = postRef.key

                // 상세보기
                holder.itemView.setOnClickListener{
                    // 상세 페이지 이동 전개 -> 글 고유 번호
                    var intent = Intent(activity, PostDetailActivity::class.java)
                    // 글 고유 번호
                    intent.putExtra(PostDetailActivity.EXTRA_KEY, key)
                    startActivity(intent)
                }
                // 좋아요 표시 처리
                val p = model
                if (p.likes.containsKey(userID())){
                    // 좋아요일때 눌렀는가? -> 풀고
                    holder.star.setImageResource(R.drawable.ic_toggle_star_24)
                } else {
                    // 아닌상태일때 눌렀는가?? -> 체크
                    holder.star.setImageResource(R.drawable.ic_toggle_star_outline_24)
                }
                // 셀 하나 하나를 세팅해 달라
                holder.bindToPost(model, View.OnClickListener {
                    // 좋아요 누르면 해당 유저가 눌렀음을 디비에 반영
                    // 효과 토글
                    // 전체 글에서 해당 글을 찾아서 수정
                    // 내글에서 해당 글을 찾아서 수정
                    val totalPostRef = mDatabase.child("/posts").child(key!!)
                    val myPostRed = mDatabase.child("user-posts").child(model.uid).child(key!!)

                    changeStarState(totalPostRef)
                    changeStarState(myPostRed)

                })
            }
        }
        // 아답터를 리사이클러뷰에 연결
        post_list.adapter = mAdapter
        // 파이프사이클에 맞춰서 리스닝처리(글이 새로 추가되거나 삭제되었을때, 새로 읽을때 등등 자동반응)


    }

    fun changeStarState(ref:DatabaseReference) {
        // 스타를 누르면 트렌젝션을 걸어서 수정
        ref.runTransaction(object : Transaction.Handler{
            override fun onComplete(p0: DatabaseError?, p1: Boolean, p2: DataSnapshot?) {
                Toast.makeText(context, if(p1) "변경완료" else "변경실패", Toast.LENGTH_SHORT).show()
            }

            override fun doTransaction(p0: MutableData): Transaction.Result {
                // 데이터를 획득 -> 못하면 반납 종료
                val p = p0.getValue<Post>(Post::class.java) ?: return Transaction.success(p0)

                if (p.likes.containsKey(userID())){
                    // 좋아요일때 눌렀는가? -> 풀고
                    p.likeCount--
                    p.likes.remove(userID())
                } else {
                    // 아닌상태일때 눌렀는가?? -> 체크
                    p.likeCount++
                    p.likes.put(userID()!!, true)
                }
                // 이렇게 가공 된 데이터를 다시 반납
                p0.value = p

                // 이 결과를 트렌젝션에 적용
                return Transaction.success(p0)
            }
        })
    }

    fun userID() = FirebaseAuth.getInstance().currentUser?.uid

    override fun onStart() {
        super.onStart()
        mAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter?.stopListening()
    }

    // 차후 추상 클래스가 되어서 서브 클래스들이 가져오고자 하는 데이터는
    // 쿼리 구성을 다양하게 주어서 폼은 같이 쓰고 데이터만 다르게 처리되는 내용 세팅
    fun getQuery (databaseRef:DatabaseReference): Query {
        // 앞에서 부터 100개
        return databaseRef.child("posts").limitToFirst(100)
    }
}
