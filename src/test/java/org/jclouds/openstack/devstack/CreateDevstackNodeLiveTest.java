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

package org.jclouds.openstack.devstack;

import static org.jclouds.openstack.devstack.GenericComputeServiceContextToOpenstackComputeServiceContext.getDevstackNode;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.RetryIfSocketNotYetOpen;
import org.jclouds.net.IPSocket;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

public class CreateDevstackNodeLiveTest {

  public static final String      SKIP_DESTROY = "jclouds.devstack.skip-destroy";
  protected ComputeServiceContext vboxContext;
  protected ComputeServiceContext openstackContext;
  private boolean                 skipDestroy;

  @BeforeClass
  public void setUp() {
    this.skipDestroy = true; // System.getProperty(SKIP_DESTROY) != null;
    DevstackVBoxSupplier supplier = new DevstackVBoxSupplier();
    this.vboxContext = supplier.getVBoxContext();
    this.openstackContext = supplier.get();
  }

  @AfterClass
  public void tearDown() {
    if (!skipDestroy) {
      vboxContext.getComputeService().destroyNodesMatching(new Predicate<NodeMetadata>() {
        @Override
        public boolean apply(NodeMetadata input) {
          return input.getGroup().equals("devstack");
        }
      });
      vboxContext.close();
    }
  }

  @Test(groups = { "live" })
  public void testDevstackIsAvailable() {
    Predicate<IPSocket> socketTester = vboxContext.getUtils().getInjector().getInstance(RetryIfSocketNotYetOpen.class);
    // dashboard is available
    socketTester.apply(new IPSocket(Iterables.getFirst(getDevstackNode(vboxContext).getPublicAddresses(), null), 80));
    // api is available
    socketTester.apply(new IPSocket(Iterables.getFirst(getDevstackNode(vboxContext).getPublicAddresses(), null), 5000));
  }

}
