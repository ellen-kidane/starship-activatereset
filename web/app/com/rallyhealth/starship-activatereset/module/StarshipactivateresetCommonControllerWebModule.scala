package com.rallyhealth.starship-activatereset.module

import com.rallyhealth.starship-activatereset.config.{StarshipactivateresetConfig, StarshipactivateresetConfigImpl, StarshipactivateresetConfigImplDefaults}
import com.rallyhealth.starship-activatereset.controller._
import com.rallyhealth.starship-activatereset.service.{HealthCheckService, HealthCheckServiceImpl}
import com.softwaremill.macwire._

import scala.concurrent.ExecutionContext

/**
 * Common required bindings for all arcade web components.
 *
 * NOTE: If you're updating this, be sure to update ArcadeCommonMacwireGlobalSpec
 */
trait StarshipactivateresetCommonControllerWebModule {

  // Note: Alpha ordering
  // Common controllers
  lazy val healthCheckService: HealthCheckService = wire[HealthCheckServiceImpl]
  lazy val healthCheckController: HealthCheckController = wire[HealthCheckControllerImpl]

  // Note: Alpha ordering
  // Dependencies of the StarshipactivateresetCommonWebModule.

  /**
   * Every arcade web component needs to initalize an [[StarshipactivateresetConfig]]. This is used to configure
   * feature flags, and to set information used by the health check controller such as service name, version etc
   *
   * This can be bound to a subclass of [[StarshipactivateresetConfig]], as long as the name remains the same.
   */
  def starship-activateresetConfig: StarshipactivateresetConfig = new StarshipactivateresetConfigImpl(StarshipactivateresetConfigImplDefaults.defaultConfigFile, StarshipactivateresetConfigImplDefaults.defaultSecretTemplatePath)
  def executionContext: ExecutionContext = play.api.libs.concurrent.Execution.Implicits.defaultContext
}

trait StarshipactivateresetCommonWebModule extends StarshipactivateresetCommonControllerWebModule
