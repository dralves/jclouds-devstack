package org.jclouds.openstack.devstack;

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

@Test(groups = { "live" })
public class CreateDevstackNodeLiveTest {

  protected ComputeServiceContext context;
  protected NodeMetadata          devstackNode;

  @BeforeGroups(groups = "live")
  public void setUp() {
    // props.setProperty(TIMEOUT_NODE_RUNNING, "2400000");
    Properties props = new Properties();
    props.setProperty(TIMEOUT_SCRIPT_COMPLETE, "2400000");
    context = new ComputeServiceContextFactory().createContext("virtualbox", "", "",
        ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule()), props);
    CreateDevstackNode createDevstackNode = new CreateDevstackNode();
    devstackNode = createDevstackNode.apply(context);
  }

  @AfterGroups(groups = "live")
  public void tearDown() {
    context.getComputeService().destroyNodesMatching(new Predicate<NodeMetadata>() {

      @Override
      public boolean apply(NodeMetadata input) {
        return input.getGroup().equals("devstack");
      }
    });
    context.close();
  }

}
