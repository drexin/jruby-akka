import akka.actor.*;

import org.jruby.Ruby;
import org.jruby.RubyClass;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.anno.JRubyMethod;
import org.jruby.anno.JRubyClass;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.Block;
import org.jruby.RubyBoolean;
import org.jruby.javasupport.JavaUtil;
import static org.jruby.runtime.Visibility.*;

@JRubyClass(name="Actor")
class AkkaUntypedActor extends RubyObject {
  public static RubyClass createActorClass(Ruby runtime) {
    RubyClass actorc = runtime.defineClass("Actor", runtime.getObject(), ACTOR_ALLOCATOR);
    actorc.setReifiedClass(AkkaUntypedActor.class);
    actorc.kindOf = new RubyModule.KindOf() {
      @Override
      public boolean isKindOf(IRubyObject obj, RubyModule type) {
        return obj instanceof AkkaUntypedActor;
      }
    };
    actorc.defineAnnotatedMethods(AkkaUntypedActor.class);
    return actorc;
  }

  private static ObjectAllocator ACTOR_ALLOCATOR = new ObjectAllocator() {
    public IRubyObject allocate(Ruby runtime, RubyClass klass) {
      return new AkkaUntypedActor(runtime, klass);
    }
  };

  ActorRef actor;

  AkkaUntypedActor(final Ruby runtime, final RubyClass klass) {
    super(runtime, klass);
  }

  protected class InnerUntypedActor extends UntypedActor {

    protected final Ruby runtime;
    protected final ThreadContext context;
    protected final Block block;

    public InnerUntypedActor(final Ruby runtime, final ThreadContext context, final Block block) {
      super();
      this.runtime = runtime;
      this.context = context;
      this.block = block;
    }

    public void onReceive(Object msg) {
      block.call(context, JavaUtil.convertJavaToRuby(runtime,msg));
    }
  }

  @JRubyMethod(visibility = PRIVATE)
  public IRubyObject initialize(final ThreadContext context, final Block block) {
    actor = Actors.actorOf(new UntypedActorFactory() {
      public UntypedActor create() {
        return new InnerUntypedActor(context.getRuntime(), context, block);
      }
    });
    actor.start();
    return this;
  }

  @JRubyMethod(name="<<")
  public void sendMesssage(final ThreadContext context, IRubyObject msg) {
    actor.sendOneWay(msg);
  }

  @JRubyMethod
  public void stop(final ThreadContext context) {
    actor.stop();
  }
}