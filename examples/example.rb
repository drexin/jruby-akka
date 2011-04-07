require 'java'
require 'javalib/akka-modules-1.0.jar'
require 'lib/jruby-akka.jar'

Java::AkkaUntypedActor.createActorClass(JRuby.runtime)

a = Actor.new do |m|
  puts m
end

a << 2
a << "Hier noch ein Test!\n\n"

a.stop