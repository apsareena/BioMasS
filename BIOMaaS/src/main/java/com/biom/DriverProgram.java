package com.biom;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.lang.Thread;

// input flag 1 = rcsb ids
// input flag 2 = patent ids

// output flag 1 = receives output files
// output flag 2 = monitoring messages
// signal 3 = vm is idle timer starts


public class DriverProgram {
	// VM attributes
    private static String image_id = "2aaa58d7-22ff-43b5-ad99-9d15363bfdd2";
    private static String instance_name = "ubuntu";
    String flavor_id = "";
    private static String key_pair_name = "ubuntulatest";
    private static String network_id = "6b6d85cd-6552-457c-ae06-6b7ccf8dac56";
    private static String floating_network_id = "080672ee-8ba4-4f03-8e9e-3887c328ec80";
	
    
    // vacant VMs hashmaps
    static HashMap<VmProperties, Integer> small_vacant_vms = new HashMap<>();
    static HashMap<VmProperties, Integer> medium_vacant_vms = new HashMap<>();
    static HashMap<VmProperties, Integer> large_vacant_vms = new HashMap<>();
    
    // flavor types
    static FlavorProperties small_flavor, medium_flavor, large_flavor;
    
    // flavor map
    static HashMap<Integer, FlavorProperties> flavor_map = new HashMap<>();    
    
    // processed inputs
    // key is protein_id, value is user_id
    static HashMap<String, GetProcesOp> processed_inputs = new HashMap<>();
    
    // small, medium, large input arrays
    static List<String> small = new ArrayList<>();
    static List<String> medium = new ArrayList<>();
    static List<String> large = new ArrayList<>();
    
    public static String funProg(String data) {
    	return data.toLowerCase();
    }
    
    public static String takeinput(String path) {
		String location = path.split(" ")[0];
		String token = path.split(" ")[1];
//		return token+"....";
		// vm flavor details
		small_flavor = new FlavorProperties(10);
		medium_flavor = new FlavorProperties(11);
		large_flavor = new FlavorProperties(12);
		
		flavor_map.put(10, small_flavor);
		flavor_map.put(11, medium_flavor);
		flavor_map.put(12, large_flavor);
		
		// taking input
		while (true) {
			
		    try (Scanner input = new Scanner(System.in)) {
		    	String flag = "myfile1";
				String userInput = location;
				System.out.println(userInput);
			    
				
				File file = new File(userInput);
				
				try {
					// checking whether the given path is valid or not
				    if (file.exists()) {
				    	if (flag.equals(token)) {
				    		// creating user id
				    		String user_id = createUserId();
					    	startExecuting(file, user_id);
				    	} else {
				    		// creating user id
				    		String user_id = createUserId();
				    		// getting the protein id size
				    		long size = fileSize(userInput);
				    		VmProperties vm;
				    		FlavorProperties flavor;
				    		// vm is created based on the protein id size
				    		if (size<=100000){
				    			vm = createVm(flavor_map.get(10), userInput, 1);
				    			flavor = flavor_map.get(10);
							} else if (size<=500000) {
								vm = createVm(flavor_map.get(11), userInput, 1);
								flavor = flavor_map.get(11);
							} else {
								vm = createVm(flavor_map.get(12), userInput, 1);
								flavor = flavor_map.get(12);
							}
				    		// request is being sent to the created vm
				    		ClientProgram.sendRequest(vm, flavor, userInput, user_id, 1, "2");
				    	}
				    	
				    } else {
				    	System.out.println("File not found");
				    }
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		    
		}
	}

	private static void startExecuting(File user_input, String user_id) {	    
		// segregating proteins based on their size
		inputDivision(user_input, user_id);	
		System.out.println("user id: "+user_id);
		System.out.println(processed_inputs);
	}
	
	private static void inputDivision(File file, String user_id) {
		
	    System.out.print("small: ");
	    System.out.println(small);
	    System.out.print("large: ");
	    System.out.println(large);
	    System.out.print("medium: ");
	    System.out.println(medium);
	    
		BufferedReader br = null;
		String s = "";
		
		// buffer reader to read the file
		try {
			br = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			while ((s = br.readLine()) != null) {
				String i = s.trim();
				// if the protein is already being processed it is not sent as a request
				if (!checkAlreadyProcessed(i) ) {
					long size = proteinSize(i);
					if (size<=100000){
						small.add(i);
					} else if (size<=500000) {
						medium.add(i);
					} else {
						large.add(i);
					}
					processed_inputs.put(i, null);
				} else {
					String user = processed_inputs.get(i).getUserId();
					System.out.println("the output of protein id "+i+" is in "+"/home/srm/ew2/BIOMaaS/src/main/webapp/WEB-INF/"+user);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		System.out.print("small: ");
	    System.out.println(small);
	    System.out.print("large: ");
	    System.out.println(large);
	    System.out.print("medium: ");
	    System.out.println(medium);
	    
	    // the active vms are checked in different threads
	    new Thread(new Runnable() {
	    	public void run() {
	    		checkExistingVms(10, small, user_id);
	    		System.out.println(flavor_map.get(10).getVacant_vm_cap());
	    	}
	    }).start();
	    new Thread(new Runnable() {
	    	public void run() {
	    		System.out.println("medium");
	    		checkExistingVms(11, medium, user_id);
	    		System.out.println(flavor_map.get(11).getVacant_vm_cap());
	    	}	
	    }).start();
	    new Thread(new Runnable() {
	    	public void run() {
	    		checkExistingVms(12, large, user_id);
	    		System.out.println(flavor_map.get(12).getVacant_vm_cap());
	    	}	
	    }).start();
	}
	
	// checks whether the input is already processed or not and the VM is alive
	private static boolean checkAlreadyProcessed(String protein_id) {
		if (processed_inputs.get(protein_id)!=null) {
			return true;
		} 
		return false;
	}
	
	
	private static void checkExistingVms(int flavor_id, List<String> id_list, String user_id) {
		System.out.println("checking existing vms");
		FlavorProperties flavor;
		// gets one flavor category based on flavor id
		flavor = flavor_map.get(flavor_id);
		// temp is the vacancy in concerned flavor
		// Ex: there are 10 small vms and are vacant, getVacant_vm_cap() = 30
		// small vms can hold 30 more protein ids in total
		int temp = flavor.getVacant_vm_cap();
		System.out.print("vacancy: ");
		System.out.println(temp);
		System.out.println(flavor.vacant_vms);
		System.out.print("no of vacant vms size: ");
		System.out.println(flavor.vacant_vms.size());
		// only if temp > 0, the input is sent to the active vms
		System.out.println(flavor_id);
		System.out.println(temp);
		if (temp > 0) {
			
			// size is the no of inputs that are yet to be sent
			System.out.println(id_list);
			int size = id_list.size();
			// if vacancy is 30, inputs are 40 (or) inputs are 30
			// 30 protein ids in 40 inputs will be sent, and the vacancy becomes 0
			if (temp <= size) {
				// vacancy is set to 0
				flavor.setVacant_vm_cap(0);
				sendInputToActiveVms(flavor, id_list, user_id);
				System.out.println(flavor.vacant_vms);
				System.out.print("no of vacant vms size: ");
				System.out.println(flavor.vacant_vms.size());
			}  // if vacancy is 30, inputs are 20
			   // 20 inputs will be sent to active vms, vacancy becomes 30-20 (vacancy - input length)
			else {
				flavor.setVacant_vm_cap(temp-size);
				// a sublist of size 20 is sent and the 20 ids are removed from the list
				sendInputToActiveVms(flavor, id_list.subList(0, temp), user_id);
				System.out.println(flavor.vacant_vms);
				System.out.print("no of vacant vms size: ");
				System.out.println(flavor.vacant_vms.size());
				id_list.subList(0, temp).clear();
			}
			// if temp<=0, a new vm is created for input processing
		} else {
			sendInputs(id_list, user_id, flavor);
		}
	}
	
	// sending input to active vms
	private static void sendInputToActiveVms(FlavorProperties flavor, List<String> id_list, String user_id) {
		// iterating over the (small or med or lar) vacant vm
		for (int i=0; i<flavor.vacant_vms.size(); i++) {
			// vacancy is the space left in each vm (max_capacity - input in vm)
			int input_cnt = flavor.vacant_vms.get(i).getInput_cnt();
			int vacancy = flavor.getMaxCapacity()-input_cnt;
			// checks whether id_list contains values or not
			int size = id_list.size();
			
			if (size > 0) {
				// no of ids in list are greater than or equal to vacancy
				// then vacancy number of ids are sent to the vm
				// Ex: vm has 5 vacancy, ids are 19, 5 are sent to the vm
				// those 5 ids are removed from the list
				// input cnt of the vm is set to input_cnt + vacancy
				
				// as the vm is not vacant anymore it is removed from vacant vms list
				// vacant capacity of the (small or med or lar) vm is also decremented
				
				if (vacancy <= size) {
					
					// vm timer is cancelled if it was on and the input is passed to the idle or vacant vm
					if (ClientProgram.vm_timer.get(flavor.vacant_vms.get(i).getFloating_ip()) != null) {
						ClientProgram.vm_timer.get(flavor.vacant_vms.get(i).getFloating_ip()).cancel();
						ClientProgram.vm_timer.remove(flavor.vacant_vms.get(i).getFloating_ip());
					}
					
					List<String> sublist = id_list.subList(0, vacancy);
					String input_string = String.join(" ", sublist);
					VmProperties vm = flavor.vacant_vms.get(i);
					ClientProgram.sendRequest(vm, flavor, input_string, user_id, vacancy, "1");
//					new Thread(new Runnable() {
//						public void run() {
//							addToProcInpMap(vm, user_id, sublist);
//						}
//					});
					id_list.subList(0, vacancy).clear();
//					flavor.vacant_vms.get(i).setInput_cnt(input_cnt + vacancy);
					flavor.vacant_vms.remove(i);
					flavor.setVacant_vm_cap(flavor.getVacant_vm_cap() - vacancy);
				}
				// no of ids in list are less than vacancy, therefore all the left out ids in the list
				 // are sent to the vm
				// ids in the list are deleted
				// input cnt is incremented to (input_cnt + idlist size)
				// vacant capacity of the (small or med or lar) vm is also decremented
				
				else {
					String input_string = String.join(" ", id_list);
					VmProperties vm = flavor.vacant_vms.get(i);
//					new Thread(new Runnable() {
//						public void run() {
//							addToProcInpMap(vm, user_id, id_list);
//						}
//					});
					
					// vm timer is cancelled if it was on and the input is passed to the idle or vacant vm
					if (ClientProgram.vm_timer.get(flavor.vacant_vms.get(i).getFloating_ip()) != null) {
						ClientProgram.vm_timer.get(flavor.vacant_vms.get(i).getFloating_ip()).cancel();
						ClientProgram.vm_timer.remove(flavor.vacant_vms.get(i).getFloating_ip());
					}
					ClientProgram.sendRequest(flavor.vacant_vms.get(i), flavor, input_string, user_id, size, "1");
					id_list.clear();
					flavor.vacant_vms.get(i).setInput_cnt(input_cnt + size);
					flavor.setVacant_vm_cap(flavor.getVacant_vm_cap() - size);
				}
				
			}
			// if there are no protein ids in list for loop breaks
			else {
				break;
			}
			
		}
	} 
	
	private static void sendInputs(List<String> id_list, String user_id, FlavorProperties flavor) {
		new Thread(new Runnable() {
			public void run() {
				int input_len = id_list.size();
				if (input_len != 0) {
					// load balancing of the input is done
					loadBalance(id_list, flavor, user_id);
					System.out.println(flavor_map.get(10).getVacant_vm_cap());
					System.out.println(flavor_map.get(11).getVacant_vm_cap());
					System.out.println(flavor_map.get(12).getVacant_vm_cap());
				}
			}
		}).start();
	}
	
	// load balance
	private static void loadBalance(List<String> id_list, FlavorProperties flavor, String user_id) {
		// calculating no of VMs
		int no_of_vms = (int) Math.ceil((double) id_list.size() / flavor.getMaxCapacity());
		int input_len = id_list.size();
		new Authentication();
		// if no of vms required are 1 all the id list is sent to the VM as input
		if (no_of_vms == 1) {
			String input_string = String.join(" ", id_list);
			System.out.println(input_string);
			new Authentication();
			VmProperties vm = createVm(flavor, input_string, input_len);
			int temp1 = flavor.getVacant_vm_cap();
			flavor.setVacant_vm_cap(flavor.getMaxCapacity()+temp1);
			// id is added to the already processed inputs hashmap
			new Thread(new Runnable() {
				public void run() {
					addToProcInpMap(vm, user_id, id_list);
				}
			});
			vm.setInput_cnt(input_len);
			// request is sent to the created VM
			new Thread(new Runnable() {
				public void run() {
					ClientProgram.sendRequest(vm, flavor, input_string, user_id, input_len, "1");
				}
			}).start();
			int temp = flavor.getVacant_vm_cap();
		    flavor.setVacant_vm_cap(temp-input_len);
		} else {
			int a = input_len / no_of_vms;
			int b = input_len % no_of_vms;
			int i = 0;
			int cnt = 0;
			while (i < input_len) {
				System.out.println(i);
				
				VmProperties vm;
				if (cnt < b && b > 0) {
					List<String> input = id_list.subList(i, i+a+1);
					String input_string = String.join(" ", input);
					System.out.println(input_string);
					new Authentication();
					vm = createVm(flavor, input_string, a+1-i);
					int temp1 = flavor.getVacant_vm_cap();
					flavor.setVacant_vm_cap(flavor.getMaxCapacity()+temp1);
					
					new Thread(new Runnable() {
						public void run() {
							addToProcInpMap(vm, user_id, input);
						}
					});
					vm.setInput_cnt(a+1);
					new Thread(new Runnable() {
						public void run() {
							ClientProgram.sendRequest(vm, flavor, input_string, user_id, input_len, "1");
						}
					}).start();
					
					int temp = flavor.getVacant_vm_cap();
				    flavor.setVacant_vm_cap(temp-a-1);
					i = i+a+1;
				} else {
					if (i+a <= input_len) {
						System.out.println(i);
						System.out.println(i+a);
						List<String> input = id_list.subList(i, i+a);
						String input_string = String.join(" ", input);
						System.out.println(input_string);
						new Authentication();
						vm = createVm(flavor, input_string, a);
						int temp1 = flavor.getVacant_vm_cap();
						flavor.setVacant_vm_cap(flavor.getMaxCapacity()+temp1);
						new Thread(new Runnable() {
							public void run() {
								addToProcInpMap(vm, user_id, input);
							}
						});
						vm.setInput_cnt(a);
						int temp = flavor.getVacant_vm_cap();
					    flavor.setVacant_vm_cap(temp-a);
					    new Thread(new Runnable() {
							public void run() {
								ClientProgram.sendRequest(vm, flavor, input_string, user_id, input_len, "1");
							}
						}).start();
						i = i+a;
					} else {
						List<String> input = id_list.subList(i, input_len);
						String input_string = String.join(" ", input);
						System.out.println(input_string);
						new Authentication();
						vm = createVm(flavor, input_string, input_len - i);
						int temp1 = flavor.getVacant_vm_cap();
						flavor.setVacant_vm_cap(flavor.getMaxCapacity()+temp1);
						new Thread(new Runnable() {
							public void run() {
								addToProcInpMap(vm, user_id, input);
							}
						});
						vm.setInput_cnt(input_len-i);
						new Thread(new Runnable() {
							public void run() {
								ClientProgram.sendRequest(vm, flavor, input_string, user_id, input_len, "1");
							}
						}).start();
						i = input_len;
						break;
					}
					
				}
				if (input_len < flavor.getMaxCapacity()) {
					flavor.addVacantVm(vm);
				}
				cnt++;
				System.out.print("cnt");
				System.out.println(cnt);
				System.out.print("i");
				System.out.println(i);
			}
			
		}
	}
	
	// adding the protein IDs after being processed to the already processed inputs hash map 
	// for later retrieval if the input from the user repeats. 
	private static void addToProcInpMap(VmProperties vm, String user_id, List<String> protein_id) {
		for (int i=0; i<protein_id.size(); i++) {
			processed_inputs.put(protein_id.get(i), null);
			processed_inputs.get(protein_id.get(i)).setUserId(user_id);
		}
	}
	
	private static VmProperties createVm(FlavorProperties flavor, String input_string, int input_len) {
		new Authentication();
		// creating VM
		VmProperties vm = new VmProperties();
		vm.setFlavor_id(flavor.getFlavor());
		vm.setInput_cnt(input_len);
		vm.setFloating_network_id(floating_network_id);
		vm.setNetwork_id(network_id);
		vm.setImage_id(image_id);
		vm.setKeypair(key_pair_name);
		
		VM create = new VM(Authentication.os, instance_name);
		create.createVm(vm);
		return vm;
	}
	
	
	
	
	// calculates protein downloadable file size
	private static long proteinSize(String protein_id) {
		URL url=null;
		try {
			url = new URL("https://files.rcsb.org/download/"+protein_id+".cif.gz");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URLConnection urlConnection= null;
		try {
			urlConnection = url.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			urlConnection.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int file_size = urlConnection.getContentLength();
		// returns file size in bytes
		return file_size;
	}
	
	
	// creates user id
	private static String createUserId() {
		UUID idOne = UUID.randomUUID();
		String usid = ""+idOne;
		int uid = usid.hashCode();
		String filterstring = ""+uid;
		usid = filterstring.replaceAll("-", "");
		return usid;
	}	
	
	// calculating the file size
	private static long fileSize(String filepath) {
		Path file = Paths.get(filepath);
		long count=0;
		try {
			count = Files.lines(file).count();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}


}

