import sbtzipplugin.ZipPlugin

lazy val root = (project in file("."))
    .enablePlugins(ZipPlugin)
  	.settings(
	    scalaVersion := "2.12.11",
    	version := "0.1",
    	sourceZipDir := file("/Home/git/sbt-zip-plugin/dataIn"),
	    targetZipDir := file("/Home/git/sbt-zip-plugin/dataOut/zip")
    // sourceZipDir := crossTarget.value
  	)
