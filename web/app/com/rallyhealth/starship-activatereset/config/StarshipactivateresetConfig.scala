package com.rallyhealth.starship-activatereset.config

import javax.xml.bind.DatatypeConverter

import com.rallyhealth.spartan.v2.config.RallyConfig
import com.rallyhealth.enigma
import com.rallyhealth.illuminati.v9.{BaseSecretConfig, SecretPath, SecretValue}
import org.bouncycastle.crypto.params.KeyParameter

import scala.io.Source

/**
 * A trait that provides the base shared configurations between all the microservices.
 *
 * Each microservice should feel free to extend this trait to add in any additional configurations which are specific
 * to that microservice.
 */
trait StarshipactivateresetConfig {

  //See chopshop-edge configuration for a better example of how to bring all this together
  protected val config: ConfigReader
  protected val listAsSetConfig: ListAsSetConfigReader
  protected val secretConfig: SecretConfigReader

  val emptySetConfiguration: String = ""

  lazy val sessionCookieMaxAge = config(StarshipactivateresetConfig.Keys.sessionCookieMaxAge, "1800").toInt // Seconds
  lazy val sessionCookieSecure = config(StarshipactivateresetConfig.Keys.sessionCookieSecure, "false").toBoolean

  // Base Config Values for the Starshipactivatereset service
  lazy val version: String = config(StarshipactivateresetConfig.Keys.version, "unknown")
  lazy val environmentName: String = config(StarshipactivateresetConfig.Keys.environmentName, "local")

  // NOTE: alternativeBaseUrls, baseDomainUrl, and baseApiUrl should not have a trailing slash as they are used in corsConfig to check
  // origin headers.
  /**
   * Semi-colon separated list of comma separated pairs of domain and api urls.
   * e.g. "http://127.0.0.0:8813,http://127.0.0.0:8813;http://127.255.255.255:8813,http://127.255.255.255:8813"
   */
  //lazy val alternativeBaseUrls: Set[StarshipactivateresetBaseUrls] = listAsSetConfig(StarshipactivateresetConfig.Keys.alternativeBaseUrls, "http://127.0.0.1:8813,http://127.0.0.1:8813", ";").flatMap(StarshipactivateresetBaseUrls.fromCommaSeparatedString)
  lazy val banzaiBuddyUrl: String = config(StarshipactivateresetConfig.Keys.banzaiBuddyUrl, "http://localhost:8080")
  lazy val baseDomainUrl: String = config(StarshipactivateresetConfig.Keys.baseDomainUrl, "http://localhost:8813")
  lazy val baseApiUrl: String = config(StarshipactivateresetConfig.Keys.baseApiUrl, "http://localhost:8813")
  lazy val connectUrl: String = config(StarshipactivateresetConfig.Keys.connectUrl, "http://localhost:8000")
  lazy val engageUrl: String = config(StarshipactivateresetConfig.Keys.engageUrl, "http://localhost:8080")
  // Survey / Rewards just use hostnames with a local reverse proxy, hence the hostnames (and lack of ports)
  lazy val rewardsUrl: String = config(StarshipactivateresetConfig.Keys.rewardsUrl, "http://rewards.engage.rally")
  lazy val surveyUrl: String = config(StarshipactivateresetConfig.Keys.surveyUrl, "http://survey.engage.rally")
  lazy val edgeValidationEnabled = config(StarshipactivateresetConfig.Keys.edgeValidationEnabled, "true").toBoolean
  lazy val edgeMaxRequestAgeMinutes: Int = config(StarshipactivateresetConfig.Keys.edgeMaxRequestAgeMinutes, "3").toInt
  /**
   * If enabled, it will add [[com.rallyhealth.starship-activatereset.filter.RallyIdContextFilter]] to the play server filters.
   */
  lazy val dgSessionEnabled: Boolean = config(StarshipactivateresetConfig.Keys.testing.dgSessionEnabled, "false").toBoolean
  lazy val acceptanceOfAnySslCertificatesExternalCalls: Boolean = config(StarshipactivateresetConfig.Keys.acceptanceOfAnySslCertificatesExternal, "false").toBoolean
  // The header name for passing rallyIds to doppelganger
  lazy val dgSessionIdHeader: String = config(StarshipactivateresetConfig.Keys.testing.dgSessionIdHeader, "Doppelganger-Session-Id")
/* GITER8 CHECK THIS
  lazy val corsConfig: CorsConfig = CorsConfig(
    // Whitelist for browsers of front end domains that are allowed to call our backend servers. Non-whitelisted websites domains like www.RallyWithDancingGifs.com can't make browser ajax requests to our endpoints.
    allowOriginDomains = listAsSetConfig(StarshipactivateresetConfig.Keys.CORS.allowOriginDomains, "").toSeq ++ Seq(baseApiUrl, baseDomainUrl) ++ alternativeBaseUrls.toSeq.flatMap(urls => Seq(urls.appDomain, urls.apiDomain)),
    exposeHeaders = config(StarshipactivateresetConfig.Keys.CORS.exposeHeaders, "WWW-Authenticate, Server-Authorization"),
    allowMethods = config(StarshipactivateresetConfig.Keys.CORS.allowMethods, "POST, GET, OPTIONS, PUT, DELETE"),
    allowHeaders = config(StarshipactivateresetConfig.Keys.CORS.allowHeaders, s"x-requested-with,content-type,Cache-Control,Pragma,Date,Starshipactivatereset-XSRF-Token,${StarshipactivateresetHeaderNames.TIMESTAMP},${StarshipactivateresetHeaderNames.LOCALE}"),
    allowCredentials = config(StarshipactivateresetConfig.Keys.CORS.allowCredentials, "true")
  )
*/
  def serviceName: String

  def features: Map[String, Boolean]

  /**
   * Get either the api from the versionKey (precedence) or the first feature flag with value true or None.
   *   ex: PROFILE_API_VERSION="v4.1" -> Some(41)
   *   ex: Starshipactivatereset_FEATURES_PROFILE_API_V43="true" -> Some(43)
   *   ex: Starshipactivatereset_FEATURES_PROFILE_API_V44="false", Starshipactivatereset_FEATURES_PROFILE_API_V43="true" -> Some(43)
   *   ex: Starshipactivatereset_FEATURES_PROFILE_API_V44="false" -> None
   * @param apiVersionKeyName is the key that will contain a version, ex: PROFILE_API_VERSION="4.1"
   * @param apiFeatureFlagKeyNames is a priority list (higher to low) of keys to find a feature flag as version. ex: ("PROFILE_API_V44"="TRUE")
   */
  def findApiVersion(apiVersionKeyName: Option[String] = None, apiFeatureFlagKeyNames: Seq[String] = Seq.empty[String]): Option[String] =
    apiVersionKeyName
      .flatMap(kn => Option(config(kn, "")).filterNot(_.trim.isEmpty))
      .orElse(
        apiFeatureFlagKeyNames
          .find(featureKey => features.get(featureKey).contains(true))
          .map(_.replaceAll("^.*V(\\d{1,3})$", "$1")) //something funky happening with parsing here GITER8 CHECK THIS
      )
      .map(_.filter(_.isDigit))
      .filterNot(_.trim.isEmpty)

  private def rawSecretToKeyParameter(rawSecret: String): KeyParameter = {
    new KeyParameter(DatatypeConverter.parseHexBinary(rawSecret.replaceAll("\\s", "")))
  }

  lazy val playSecret: String = secretConfig(StarshipactivateresetConfig.Keys.playSecret, StarshipactivateresetConfig.Defaults.playSecret).value

  lazy val secureLoggerConfig: StarshipactivateresetSecureLoggerConfig = StarshipactivateresetSecureLoggerConfig(
    secureEnabled = config(StarshipactivateresetConfig.Keys.secureLoggerEnabled, StarshipactivateresetConfig.Defaults.secureLoggerEnabled).toBoolean,
    secureLinkEnabled =
      config(StarshipactivateresetConfig.Keys.secureLoggerLinkEnabled, StarshipactivateresetConfig.Defaults.secureLoggerLinkEnabled).toBoolean,
    secureKeyParameter = {
      rawSecretToKeyParameter(secretConfig(
        StarshipactivateresetConfig.Keys.secureLoggerEncryptKey,
        StarshipactivateresetConfig.Defaults.secureLoggerEncryptKey).value)
    })

  lazy val secureLoggerEncryptionService: enigma.v4.logging.SecureLoggerEncryptionService = StarshipactivateresetSecureLoggerEncryptionService(secureLoggerConfig)

  lazy val sortingHatMPEBaseUrl: String = config(StarshipactivateresetConfig.Keys.sortingHatMPEBaseUrl, "http://localhost:10083/sortinghat")
  lazy val noCacheRallyIdProbeUsersWhiteList: Set[String] = listAsSetConfig(StarshipactivateresetConfig.Keys.noCacheRallyIdProbeUserWhiteList, emptySetConfiguration)
}

object StarshipactivateresetConfig {

  object Keys {

    val sessionCookieMaxAge = "starshipactivatereset.session.cookie.maxage"
    val sessionCookieSecure = "starshipactivatereset.session.cookie.secure"
    val version = "starshipactivatereset.web.version"
    val serviceName = "starshipactivatereset.serviceName"
    val environmentName = "starshipactivatereset.env"
    val featuresPrefix = "starshipactivatereset.features"
    val alternativeBaseUrls = "starshipactivatereset.web.alternative.base.urls"
    val baseDomainUrl = "starshipactivatereset.web.base.url"
    val baseApiUrl = "starshipactivatereset.web.base.api.url"
    val connectUrl = "starshipactivatereset.web.rally.connect.url"
    val engageUrl = "starshipactivatereset.web.rally.engage.url"
    val rewardsUrl = "starshipactivatereset.web.rally.rewards.url"
    val surveyUrl = "starshipactivatereset.web.rally.survey.url"
    val secureLoggerEnabled = "starshipactivatereset.logging.encrypt.enabled"
    val secureLoggerLinkEnabled = "starshipactivatereset.logging.encrypt.link.enabled"
    val edgeValidationEnabled = "starshipactivatereset.edge.validation.enabled"
    val edgeMaxRequestAgeMinutes = "starshipactivatereset.edge.max.request.age.minutes"
    val sortingHatMPEBaseUrl = "starshipactivatereset.sortinghat.mpe.baseUrl"
    val noCacheRallyIdProbeUserWhiteList = "starshipactivatereset.nocache.probeuser.rallyids"
    val banzaiBuddy = "banzai.buddy"
    val banzaiBuddyUrl = "starshipactivatereset.banzai.buddy.url"
    val acceptanceOfAnySslCertificatesExternal = "starshipactivatereset.web.ssl.certificate.accept.external.any"

    object testing {
      val dgSessionIdHeader = "starshipactivatereset.web.testing.doppelganger.sessionId.header"
      val dgSessionEnabled = "starshipactivatereset.web.testing.doppelganger.session.enabled"
    }

    // keys (or "secret paths" in lib-illuminati terminology) used to securely fetch secret values
    val playSecret = "starshipactivatereset.play.secret"
    val secureLoggerEncryptKey = "starshipactivatereset.logging.encrypt.key"

    object CORS {
      val allowOriginDomains = "starshipactivatereset.web.cors.allowOriginDomains"
      val exposeHeaders = "starshipactivatereset.web.cors.exposeHeaders"
      val allowMethods = "starshipactivatereset.web.cors.allowMethods"
      val allowHeaders = "starshipactivatereset.web.cors.allowHeaders"
      val allowCredentials = "starshipactivatereset.web.cors.allowCredentials"
    }
  }

  object Defaults {

    val secureLoggerEnabled: String = "true"
    val secureLoggerLinkEnabled: String = "true"

    // defaults for secrets
    val playSecret: String = "uR]p^;AgwpDVWEvjpWgI_LG2<k7DBIIm;tm5KXrBre6=4s<f<iup[9yB6N8MkINi"
    val secureLoggerEncryptKey: String =
      "F5 80 95 DE E6 F1 BB EB 2C 42 C7 88 E7 71 A3 1D 67 B4 1C EC 44 16 D3 30 21 E5 19 17 2E F6 D5 1C"
  }
}

/**
 * The Starshipactivatereset config that allows to read files backed by [[com.rallyhealth.config.RallyConfig]] that
 * gives preference to environment config over file config.
 *
 * Each microservice needs to provide their own implementation of the config, but many probably don't need to
 * override anything other than the default value for the serviceName
 */
class StarshipactivateresetConfigImpl(confFile: String, secretTemplateResourcePath: String)
  extends StarshipactivateresetConfig {

  override def serviceName = "starship-activatereset"

  protected lazy val rallyConfig: RallyConfig = RallyConfig("starship-activatereset.conf")
  protected lazy val secretConfigImpl: BaseSecretConfig = BaseSecretConfig.fromTemplateFilename(rallyConfig, secretTemplateResourcePath)

  override protected lazy val config: ConfigReader = new ConfigReader(rallyConfig.get)
  override protected lazy val listAsSetConfig: ListAsSetConfigReader = new ListAsSetConfigReader(rallyConfig.get)

  override protected lazy val secretConfig: SecretConfigReader = new SecretConfigReader(secretConfigImpl.get)

  private lazy val featuresConfig: RallyConfig = rallyConfig.sub(StarshipactivateresetConfig.Keys.featuresPrefix)
  override lazy val features: Map[String, Boolean] = featuresConfig.keys().map(k => (k, featuresConfig.get(k, "false").toBoolean)).toMap

}

object StarshipactivateresetConfigImplDefaults {

  val defaultConfigFile = "starshipactivatereset.conf"
  val defaultSecretTemplatePath = "/illuminati-templates/starshipactivatereset.json"
}

/**
 * Reader that provides configuration value for the key and default if none is provided as defined in readOrElse
 */
class ConfigReader(readOrElse: (String, => String) => String) {

  def apply(key: String, default: => String): String = readOrElse(key, default)
}

class ListAsSetConfigReader(readOrElse: (String, => String) => String) {

  def apply(key: String, default: => String, delimiter: String = ","): Set[String] = readOrElse(key, default).split(delimiter).map(_.trim).filterNot(_.isEmpty).toSet
}

/**
 * Reader that provides the value for a key, where the value is secret and so must be accessed differently
 * than other config values. This is separate from [[ConfigReader]] to provide stronger type safety.
 *
 * Note that the default implementation of this ([[com.rallyhealth.illuminati.v9.SecretConfig]]) has different defaulting
 * semantics than [[ConfigReader]]. Namely, it will not default when running in live mode.
 */
sealed class SecretConfigReader(readOrPossiblyElse: (SecretPath, => SecretValue) => SecretValue) {

  def apply(key: String, default: => String): SecretValue = readOrPossiblyElse(SecretPath(key), SecretValue(default))
}

case class CorsConfig(
  allowOriginDomains: Seq[String],
  exposeHeaders: String,
  allowMethods: String,
  allowHeaders: String,
  allowCredentials: String
)

/**
 * Implements [[enigma.v4.logging.SecureLoggerConfig]], so that this can replace the underlying config mechanism for the secure
 * logger, in order to pull its secrets through lib-illuminati.
 *
 * This is expected to be macwired into classes and not read from [[enigma.v4.logging.SecureLoggerConfig.shared]].
 * Shaded versions of enigma refer do not share the same shaded object and we do not want to accidentally fallback to[[enigma.v4.logging.SecureLoggerConfigImpl]]
 */
case class StarshipactivateresetSecureLoggerConfig(
  secureEnabled: Boolean,
  secureLinkEnabled: Boolean,
  secureKeyParameter: KeyParameter) extends enigma.v4.logging.SecureLoggerConfig

/**
 * This is expected to be macwired into classes and not read from [[enigma.v4.logging.SecureLoggerEncryptionService.shared]].
 * Shaded versions of enigma refer do not share the same shaded object and we do not want to accidentally fallback to a [[enigma.v4.logging.SecureLoggerEncryptionService]] using a [[enigma.v4.logging.SecureLoggerConfigImpl]]
 */
object StarshipactivateresetSecureLoggerEncryptionService {

  def apply(StarshipactivateresetSecureLoggerConfig: StarshipactivateresetSecureLoggerConfig) = enigma.v4.logging.SecureLoggerEncryptionService(StarshipactivateresetSecureLoggerConfig)
}
