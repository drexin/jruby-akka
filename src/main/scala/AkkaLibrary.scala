import org.jruby.Ruby
import org.jruby.anno.JRubyMethod
import org.jruby.runtime.Block
import org.jruby.runtime.ThreadContext
import org.jruby.runtime.builtin.IRubyObject
import akka.stm.Atomic

class AkkaLibrary {
  def load(runtime: Ruby, wrap: Boolean) {
    val akkaModule = runtime.defineModule("Akka");

    runtime.getKernel().defineAnnotatedMethods(classOf[AkkaAtomic]);
    AkkaUntypedActor.createActorClass(runtime, akkaModule);
  }

  class AkkaAtomic {
    @JRubyMethod
    def atomic(context: ThreadContext, self: IRubyObject, block: Block) = {
      (new Atomic[IRubyObject]() {
        def atomically() = {
          block.call(context);
        }
      }).execute;
    }
  }
}