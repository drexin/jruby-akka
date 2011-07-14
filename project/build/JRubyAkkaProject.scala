import sbt._
import de.element34.sbteclipsify._
import java.io.File

class JRubyAkkaProject(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with AkkaProject {
  val jruby = "org.jruby" % "jruby" % "1.6.3"
  val akkaActors = akkaModule("actor")
  val akkaStm = akkaModule("stm")

  override def defaultJarName = "jruby-akka.jar"
  override def dependencyPath = "dependencies" / "lib"
  override def managedDependencyPath = "dependencies" / "lib_managed"

  override lazy val `update` = updateAction
  override def updateAction = task {
    val x = super.updateAction.run
    FileUtilities.createDirectory("javalib", log)
    FileUtilities.createDirectory(dependencyPath, log)
    val filesToCopy = Array(new File("project/boot/scala-"+buildScalaVersion+"/lib/scala-library.jar")) ++
            ((new File((managedDependencyPath / "compile").toString)).listFiles ++
            (new File((dependencyPath).toString)).listFiles)
    FileUtilities.copyFilesFlat(filesToCopy, "javalib", log)
    x
  }

  override lazy val `package` = packageAction
  override def packageAction = task {
    val x = super.packageAction.run
    FileUtilities.createDirectory("lib", log)
    FileUtilities.copyFile("target" / ("scala_"+buildScalaVersion) / defaultJarName, "lib" / defaultJarName, log)
    x
  }
}

