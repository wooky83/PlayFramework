package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import models.{Member, MemberService}
import play.api.data
import play.api.data.Forms._
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import play.api.i18n._
import play.api.Play.current

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, ms: MemberService, messagesAction: MessagesActionBuilder) extends AbstractController(cc){

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Hello World!!!?"))
  }

  def list = Action {
    Ok(views.html.list(Member.getList))
  }

  def listDB = Action {
    Ok(views.html.listDB(ms.getList))
  }

  def form = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.form(joinForm))
  }

  def save = Action { implicit request =>
    val form = joinForm.bindFromRequest()
    println("whywhyLookHere~!!")
    form.fold(
      hasErrors => {
        println(hasErrors)
        Redirect(routes.HomeController.form)
      },
      member => {
        ms.insert(member)
        Redirect(routes.HomeController.listDB)
      })
  }

  def uploadFile = Action(parse.multipartFormData) { request =>
    request.body.file("picture").map { picture =>
      import java.io.File
      val filename = picture.filename
      println(filename)
      picture.ref.moveTo(new File(s"/tmp/picture/$filename"))
      Ok("파일업로드 완료")
    }.getOrElse {
      Redirect(routes.HomeController.index).flashing("error" -> "파일이 없습니다")
    }

  }

  val joinForm = Form(
    mapping(
      "mid" -> ignored(0),
      "userId" -> nonEmptyText,
      "password" -> nonEmptyText,
      "nickname" -> nonEmptyText,
      "email" -> nonEmptyText,
      "regdate" -> optional(date("yyyy-MM-dd")))(Member.apply)(Member.unapply _))
}
