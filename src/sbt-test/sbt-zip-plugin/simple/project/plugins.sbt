
// Manual Declaration

addSbtPlugin("io.demo.sbt" % "sbt-zip-plugin" % "0.1-SNAPSHOT")

// OR Dynamic

// sys.props.get("plugin.version") match {
//   case Some(x) => {
//   	addSbtPlugin("io.demo.sbt" % "sbt-zip-plugin" % x)
//   }
//   case _ => sys.error("""|The system property 'plugin.version' is not defined.
//                          |Specify this property using the scriptedLaunchOpts -D.""".stripMargin)
// }
