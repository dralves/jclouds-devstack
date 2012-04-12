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

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;

import com.google.common.collect.ImmutableSet;

/**
 * All-in-one script that installs Devstack on a vm through jclouds-virtualbox. In order to pass a single script to
 * TemplateOptions, this script also executes {@link AdminAccess}.
 * 
 * @author David Alves
 * 
 */
public class Devstack implements Statement {

  public static Statement inVm() {
    return new Devstack();
  }

  public Iterable<String> functionDependencies(OsFamily family) {
    return ImmutableSet.of();
  }

  public String render(OsFamily family) {
    switch (family) {
      case UNIX:
        return "apt-get update\n" + "apt-get install -qqy git\n"
            + "git clone https://github.com/cloudbuilders/devstack.git\n" + "cd devstack\n"
            + "echo ADMIN_PASSWORD=password > localrc\n" + "echo MYSQL_PASSWORD=password >> localrc\n"
            + "echo RABBIT_PASSWORD=password >> localrc\n" + "echo SERVICE_TOKEN=tokentoken >> localrc\n"
            + "echo FLAT_INTERFACE=br100 >> localrc\n" + "./stack.sh";
      default:
        throw new UnsupportedOperationException("Only *nix is supported");
    }
  }
}
