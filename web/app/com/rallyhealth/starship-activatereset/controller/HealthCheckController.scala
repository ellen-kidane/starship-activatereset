package com.rallyhealth.starship-activatereset.controller

import com.rallyhealth.starship-activatereset.models.HealthCheckResponse
import com.rallyhealth.starship-activatereset.service.HealthCheckService
import com.rallyhealth.spartan.v2.util.logging.DefaultLogger
import io.swagger.annotations.{ApiOperation, ApiResponse, ApiResponses}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, EssentialAction}

import scala.concurrent.ExecutionContext

/**
 * All the Ops monitoring API resides here.
 *
 */
trait HealthCheckController extends Controller {

  /**
   * Returns the status of the Play app including the currently deployed version.
   */
  @ApiOperation(
    value = "Basic health check to verify the app is alive and responding to requests.",
    nickname = "monitorPlay",
    httpMethod = "GET",
    response = classOf[HealthCheckResponse],
    position = 0)
  @ApiResponses(Array(
    new ApiResponse(
      code = 200,
      message = "The monitor call succeeded."),
    new ApiResponse(
      code = 500,
      message = "The monitor call failed unexpectedly."),
    new ApiResponse(
      code = 503,
      message = "The monitor call failed, indicating the service is unavailable.")))
  def monitorPlay: EssentialAction

}

class HealthCheckControllerImpl(healthCheckService: HealthCheckService)(executionContext: ExecutionContext)
  extends HealthCheckController
  with DefaultLogger {

  implicit val ec: ExecutionContext = executionContext

  override def monitorPlay = Action.async {
    healthCheckService.monitorPlay() map {
      case resp if resp.isSuccess => Ok(Json.toJson(resp))
      case resp => ServiceUnavailable(Json.toJson(resp))
    } recover {
      case ex: Exception =>
        logger.error("Unexpected error during mongo health check.", ex)
        InternalServerError
    }
  }

}
