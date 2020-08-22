package com.sungho

import akka.actor.{Actor, ActorRef, Props}

object ActorForGetStateMsgTest {

  def props = Props(new ActorForGetStateMsgTest)

  case class ChangeNameMsg(name: String)

  case class GetState(testActor: ActorRef)

}

class ActorForGetStateMsgTest extends Actor {

  import ActorForGetStateMsgTest._

  var name = "default name"

  override def receive: Receive = {
    case ChangeNameMsg(name) =>
      this.name = name
    case GetState(testActor) =>
      testActor ! this.name
  }
}
