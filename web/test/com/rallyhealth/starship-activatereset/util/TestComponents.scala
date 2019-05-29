package com.rallyhealth.starship-activatereset.util

import com.rallyhealth.starship-activatereset.StarshipactivateresetComponents
import com.rallyhealth.starship-activatereset.config.{ConfiguredContext, DefaultConfiguredContext}
import com.rallyhealth.illuminati.v9.MockSecretConfig
import com.rallyhealth.spartan.v2.config.{MemoryRallyConfig, RallyConfig}
import play.api.ApplicationLoader.Context
import play.api.{Configuration, Environment}
import play.core.DefaultWebCommands
class TestComponents(
  configuredContext: ConfiguredContext = TestConfiguredContext()
) extends StarshipactivateresetComponents(configuredContext)

object TestConfiguredContext {
  def apply(
    environment: Environment = Environment.simple(),
    rallyConfig: RallyConfig = new MemoryRallyConfig(Map.empty)
  ): ConfiguredContext = {
    new DefaultConfiguredContext(
      originalContext = Context(
        environment = environment,
        sourceMapper = None,
        webCommands = new DefaultWebCommands,
        initialConfiguration = Configuration.load(environment)
      ),
      rallyConfig = rallyConfig,
      secretConfig = new MockSecretConfig(rallyConfig)
    )
  }
}
