package com.probe

import akka.actor.{Actor, Props}
import akka.util.Timeout

object BoxOffice {
  def props(implicit timeout: Timeout) = Props(new BoxOffice)

  def name = "boxOffice"

  case object GetEvents

  case class CreateEvent(name: String, tickets: Int)

  case class Event(name: String, tickets: Int)

  case class Events(events: Vector[Event])

  sealed trait EventResponse

  case class EventCreated(event: Event) extends EventResponse

  case object EventExists extends EventResponse

}

class BoxOffice(implicit timeout: Timeout) extends Actor {
  import BoxOffice._
  import context._
  override def receive: Receive = {
    case CreateEvent(name, tickets) =>
      def create() = {

      }

      context.child(name).fold(create())(_ => sender() ! EventExists)
  }
}
