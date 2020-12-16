package com.demo.app

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
    zip := zipTask.value,
    libCall := fileTask.value
  )

  private def zipTask =  Def.task {
    println("pluginZipTask...")
    val log = sLog.value
    lazy val out = new File(targetZipDir.value, sourceZipDir.value.getName + ".zip")
    IO.zip(Path.allSubpaths(sourceZipDir.value), out)
    out
  }

  private def fileTask =  Def.task {
    println("pluginLibTask...")
    val libFileClass = new LibFileClass()
    val file = new File(sourceDataName.value)
    println("file size before: " + file.length())
    libFileClass.libFileAppendFunction(Array(file), "Plugin")
    println("filesize after: " + file.length())
  }


}
