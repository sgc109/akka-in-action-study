package com.probe

import akka.actor.{ActorSystem, Props}
import akka.testkit.{CallingThreadDispatcher, EventFilter, TestKit}
import com.typesafe.config.{Config, ConfigFactory}
import org.scalatest.wordspec.AnyWordSpecLike
import Greeter01Test._

class Greeter01Test extends TestKit(testSystem)
  with AnyWordSpecLike
  with StopSystemAfterAll {

  "The Greeter" must {
    "say Hello World! when a Greeting(\"World\") is sent to it" in {
      val dispatcherId = CallingThreadDispatcher.Id
      val props = Props[Greeter].withDispatcher(dispatcherId)
      val greeter = system.actorOf(props)
      EventFilter.info(message = "Hello World!", occurrences = 1)
        .intercept {
          greeter ! Greeting("World")
        }
    }
  }

}

object Greeter01Test {
  val testSystem: ActorSystem = {
    val config: Config = ConfigFactory.parseString(
      """
         akka.loggers = [akka.testkit.TestEventListener]
      """
    )
    ActorSystem("testsystem", config)
  }
}
