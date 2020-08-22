package com.sungho

import akka.actor.{Actor, Props}

object ActorForTestActorRefTest {

  def props = Props(new ActorForTestActorRefTest)

  case class ChangeNameMsg(name: String)

}

class ActorForTestActorRefTest extends Actor {

  import ActorForTestActorRefTest._

  var name = "default name"

  override def receive: Receive = {
    case ChangeNameMsg(name) =>
      this.name = name
  }
}
