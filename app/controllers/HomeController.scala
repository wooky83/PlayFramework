package controllers

import javax.inject._

import play.api._
import play.api.mvc._
import models.{Member, PeopleDataAccess, Person}
import play.api.data
import play.api.data.Forms._
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import play.api.i18n._
import play.api.Play.current
import play.api.data.validation.Constraints.{max, min}
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(cc: MessagesControllerComponents, messagesAction: MessagesActionBuilder, md: PeopleDataAccess)(implicit ec: ExecutionContext) extends MessagesAbstractController(cc){

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

  def jsonTest = Action {
    Ok(Json.toJson(Map("first"->1, "second"->2)))
  }

  def form = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    Ok(views.html.form(joinForm))
  }

  def save = Action.async { implicit request =>
    joinForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.form(errorForm)))
      },
      person => {
        md.insert(person.userId, person.password, person.nickname, person.score, person.level, person.regdate).map { _ =>
          Redirect(routes.HomeController.getPersons)
        }
      }
    )
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

  val joinForm = Form {
    mapping(
      "userId" -> nonEmptyText,
      "password" -> nonEmptyText,
      "nickname" -> nonEmptyText,
      "score" -> number.verifying(min(0), max(100)),
      "level" -> number.verifying(min(0), max(100)),
      "regdate" -> sqlDate("yyyy-MM-dd")
    )(CreatePersonForm.apply)(CreatePersonForm.unapply _)
  }

  def getPersons = Action.async { implicit request =>
    md.list.map { people =>
      Ok(Json.toJson(people))
    }
  }
}

case class CreatePersonForm(userId: String, password: String, nickname: String, score: Int, level: Int, regdate: java.sql.Date)
