package com.rallyhealth.starship-activatereset.service

import com.rallyhealth.starship-activatereset.config.StarshipactivateresetConfig
import com.rallyhealth.starship-activatereset.models.{HealthCheckResponse, HealthCheckStatus}
import com.rallyhealth.spartan.v2.util.logging.DefaultLogger
import org.joda.time.Duration

import scala.concurrent.{ExecutionContext, Future}

/**
 * Basic health check service which can verify that the arcade microservice is alive and functioning.
 *
 * If a microservice has additional health checks they'd like to perform, they can feel free to extend this trait
 * and the corresponding default implementation.
 */
trait HealthCheckService {

  /**
   * Sanity check if the application is up.
   *
   * @return HealthCheckResponse indicating that the application is up
   */
  def monitorPlay(): Future[HealthCheckResponse]
}

class HealthCheckServiceImpl(
  starship-activateresetConfig: StarshipactivateresetConfig,
  executionContext: ExecutionContext)
  extends HealthCheckService
  with DefaultLogger {

  implicit val ec: ExecutionContext = executionContext

  def monitorPlay(): Future[HealthCheckResponse] = {
    val start = System.currentTimeMillis()
    Future.successful(HealthCheckResponse(
      status = HealthCheckStatus.Success,
      serviceName = starship-activateresetConfig.serviceName,
      environment = starship-activateresetConfig.environmentName,
      version = starship-activateresetConfig.version,
      duration = new Duration(System.currentTimeMillis() - start).getMillis))
  }
}
