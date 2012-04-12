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

import static com.google.common.base.Preconditions.checkState;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

/**
 * Boots up a node in the provided {@link ComputeServiceContext} and installs devstack on it. Returns the
 * {@link NodeMetadata} for the devstack node.
 * 
 * @author David Alves
 * 
 */
@Singleton
public class CreateDevstackNode implements Function<ComputeServiceContext, NodeMetadata> {

  @Resource
  @Named(ComputeServiceConstants.COMPUTE_LOGGER)
  protected Logger logger = Logger.NULL;

  @Override
  public NodeMetadata apply(ComputeServiceContext input) {
    ComputeService original = input.getComputeService();

    logger.info("Creating devstack node on provider: %s", input.getProviderSpecificContext().getDescription());
    NodeMetadata devstackNode;

    try {
      devstackNode = Iterables.getOnlyElement(input.getComputeService().createNodesInGroup("devstack", 1));
    } catch (RunNodesException e) {
      throw Throwables.propagate(e);
    }
    original.runScriptOnNode(devstackNode.getId(), AdminAccess.standard());
    String address = Iterables.getFirst(devstackNode.getPublicAddresses(), null);
    logger.info("Running devstack script on node: [id= " + devstackNode.getId() + " address= " + address + "]");
    try {
      checkState(original.submitScriptOnNode(devstackNode.getId(), Devstack.inVm(), RunScriptOptions.NONE)
          .get(20, TimeUnit.MINUTES).getExitStatus() == 0);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
    logger.info("Devstack installed. Dashboard available at: http://%s Ssh available at: ssh me@%s", address, address);
    return devstackNode;

  }
}
