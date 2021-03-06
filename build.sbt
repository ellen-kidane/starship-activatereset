import sbt.Keys._
import Dependencies._
import play.sbt.PlayImport.PlayKeys._

name := """starship-activatereset"""
organization := "com.rallyhealth"
organization in ThisBuild := "com.rallyhealth"

scalaVersion in ThisBuild := "2.11.6"
scalaVersion := "2.11.6"

resolvers in ThisBuild += rallyArtifactoryLibSnapshotsResolver.value
resolvers in ThisBuild ++= Seq(
  "Artifactory Libs Release" at "https://artifacts.werally.in/artifactory/libs-release",
  "Artifactory Libs Release local" at "https://artifacts.werally.in/artifactory/libs-release-local")

lazy val IntegrationTestWithUnitTestAsDep = config("it") extend Test

lazy val `starship-activatereset` = (project in file("."))
  .settings(publish := {}, publishLocal := {})
  .configs(IntegrationTestWithUnitTestAsDep)
  .settings(Defaults.itSettings: _*)
  .aggregate(`starship-activateresetWeb`, `starship-activateresetModels`)

lazy val `starship-activateresetModels` = (project in file("models"))
  .configs(IntegrationTestWithUnitTestAsDep)
  .settings(Defaults.itSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      // app dependencies, keep alpha
      Rally.libSpartanPlayJson,
      Rally.playJsonOps,
      Rally.libSpartanMacros,

      // Test only dependencies, keep alpha
      Ext.Test.mockito,
      Ext.Test.scalaCheck,
      //Ext.Test.scalaTest,
      Rally.Test.libSpartanPlayJsonTest,
      //Rally.Test.playJsonTests,
      Rally.Test.scalacheckOps
    )
  )
  .settings(coverageMinimum := 0.0)
  .settings(coverageFailOnMinimum := true)

lazy val `starship-activateresetWeb` = (project in file("web"))
  .enablePlugins(RallyPlayPlugin)
  .enablePlugins(RallyKamonPlugin)
  .enablePlugins(RallyDockerSbtPlugin)
  .configs(IntegrationTestWithUnitTestAsDep)
  .dependsOn(`starship-activateresetModels`)
  .settings(Defaults.itSettings: _*)
  .settings(scalacOptions -= "-deprecation") // both -deprecation and -deprecation:false are upstream
  .settings(
    routesGenerator := InjectedRoutesGenerator,
    playDefaultPort := 9000,
    coverageExcludedPackages := "<empty>;router\\..*;",
    scalacOptions ++= Seq("-Xlint:-missing-interpolator"), // -xlint doesn't play well with the routes file
    libraryDependencies ++= Seq(
      // app dependencies, keep alpha
      filters,
      Ext.aspectJWeaver,
      Ext.macwireMacros,
      Ext.macwireUtil,
      Rally.libCarestatsCore,
      //Rally.libCareStatsTest,
      Rally.libSpartanPlayJson,
      Rally.playModuleLogback,
      Rally.libRqClientPlay,
      Rally.libEnigma,
      Rally.libIlluminati,
      Rally.libSentinelBouncyCastle,
      Rally.libSentinelPlay25,
      // test only dependencies, keep alpha
      Ext.Test.mockito,
      Ext.Test.scalaCheck,
      Ext.Test.scalaTest,
      Ext.Test.scalaTestPlusPlay,
      Ext.swaggerPlay,
      Ext.swaggerScalaModule,
      Rally.Test.libSpartanPlayJsonTest,
      Rally.Test.libTestkit,
      Rally.Test.libRqClientTestkit,
      Rally.Test.scalacheckOps
    ),

    // Nobody should need to consume the starship-activatereset web jar or test jar for any reason
    // and we deploy using the zip file generated by the play plugin
    // So explicitly disable publishing of the main jar and the tests jar
    publishArtifact in(Compile, packageBin) := false,
    publishArtifact in Test := false,

    fork in Test := true
  )
  .settings(coverageMinimum := 0.0)
  .settings(coverageFailOnMinimum := true)

// Paranoid defensive spell: No major evictions.
//rallyVerifyDependenciesForbidEvictionsRules ++= libraryDependencies.value.map(ForbidSemVerDifference.forModuleName(_, DiffLevel.Major))
// Test to start server and hit healthcheck - example of tests - possibly into an IT directory
// See doug chat history
