package com.biom;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.lang.Math;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.options.PortListOptions;




public class Networks {
	private OSClientV3 os = null;
	private ArrayList<String> existing_fixed_ips = new ArrayList<String>();
	
	
	// network details
	public Networks(OSClientV3 os) {
		this.os = os;
	}
	
	
	// create fixed ip
	private void existingFixedIps(VmProperties vm) {
		List<? extends Port> ports = os.networking().port().list();
	    String s="";
		for (int i=0; i<ports.size(); i++) {
			if (ports.get(i).getNetworkId().equals(vm.getNetwork_id())){
				Set<? extends IP> obj;
				obj = ports.get(i).getFixedIps();
				s = obj.toString();
				int y = s.indexOf(",");
				int z=s.lastIndexOf(".");
				String m = s.substring(z+1,y);
				existing_fixed_ips.add(m);
			}
		}
			
	}
	
	// create fixedIp
	public String getIpAddress(VmProperties vm) {
		existingFixedIps(vm);
	    int min = 2;
	    int max = 254;
		String random = Integer.toString((int)(Math.random() * (max-min+1)) + min);
		if (existing_fixed_ips.contains(random)==false) {
			 existing_fixed_ips.add(random);
			 return "192." + "168." + "222."+ random;	 
		}	
		return getIpAddress(vm);
    }
	
	
	// attaching floating ip to the VM
	public void attachFloatingIp(Server sc, VmProperties vm) {
		new Authentication();
		Port port = os.networking().port().list(
				PortListOptions.create().deviceId(sc.getId()).networkId(vm.getNetwork_id())
		).get(0);
		
		NetFloatingIP fip = Builders.netFloatingIP().portId(port.getId()).floatingNetworkId(vm.getFloating_network_id()).build();
		fip = os.networking().floatingip().create(fip);
		System.out.println("boom");
		vm.setFloating_ip(fip.getFloatingIpAddress());
		vm.setFp_id(fip.getId());
	}



}
