import akka.stm.Atomic;

import java.io.IOException;
import org.jruby.Ruby;
import org.jruby.RubyModule;
import org.jruby.anno.JRubyMethod;
import org.jruby.runtime.load.Library;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.runtime.Block;

public class AkkaLibrary implements Library{
  public void load(final Ruby runtime, boolean wrap) throws IOException {
    RubyModule akkaModule = runtime.defineModule("Akka");

    runtime.getKernel().defineAnnotatedMethods(AkkaAtomic.class);
    AkkaUntypedActor.createActorClass(runtime, akkaModule);
  }

  public static class AkkaAtomic {

    @JRubyMethod
    public static IRubyObject atomic(final ThreadContext context, final IRubyObject self, final Block block) {
      return new Atomic<IRubyObject>() {
        public IRubyObject atomically() {
          return block.call(context);
        }
      }.execute();
    }
  }
}
