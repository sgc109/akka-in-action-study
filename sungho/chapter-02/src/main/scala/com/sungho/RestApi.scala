package com.sungho

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {

  override implicit def requestTimeout: Timeout = timeout

  override def createBoxOffice() = system.actorOf(BoxOffice.props, BoxOffice.name)
}

trait RestRoutes extends BoxOfficeApi with EventMarshalling {
  import akka.http.scaladsl.model.StatusCodes._

  def routes: Route = eventsRoute

  def eventsRoute =
    pathPrefix("events") {
      pathEndOrSingleSlash {
        get {
          onSuccess(getEvents()) { events =>
            complete(OK, events)
          }
        }
      }
    }
}

trait BoxOfficeApi {

  import BoxOffice._

  def createBoxOffice(): ActorRef

  implicit def requestTimeout: Timeout

  lazy val boxOffice = createBoxOffice()

  def getEvents() =
    boxOffice.ask(GetEvents).mapTo[Events]
}
