package com.sungho

import akka.actor.{Actor, Props}

object BoxOffice {
  def props = Props(new BoxOffice)
  def name = "boxOffice"

  case object GetEvents

  case class Event(name:String, tickets: Int)
  case class Events(events: Vector[Event])

}

class BoxOffice extends Actor {
//  import BoxOffice._
//  import context._

  override def receive: Receive = ???
}
