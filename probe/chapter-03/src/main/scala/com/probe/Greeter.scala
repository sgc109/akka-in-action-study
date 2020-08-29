package com.probe

import akka.actor.{Actor, ActorLogging}

case class Greeting(message: String)

class Greeter extends Actor with ActorLogging {
  override def receive: Receive = {
    case Greeting(message) => log.info("Hello {}!", message)
  }
}
