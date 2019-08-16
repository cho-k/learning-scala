package controllers

import play.api.libs.json.Json
import play.api.libs.json.Writes

case class Post(id: String,
                user_id: String,
                text: String,
                parent_post_id: String,
                comment_count: Int,
                posted_at: String)

object Post {
  implicit val writes: Writes[Post] = Json.writes[Post]
}
