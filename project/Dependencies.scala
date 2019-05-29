import com.rallyhealth.sbt.shading.ShadingImplicits._
import sbt.{ModuleID, _}

object Dependencies {

  val playString = "play25"

  object Rally {

    // TODO READ THIS | LIBRARY DEPENDENCIES | Dos and Don'ts: https://wiki.audaxhealth.com/x/Wp_8AQ

    val libSpartanVersion = "2.8.0"
    val libCareStatsVersion = "4.5.0"
    val playModuleLogbackVersion = "1.1.0"
    val playModuleSwaggerVersion = "2.1.0"
    val libRqClientPlayVersion = "1.4.0"
    val playJsonOpsVersion = "1.5.0"
    val libKamonVersion = "0.0.2"
    val libEnigmaVersion = "4.3.0"
    val libIlluminatiVersion = "9.3.0"
    val libSentinelVersion = "3.2.0"

    val libCarestatsCore = "com.rallyhealth.core" %% "lib-carestats-core" % libCareStatsVersion shaded
    val libSpartan = "com.rallyhealth.core" %% "lib-spartan-v2" % libSpartanVersion shaded
    val libSpartanMacros = "com.rallyhealth.core" %% "lib-spartan-macros" % libSpartanVersion shaded
    val libSpartanPlayJson = "com.rallyhealth.core" %% s"lib-spartan-$playString-json" % libSpartanVersion shaded
    val playModuleLogback = "com.rallyhealth.core" %% "play-module-logback" % playModuleLogbackVersion
    val playModuleSwagger = "com.rallyhealth.core" %% "play-module-swagger" % playModuleSwaggerVersion
    val libSentinelBouncyCastle = "com.rallyhealth.core" %% "lib-sentinel-bouncycastle" % libSentinelVersion shaded
    val libSentinelPlay25 = "com.rallyhealth.core" %% "lib-sentinel-play25" % libSentinelVersion shaded
    val libRqClientPlay = "com.rallyhealth.rq" %% s"lib-rq-client-$playString" % libRqClientPlayVersion shaded
    val playJsonOps: ModuleID = "com.rallyhealth" %% "play-json-ops-25" % playJsonOpsVersion

    val libKamon = "com.rallyhealth" %% "rally-kamon-sbt-plugin" % libKamonVersion

    val libEnigma: ModuleID = "com.rallyhealth.core" %% s"lib-enigma-$playString-v4" % libEnigmaVersion
    val libIlluminati: ModuleID = "com.rallyhealth.core" %% s"lib-illuminati-$playString" % libIlluminatiVersion % "compile;test->test" shaded

    object Test {
      val libRqClientTestKitVersion = "1.4.0"
      val libTestkitVersion = "0.1.0"
      val scalacheckops = "1.2.0"

      val libSpartanPlayJsonTest = libSpartanPlayJson % "test->test"
      val libRqClientTestkit = "com.rallyhealth.rq" %% s"lib-rq-client-testkit" % libRqClientTestKitVersion % "test" shaded
      val libTestkit = "com.rallyhealth.testkit" %% s"lib-testkit-$playString" % libTestkitVersion % "test"
      val scalacheckOps = "com.rallyhealth" %% "scalacheck-ops" % scalacheckops % "test"
    }
  }

  object Play25 {

    val enumeratum = "com.beachape" %% "enumeratum-play" % "1.5.9"
    val libHttpInterceptorBundle = "com.rallyhealth.interceptor" %% s"lib-http-interceptor-$playString-bundle-unstable" % "1.9.1"
    val libIlluminati = "com.rallyhealth.core" %% s"lib-illuminati-$playString" % "9.1.0" % "compile;test->test" shaded
    val playModuleLogback = "com.rallyhealth.core" %% "play-module-logback" % "1.1.0"
  }

  object Ext {
    private val macwireVersion = "2.3.0"
    /**
      * Explicit akka-actor update since 2.3.9 is affected by https://github.com/akka/akka/pull/17102
      */
    private val akkaVersion = "2.3.14"

    private val slf4jVersion = "1.7.12"

    //Alpha ordering
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
    val aspectJWeaver = "org.aspectj" % "aspectjweaver" % "1.8.10"
    val loggingSlf4j = "org.slf4j" % "slf4j-api" % slf4jVersion
    val macWireMacros = "com.softwaremill.macwire" %% "macros" % macwireVersion % "provided"
    val macWireProxy = "com.softwaremill.macwire" %% "proxy" % macwireVersion
    val macWireUtil = "com.softwaremill.macwire" %% "util" % macwireVersion
    val macwireMacros = "com.softwaremill.macwire" %% "macros" % macwireVersion % "provided"
    val macwireProxy = "com.softwaremill.macwire" %% "proxy" % macwireVersion
    val macwireUtil = "com.softwaremill.macwire" %% "util" % macwireVersion
    val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
    val swaggerPlay = "io.swagger" %% "swagger-play2" % "1.5.3" exclude ("org.reflections", "reflections") //check this carefully
    val swaggerScalaModule = "io.swagger" %% "swagger-scala-module" % "1.0.4"

    object Test {

      //Alpha ordering
      val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
      val scalaTestPlusPlay = "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "test"
      val mockito = "org.mockito" % "mockito-all" % "1.10.19" % "test"
      val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.12.4" % "test"
    }
  }

}
