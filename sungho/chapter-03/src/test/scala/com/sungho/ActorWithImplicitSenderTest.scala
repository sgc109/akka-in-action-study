package com.sungho

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ActorWithImplicitSenderTest extends TestKit(ActorSystem("test"))
  with AnyWordSpecLike
  with Matchers
  with ImplicitSender
  with BeforeAndAfterAll {

  "ActorForImplicitSenderTest" must {
    "change it's name to the one sent by message" in {
      import com.sungho.ActorForImplicitSenderTest._

      val actor = system.actorOf(ActorForImplicitSenderTest.props)
      actor ! ChangeNameMsg("sungho")
      expectMsg("sungho")
    }
  }
}
