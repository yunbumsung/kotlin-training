package com.test.edu.kotlin_fb_demo.frag


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

import com.test.edu.kotlin_fb_demo.R
import kotlinx.android.synthetic.main.fragment_root.*

class RootFragment : Fragment() {
    private val mDataBase: DatabaseReference
    private val TAG:String = "SignInActivity"

    init {
        // 디비의 시작점 경로 : /
        mDataBase = FirebaseDatabase.getInstance().getReference()
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
        val postQuery = getQuery(mDataBase)


    }

    // 차후 추상 클래스가 되어서 서브 클래스들이 가져오고자 하는 데이터는
    // 쿼리 구성을 다양하게 주어서 폼은 같이 쓰고 데이터만 다르게 처리되는 내용 세팅
    fun getQuery (databaseRef:DatabaseReference): Query {
        // 앞에서 부터 100개
        return databaseRef.child("posts").limitToFirst(100)
    }
}
