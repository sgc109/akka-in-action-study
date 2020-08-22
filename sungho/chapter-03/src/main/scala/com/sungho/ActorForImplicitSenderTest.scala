package com.sungho

import akka.actor.{Actor, Props}

object ActorForImplicitSenderTest {
  def props = Props(new ActorForImplicitSenderTest)

  case class ChangeNameMsg(name: String)
}

class ActorForImplicitSenderTest extends Actor {
  import ActorForImplicitSenderTest._

  var name = "default name"

  override def receive: Receive = {
    case ChangeNameMsg(name) =>
      this.name = name
      sender() ! name
  }
}
