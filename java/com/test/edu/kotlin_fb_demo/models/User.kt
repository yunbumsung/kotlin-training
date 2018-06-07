package com.test.edu.kotlin_fb_demo.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class User {
    lateinit var username:String
    lateinit var email:String

    // FB 실시간 디비에서 모델에 데이터를 넣고 한번에 밀어 넣을때는
    // 기본 생성자가 있어야 한다.
    constructor() {

    }

    constructor(username:String, email:String) {
        this.username = username
        this.email = email
    }
}
