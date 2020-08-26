package com.sungho

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{OneForOneStrategy, Props, SupervisorStrategy, Terminated}

class Supervisor(name: String) extends LifecycleAwareActor(name) {
  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _: Exception => Stop
  }

  val child = context.actorOf(Props(new ChildActor("childActor")))

  context.watch(child)

  child ! "msg"

  override def receive: Receive = {
    case Terminated(child) =>
      log.info(s"Actor ${child} is stopped")
  }
}
