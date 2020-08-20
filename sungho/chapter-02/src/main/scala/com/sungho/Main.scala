package com.sungho

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

object Main extends App with RequestTimeout {

  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system = ActorSystem()
  val ec = system.dispatcher

  val api = new RestApi(system, requestTimeout(config)).routes
  val bindingFuture: Future[ServerBinding] = Http().newServerAt(host, port).bind(api)
}

trait RequestTimeout {
  import scala.concurrent.duration.Duration

  def requestTimeout(config: Config): Timeout = {
    val t = config.getString("akka.http.server.request-timeout")
    val d = Duration(t)
    FiniteDuration(d.length, d.unit)
  }
}
