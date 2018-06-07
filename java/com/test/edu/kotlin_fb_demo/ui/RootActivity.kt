package com.test.edu.kotlin_fb_demo.ui

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

open class RootActivity : AppCompatActivity() {
    // 통신 중에 비동기적으로 작업이 진행되는 동안 로딩 처리
    private var mProgressDialog:ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    // 로딩
    // 특정 문구를 받아서 로딩 메시지 처리
    // 아무것도 넣지 않으면 "..로딩.." 기본 문구로 처리
    fun showProgressDialog(msg:String = "...로딩...") {
        // mProgressDialog 객체가 널이면 생성 ProgressDialog(this) 아니면 그냥 사용
        mProgressDialog = mProgressDialog ?: ProgressDialog(this)
        // 백키를 누르면 창이 닫히는 것 거부
        //p.setCancelable(false)
        // 메시지 설정
        mProgressDialog!!.setMessage(msg)
        // 화면에 보인다
        mProgressDialog!!.show()
    }
    // 로딩 닫기
    fun hideProgressDialog() {
        // mProgressDialog 객체가 존재하고, mProgressDialog가 화면에 보이고 -> 닫아라
        val flag = mProgressDialog?.isShowing ?: false
        if (flag) mProgressDialog?.dismiss()
    }
    // 로그아웃
    open fun signOut() {
        // 파이어베이스 세션 로그아웃
        FirebaseAuth.getInstance().signOut()
        // 토스트 처리
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
        // 로그인으로 이동
        startActivity(Intent(this, SignInActivity::class.java))

    }

    // 세션값 획득
    // 백키처리
    override fun onBackPressed() {
        // 다이얼로그가 떠있다 백키 누르면 창이 닫힘,
        // 안떠있다. 백키 누르면 앱이 종료
        val flag = mProgressDialog?.isShowing ?: false
        if (flag) {
            hideProgressDialog()
        } else {
            super.onBackPressed()
        }
    }
}
