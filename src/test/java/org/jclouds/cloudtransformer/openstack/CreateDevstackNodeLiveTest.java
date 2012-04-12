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

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.google.common.base.Predicate;

public class CreateDevstackNodeLiveTest {

  public static final String      SKIP_DESTROY = "jclouds.devstack.skip-destroy";
  protected ComputeServiceContext vboxContext;
  private boolean                 skipDestroy;

  @BeforeClass
  public void setUp() {
    this.skipDestroy = System.getProperty(SKIP_DESTROY) != null;
    VBoxSupplier supplier = new VBoxSupplier();
    this.vboxContext = supplier.get();
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

}
