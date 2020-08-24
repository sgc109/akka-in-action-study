package com.probe

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.{ExecutionContext, Future}

class RestApi(system: ActorSystem, timeout: Timeout) extends RestRoutes {
  override implicit def executionContext: ExecutionContext = system.dispatcher

  override implicit def requestTimeout: Timeout = timeout

  override def createBoxOffice(): ActorRef = system.actorOf(BoxOffice.props, BoxOffice.name)
}

trait RestRoutes extends BoxOfficeApi with EventMarshalling {

  import akka.http.scaladsl.model.StatusCodes._

  def routes: Route = eventsRoute ~ eventRoute ~ ticketsRoute

  def eventsRoute: Route =
    pathPrefix("events") {
      pathEndOrSingleSlash {
        get {
          onSuccess(getEvents) { events =>
            complete(OK, events)
          }
        }
      }
    }

  def eventRoute: Route =
    pathPrefix("events" / Segment) { event =>
      pathEndOrSingleSlash {
        post {
          entity(as[EventDescription]) { ed =>
            onSuccess(createEvent(event, ed.tickets)) {
              case BoxOffice.EventCreated(event) => complete(Created, event)
              case BoxOffice.EventExists =>
                complete(BadRequest, Error(s"$event event already exists."))
            }
          }
        } ~
          get {
            onSuccess(getEvent(event)) {
              _.fold(complete(NotFound))(e => complete(OK, e))
            }
          } ~
          delete {
            onSuccess(cancelEvent(event)) {
              _.fold(complete(NotFound))(e => complete(OK, e))
            }
          }
      }
    }

  def ticketsRoute: Route =
    pathPrefix("events" / Segment / "tickets") { event =>
      post {
        pathEndOrSingleSlash {
          entity(as[TicketRequest]) { request =>
            onSuccess(requestTickets(event, request.tickets)) { tickets =>
              if (tickets.entries.isEmpty) {
                complete(NotFound)
              } else {
                complete(Created, tickets)
              }
            }
          }
        }
      }
    }
}

trait BoxOfficeApi {

  import BoxOffice._

  implicit def executionContext: ExecutionContext

  implicit def requestTimeout: Timeout

  def createBoxOffice(): ActorRef

  private lazy val boxOffice = createBoxOffice()

  def getEvents: Future[Events] =
    boxOffice.ask(GetEvents).mapTo[Events]

  def createEvent(event: String, numberOfTickets: Int): Future[EventResponse] =
    boxOffice.ask(CreateEvent(event, numberOfTickets))
      .mapTo[EventResponse]

  def getEvent(event: String): Future[Option[Event]] =
    boxOffice.ask(GetEvent(event)).mapTo[Option[Event]]

  def cancelEvent(event: String): Future[Option[Event]] =
    boxOffice.ask(CancelEvent(event)).mapTo[Option[Event]]

  def requestTickets(event: String, tickets: Int): Future[TicketSeller.Tickets] =
    boxOffice.ask(GetTickets(event, tickets)).mapTo[TicketSeller.Tickets]
}

