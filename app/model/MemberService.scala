package model

import javax.inject._
import play.api.db.DBApi
import anorm._
import anorm.SqlParser._
import java.util.Date

@Singleton
class MemberService @Inject() (dbapi: DBApi){
  private val db = dbapi.database("default")

  val basicMember = {
      get[Int]("member.mid") ~
      get[String]("member.userId") ~
      get[String]("member.password") ~
      get[String]("member.nickname") ~
      get[String]("member.email") ~
      get[Option[Date]]("member.regdate") map {
        case mid ~ userId ~ password ~ nickname ~ email ~ regdate =>
          Member(mid, userId, password, nickname, email, regdate)
      }
  }

  def getList = db.withConnection { implicit connection =>
    SQL("SELECT * FROM member").as(basicMember *)

  }
}

