package com.probe

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout

import scala.concurrent.Future

object BoxOffice {
  def props(implicit timeout: Timeout) = Props(new BoxOffice)

  def name = "boxOffice"

  case class CancelEvent(name: String)

  case class GetTickets(name: String, tickets: Int)

  case class GetEvent(name: String)

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

    case GetEvent(event) =>
      def getEvent = (child: ActorRef) => child forward TicketSeller.GetEvent

      def notFound = () => sender() ! None

      context.child(event).fold(notFound())(getEvent)

    case GetEvents =>
      import akka.pattern.{ask, pipe}

      def getEvents = context.children.map { child =>
        self.ask(GetEvent(child.path.name)).mapTo[Option[Event]]
      }

      def convertToEvents(future: Future[Iterable[Option[Event]]]) =
        future.map(_.flatten).map(events => Events(events.toVector))

      pipe(convertToEvents(Future.sequence(getEvents))) to sender()

    case GetTickets(event, tickets) =>
      def notFound(): Unit = {
        sender() ! TicketSeller.Tickets(event)
      }

      def buy = {
        (child: ActorRef) => child.forward(TicketSeller.Buy(tickets))
      }

      context.child(event).fold(notFound())(buy)
    case CancelEvent(event) =>
      def notFound(): Unit = {
        sender() ! None
      }

      def cancel = {
        (child: ActorRef) => child forward TicketSeller.Cancel
      }

      context.child(event).fold(notFound())(cancel)
  }
}
