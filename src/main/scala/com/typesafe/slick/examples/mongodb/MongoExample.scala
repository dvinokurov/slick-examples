package com.typesafe.slick.examples.mongodb


import com.mongodb.casbah.Imports._
import scala.slick.mongodb.MongoBackend.Database
import scala.slick.mongodb.{MongoQuery => Query,GetResult}
import com.novus.salat._
import com.novus.salat.annotations._
import com.novus.salat.global._
/**
 * User: Dmytro Vynokurov
 * Date: 21.05.14
 * Time: 21:33
 */

case class Person(id: Double, name: String)

object MongoExampleClass extends App{

  // Casbah example:
  def casbah():Unit = {

    println("\n====================\n===    Casbah:   ===\n====================\n")

    val mongoClient = MongoClient()
    val mongoDB = mongoClient("test")
    val collection = mongoDB("slick_dev")

    for {
      person <- collection.find()
    } {
      println(person)
      val po = grater[Person].asObject(person)
    }


  }

  //Slick example:
  def slick() = {
    println("\n====================\n===    Slick:    ===\n====================\n")

    // Result set getter
    implicit val rconv = GetResult(r => Person(r.get("id").get.asInstanceOf[Double], r.get("name").get.asInstanceOf[String]))

    Database.forURL("mongodb://localhost:27017/test") withSession { implicit session =>
      // Iterate through all people and output them

      Query[Person]("slick_dev") foreach{ c=>
        println(s"${c.id} - ${c.name}")
      }

      Query[Person].query("slick_dev","{id:1}") foreach{ c=>
        println(s"${c.id} - ${c.name}")
      }

      val personQuery = Query[Person]("slick_dev")
      println(personQuery.findOne())
      personQuery.find().foreach(p=>println(p))



    }
  }


//  casbah()
  slick()

}
