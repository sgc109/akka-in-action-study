package com.probe

import akka.actor.{Actor, ActorRef, Props}
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

  def createTicketSeller(name: String): ActorRef = context.actorOf(TicketSeller.props(name), name)

  override def receive: Receive = {
    case CreateEvent(name, tickets) =>
      def create(): Unit = {
        val ticketSeller = createTicketSeller(name)
        val newTickets = (1 to tickets).map { ticketId => TicketSeller.Ticket(ticketId) }.toVector
        ticketSeller ! TicketSeller.Add(newTickets)
        sender() ! EventCreated(Event(name, tickets))
      }

      context.child(name).fold(create())(_ => sender() ! EventExists)
  }
}
