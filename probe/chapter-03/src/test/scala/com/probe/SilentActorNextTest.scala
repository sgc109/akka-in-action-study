package com.probe

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SilentActorNextTest extends TestKit(ActorSystem("testsystem"))
  with AnyWordSpecLike
  with Matchers
  with StopSystemAfterAll {
  "A Silent Actor" must {
    "change state when it receives a message, single" in {
      import SilentActor._
      val silentActor = TestActorRef[SilentActor]
      silentActor ! SilentMessage("whisper1")
      silentActor ! SilentMessage("whisper2")
      silentActor ! GetState(testActor)
      expectMsg(Vector("whisper1", "whisper2"))
    }
  }
}

object SilentActor {

  case class SilentMessage(data: String)

  case class GetState(receiver: ActorRef)

}

class SilentActor extends Actor {

  import SilentActor._

  var internalState: Seq[String] = Vector[String]()

  override def receive: Receive = {
    case SilentMessage(data) =>
      internalState = internalState :+ data

    case GetState(receiver) => receiver ! internalState
  }
}

