package com.sungho

class ChildActor(name: String) extends LifecycleAwareActor(name) {
  override def receive: Receive = {
    case msg => throw new Exception
  }
}
