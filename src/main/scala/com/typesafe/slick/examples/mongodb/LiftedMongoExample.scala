package com.typesafe.slick.examples.mongodb


import scala.language.implicitConversions
import scala.slick.backend.DatabaseComponent
import scala.slick.mongodb.direct.MongoBackend
import scala.slick.mongodb.lifted.MongoDriver.simple._


object LiftedMongoExample extends App{

  // Definition of the `people` collection
  class People(tag: Tag) extends Table[(Int, String, Long)](tag, "people") {
    def id = column[Int]("id")
    def name = column[String]("name")
    def number = column[Long]("number")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name, number)
  }
  val people = TableQuery[People]

  Database.forURL("mongodb://localhost:27017/test") withSession { implicit session: MongoBackend#SessionDef =>
    cleanup

    people += (1,"John",123L)
    people ++= List(
      (2,"A",234L),
      (3,"B",345L),
      (4,"C",456L)
    )

    print("0. Simple foreach:")
    people foreach { case (id, name, number) =>
      println(" foreach:  " + id + "\t" + name)
    }

    print("1. Select arbitrary columns:")
    val q1 = for{
      p <- people
    } yield (p.number,p.name)
    q1 foreach println

    print("2. Select single column:")
    val q2 = for{
      p <- people
    } yield p.id
    q2 foreach println

    print("3 Query with filter:")
    val q3 = for{
      p <- people if p.id>2
    } yield p.name
    q3 foreach println

    print("4 Query with conjunction:")
    val q4 = for{
      p <- people if p.id<4 && p.name>"A"
    } yield (p.id, p.name, p.number)
    q4 foreach println

    print("5 Query with conjunction of the same column:")
    val q5 = for{
      p <- people if p.id>2 && p.id<4
    } yield (p.id, p.name, p.number)
    q5 foreach println

    print("6 Query with equality:")
    val q6 = for{
      p <- people if  p.name==="A"
    } yield (p.id, p.name, p.number)
    q6 foreach println

    print("7 Query with unequality:")
    val q7 = for{
      p <- people if  p.name=!="A"
    } yield (p.id, p.name, p.number)
    q7 foreach println

    print("8 Check if column values belongs to set:")
    val q8 = for{
      p <- people if  p.name inSet Set("A","B")
    } yield (p.id, p.name, p.number)
    q8 foreach println

    print("9 Check if column belongs to the set of single element:")
    val q9 = for{
      p <- people if  p.name inSet Set("A")
    } yield (p.id, p.name, p.number)
    q9 foreach println

    print("10 Disjunction:")
    val q10 = for{
      p <- people if  p.name === "A" || p.id>3
    } yield (p.id, p.name, p.number)
    q10 foreach println

    print("11 Negation of column affiliating to a set:")
    val q11 = for{
      p <- people if  !(p.name inSet Set("A"))
    } yield (p.id, p.name, p.number)
    q11 foreach println

    print("12 Double negation:")
    val q12 = for{
      p <- people if  !(!(p.name inSet Set("A")))
    } yield (p.id, p.name, p.number)
    q12 foreach println

    print("13 Negation of disjunction:")
    val q13 = for{
      p <- people if  !(p.name === "A" || p.id>3)
    } yield (p.id, p.name, p.number)
    q13 foreach println

    print("14 Negation of conjunction:")
    val q14 = for{
      p <- people if  !((p.name inSet Set("A","B")) && p.id<3)
    } yield (p.id, p.name, p.number)
    q14 foreach println

    print("15 Negation of comparison:")
    val q15 = for{
      p <- people if  !(p.id<3)
    } yield (p.id, p.name, p.number)
    q15 foreach println
  }

  def cleanup(implicit session1: DatabaseComponent#SessionDef): Unit = {
    import scala.language.implicitConversions
    import scala.slick.mongodb.direct.{GetResult, MongoBackend, MongoQuery}
    case class Person(id: Int, name: String,number: Long)
    implicit def converter = GetResult(r => Person(r.get("id").get.asInstanceOf[Int], r.get("name").get.asInstanceOf[String],r.get("number").get.asInstanceOf[Long]))
    implicit val session = session1.asInstanceOf[MongoBackend#SessionDef]
    println()
//    MongoQuery[Person]("people").find() foreach println
    MongoQuery[Person]("people").remove()
  }
}
