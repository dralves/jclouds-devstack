*Rationale* - sometimes it is useful, for testing purposes or otherwise, to have the ability of "transforming" a cloud into another one. 
Possible use cases are running openstack tests on ec2 or trying eucalyptus on virtualbox.  

#Usage

Example booting Devstack on a virtualbox vm:

>// build a vbox context that includes the CloudTransformers module (any other context that includes this module can be used)
>ComputeServiceContext vboxContext = new VBoxSupplier().get();
>
>CloutTransformer transformer = CloudTransformers.transformerFor(vboxContext,"openstack-nova");
>
>// get the new cloud
>ComputServiceContext novaContext = transformer.apply(vboxContext);
>
>// even simpler
>ComputServiceContext novaContext = CloudTransformers.transform(vboxContext,"openstack-nova");

Devstack dashboard will be available at: http://new_node_ip, and is accessible with the credentials admin/password.
The vm will be acessible by ssh toor@new_node_ip with the password 'password' 
