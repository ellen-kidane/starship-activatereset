package com.rallyhealth.starship-activatereset

import com.rallyhealth.starship-activatereset.config.{StarshipactivateresetConfig, ConfiguredContext}
import com.rallyhealth.starship-activatereset.module.StarshipactivateresetCommonWebModule
import com.rallyhealth.logback.{DefaultPlayModuleLogbackModule, PlayModuleLogbackModule}
import com.rallyhealth.spartan.v2.util.logging.DefaultLogger
import com.softwaremill.macwire._
import controllers.Assets
import play.api.ApplicationLoader.Context
import play.api._
import play.api.i18n._
import play.api.libs.ws.ahc.AhcWSComponents
import play.api.mvc._
import play.api.routing.Router
import router.Routes
import com.softwaremill.macwire.Wired

/**
  * Application loader that wires up the application dependencies using Macwire
  */
class StarshipactivateresetApplicationLoader extends ApplicationLoader {
  def load(context: Context): Application = {
    LoggerConfigurator(context.environment.classLoader).foreach {
      _.configure(context.environment)
    }
    val defaultPlaySecret = StarshipactivateresetConfig.Defaults.playSecret
    val configured = ConfiguredContext.apply(context, "starship-activatereset", "/illuminati-templates/starshipactivatereset.json") { secretConfig =>
      Map(
        "play.crypto.secret" -> secretConfig.get(StarshipactivateresetConfig.Keys.playSecret, defaultPlaySecret)
      )
    }

    val components = new StarshipactivateresetComponents(configured)
    components.application
  }
}

class StarshipactivateresetComponents(configs: ConfiguredContext) extends BuiltInComponentsFromContext(configs.context)
  with AhcWSComponents
  with StarshipactivateresetCommonWebModule
  with BuiltInComponents
  with DefaultLogger
  with I18nComponents {

  lazy val playModuleLogbackModule: PlayModuleLogbackModule = wire[DefaultPlayModuleLogbackModule]

  lazy val assets: Assets = wire[Assets]
  lazy val router: Router = {
    // add the prefix string in local scope for the Routes constructor
    val prefix: String = "/"
    wire[Routes]
  }

}
