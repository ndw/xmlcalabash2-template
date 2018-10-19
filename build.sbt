import java.io.{BufferedReader, InputStreamReader}

name         := "xmlcalabash2-template"
organization := "com.xmlcalabash.demo"
version      := "1.0.0"
scalaVersion := "2.12.6"

lazy val xmlCalabashVersion = "1.9.15"

buildInfoKeys ++= Seq[BuildInfoKey](
  "versopm" -> version,
  // Hat tip to: https://stackoverflow.com/questions/24191469/how-to-add-commit-hash-to-play-templates
  "gitHash" -> new java.lang.Object() {
    override def toString: String = {
      try {
        val extracted = new InputStreamReader(
          java.lang.Runtime.getRuntime.exec("git rev-parse HEAD").getInputStream
        )
        new BufferedReader(extracted).readLine
      } catch {
        case ex: Exception => "FAILED"
      }
    }}.toString()
)

lazy val meerschaum = (project in file(".")).
  enablePlugins(BuildInfoPlugin).
  settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion),
    buildInfoPackage := "com.xmlcalabash.demo.sbt"
  )

resolvers += DefaultMavenRepository
resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"
resolvers += "Restlet" at "http://maven.restlet.com"
resolvers += "My Maven Repository" at "https://nwalsh.com/maven/repo"
resolvers += "Local Maven Repository" at "file:///space/websites/nwalsh.com/build/website/maven/repo"
resolvers += "Private Repository" at "https://nwalsh.com/build/website/maven/repo"

libraryDependencies ++= Seq(
  "org.apache.logging.log4j" % "log4j-api" % "2.11.0",
  "org.apache.logging.log4j" % "log4j-core" % "2.11.0",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.11.0",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "org.slf4j" % "jcl-over-slf4j" % "1.7.25",
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "net.sf.saxon" % "Saxon-HE" % "9.8.0-14",
  "com.ibm.icu" % "icu4j" % "59.1",
  "com.xmlcalabash" % "xml-calabash_2.12" % xmlCalabashVersion
)

scalacOptions := Seq("-unchecked", "-deprecation")

// I'm publishing the informal pre-release builds on my own repo
publishTo := Some(Resolver.file("file",
  new File("/space/websites/nwalsh.com/build/website/maven/repo")))
