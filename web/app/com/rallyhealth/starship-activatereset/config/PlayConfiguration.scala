package com.rallyhealth.starship-activatereset.config

import com.typesafe.config.ConfigFactory
import play.api.Configuration

object PlayConfiguration {

  /**
    * Generate a string that's usable by the [[ConfigFactory]]'s parseString function.
    *
    * @param playKey   play's expected config key
    * @param playValue value for this configuration
    * @return [[ConfigFactory]] readable string representing this configuration
    */
  private def wrapConfigurationStrings(playKey: String, playValue: String): String =
    playKey + "=\"\"\"" + playValue + "\"\"\""

  def apply(configs: Map[String, String]): Configuration = {
    configs.map {
      case (key, value) => Configuration(ConfigFactory.parseString(wrapConfigurationStrings(key, value)))
    }.foldLeft(Configuration.empty)(_ ++ _)
  }
}
