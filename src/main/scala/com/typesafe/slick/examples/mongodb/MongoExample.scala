package com.typesafe.slick.examples.mongodb


import com.mongodb.casbah.Imports._
import com.novus.salat._
import com.novus.salat.global._
import scala.language.implicitConversions

import scala.slick.mongodb.direct.MongoBackend.Database
import scala.slick.mongodb.direct.MongoQuery._
import scala.slick.mongodb.direct.{GetResult, MongoQuery => Query}

case class Person(id: Int, name: String)

object MongoExampleClass extends App{
  //implicit conversions - not required for manual GetResult
  implicit def personAsDBObject(person: Person):DBObject = grater[Person].asDBObject(person)
  implicit def dbObjectAsPerson(dbObject: DBObject):Person = grater[Person].asObject(dbObject)
  implicit def mongoDBObjectAsPerson(mongoDBObject: MongoDBObject):Person = dbObjectAsPerson(mongoDBObject.underlying)

//   Result set getter:

//   manual:
//implicit def converter1 = GetResult(r => Person(r.get("id").get.asInstanceOf[Int], r.get("name").get.asInstanceOf[String]))

//   explicit using Salat:
//  implicit def converter2 = GetResult(mongoDBObjectAsPerson)

//   implicit using Salat:
  implicit def converter3 = GetResult[Person]

  Database.forURL("mongodb://localhost:27017/test") withSession { implicit session =>

    println("\nWe can treat MongoQuery as TypedMongoCollection, so all the basic operations are provided:")
    val query:Query[Unit,Person] = Query[Person]("people")
    query.insert(MongoDBObject("id" -> 1,"name" -> "George"))
    query.insert(MongoDBObject("id" -> 2,"name" -> "Peter"))
    query.insert(Person(3,"James"))// this line requires implicit of type  Person=>DBObject

    println("\nFind all with MongoCollection's find() - results are converted from DBObjects automatically")
    query.find().foreach{
      p=>println(p)
    }

    println("\nFindOne as both DBObject and Person type:")
    println(query.findOne())
    println(query.findOneTyped())

    println("\nMongoQuery can be used in a Slick way too (using invoker component):")
    query.list.foreach(println)
    println(query.first)

    println("\nIterate through all people and output them:")
    Query[Person]("people") foreach println

    println("\nIterate through people with id:1 and output them - hardcoded parameters")
    Query.query[Unit,Person]("people","{id:1}") foreach{ c=>
      println(s"Filtered: $c")
    }

    println("\nQuerying with parameters - parameters will be placed instead of ?")
    val queryWithParameters0 = Query.query[Int,Person]("people","{id:?}")
    queryWithParameters0(1) foreach println

    println("\nQuerying with parameters - parameters will be placed instead of ?")
    val queryWithParameters1 = Query.query[String,Person]("people","{name:?}")
    queryWithParameters1("Peter") foreach println

    println("\nQuerying with parameters -this one should fail since ? is inside quotes")
    val queryWithParameters2 = Query.query[String,Person]("people","{name:\"?\"}")
    queryWithParameters2("Peter") foreach println

    println("\nQuerying with parameters - this one fails too")
    val queryWithParameters3 = Query.query[String,Person]("people","{name:'?'}")
    queryWithParameters3("Peter") foreach println

    println("\nQuerying with parameters - we can pass tuple as a parameter as well")
    val queryWithParameters4 = Query.query[(Int,String),Person]("people","{id:?, name:?}")
    queryWithParameters4(2,"Peter") foreach println

    println("\nQuerying with parameters - any Iterable is OK too")
    val queryWithParameters5 = Query.query[List[_],Person]("people","{id:?, name:?}")
    queryWithParameters5(List(2,"Peter")) foreach println

    println("\nQuerying with parameters - any Iterable is OK too - parameters swapped ")
    val queryWithParameters6 = Query.query[List[_],Person]("people","{name:?,id:?}")
    queryWithParameters6(List("Peter",2)) foreach println

    println("\nTest query.list:")
    val q = Query[Person]("people")
    q.list foreach println

    println("\nString interpolation:")
    def findPeople(id: Int,name: String):Query[Unit,Person] = mq"{id:$id,name:$name}".in("people")
    findPeople(1,"George") foreach println
    findPeople(2,"Peter") foreach println

//    println("\nString interpolation:")
//    case class Man(id: Int, name: String)
//    implicit def converterMan = GetResult(r => Man(r.get("id").get.asInstanceOf[Int], r.get("name").get.asInstanceOf[String]))
//    Query[Man]("Man").insert(MongoDBObject("id"->1,"name"->"Indiana"))
//    Query.forType[Man] foreach println

    println("\nCleanup: remove Peter")
    Query[Person]("people").remove(MongoDBObject("name"->"Peter"))
    query.find() foreach println

    println("\nCleanup")
    Query[Person]("people").remove()
    query.find() foreach println
  }
}
