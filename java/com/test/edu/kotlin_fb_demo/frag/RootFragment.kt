package com.test.edu.kotlin_fb_demo.frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

import com.test.edu.kotlin_fb_demo.R
import com.test.edu.kotlin_fb_demo.models.Post
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
                // 셀 하나 하나를 세팅해 달라
                holder.bindToPost(model, View.OnClickListener {

                })
            }
        }
        // 아답터를 리사이클러뷰에 연결
        post_list.adapter = mAdapter
        // 파이프사이클에 맞춰서 리스닝처리(글이 새로 추가되거나 삭제되었을때, 새로 읽을때 등등 자동반응)


    }

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
