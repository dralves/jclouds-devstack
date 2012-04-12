/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.cloudtransformer.openstack;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.v2_0.config.CredentialType;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties;
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
public class DevstackNodeToOpenstack implements Function<NodeMetadata, ComputeServiceContext> {

  @Override
  public ComputeServiceContext apply(NodeMetadata input) {

    String address = Iterables.getFirst(input.getPublicAddresses(), null);

    Properties overrides = new Properties();
    overrides.setProperty(Constants.PROPERTY_ENDPOINT, "http://" + address + ":5000");
    overrides.setProperty(KeystoneProperties.CREDENTIAL_TYPE, CredentialType.PASSWORD_CREDENTIALS.toString());
    overrides.setProperty(KeystoneProperties.VERSION, "2.0");

    return new ComputeServiceContextFactory().createContext("openstack-nova", "admin:admin", "password",
        ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule()), overrides);
  }
}
