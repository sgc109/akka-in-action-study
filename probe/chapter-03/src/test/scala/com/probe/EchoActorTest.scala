package com.probe

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestActors.EchoActor
import akka.testkit.{ImplicitSender, TestKit}
import akka.util.Timeout
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.language.postfixOps
import scala.util.{Failure, Success}

class EchoActorTest extends TestKit(ActorSystem("testsystem"))
  with AnyWordSpecLike
  with ImplicitSender
  with StopSystemAfterAll {

  "An EchoActor" must {
    "Reply with  the same message it receives" in {

      import akka.pattern.ask

      import scala.concurrent.duration._
      implicit val timeout: Timeout = Timeout(3 seconds)
      implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

      val echo = system.actorOf(Props[EchoActor], "echo1")
      val future = echo.ask("some message")
      future.onComplete {
        case Failure(_) =>
        case Success(_) =>
      }

      Await.ready(future, timeout.duration)
    }

    "Reply with the same message it receives without ask" in {
      val echo = system.actorOf(Props[EchoActor], "echo2")
      echo ! "some message"
      expectMsg("some message")
    }
  }

}
