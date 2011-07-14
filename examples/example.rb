require 'java'
require 'lib/jruby-akka.rb'

Java::AkkaLibrary.new.load(JRuby.runtime, false)

a = Akka::Actor.new do |m|
  reply("Received: #{m}")
end

a.start

puts a.send_request_reply "foo"

a.stop