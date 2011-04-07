require 'java'
require 'javalib/akka-modules-1.0.jar'
require 'lib/jruby-akka.jar'

Java::AkkaUntypedActor.createActorClass(JRuby.runtime)

a = Actor.new do |m|
  reply("Received: #{m}")
end

a.start

puts a.send_request_reply_future("foo").result

a.stop