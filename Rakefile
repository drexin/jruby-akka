require 'ant'

directory "pkg/classes"

desc "Clean up build artifacts"
task :clean do
  rm_rf "pkg/classes"
  rm_rf "lib/jruby-akka.jar"
end

desc "Compile the extension"
task :compile => "pkg/classes" do |t|
  ant.javac :srcdir => "src", :destdir => t.prerequisites.first,
    :source => "1.6", :target => "1.6", :debug => true,
    :classpath => "${java.class.path}:${sun.boot.class.path}:javalib/akka-modules-1.0.jar"
end

desc "Build the jar"
task :jar => :compile do
  ant.jar :basedir => "pkg/classes", :destfile => "lib/jruby-akka.jar", :includes => "**/*.class"
end

task :package => :jar

desc "Run the specs"
task :spec => :jar do
  ruby "-S", "spec", "spec"
end
