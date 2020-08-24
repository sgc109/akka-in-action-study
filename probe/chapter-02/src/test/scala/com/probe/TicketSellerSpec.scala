package com.probe

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class TicketSellerSpec extends TestKit(ActorSystem("testTickets"))
  with AnyWordSpecLike
  with Matchers
  with ImplicitSender
  with StopSystemAfterAll {
  "The TicketSeller" must {
    "Sell tickets until they are sold out" in {
      import TicketSeller._
      def mkTickets = (1 to 10).map(i => Ticket(i)).toVector

      val event = "RHCP"
      val ticketingActor = system.actorOf(TicketSeller.props(event))
      ticketingActor ! Add(mkTickets)
      ticketingActor ! Buy(1)

      expectMsg(Tickets(event, Vector(Ticket(1))))
    }
  }

}
