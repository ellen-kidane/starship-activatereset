package com.rallyhealth.starship-activatereset.config

import com.rallyhealth.illuminati.v9.{BaseSecretConfig, SecretConfig}
import com.rallyhealth.spartan.v2.config.RallyConfig
import com.softwaremill.macwire.Module
import play.api.ApplicationLoader.Context

object ConfiguredContext {

  /**
    *
    * @param context                    usually provided by: def load(context: Context)
    * @param appName                    e.g. "edge"
    * @param illuminatiTemplateFilename e.g. "/illuminati-templates/edge-web.json"
    * @param playConfigOverrides        to be injected into play's context.initialContext
    */
  def apply(
    context: Context,
    appName: String,
    illuminatiTemplateFilename: String
  )(
    playConfigOverrides: SecretConfig => Map[String, String]
  ): ConfiguredContext = {
    val rallyConfig = RallyConfig(appName)
    val secretConfig = BaseSecretConfig.fromTemplateFilename(rallyConfig, illuminatiTemplateFilename)
    lazy val appliedPlayOverrides = playConfigOverrides(secretConfig)

    new DefaultConfiguredContext(context, rallyConfig, secretConfig) {
      override protected def playConfigOverrides: Map[String, String] = appliedPlayOverrides
    }
  }
}

@Module
trait ConfiguredContext {

  def context: Context

  def rallyConfig: RallyConfig

  def secretConfig: SecretConfig
}

@Module
class DefaultConfiguredContext(
  originalContext: Context,
  override val rallyConfig: RallyConfig,
  override val secretConfig: SecretConfig
) extends ConfiguredContext {

  protected def playConfigOverrides: Map[String, String] = Map.empty

  override val context: Context = {
    val modifiedConfiguration = originalContext.initialConfiguration ++ PlayConfiguration(playConfigOverrides)
    originalContext.copy(initialConfiguration = modifiedConfiguration)
  }
}
