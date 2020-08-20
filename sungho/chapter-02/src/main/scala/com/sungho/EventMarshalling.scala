package com.sungho

import spray.json._

trait EventMarshalling extends DefaultJsonProtocol{
  import com.sungho.BoxOffice._

  implicit val eventFormat = jsonFormat2(Event)
  implicit val eventsFormat = jsonFormat1(Events)
}
