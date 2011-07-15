import akka.actor._

import org.jruby.Ruby
import org.jruby.RubyClass
import org.jruby.RubyModule
import org.jruby.RubyObject
import org.jruby.anno.JRubyMethod
import org.jruby.anno.JRubyClass
import org.jruby.runtime.ObjectAllocator
import org.jruby.runtime.ThreadContext
import org.jruby.runtime.builtin.IRubyObject
import org.jruby.javasupport.JavaUtil
import org.jruby.runtime.Block
import org.jruby.runtime.Visibility._

@JRubyClass(name=Array("Actor"))
class AkkaUntypedActor(val runtime: Ruby, val klass: RubyClass) extends RubyObject(runtime, klass) {
  var actor: ActorRef = null

  protected class InnerUntypedActor(runtime: Ruby, context: ThreadContext, parent: RubyObject, block: Block) extends UntypedActor {
    def onReceive(msg: Any) {
      val args = Array(JavaUtil.convertJavaToRuby(runtime,msg))
      parent.instance_exec(context, args, block)
    }
  }

  @JRubyMethod(visibility = PRIVATE)
  def initialize(context: ThreadContext, block: Block) = {
    val self = this
    actor = Actors.actorOf(new UntypedActorFactory() {
      def create() = {
        new InnerUntypedActor(context.getRuntime(), context, self, block)
      }
    })
    this
  }

  @JRubyMethod(name=Array("reply"))
  def replyUnsafe(context: ThreadContext, msg: IRubyObject) {
    actor.replyUnsafe(msg)
  }

  @JRubyMethod(name=Array("send_request_reply"))
  def sendRequestReply(context: ThreadContext, msg: IRubyObject) = {
    JavaUtil.convertJavaToRuby(context.getRuntime(), actor.sendRequestReply(msg))
  }

  @JRubyMethod(name=Array("send_request_reply_future"))
  def sendRequestReplyFuture(context: ThreadContext, msg: IRubyObject) = {
    JavaUtil.convertJavaToRuby(context.getRuntime(), actor.sendRequestReplyFuture(msg))
  }

  @JRubyMethod(name=Array("<<"))
  def sendMesssage(context: ThreadContext, msg: IRubyObject) {
    actor.sendOneWay(msg)
  }

  @JRubyMethod
  def stop(context: ThreadContext) {
    actor.stop()
  }

  @JRubyMethod
  def start(context: ThreadContext) {
    actor.start()
  }
}

object AkkaUntypedActor {
  def createActorClass(runtime: Ruby, akkaModule: RubyModule) = {
    val actorc = runtime.defineClassUnder("Actor", runtime.getObject(), ActorAllocator, akkaModule)
    actorc.setReifiedClass(classOf[AkkaUntypedActor])
    actorc.kindOf = new RubyModule.KindOf() {
      override def isKindOf(obj: IRubyObject, aType: RubyModule) = {
        obj.isInstanceOf[AkkaUntypedActor]
      }
    }
    actorc.defineAnnotatedMethods(classOf[AkkaUntypedActor])
    actorc
  }

  object ActorAllocator extends ObjectAllocator() {
	def allocate(runtime: Ruby, klass: RubyClass) = {
      new AkkaUntypedActor(runtime, klass)
    }
  }
}