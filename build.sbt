lazy val root = (project in file("."))
	.enablePlugins(SbtPlugin)
	.settings(
		name := "sbt-zip-plugin",
		organization := "com.demo.app",
		version := "0.1-SNAPSHOT",
		sbtPlugin := true,
		libraryDependencies += "com.demo.app" %% "sbt-zip-app" % "0.1-SNAPSHOT"
 )
