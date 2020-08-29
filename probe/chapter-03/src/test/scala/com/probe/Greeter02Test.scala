package com.probe

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, UnhandledMessage}
import akka.testkit.TestKit
import org.scalatest.wordspec.AnyWordSpecLike

class Greeter02Test extends TestKit(ActorSystem("testsystem"))
  with AnyWordSpecLike
  with StopSystemAfterAll {

  "The Greeter" must {
    "say Hello World! when a Greeting(\"World\") is sent to it" in {
      val props = Greeter02.props(Some(testActor))
      val greeter = system.actorOf(props, "greeter02-1")
      greeter ! Greeting("World")
      expectMsg("Hello World!")
    }
    "say something else and see what happens" in {
      val props = Greeter02.props(Some(testActor))
      val greeter = system.actorOf(props, "greeter02-2")
      system.eventStream.subscribe(testActor, classOf[UnhandledMessage])
      greeter ! "World"
      expectMsg(UnhandledMessage("World", system.deadLetters, greeter))
    }
  }

}

object Greeter02 {
  def props(listener: Option[ActorRef] = None): Props = Props(new Greeter02(listener))
}

class Greeter02(listener: Option[ActorRef]) extends Actor with ActorLogging {
  override def receive: Receive = {
    case Greeting(who) =>
      val message = "Hello " + who + "!"
      log.info(message)
      listener.foreach(_ ! message)
  }
}