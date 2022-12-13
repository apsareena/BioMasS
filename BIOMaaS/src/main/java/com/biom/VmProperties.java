package com.biom;

// Vm properties
public class VmProperties{
	
	private String server_id;
	private int flavor_id;
	private String floating_ip;
	private String fp_id;
	private String ip;
	private int input_cnt;
	private String image_id;
	private String network_id;
	private String floating_network_id;
	private String keypair;
	
	// keypair name
	public String getKeypair() {
		return keypair;
	}

	public void setKeypair(String keypair) {
		this.keypair = keypair;
	}

	// no of inputs inside the VM
	public int getInput_cnt() {
		return input_cnt;
	}

	public void setInput_cnt(int input_cnt) {
		this.input_cnt = input_cnt;
	}

	// id of the image used to create a VM
	public String getImage_id() {
		return image_id;
	}

	public void setImage_id(String image_id) {
		this.image_id = image_id;
	}

	// private network id
	public String getNetwork_id() {
		return network_id;
	}

	public void setNetwork_id(String network_id) {
		this.network_id = network_id;
	}

	// public network id
	public String getFloating_network_id() {
		return floating_network_id;
	}

	public void setFloating_network_id(String floating_network_id) {
		this.floating_network_id = floating_network_id;
	}
	
	// instance id
	public String getServer_id() {
		return server_id;
	}

	public void setServer_id(String server_id) {
		this.server_id = server_id;
	}

	// flavor of the instance
	public int getFlavor_id() {
		return flavor_id;
	}

	public void setFlavor_id(int flavor_id) {
		this.flavor_id = flavor_id;
	}

	// public ip address of VM
	public String getFloating_ip() {
		return floating_ip;
	}

	public void setFloating_ip(String floating_ip) {
		this.floating_ip = floating_ip;
	}

	// public ip id or floating ip id of the instance
	public String getFp_id() {
		return fp_id;
	}

	public void setFp_id(String fp_id) {
		this.fp_id = fp_id;
	}

	// private ip address of the instance
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public VmProperties(){

	}
}
