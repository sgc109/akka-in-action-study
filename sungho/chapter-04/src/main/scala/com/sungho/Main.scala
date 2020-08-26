package com.sungho

import akka.actor.{ActorSystem, PoisonPill, Props}

object Main extends App {
  val system = ActorSystem("actorSystem")
  val supervisor = system.actorOf(Props(new Supervisor("supervisor")))

}
