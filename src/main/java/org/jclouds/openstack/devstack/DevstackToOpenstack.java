package org.jclouds.openstack.devstack;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * Returns an openstack {@link ComputeServiceContext} provided a {@link NodeMetadata} that as devstack installed.
 * 
 * @author David Alves
 * 
 */
public class DevstackToOpenstack implements Function<NodeMetadata, ComputeServiceContext> {

  @Override
  public ComputeServiceContext apply(NodeMetadata input) {

    String address = Iterables.getFirst(input.getPublicAddresses(), null);

    Properties overrides = new Properties();
    overrides.setProperty(Constants.PROPERTY_ENDPOINT, "https://" + address);

    return new ComputeServiceContextFactory().createContext("openstack-nova", "admin", "password",
        ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule()), overrides);
  }
}
