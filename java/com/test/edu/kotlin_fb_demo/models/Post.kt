package com.test.edu.kotlin_fb_demo.models

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import java.util.HashMap

@IgnoreExtraProperties
class Post
{
    // uid: 작성자의 고유 아이디(익명아이디:해시값)
    // author: 작성자 이름
    // title: 글 제목
    // body : 글 내용
    // ------------------
    // likeCount: 종아요 개수
    // likes : Map<String, Boolean> 누가 좋아요 했는지 기록
    lateinit var uid:String
    lateinit var author:String
    lateinit var title:String
    lateinit var body:String
    var likeCount = 0
    var likes:Map<String, Boolean> = HashMap()

    constructor(){}
    constructor(uid: String, author: String, title: String, body: String) {
        this.uid = uid
        this.author = author
        this.title = title
        this.body = body
    }
    // 여러 게시판에(디비상으로 가지(전체글, 내글))에 동시에 데이터를 넣기 위한 방법
    @Exclude
    fun toMap():Map<String, Any>{
        return hashMapOf("uid" to uid,
                "author" to author,
                "title" to title,
                "body" to body,
                "likeCount" to likeCount,
                "likes" to likes
                )
    }
}