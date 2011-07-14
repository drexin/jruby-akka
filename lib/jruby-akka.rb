require 'java'
require 'javalib/scala-library.jar'
require 'javalib/akka-actor-1.1.3.jar'
require 'javalib/akka-stm-1.1.3.jar'
require 'javalib/multiverse-alpha-0.6.2.jar'
require 'lib/jruby-akka.jar'

Java::AkkaLibrary.new.load(JRuby.runtime, false)
