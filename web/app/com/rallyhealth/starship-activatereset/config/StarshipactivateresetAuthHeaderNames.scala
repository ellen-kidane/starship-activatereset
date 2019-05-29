package com.rallyhealth.starship-activatereset.config

/**
 * All final vals MUST not be type annotated to keep them constants. (Type annotations convert them into functions)
 * https://stackoverflow.com/questions/4074944/best-practice-for-use-constants-in-scala-annotations
 */
object StarshipactivateresetAuthHeaderNames {

  final val AdminId = "X-Starship-activatereset-Admin-Id"
  final val AdminLevel = "X-Starship-activatereset-Admin-Level"
  final val OAuthEncryptedUserId = "X-OAuth-Encrypted-User-Id"
  final val OAuthToken = "X-OAuth-Token"
  final val OAuthTokenExpiry = "X-OAuth-Token-Expiry"
  final val OAuthSource = "X-OAuth-Source"
  final val OAuthPortalSource = "X-OAuth-Portal-Source"
  final val OAuthProvider = "X-OAuth-Provider"
  final val RallyId = "X-RallyId"
  final val TimeStamp = "X-UTC-Time-Stamp"
  /** Hash-based message authentication code of RallyId and TimeStamp */
  final val Hmac = "X-Starship-activatereset-Verification-Hmac"
  final val SwaggerBypass = "X-Swagger-Hmac-Bypass"

  /**
   * This header should contain product and environment information i.e rally-starship-activatereset-dev
   * and is used by when making optum requests so that optum can more easily trace the origin of the request
   * for debugging purposes. The header key that is actually sent to optum is "actor" -> "headerValue" however per
   * the slight convention above we keep our own header keys internally and translate them when passing the request through our infrastructure
   * at the point of necessity i.e. when making a call to optum.
   */
  final val RequestOriginActor = "X-Starship-activatereset-Actor"
}
