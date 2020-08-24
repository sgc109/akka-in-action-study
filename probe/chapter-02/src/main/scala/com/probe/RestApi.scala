package com.probe

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {

}

trait RestRoutes extends BoxOfficeApi {

}

trait BoxOfficeApi {
  import BoxOffice._

  def createBoxOffice() = ActorRef

  private val boxOffice = createBoxOffice()

}

