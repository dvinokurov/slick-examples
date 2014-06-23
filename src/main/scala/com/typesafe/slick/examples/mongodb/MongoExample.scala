package com.typesafe.slick.examples.mongodb


import com.mongodb.casbah.Imports._
import scala.slick.mongodb.MongoBackend.Database
import scala.slick.mongodb.{MongoQuery => Query,GetResult}
import Query._

case class Person(id: Double, name: String)

object MongoExampleClass extends App{

    // Result set getter
    implicit def converter = GetResult(r => Person(r.get("id").get.asInstanceOf[Int], r.get("name").get.asInstanceOf[String]))

    Database.forURL("mongodb://localhost:27017/test") withSession { implicit session =>

      println("\nWe can treat MongoQuery as TypedMongoCollection, so all the basic operations are provided:")
      val query = Query[Person]("people")
      query.insert(MongoDBObject("id" -> 1,"name" -> "George"))
      query.insert(MongoDBObject("id" -> 2,"name" -> "Peter"))

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

//      In progress - not sure it's possible, but it'd be nice to have such functionality:
//      println("\nMagic with StringContext")
//      def personById(id: Int) = mq("people")"{id:$id}"
//      or:
//      def personById(id: Int) = mq"$people{id:$id}"

      println("\nCleanup: remove Peter")
      Query[Person]("people").remove(MongoDBObject("name"->"Peter"))
      query.find() foreach println

      println("\nCleanup")
      Query[Person]("people").remove()
      query.find() foreach println
    }
}
