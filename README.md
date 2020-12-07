# sbt-zip-plugin

# Write and test a scala SBT plugin

This demo is retooled from 

https://medium.com/@phkadam2008/write-test-your-own-scala-sbt-plugin-6701b0e36a62

##  Versions - SBT Scala Java

C:\Home\git>sbt -version
sbt version in this project: 1.4.1
sbt script version: 1.4.4

C:\Home\git>sbt scalaVersion
2.12.11

C:\Home\git>java -version
java version "15.0.1" 2020-10-20
Java(TM) SE Runtime Environment (build 15.0.1+9-18)
Java HotSpot(TM) 64-Bit Server VM (build 15.0.1+9-18, mixed mode, sharing)

## Create a simple scala project 

Directory structure:

project/plugins.sbt
build.sbt

src/main/scala/sbtzipplugin/ZipKeys.scala
src/main/scala/sbtzipplugin/ZipPlugin.scala

src/sbt-test/sbt-zip-plugin/simple/test
src/sbt-test/sbt-zip-plugin/simple/build.sbt
src/sbt-test/sbt-zip-plugin/simple/project/plugins.sbt
src/sbt-test/sbt-zip-plugin/simple/src/main/scala/Hello.scala

### plugins.sbt

Add scripted plugin dependency. Provides a framework for testing the plugin.

```
libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
```

### build.sbt

```
lazy val root = (project in file("."))
 .settings(
   name := "sbt-zip-plugin",
   organization := "io.demo.sbt",
   version := "0.1-SNAPSHOT",
   sbtPlugin := true,
   // scriptedLaunchOpts += ("-Dplugin.version=" + version.value),
   // scriptedLaunchOpts ++= sys.process.javaVmArguments.filter(
     // a => Seq("-Xmx", "-Xms", "-XX", "-      Dsbt.log.noformat").exists(a.startsWith)
   // ),
   // scriptedBufferLog := false
 )

```

#### name

#### Organization

#### version

#### sbtPlugin

flag to true, adds sbt as a dependency and automatically creates plugins descriptor file at sbt/sbt.autoplugins.

#### scriptedLaunchOpts (fails to build)

setting is the sequence of options which are passed to JVM launching scripted tasks

### scriptedBufferLog  (fails to build)

flag to false displays logs on the console when we run plugin tests using scripted tasks.


## Create the sbt plugin

### Keys and Tasks

Definine sbt Keys and Tasks required for the plugin. Plugins usually follow a convention

Keys are specified as: ${PluginName}Keys.scala

Tasks are defined as: ${PluginName}Plugin.scala 

In this case they are ZipKeys.scala and ZipPlugin.scala

### ZipKeys.scala

Sbt defines three types of keys:

SettingKey[T]: a key for a value computed once (the value is computed when loading the subproject, and kept around).

TaskKey[T]: a key for a value, called a task, that has to be recomputed each time, potentially with side effects.

InputKey[T]: a key for a task that has command line arguments as input. Check out Input Tasks for more details.

#### Define three keys

A consumer plugin can initialize the values of sourceZipDir and targetZipDir keys. 

The plugin defines a default value for targetZipDir which will be used by zip task if a consumer does not override this value.

```
package sbtzipplugin

import sbt._

trait ZipKeys {
  lazy val sourceZipDir = settingKey[File]("source directory to generate zip from.")
  lazy val targetZipDir = settingKey[File]("target directory to store generated zip.")
  lazy val zip = taskKey[Unit]("Generates zip file which includes all files from sourceZipDir")
}

```

### ZipPlugin.scala 

uses keys defined in ZipKeys.scala to evaluate zip task

The plugin extendeds AutoPlugin. We can override the following methods:

trigger: Determines whether this AutoPlugin will be activated for this project when the `requires` clause is satisfied.

requires: Defines the dependencies of the plugin.

projectSettings: Sequence of settings to be added to the project scope where this plugin is activated.

zipTask in ZipPlugin retrieves the value of sourceZipDir key and creates a zip file containing all of the files from sourceZipDir at location targetZipDir.value

```
package sbtzipplugin

import java.io.File

import sbt.Keys.{sLog, target}
import sbt._
import sbt.io.{IO, Path}

object ZipPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport extends ZipKeys
  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(
    targetZipDir := target.value / "zip",
    zip := zipTask.value
  )

  private def zipTask =  Def.task {
    val log = sLog.value
    lazy val zip = new File(targetZipDir.value, sourceZipDir.value.getName + ".zip")

    log.info("Zipping file...")
    IO.zip(Path.allSubpaths(sourceZipDir.value), zip)
    zip
  }
}
```

## Publishing Locally

https://www.scala-sbt.org/1.x/docs/Publishing.html

C:\Users\rsteinberger\.ivy2\local\io.demo.sbt\sbt-zip-plugin\scala_2.12\sbt_1.0\0.1-SNAPSHOT
downloading                       io.demo.sbt:sbt-zip-plugin_2.12:0.1-SNAPSHOT


### Define the repository

To publish to the local Ivy repository referenced as ${ivy.default.ivy.user.dir}, this is by default the directory .ivy2 in your user home, enter:

```
sbt:sbt-zip-plugin>publishLocal
```

The publishLocal task will publish to the “local” Ivy repository. By default, this is at $HOME/.ivy2/local/. Other builds on the same machine can then list the project as a dependency. 

For example, if the project you are publishing has configuration parameters like:

```
ThisBuild / organization := "io.demo.sbt"
ThisBuild / version      := "0.1-SNAPSHOT"

name := "sbt-zip-plugin"
```

Then another build on the same machine can depend on it:

```
libraryDependencies += "io.demo.sbt" %% "sbt-zip-plugin" % "0.1-SNAPSHOT"
```

The version number you select must end with SNAPSHOT, or you must change the version number each time you publish to indicate that it’s a changing artifact.

Note: SNAPSHOT dependencies should be avoided beyond local testing since it makes dependency resolution slower and the build non-repeatable.

## Test Setup

https://medium.com/@phkadam2008/write-test-your-own-scala-sbt-plugin-6701b0e36a62

Simple is a new sbt project which uses the sbt-zip-plugin

## simple/build.sbt 

Configure the test project build.sbt 

Enabling the ZipPlugin and initializing the value of key sourceZipDir to crossTarget.value. 

That means ZipPlugin will generate a zip file containing all the files from crossTarget.value directory in this case will be `target/scala-2.12`

```
import sbtzipplugin.ZipPlugin

lazy val root = (project in file("."))
    .enablePlugins(ZipPlugin)
  	.settings(
    scalaVersion := "2.12.4",
    version := "0.1",
    sourceZipDir := crossTarget.value
  )
  ```

sbt:sbt-zip-plugin> show crossTarget
[info] C:\Home\git\sbt-zip-plugin\target\scala-2.12\sbt-1.0


## simple/project/plugins.sbt

We are adding our newly created ZipPlugin dependency to the simple project. 

And the little trick here is to test our plugin against cross-version if required is to get plugin version number from system property.

```
sys.props.get("plugin.version") match {
  case Some(x) => addSbtPlugin("com.eed3si9n" % "sbt-assembly" % x)
  case _ => sys.error("""|The system property 'plugin.version' is not defined.
                         |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
}
```


## simple/test

Add a script to test the ZipPlugin. Name it `test` as scripted plugin looks for a file with name `test` in the sbt-test directory.

`> task` executes sbt task named zip
`$ exists` checks if file exists

```
> zip
$ exists target/zip/scala-2.12.zip
```

## Run the test

Enter into sbt-zip-plugin projects sbt console and enter


```
sbt:sbt-zip-plugin> reload
sbt:sbt-zip-plugin> compile
sbt:sbt-zip-plugin> publishLocal
sbt:sbt-zip-plugin> scripted
```

C:\Users\RSTEIN~1\AppData\Local\Temp\sbt_98ec9717\target\zip\dataOut.zip
























