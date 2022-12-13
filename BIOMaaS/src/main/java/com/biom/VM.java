package com.biom;

import java.util.concurrent.TimeUnit;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;




public class VM{
	private static OSClientV3 os = null;
	private String instance_name;
	
	// vm details
	public VM(OSClientV3 os, String instance_name) {
		VM.os = os;
		this.instance_name = instance_name;
	}
	
	// create vm
	public void createVm(VmProperties vm) {	
		Networks n = new Networks(os);
		String fixed_ip = n.getIpAddress(vm);
		vm.setIp(fixed_ip); 

		ServerCreate sc = Builders.server().name(instance_name).keypairName(vm.getKeypair())
				.flavor(String.valueOf(vm.getFlavor_id()))
				.image(vm.getImage_id())
				.build();
		sc.addNetwork(vm.getNetwork_id(), vm.getIp()) ;
		Server server = os.compute().servers().boot(sc);
		try {
			TimeUnit.SECONDS.sleep(20);;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		n.attachFloatingIp(server, vm);
		vm.setServer_id(server.getId());	
	}
	
	// once the timer stops the VM is deleted
	public static void deleteVM(VmProperties vm) {
		new Authentication();
		try {
			os.networking().floatingip().delete(vm.getFp_id());
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println(os.compute().servers().delete(vm.getServer_id()));
		System.out.println("vm deleted");
	}
	
}

