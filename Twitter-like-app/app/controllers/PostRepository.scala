package controllers

import scalikejdbc._

object PostRepository {

  def getAllPosts(post_id: String): Seq[Post] = DB readOnly { implicit session =>
    if (post_id != "") {
      sql"SELECT parent_posts.id, users.user_id, parent_posts.text, parent_posts.parent_post_id, parent_posts.posted_at, (SELECT COUNT(*) FROM posts child_posts WHERE child_posts.parent_post_id = parent_posts.id) AS comment_count FROM posts parent_posts join users on parent_posts.user_id = users.user_id where parent_posts.parent_post_id = $post_id order by parent_posts.posted_at desc"
        .map { row =>
          Post(
            row.string("id"),
            row.string("user_id"),
            row.string("text"),
            row.string("parent_post_id"),
            row.int("comment_count"),
            row.string("posted_at")
          )
        }
        .list()
        .apply()
    } else {
      sql"SELECT parent_posts.id, users.user_id, parent_posts.text,parent_posts.parent_post_id,  parent_posts.posted_at, (SELECT COUNT(*) FROM posts child_posts WHERE child_posts.parent_post_id = parent_posts.id) AS comment_count FROM posts parent_posts join users on parent_posts.user_id = users.user_id order by parent_posts.posted_at desc"
        .map { row =>
          Post(
            row.string("id"),
            row.string("user_id"),
            row.string("text"),
            row.string("parent_post_id"),
            row.int("comment_count"),
            row.string("posted_at")
          )
        }
        .list()
        .apply()
    }
  }

  def insertPost(post: PostRequest): Unit = DB localTx { implicit session =>
    val id = java.util.UUID.randomUUID().toString()
    sql"INSERT INTO posts (id, user_id, text, parent_post_id) VALUES ($id, ${post.user_id}, ${post.text}, ${post.post_id})"
      .update()
      .apply()
  }

  def matchUser(user_id: String) = DB readOnly { implicit session =>
    sql"SELECT COUNT(*) AS match FROM users where user_id = $user_id"
      .map(_.int(1))
      .single()
      .apply
      .getOrElse(0)
  }
}
