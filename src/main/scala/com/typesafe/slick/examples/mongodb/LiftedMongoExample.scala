package com.typesafe.slick.examples.mongodb

import com.mongodb.casbah.commons.MongoDBObject

import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.slick.mongodb.direct.MongoBackend.Database
import scala.slick.mongodb.direct.MongoQuery._
import scala.slick.mongodb.direct.{GetResult, MongoQuery => Query}
import scala.slick.mongodb.lifted.Document._
import scala.slick.mongodb.lifted.{Document, DocumentQuery, InnerDocument}


// TODO: remove
case class Person2(id: Int, name: String)

object LiftedMongoExample extends App{

  class People extends Document("people") {
    def id = value[Int]("id")
    def name = value[String]("name")
//    def nums = array[Int]("nums")

    def * = (id,name)

    override def toString = s"Collection: $collectionName"
  }
  val people = DocumentQuery[People]

  class Job extends InnerDocument("job"){
    def position = value[String]("position")
    def employer = value[String]("employer")
  }

  // TODO: remove
  implicit def converter1 = GetResult(r => Person2(r.get("id").get.asInstanceOf[Int], r.get("name").get.asInstanceOf[String]))

  Database.forURL("mongodb://localhost:27017/test") withSession { implicit session =>

    //TODO: replace with lifted insertion:
    println("\nWe can treat MongoQuery as TypedMongoCollection, so all the basic operations are provided:")
    val query = Query[Person2]("people")
    query.insert(MongoDBObject("id" -> 1,"name" -> "George"))
    query.insert(MongoDBObject("id" -> 2,"name" -> "Peter"))

    people.sayHi

    println(people.document)

    people foreach println

    people.remove()



  }
}
