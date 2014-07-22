package com.typesafe.slick.examples.mongodb

import com.mongodb.casbah.commons.MongoDBObject

import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.slick.mongodb.direct.MongoBackend.Database
import scala.slick.mongodb.lifted.Document._
import scala.slick.mongodb.lifted.{Document, DocumentQuery, InnerDocument}


object LiftedMongoExample extends App{

  class People extends Document("people") {
    def id = value[Int]("id")
    def name = value[String]("name")
//    def nums = array[Int]("nums")

    def * = (id,name)

    // TODO: remove - just for testing and demonstration purposes
    override def toString = s"Collection: $collectionName"
  }
  val people = DocumentQuery[People]

  class Job extends InnerDocument("job"){
    def position = value[String]("position")
    def employer = value[String]("employer")
  }


  Database.forURL("mongodb://localhost:27017/test") withSession { implicit session =>

    people.insert(MongoDBObject("id" -> 1,"name" -> "George"))
    people.insert(MongoDBObject("id" -> 2,"name" -> "Peter"))

    people.sayHi()

    println(people.document)

    people foreach println

    people.remove()



  }
}
