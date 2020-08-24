package com.probe

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

object Main extends App with RequestTimeout {

  val config: Config = ConfigFactory.load()
  val host: String = config.getString("http.host")
  val port: Int = config.getInt("http.port")

  implicit val system: ActorSystem = ActorSystem()
  implicit val dispatcher: ExecutionContextExecutor = system.dispatcher

  val api = new RestApi(system, requestTimeout(config)).routes

  private val eventualBinding: Future[Http.ServerBinding] = Http().newServerAt(host, port).bind(api)

  val log = Logging(system.eventStream, "go-ticks")

  eventualBinding.map { serverBinding =>
    log.info(s"RestApi bound to ${serverBinding.localAddress}")
  }.onComplete {
    case Success(_) =>
    case Failure(exception) =>
      log.error(exception, "failed to bind to {}:{}!", host, port)
      system.terminate()
  }
}

trait RequestTimeout {

  import scala.concurrent.duration._

  def requestTimeout(config: Config): Timeout = {
    val timeout = config.getString("akka.http.server.request-timeout")
    val duration = Duration(timeout)
    FiniteDuration(duration.length, duration.unit)
  }
}
