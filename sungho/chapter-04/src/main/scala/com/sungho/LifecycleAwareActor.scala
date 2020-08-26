package com.sungho

import akka.actor.{Actor, ActorLogging}

class LifecycleAwareActor(name: String) extends Actor with ActorLogging {

  override def preStart(): Unit = {
    log.info(s"${name} preStart()")
  }

  override def postStop(): Unit = {
    log.info(s"${name} postStop()")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    log.info(s"${name} preRestart()")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    log.info(s"${name} postRestart()")
    super.postRestart(reason)
  }

  override def receive: Receive = {
    case _ => sender() ! _
  }
}
