package com.test.edu.kotlin_fb_demo.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.test.edu.kotlin_fb_demo.R
import com.test.edu.kotlin_fb_demo.models.Post

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var post_author_thumb:ImageView
    var post_author_name:TextView
    var post_title_view:TextView
    var post_body_view:TextView
    var star:ImageView
    var star_count:TextView

    init {
        post_author_thumb = itemView.findViewById(R.id.post_author_thumb)
        post_author_name = itemView.findViewById(R.id.post_author_name)
        post_title_view = itemView.findViewById(R.id.post_title_view)
        post_body_view = itemView.findViewById(R.id.post_body_view)
        star = itemView.findViewById(R.id.star)
        star_count = itemView.findViewById(R.id.star_count)
    }

    // 글이 전달되면 뷰에 세팅
    fun bindToPost(post:Post, starClick:View.OnClickListener) {
            post_author_name.text = post.author
        post_title_view.text = post.title
        post_body_view.text = post.body
        star_count.text = "${post.likeCount}"
    }
}