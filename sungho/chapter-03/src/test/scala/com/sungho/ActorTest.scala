package com.sungho

import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must._
import org.scalatest.wordspec.AnyWordSpecLike

class ActorTest extends TestKit(ActorSystem("test"))
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {


  "ActorForTestActorRefTest" must {
    "change it's name to the one sent by message" in {
      import com.sungho.ActorForTestActorRefTest._

      val actor = TestActorRef[ActorForTestActorRefTest]
      actor ! ChangeNameMsg("sungho")
      actor.underlyingActor.name must be("sungho")
    }
  }

  "ActorForGetStateMsgTest" must {
    "change it's name to the one sent by message" in {
      import com.sungho.ActorForGetStateMsgTest._

      val actor = system.actorOf(ActorForGetStateMsgTest.props)

      actor ! ChangeNameMsg("sungho")
      actor ! GetState(testActor)
      expectMsg("sungho")
    }
  }

  "ActorForConstructorTest" must {
    "change it's name to the one sent by message" in {
      import com.sungho.ActorForConstructorTest._

      val actor = system.actorOf(ActorForConstructorTest.props(testActor))

      actor ! ChangeNameMsg("sungho")
      actor ! GetState
      expectMsg("sungho")
    }
  }

  override def afterAll(): Unit = {
    super.afterAll()
    system.terminate()
  }
}


