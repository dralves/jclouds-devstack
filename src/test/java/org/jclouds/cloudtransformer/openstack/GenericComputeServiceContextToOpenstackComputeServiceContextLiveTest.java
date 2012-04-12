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

import static junit.framework.Assert.assertTrue;
import static org.jclouds.cloudtransformer.openstack.GenericComputeServiceContextToOpenstackComputeServiceContext.getDevstackNode;

import org.jclouds.cloudtransformer.CloudTransformer;
import org.jclouds.cloudtransformer.CloudTransformers;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.net.IPSocket;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class GenericComputeServiceContextToOpenstackComputeServiceContextLiveTest extends CreateDevstackNodeLiveTest {

  @Test(groups = { "live" })
  public void transformComputeServiceContext() {
    CloudTransformer transformer = CloudTransformers.tranformerFor(vboxContext, "openstack-nova");
    ComputeServiceContext openstackContext = transformer.apply(vboxContext);
    // test the service by listing images
    assertTrue(openstackContext.getComputeService().listImages().size() != 0);
  }

  @Test(groups = { "live" }, dependsOnMethods = "transformComputeServiceContext")
  public void testDevstackIsAvailable() {
    Predicate<IPSocket> socketTester = vboxContext.getUtils().getInjector().getInstance(RetryIfSocketNotYetOpen.class);
    // dashboard is available
    socketTester.apply(new IPSocket(Iterables.getFirst(getDevstackNode(vboxContext).getPublicAddresses(), null), 80));
    // api is available
    socketTester.apply(new IPSocket(Iterables.getFirst(getDevstackNode(vboxContext).getPublicAddresses(), null), 5000));
  }
}
