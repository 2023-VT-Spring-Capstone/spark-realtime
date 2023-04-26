package com.capstone.realtime.bean

case class PostInfo (
                     var author: String,
                     var author_flair_text: String,
                     var created_time: String,
                     var id: String,
                     var is_original: String,
                     var is_self: String,
                     var permalink: String,
                     var title: String,
                     var body: String,
                     var score: Long,
                     var upvote_ratio: Float,
                     var num_comments: Long,
                     var url: String,
                     var subreddit: String,
                     var ticker: String,
                     var label: String,
                     var bullish: Float,
                     var neutral: Float,
                     var bearish: Float,
                     var sentiment: String,

                     var date: String
                   ){
  def this() {
    this(null, null, null, null, null, null, null, null, null, 0L, 0F, 0L, null, null, null, null, 0F, 0F, 0F, null, null)
  }
}
