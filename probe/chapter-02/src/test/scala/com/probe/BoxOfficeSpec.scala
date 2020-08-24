package com.probe

import akka.actor.ActorSystem
import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
import com.probe.BoxOffice.{CreateEvent, Event, EventCreated, Events, GetEvents}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class BoxOfficeSpec extends TestKit(ActorSystem("textBoxOffice"))
  with AnyWordSpecLike
  with Matchers
  with ImplicitSender
  with DefaultTimeout
  with StopSystemAfterAll {
  "The BoxOffice" must {
    "Create an event and get tickets from the correct Ticket Seller" in {
      val boxOffice = system.actorOf(BoxOffice.props)
      val eventName = "RHCP"

      boxOffice ! CreateEvent(eventName, 10)
      expectMsg(EventCreated(Event(eventName, 10)))

      boxOffice ! GetEvents
      expectMsg(Events(Vector(Event(eventName, 10))))
    }
  }

}
