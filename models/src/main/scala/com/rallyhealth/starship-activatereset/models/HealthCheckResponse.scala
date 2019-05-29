package com.rallyhealth.starship-activatereset.models

import com.rallyhealth.spartan.v2.datetime.DateTimeHelpers
import com.rallyhealth.spartan.v2.macros.SealedTraitMacros
import org.joda.time.format.PeriodFormat
import org.joda.time.{DateTime, Period}
import play.api.libs.json.{Format, Json}

/**
  * Model for the responses returned by health checks. Indicates that the microservice is alive, as well as displays
  * basic information about the status of the microservice.
  *
  * @param status the status of the healthcheck, i.e. did it succeed or did the check fail
  * @param duration time represented as a [[Long]] that the healthcheck took to complete
  * @param serviceName the name of the microservice, for example "arcade-ledger"
  * @param environment the environment that the microservice is running on, for example "dev" or "prod"
  * @param version the version of the microservice that is deployed, usually this value is only populated if the
  *                microservice was deployed through mesos, otherwise it's often "unknown"
  * @param started the [[DateTime]] that this microservice was started.
  * @param uptime how long the microservice has been running, represented as a string.
  *               see: [[com.rallyhealth.HealthCheckResponse#calculateUptime()]]
  * @param message an optional message returned by the health check.
  */
case class HealthCheckResponse(
  status: HealthCheckStatus,
  duration: Long,
  serviceName: String,
  environment: String,
  version: String = HealthCheckResponse.defaultVersion,
  started: DateTime = HealthCheckResponse.startedDate,
  uptime: String = HealthCheckResponse.calculateUptime,
  message: Option[String] = None) {
  /**
    * Whether the healthcheck succeeded or not
    */
  lazy val isSuccess: Boolean = status == HealthCheckStatus.Success
}

object HealthCheckResponse extends DefaultStarshipactivateresetFormats {
  val defaultVersion = "unknown"

  /**
    * the [[DateTime]] when the microservice started up.
    */
  val startedDate: DateTime = DateTimeHelpers.now
  private val periodFormatter = PeriodFormat.getDefault

  /**
    * "pretty" displayed timespan representing how long the microservice has been running.
    */
  def calculateUptime: String = periodFormatter.print(new Period(startedDate, DateTimeHelpers.now))

  implicit val healthCheckResponseFormat: Format[HealthCheckResponse] = Json.format
}

/**
  * Represents the status of a microservice healthcheck, i.e. did it succeed or did it fail.
  */
sealed abstract class HealthCheckStatus(val status: String)
object HealthCheckStatus extends DefaultStarshipactivateresetFormats {
  case object Success extends HealthCheckStatus("success")
  case object Failure extends HealthCheckStatus("failure")

  val statuses: Set[HealthCheckStatus] = SealedTraitMacros.objectInstances

  /**
    * Parses a raw [[String]] and attempts to find the corresponding instance of [[HealthCheckStatus]] it matches.
    *
    * @throws IllegalArgumentException if provided an invalid value which doesn't match any [[HealthCheckStatus]] in
    *                                  [[statuses]].
    */
  def fromString(rawString: String): HealthCheckStatus =
    statuses.find(_.status == rawString).getOrElse(throw new IllegalArgumentException(
      s"Problem converting '$rawString' into instance of HealthCheckStatus. Make sure that's a valid value."))

  implicit val healthCheckStatusFormat: Format[HealthCheckStatus] = Format.asString(fromString, _.status)
}
