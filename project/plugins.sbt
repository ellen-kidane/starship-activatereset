logLevel := Level.Warn

// See https://wiki.audaxhealth.com/display/ENG/Build+Structure#BuildStructure-Localconfiguration
credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

resolvers += Resolver.url("Rally Plugin Releases", url("https://artifacts.werally.in/artifactory/ivy-plugins-release"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.18") //GITER8CHECKTHIS

//addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.10.6")

// gives us the rally-version.properties stuff
addSbtPlugin("com.rallyhealth" %% "rally-sbt-plugin" % "0.13.0")
addSbtPlugin("com.rallyhealth" %% "rally-play-sbt-plugin" % "1.1.0")
addSbtPlugin("com.rallyhealth.sbt" %% "rally-shading-sbt-plugin" % "1.0.2")
// addSbtPlugin("com.rallyhealth" %% "rally-versioning" % "1.2.0") //GITER8CHECKTHIS
addSbtPlugin("com.rallyhealth" % "rally-kamon-sbt-plugin" % "0.0.2")
addSbtPlugin("com.rallyhealth.sbt" %% "rally-docker-sbt-plugin" % "1.7.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.0")
