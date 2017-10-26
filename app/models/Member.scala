package models

import java.util.{Calendar, Date}
import javax.inject.{Inject, Singleton}

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.lifted.Tag
import slick.driver.MySQLDriver.api._
import scala.concurrent.{ Future, ExecutionContext }

case class Member (mid:Int, userId:String, password:String, nickname:String, email:String, regdate:Option[Date])

object Member {

  def getList = {
    members
  }

  var date = Option.apply(Calendar.getInstance.getTime)
  var members = Set(
    Member(1, "admin", "12345", "admin", "admin@google.com", date),
    Member(2, "Kim Gyu Bum", "1493", "Tiger2k", "Tiger2k@naver.com", date),
    Member(3, "Kim Gyu Tae", "1245", "gyutae", "int@int.com", date),
    Member(4, "gosoo", "345", "hasoo1007", "ex@exo.com", date),
  )
}

