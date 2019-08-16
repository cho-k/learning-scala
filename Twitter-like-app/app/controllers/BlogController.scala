package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import play.api.libs.json._

@Singleton
case class PostRequest(user_id: String, text: String, post_id: String)

class BlogController @Inject()(cc: ControllerComponents)
    extends AbstractController(cc)
    with I18nSupport {

  private[this] val blogForm = Form(
    mapping(
      "user_id" -> nonEmptyText,
      "text" -> nonEmptyText(minLength = 1, maxLength = 100),
      "post_id" -> text
    )(PostRequest.apply)(PostRequest.unapply)
  )

  def get() = Action { implicit request =>
    Ok(views.html.index(PostRepository.getAllPosts("")))
  }

  def create() = Action { implicit request =>
    Ok(views.html.create(blogForm, ""))
  }

  def post() = Action { implicit request =>
    blogForm.bindFromRequest.fold(
      hasErrors => {
        BadRequest("エラーコード 400\nuser_id と text を正しく入力してください。\n" + hasErrors.toString)
      },
      success => {
        if (PostRepository.matchUser(success.user_id) == 0) {
          BadRequest("エラーコード 400\nユーザーが存在しません。存在する user_id を入力してください。")
        } else {
          val post = PostRequest(success.user_id, success.text, success.post_id)
          PostRepository.insertPost(post)
          Ok(Json.toJson(Some(Json.obj("result" -> Json.toJson("OK")))))
          Redirect("/posts")
        }
      }
    )
  }

  def getComments(post_id: String) = Action { implicit request =>
    Ok(views.html.comments(PostRepository.getAllPosts(post_id)))
  }

  def createComment(post_id: String) = Action { implicit request =>
    Ok(views.html.create(blogForm, post_id))
  }

  def getJSON() = Action { implicit request =>
    Ok(
      Json.prettyPrint(
        Json.toJson(Some(Json.obj("posts" -> Json.toJson(PostRepository.getAllPosts("")))))))
  }

  def getCommentsJSON(post_id: String) = Action { implicit request =>
    Ok(
      Json.prettyPrint(
        Json.toJson(Some(Json.obj("posts" -> Json.toJson(PostRepository.getAllPosts(post_id)))))))
  }
}
