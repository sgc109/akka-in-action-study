package com.probe

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.util.Random

class SendingActorTest extends TestKit(ActorSystem("testsystem"))
  with AnyWordSpecLike
  with Matchers
  with StopSystemAfterAll {
  "A Sending Actor" must {
    "send a message to another actor when it has finished processing" in {
      import SendingActor._
      val props = SendingActor.props(testActor)
      val sendingActor = system.actorOf(props, "sendingActor")

      val size = 1000
      val maxInclusive = 100000

      def randomEvents = (0 until size).map { _ => Event(Random.nextInt(maxInclusive)) }.toVector

      val unsorted = randomEvents
      val sortEvents = SortEvents(unsorted)
      sendingActor ! sortEvents

      expectMsgPF() {
        case SortedEvents(events) =>
          events.size must be(size)
          unsorted.sortBy(_.id) must be(events)
      }
    }
  }

}

object SendingActor {
  def props(receiver: ActorRef): Props = Props(new SendingActor(receiver))

  case class Event(id: Long)

  case class SortEvents(unsorted: Vector[Event])

  case class SortedEvents(sorted: Vector[Event])

}

class SendingActor(receiver: ActorRef) extends Actor {

  import SendingActor._

  override def receive: Receive = {
    case SortEvents(unsorted) =>
      receiver ! SortedEvents(unsorted.sortBy(_.id))
  }
}
