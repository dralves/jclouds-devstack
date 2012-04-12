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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.cloudtransformer.CloudTransformer;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Transforms the provided context into an openstack context by booting a vm in the provided context's
 * {@link ComputeService} (if there isn't already one) and returning an adequately configured "openstack-nova" context.
 * 
 * @author David Alves
 * 
 */
@Singleton
public class GenericComputeServiceContextToOpenstackComputeServiceContext implements CloudTransformer {

  @Override
  public Set<String> supportedSourceClouds() {
    return ImmutableSet.of(ANY_CLOUD);
  }

  @Override
  public String targetCloud() {
    return "openstack-nova";
  }

  @Override
  public ComputeServiceContext apply(ComputeServiceContext input) {
    NodeMetadata devstackNode;
    try {
      devstackNode = getDevstackNode(input);
    } catch (NoSuchElementException e) {
      CreateDevstackNode createDevstackNode = new CreateDevstackNode();
      devstackNode = createDevstackNode.apply(input);
    }
    return new DevstackNodeToOpenstack().apply(devstackNode);
  }

  public static NodeMetadata getDevstackNode(ComputeServiceContext ctx) {
    return Iterables.getOnlyElement(ctx.getComputeService().listNodesDetailsMatching(new Predicate<ComputeMetadata>() {
      @Override
      public boolean apply(ComputeMetadata input) {
        checkArgument(input instanceof NodeMetadata);
        return ((NodeMetadata) input).getGroup().equals("devstack");
      }
    }));
  }

}
