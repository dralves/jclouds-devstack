
#Setup

Have clouds virtualbox working. see: https://github.com/jclouds/jclouds/tree/master/labs/virtualbox

That's it! 

--------------

#Booting devstack in a VM

start a clojure command line:

> mvn clean install clojure:repl 

issue the following commands:

>(use 'org.jclouds.compute2)   
>(import 'org.jclouds.openstack.devstack.Devstack)   
>(def compute (compute-service "virtualbox" "" "" :sshj :slf4j))   
>(create-nodes compute "devstack" 1 (build-template compute { :run-script (Devstack/inVm) } ))   

Devstack dashboard will be available at: http://new_node_ip, and is accessible with the credentials admin/password.

The vm will be acessible by ssh toor@new_node_ip with the password 'password' 

#Note

Due to the fact that that addresses are assigned by DHCP there is currently no way to find out exactly which address is assigned to the vm running devstack. 
This will be solved sortly but, typically, it will be one of the first ips of the assigned network.