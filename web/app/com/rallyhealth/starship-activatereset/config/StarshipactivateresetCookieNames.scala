package com.rallyhealth.starship-activatereset.config

object StarshipactivateresetCookieNames {
  val Heartbeat = "Starship-activatereset-Heartbeat"
  val Session = "Starship-activatereset-Session"
  val XsrfToken = "Starship-activatereset-XSRF-Token"

  val all: Seq[String] = Seq(Heartbeat, Session, XsrfToken)
}
