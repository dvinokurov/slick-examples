package com.typesafe.slick.examples.mongodb


import com.mongodb.casbah.Imports._
import scala.slick.mongodb.MongoBackend.Database
import scala.slick.mongodb.{MongoQuery => Query,GetResult}
import com.mongodb.BasicDBObject

case class Person(id: Double, name: String)

object MongoExampleClass extends App{

    // Result set getter
    implicit def converter = GetResult(r => Person(r.get("id").get.asInstanceOf[Double], r.get("name").get.asInstanceOf[String]))
    implicit def DBObjectAsR(dbObject: BasicDBObject):Person = converter(new MongoDBObject(dbObject))

    Database.forURL("mongodb://localhost:27017/test") withSession { implicit session =>

      // Iterate through all people and output them
      Query[Person]("people") foreach println

      Query.query[Unit,Person]("people","{id:1}") foreach{ c=>
        println(s"Filtered: $c")
      }

      val personQuery = Query[Person]("people")
      println(personQuery.findOneTyped())
      personQuery.find().foreach{
        p=>println(p)
      }
    }
}
