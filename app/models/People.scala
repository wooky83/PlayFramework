package models

import javax.inject.{Inject, Singleton}
import java.sql.Date
import java.util.Calendar
import play.api.libs.json._
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PeopleDataAccess @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit  ec: ExecutionContext) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class PeopleTable(tag: Tag) extends Table[Person](tag, "people") {

    def userSeq = column[Int]("userSeq", O.PrimaryKey, O.AutoInc)
    def userId = column[String]("userId")
    def password = column[String]("password")
    def nickname = column[String]("nickname")
    def score = column[Int]("score", O.Default(0))
    def level = column[Int]("level", O.Default(0))
    def regdate = column[Date]("regdate")

    def * = (userSeq, userId, password, nickname, score, level, regdate) <> ((Person.apply _).tupled, Person.unapply)
  }

  private val people = TableQuery[PeopleTable]

  def insert(userId: String, password: String, nickname: String, score: Int, level: Int, regdate: java.sql.Date) : Future[Person] = db.run {

    (people.map(p => (p.userId, p.password, p.nickname, p.score, p.level, p.regdate))
      returning people.map(_.userSeq)
      into ((ggda, userSeq) => Person(userSeq, ggda._1,  ggda._2,  ggda._3,  ggda._4,  ggda._5,  ggda._6))
      ) += (userId, password, nickname, score, level, regdate)
  }

  def list: Future[Seq[Person]] = db.run {
    people.result
  }
}

case class Person(userSeq:Int, userId:String, password:String, nickname:String, score:Int, level:Int, regdate:Date)

object Person {
  implicit val personFormat = Json.format[Person]
}

