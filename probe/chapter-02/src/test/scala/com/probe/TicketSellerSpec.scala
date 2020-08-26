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

      val range = 2 to 10
      range.foreach(_ => ticketingActor ! Buy(1))

      val tickets = receiveN(9)
      tickets.zip(range).foreach { case (Tickets(_, Vector(Ticket(id))), ix) => id must be(ix) }

      ticketingActor ! Buy(1)
      expectMsg(Tickets(event))
    }

    "Sell tickets in batches until they are sold out" in {
      import TicketSeller._
      val firstBatchSize = 10

      def mkTickets = {
        (1 to (10 * firstBatchSize)).map(i => Ticket(i)).toVector
      }

      val event = "Madlib"
      val ticketingActor = system.actorOf(TicketSeller.props(event))
      ticketingActor ! Add(mkTickets)
      ticketingActor ! Buy(firstBatchSize)
      val bought = (1 to firstBatchSize).map(Ticket).toVector
      expectMsg(Tickets(event, bought))

      val secondBatchSize = 5
      val numberOfBatches = 18

      val batches = 1 to numberOfBatches * secondBatchSize
      batches.foreach(_ => ticketingActor ! Buy(secondBatchSize))

      val tickets = receiveN(numberOfBatches)
      tickets.zip(batches).foreach {
        case (Tickets(_, bought), ix) =>
          bought.size must equal(secondBatchSize)
          val last = ix * secondBatchSize + firstBatchSize
          val first = ix * secondBatchSize + firstBatchSize - (secondBatchSize - 1)
          bought.map(_.id) must equal((first to last).toVector)
        case _ =>
      }

      ticketingActor ! Buy(1)
      expectMsg(Tickets(event))

      ticketingActor ! Buy(10)
      expectMsg(Tickets(event))
    }
  }

}
