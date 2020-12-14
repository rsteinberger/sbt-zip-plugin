lazy val root = (project in file("."))
	.enablePlugins(SbtPlugin)
	.settings(
		name := "sbt-zip-plugin",
		organization := "io.demo.sbt",
		version := "0.1-SNAPSHOT",
		sbtPlugin := true,
	    scriptedLaunchOpts := { scriptedLaunchOpts.value ++
	      Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
	    },
	   scriptedBufferLog := false,
	   libraryDependencies += "io.demo.sbt" %% "sbt-hello" % "0.1-SNAPSHOT"
 )
