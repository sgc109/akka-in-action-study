package com.probe

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.ExecutionContextExecutor

object Main extends App with RequestTimeout {

  private val config: Config = ConfigFactory.load()
  private val host: String = config.getString("http.host")
  private val port: Int = config.getInt("http.port")

  private val system: ActorSystem = ActorSystem()
  private val dispatcher: ExecutionContextExecutor = system.dispatcher

//  new RestApi(system, requestTimeout(config)).routes


}

trait RequestTimeout {
  import scala.concurrent.duration._
  def requestTimeout(config: Config): Timeout = {
    val timeout = config.getString("akka.http.server.request-timeout")
    val duration = Duration(timeout)
    FiniteDuration(duration.length, duration.unit)
  }
}
