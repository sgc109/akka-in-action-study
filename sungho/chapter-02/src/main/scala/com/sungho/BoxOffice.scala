package com.sungho

import akka.actor.{Actor, Props}
import com.sungho.BoxOffice.GetEvents

object BoxOffice {
  def props = Props(new BoxOffice)
  def name = "boxOffice"

  case object GetEvent
  case object GetEvents

  case class Event(name:String, tickets: Int)
  case class Events(events: Vector[Event])

}

class BoxOffice extends Actor {
  import BoxOffice._
  import context._

  override def receive: Receive = {
    case GetEvents =>
      import akka.pattern.pipe
      import akka.pattern.ask

      def getEvents = context.children.map { child =>
        self.ask(GetEvent)
      }
      pipe()
  }
}
