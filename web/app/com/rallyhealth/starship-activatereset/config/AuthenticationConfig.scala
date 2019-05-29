package com.rallyhealth.starship-activatereset.config

import java.security.interfaces.RSAPublicKey

import com.rallyhealth.enigma.v4.util.KeyHelpers
import com.rallyhealth.illuminati.v9.SecretConfig
import com.rallyhealth.sentinel.v3.core.{ArachneSessionSentinelImpl, AsyncArachneSentinelImpl}
import com.rallyhealth.sentinel.v3.play.{PlayArachneSentinel, PlayArachneSentinelConfig, PlayAsyncArachneSessionSentinel}
import com.rallyhealth.sentinel.v3.util.KeyParser
import com.rallyhealth.spartan.v2.config.{RallyConfig, RallyConfigExImpl}

trait AuthenticationServiceConfig {
  def hmacKey: Array[Byte]
}

class AuthenticationServiceConfigImpl(
  rallyConfig: RallyConfig,
  secretConfig: SecretConfig
) extends AuthenticationServiceConfig {

  val hmacKey: Array[Byte] = KeyHelpers.parseKeyParam(
    secretConfig.get(
      "com.rallyhealth.web.edge.hmac.key",
      "77 92 89 CE 37 02 6F F2 81 9C 2B 75 6B DC C9 A1 9E A4 5E 83 D5 85 0E CE 61 A9 49 22 6B 94 13 15"
    ),
    enforceKeySize = None
  ).getKey

  private val authFrameworks = {

    // rich way of getting configs starting prefixed with "auth"
    val authConfig = new RallyConfigExImpl(rallyConfig.sub("auth"))

    object Arachne {

      val enabled: Boolean = authConfig.getBoolean("arachne.enabled", dflt = false)

      private lazy val authnPublicKey: RSAPublicKey = KeyParser.parseRSAPublicKeyFromX509PEM(
        authConfig.getString(
          "arachne.publicKey",
          """
            |-----BEGIN PUBLIC KEY-----
            |MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA31zPDw9dyi69By0uTn31
            |NXP1DA6EeQ+yo5uIPqXlX4qRHJFSl7eC/tvDIhsLrEcmR0LqnCu12q8UpUkd2fdl
            |JUhUdkaJhU07sS6PoD+LACZSJd8pBDdI6+PFaS4gopjjAuCVNZzJiB1JMyQpeM+r
            |wY2VTMdGlOopEPji/7e9/foeV6ShcKBMci/diJj1ilPYWDlKGg9LCGW39Wr8tisT
            |aQSxCq5AggAPS7Zu+p5c/CNtjsjZUq20IOdEOb92IHxfwEW2EI4HYfkDZILqSfzM
            |GjLjBorAPLoEqASWh2ZutZYfHqjHHwn7K7LgR7Imgdp5xuuQ7HT88UDi2umBE3Ah
            |WwIDAQAB
            |-----END PUBLIC KEY-----
          """.stripMargin
        )
      )

      private lazy val xsrfEnabled: Boolean = authConfig.getBoolean("arachne.xsrf.enabled", dflt = true)
      private lazy val productName: String = authConfig.getString("arachne.product.name", "starship-activatereset")
    }
  }
}
