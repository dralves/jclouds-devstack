package org.jclouds.openstack.devstack;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

@Test(groups = { "live" })
public class CreateDevstackNodeLiveTest {

  public static final String      SKIP_CREATE  = "jclouds.devstack.skip-create";
  public static final String      SKIP_DESTROY = "jclouds.devstack.skip-destroy";

  protected ComputeServiceContext context;
  protected boolean               skipCreate;
  private boolean                 skipDestroy;

  @BeforeGroups(groups = "live")
  public void setUp() {
    this.skipCreate = System.getProperty(SKIP_CREATE) != null;
    this.skipDestroy = System.getProperty(SKIP_DESTROY) != null;
    Properties props = new Properties();
    props.setProperty(TIMEOUT_SCRIPT_COMPLETE, "2400000");
    context = new ComputeServiceContextFactory().createContext("virtualbox", "", "",
        ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule()), props);
    if (!skipCreate) {
      CreateDevstackNode createDevstackNode = new CreateDevstackNode();
      createDevstackNode.apply(context);
    }
  }

  @AfterGroups(groups = "live")
  public void tearDown() {
    if (!skipDestroy) {
      context.getComputeService().destroyNodesMatching(new Predicate<NodeMetadata>() {

        @Override
        public boolean apply(NodeMetadata input) {
          return input.getGroup().equals("devstack");
        }
      });
      context.close();
    }
  }

  public NodeMetadata getDevstackNode() {
    return Iterables.getOnlyElement(context.getComputeService().listNodesDetailsMatching(
        new Predicate<ComputeMetadata>() {

          @Override
          public boolean apply(ComputeMetadata input) {
            checkArgument(input instanceof NodeMetadata);
            return ((NodeMetadata) input).getGroup().equals("devstack");
          }
        }));
  }

  public void testDevstackIsAvailable() {
    // for now we just test that the something is available where the dashboard should be
    // TODO test specifically that the openstack dashboard is present at the address.
    Predicate<IPSocket> socketTester = context.getUtils().getInjector().getInstance(RetryIfSocketNotYetOpen.class);
    socketTester.apply(new IPSocket(Iterables.getFirst(getDevstackNode().getPublicAddresses(), null), 80));
  }

}
