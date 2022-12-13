package com.biom;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.model.common.Identifier;


public class Authentication {
	
	public static OSClientV3 os=null;
	
	public Authentication() {
		// adding ssl certificate
		String certificatesTrustStorePath = "/etc/ssl/certs/java/cacerts";
		System.setProperty("javax.net.ssl.trustStore", certificatesTrustStorePath);
		
		// creating instance of OSClientV3 for authentication
		os = OSFactory.builderV3()
		        .endpoint("https://10.1.138.27:5000/v3/")
		        .credentials("admin", "HtHcDViMZLv23I3vI1tqgjXsBggQqzRp", Identifier.byName("Default"))
		        .scopeToProject(Identifier.byId("0c140ae7aad8438d98210df181d98273"))
		        .authenticate();	
	
	}
		
}
