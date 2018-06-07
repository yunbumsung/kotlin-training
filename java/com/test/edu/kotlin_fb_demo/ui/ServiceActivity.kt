package com.test.edu.kotlin_fb_demo.ui

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.test.edu.kotlin_fb_demo.R
import kotlinx.android.synthetic.main.activity_service.*

class ServiceActivity : RootActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    // 로그아웃 기능, 글쓰기 기능 넣기
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_newpost -> newPost()
            R.id.menu_signout -> signOut()
        }
        return super.onOptionsItemSelected(item)
    }

    // 메뉴 세팅
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.side, menu)
        return true // super.onCreateOptionsMenu(menu)
    }

    fun newPost() {
        // 새 글 쓰기 화면 이동
    }

    override fun signOut() {
        // 로그아웃
        super.signOut()
        finish()
    }
}
