package com.sungho

import akka.actor.{Actor, ActorRef, Props}

object ActorForConstructorTest {
  def props(testActor: ActorRef) = Props(new ActorForConstructorTest(testActor))

  case class ChangeNameMsg(name: String)

  case object GetState

}

class ActorForConstructorTest(testActor: ActorRef) extends Actor {

  import com.sungho.ActorForConstructorTest._

  var name = "default name"

  override def receive: Receive = {
    case ChangeNameMsg(name) =>
      this.name = name
    case GetState =>
      testActor ! this.name
  }
}
