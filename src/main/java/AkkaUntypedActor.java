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
import static org.jruby.runtime.Visibility.*;

@JRubyClass(name="Actor")
class AkkaUntypedActor extends RubyObject {
  public static RubyClass createActorClass(final Ruby runtime, RubyModule akkaModule) {
    RubyClass actorc = runtime.defineClassUnder("Actor", runtime.getObject(), ACTOR_ALLOCATOR, akkaModule);
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
    public IRubyObject allocate(final Ruby runtime, final RubyClass klass) {
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
    protected final RubyObject parent;

    public InnerUntypedActor(final Ruby runtime, final ThreadContext context, RubyObject parent, final Block block) {
      super();
      this.runtime = runtime;
      this.context = context;
      this.parent = parent;
      this.block = block;
    }

    public void onReceive(final Object msg) {
      IRubyObject[] args = {JavaUtil.convertJavaToRuby(runtime,msg)};
      parent.instance_exec(context, args, block);
    }
  }

  @JRubyMethod(visibility = PRIVATE)
  public IRubyObject initialize(final ThreadContext context, final Block block) {
    final RubyObject self = this;
    actor = Actors.actorOf(new UntypedActorFactory() {
      public UntypedActor create() {
        return new InnerUntypedActor(context.getRuntime(), context, self, block);
      }
    });
    return this;
  }

  @JRubyMethod(name="reply")
  public void replyUnsafe(final ThreadContext context, final IRubyObject msg) {
    actor.replyUnsafe(msg);
  }

  @JRubyMethod(name="send_request_reply")
  public IRubyObject sendRequestReply(final ThreadContext context, final IRubyObject msg) {
    return JavaUtil.convertJavaToRuby(context.getRuntime(), actor.sendRequestReply(msg));
  }

  @JRubyMethod(name="send_request_reply_future")
  public IRubyObject sendRequestReplyFuture(final ThreadContext context, final IRubyObject msg) {
    return JavaUtil.convertJavaToRuby(context.getRuntime(), actor.sendRequestReplyFuture(msg));
  }

  @JRubyMethod(name="<<")
  public void sendMesssage(final ThreadContext context, final IRubyObject msg) {
    actor.sendOneWay(msg);
  }

  @JRubyMethod
  public void stop(final ThreadContext context) {
    actor.stop();
  }

  @JRubyMethod
  public void start(final ThreadContext context) {
    actor.start();
  }
}
