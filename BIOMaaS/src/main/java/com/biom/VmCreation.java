package com.biom;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.network.IP;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.network.Port;
import org.openstack4j.model.network.options.PortListOptions;
import org.openstack4j.openstack.OSFactory;

public class VmCreation {
	// VM attributes
    private static String image_id = "2aaa58d7-22ff-43b5-ad99-9d15363bfdd2";
    private static String instance_name = "ubuntu";
    private static String flavor_id = "3";
    private static String key_pair_name = "ubuntulatest";
    private static String network_id = "6b6d85cd-6552-457c-ae06-6b7ccf8dac56";
    private static String floating_network_id = "080672ee-8ba4-4f03-8e9e-3887c328ec80";
    private static ArrayList<String> existing_fixed_ips = new ArrayList<String>();

	public static void main(String[] args) {
		OSClientV3 os=null;
		String certificatesTrustStorePath = "/etc/ssl/certs/java/cacerts";
		System.setProperty("javax.net.ssl.trustStore", certificatesTrustStorePath);
		
		os = OSFactory.builderV3()
		        .endpoint("https://10.1.138.27:5000/v3/")
		        .credentials("admin", "HtHcDViMZLv23I3vI1tqgjXsBggQqzRp", Identifier.byName("Default"))
		        .scopeToProject(Identifier.byId("0c140ae7aad8438d98210df181d98273"))
		        .authenticate();
		createVm(os);
		

	}
	
	public static String createVm(OSClientV3 os) {	
		String fixed_ip="";
//		Networks n = new Networks(os, network_id, floating_network_id);
//	fixed_ip = n.getIpAddress();
fixed_ip = getIpAddress(os);
		ServerCreate sc = Builders.server().name(instance_name).keypairName(key_pair_name).flavor(flavor_id).image(image_id).build();
		sc.addNetwork(network_id, fixed_ip) ;
		Server server = os.compute().servers().boot(sc);
		try {
			TimeUnit.SECONDS.sleep(20);;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		String[] floating_ip_id = n.attachFloatingIp(server).split(" ");
		String server_id = server.getId();
//		System.out.println(floating_ip_id[0]);
		System.out.println(server_id);
		return " "+server_id;
	
	}
	
	// create fixed ip
		private static void existingFixedIps(OSClientV3 os) {
			List<? extends Port> ports = os.networking().port().list();
		    String s="";
			for (int i=0; i<ports.size(); i++) {
				if (ports.get(i).getNetworkId().equals(network_id)){
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
		public static String getIpAddress(OSClientV3 os) {
			existingFixedIps(os);
		    int min = 2;
		    int max = 254;
			String random = Integer.toString((int)(Math.random() * (max-min+1)) + min);
			if (existing_fixed_ips.contains(random)==false) {
				 existing_fixed_ips.add(random);
				 return "192." + "168." + "222."+ random;	 
			}	
			return getIpAddress(os);
	    }
		
		// floating ip
		public String attachFloatingIp(Server sc, OSClientV3 os) {
			Port port = os.networking().port().list(
					PortListOptions.create().deviceId(sc.getId()).networkId(network_id)
			).get(0);
			
			NetFloatingIP fip = Builders.netFloatingIP().portId(port.getId()).floatingNetworkId(network_id).build();
			fip = os.networking().floatingip().create(fip);
			return fip.getFloatingIpAddress()+" "+fip.getId();
		}

}
