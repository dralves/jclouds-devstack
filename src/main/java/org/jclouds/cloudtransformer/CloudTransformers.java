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

package org.jclouds.cloudtransformer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.compute.ComputeServiceContext;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;

public class CloudTransformers {

  private static final TypeLiteral<Map<String, CloudTransformer>> mapOfString = new TypeLiteral<Map<String, CloudTransformer>>() {
                                                                              };

  private CloudTransformers() {
  }

  public static ComputeServiceContext tranform(ComputeServiceContext source, String target) {
    return tranformerFor(source, target).apply(source);
  }

  public static CloudTransformer tranformerFor(ComputeServiceContext source, String target) {
    return checkNotNull(source.utils().injector().getInstance(Key.get(mapOfString)).get(target),
        " no transformer were registered for targget cloud: " + target);
  }

}
