package com.probe

import akka.actor.{Actor, Props}

object TicketSeller {
  def props(event: String) = Props(new TicketSeller(event))

  case class Ticket(id: Int)
  case class Add(tickets: Vector[Ticket])
  case class Buy(tickets: Int)
  case class Tickets(event: String, entries: Vector[Ticket] = Vector.empty[Ticket])
}

class TicketSeller(event: String) extends Actor {
  override def receive: Receive = ???
}
