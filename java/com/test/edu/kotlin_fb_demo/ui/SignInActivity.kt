package com.test.edu.kotlin_fb_demo.ui

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.test.edu.kotlin_fb_demo.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo
import com.miguelbcr.ui.rx_paparazzo2.entities.size.SmallSize
import com.test.edu.kotlin_fb_demo.models.User
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.io.File


class SignInActivity : RootActivity(), View.OnClickListener {
    private val mAuth: FirebaseAuth
    private val mDataBase: DatabaseReference
    private val TAG:String = "SignInActivity"

    init {
        // 인증에 필요한 모든 작업
        mAuth = FirebaseAuth.getInstance()
        // 디비의 시작점 경로 : /
        mDataBase = FirebaseDatabase.getInstance().getReference()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        // 버튼 이벤트
        btn_sign_in.setOnClickListener(this)
        btn_sign_up.setOnClickListener(this)

        // 사진을 클릭하면 호출
        profile.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        // 인증이 살아 있다면 -> 세션이 살아 있다면 -> 로그아웃하지 않았다면
        if (mAuth.currentUser !== null) {
            onAuthSuccess(mAuth.currentUser!!)
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onClick(v: View?) {
        // btn_sign_in 누르면 sign_in() 호출
        // btn_sign_up 누르면 sign_up() 호출
        val id = v?.id ?: -1
        when(id) {
            R.id.btn_sign_in -> sign_in()
            R.id.btn_sign_up -> sign_up()
            R.id.profile -> getPicture()
        }
    }

    fun sign_in () {
        // 로그인
        // 입력폼 검사 OK
        if (!validForm()) return
        // 로딩띠우기
        showProgressDialog("..로그인 중..")
        // 이메일, 비번 획득
        val email = field_email.text.toString()
        val password = field_password.text.toString()
        // fb 이메일과 비번으로 로그인 진행
        mAuth.signInWithEmailAndPassword(email, password)
        // 완료가 떨어지면
        // 로딩 닫고
        // 인증성공으로 함수 호출 -> 화면이동
                .addOnCompleteListener(this) { task ->
                    // 로딩 닫기
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // 로그인 성공
                        // 성공하면 -> 로그인
                        Log.i(TAG, "로그인 성공 ${task.result.user}")
                        onAuthSuccess(task.result.user)
                    } else {
                        // 로그인 실패
                        // 실패하면 -> 모라하고
                        Toast.makeText(this@SignInActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun sign_up () {
        // 회원 가입
        // 입력폼 검사 OK
        if (!validForm()) return
        // 로딩띠우기
        showProgressDialog("..회원 가입 중..")
        // 이메일, 비번 획득
        val email = field_email.text.toString()
        val password = field_password.text.toString()
        // fb 이메일과 비번으로 계정 생성하기 진행
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    // 로딩 닫기
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // 가입 성공
                        // 성공하면 -> 로그인
                        Log.i(TAG, "가입 성공 ${task.result.user}")
                        onAuthSuccess(task.result.user)
                    } else {
                        // 가입 실패
                        // 실패하면 -> 모라하고
                        Toast.makeText(this@SignInActivity, "가입 실패", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun validForm():Boolean {
        // 비웠는지 체크 각각 -> 에러메시지 처리 및 초기화
        var result:Boolean = true
        if(TextUtils.isEmpty(field_email.text.toString())) {
            result = false
            field_email.setError("이메일 값이 비었습니다.")
        } else {
            field_email.setError(null)
        }

        if(TextUtils.isEmpty(field_password.text.toString())) {
            result = false
            field_password.setError("비밀번호가 부정확합니다.")
        } else {
            field_password.setError(null)
        }

        return result
    }

    // 파이어베이스 유저 정보를 인자로 받는다
    private fun onAuthSuccess (user: FirebaseUser) {
        // 실시간 데이터베이스 입력 -> 비동기
        var email = user.email!!
        val userName = if(user.email!!.contains("@")){
            email.split("@".toRegex())[0]
        }else{
            email
        } // email에서 @ 앞부분만 취한다.
        updateUserInfo(user.uid, userName, email)

        // 화면 전환 -> 서비스 화면 이동
        startActivity(Intent(this, ServiceActivity::class.java))
        finish()
    }
    // 회원 정보 가입 혹은 업데이트
    private fun updateUserInfo(uid:String, name:String, email:String) {
        val user = User(name, email)
        // /users/해시값(중복되지 않은)/유저(구조)
        // /users/uid(유저의 익명아이디->해시값)/유저정보(구조)
        mDataBase.child("users").child(uid).setValue(user)
                .addOnCompleteListener(this) { task -> Log.i(TAG, "디비 입력 결과 ${task.isSuccessful}") }
    }

    // 카메라 띠우기(포토앨범, 파일경로) -> 크롭 -> 파일로 다운 -> 업로드 -> 다운로드 URL 획득
    fun getPicture() {
        RxPaparazzo.single(this)
                .crop()
                .size(SmallSize()) // 해상도 조절
                .usingCamera()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    response ->
                    // 파일 경로
                    Log.i(TAG, response.data().file.absolutePath)
                    uploadFile(response.data().file)
                }

    }

    fun uploadFile(file: File) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReference() // /
        // 파일 Uri 개체
        val uri = Uri.fromFile(file)
        // 업로드할 파일의 경로
        val uRef = storageRef.child("thumb/${uri.lastPathSegment}")
        uRef.putFile(uri)
                .continueWithTask {
                    task ->
                    // 다운로드 URL 요청
                    uRef.downloadUrl
                }
                .addOnCompleteListener {
                    task ->
                    if (task.isSuccessful) {
                        Log.i(TAG, task.result.toString())
                        Toast.makeText(this@SignInActivity, "업로드되었습니다.", Toast.LENGTH_SHORT)
                    }
                }

    }
}
