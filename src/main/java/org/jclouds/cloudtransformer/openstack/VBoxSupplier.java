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

import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_SCRIPT_COMPLETE;

import java.util.Properties;

import org.jclouds.cloudtransformer.CloudTransformerModule;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Provides an a vbox computeservice context.
 * 
 * @author David Alves
 * 
 */

public class VBoxSupplier implements Supplier<ComputeServiceContext> {

  ComputeServiceContext vboxContext;

  @Override
  public ComputeServiceContext get() {
    return getOrBuildVBoxContext();
  }

  private ComputeServiceContext getOrBuildVBoxContext() {
    if (vboxContext == null) {
      Properties props = new Properties();
      props.setProperty(TIMEOUT_SCRIPT_COMPLETE, "2400000");
      vboxContext = new ComputeServiceContextFactory().createContext("virtualbox", "", "",
          ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule(), new CloudTransformerModule()),
          props);
    }
    return vboxContext;
  }

}
