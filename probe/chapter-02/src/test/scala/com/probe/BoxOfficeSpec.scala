package com.probe

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import com.probe.BoxOffice._
import com.probe.TicketSeller.{Add, Ticket, Tickets}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class BoxOfficeSpec extends TestKit(ActorSystem("textBoxOffice"))
  with AnyWordSpecLike
  with Matchers
  with ImplicitSender
  with DefaultTimeout
  with StopSystemAfterAll {
  "The BoxOffice" must {
    "Create an event and get tickets from the correct Ticket Seller" in {
      val boxOffice = system.actorOf(BoxOffice.props)
      val eventName = "RHCP"

      boxOffice ! CreateEvent(eventName, 10)
      expectMsg(EventCreated(Event(eventName, 10)))

      boxOffice ! GetEvents
      expectMsg(Events(Vector(Event(eventName, 10))))

      boxOffice ! GetEvent(eventName)
      expectMsg(Some(Event(eventName, 10)))

      boxOffice ! GetTickets(eventName, 1)
      expectMsg(Tickets(eventName, Vector(Ticket(1))))

      boxOffice ! GetTickets("probe", 1)
      expectMsg(Tickets("probe"))
    }

    "Create a child actor when an event is created and sends it a Tickets message" in {
      val boxOffice = system.actorOf(Props(
        new BoxOffice {
          override def createTicketSeller(name: String): ActorRef = testActor
        }
      ))
      val tickets = 3
      val eventName = "RHCP"
      val expectedTickets = (1 to tickets).map(Ticket).toVector
      boxOffice ! CreateEvent(eventName, tickets)
      expectMsg(Add(expectedTickets))
      expectMsg(EventCreated(Event(eventName, tickets)))
    }

    "Get and cancel an event that is not created yet" in {
      val boxOffice = system.actorOf(BoxOffice.props)
      val noneExitEventName = "noExitEvent"
      boxOffice ! GetEvent(noneExitEventName)
      expectMsg(None)

      boxOffice ! CancelEvent(noneExitEventName)
      expectMsg(None)
    }

    "Cancel a ticket which event is not created" in {
      val boxOffice = system.actorOf(BoxOffice.props)
      val noneExitEventName = "noExitEvent"

      boxOffice ! CancelEvent(noneExitEventName)
      expectMsg(None)
    }

    "Cancel a ticket which event is created" in {
      val boxOffice = system.actorOf(BoxOffice.props)
      val eventName = "RHCP"
      val tickets = 10
      boxOffice ! CreateEvent(eventName, tickets)
      expectMsg(EventCreated(Event(eventName, tickets)))

      boxOffice ! CancelEvent(eventName)
      expectMsg(Some(Event(eventName, tickets)))
    }
  }

}
