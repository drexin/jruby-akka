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
  
  override lazy val `update` = { 
    val x = updateAction
    val filesToCopy = Array(new File("project/boot/scala-"+buildScalaVersion+"/lib/scala-library.jar")) ++ 
    		((new File((managedDependencyPath / "compile").toString)).listFiles ++
    		(new File((dependencyPath).toString)).listFiles)
    FileUtilities.copyFilesFlat(filesToCopy, "javalib", log)
    x
  }
  
  override lazy val `package` = {
    val x = packageAction
    FileUtilities.copyFile("target" / ("scala_"+buildScalaVersion) / defaultJarName, "lib" / defaultJarName, log)
    x
  }
}

