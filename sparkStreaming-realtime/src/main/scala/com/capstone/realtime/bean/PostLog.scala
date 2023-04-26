package com.capstone.realtime.bean

case class PostLog (
                     author: String,
                     author_flair_text: String,
                     created_time: String,
                     id: String,
                     is_original: String,
                     is_self: String,
                     permalink: String,
                     title: String,
                     body: String,
                     score: Long,
                     upvote_ratio: Float,
                     num_comments: Long,
                     url: String,
                     subreddit: String,
                     ticker: String,
                     label: String,
                     bullish: Float,
                     neutral: Float,
                     bearish: Float,
                     sentiment: String
                   ){

}
