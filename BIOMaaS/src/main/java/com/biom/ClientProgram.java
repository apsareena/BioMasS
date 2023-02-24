package com.biom;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;



public class ClientProgram {
	static HashMap<String, Timer> vm_timer = new HashMap<>();
	
	public static final String file = "/home/srm/ew2/BIOMaaS/src/main/webapp/WEB-INF/";
	public final static int FILE_SIZE = 1024*1024;
	DataInputStream dis = null;
	static DataOutputStream dos = null;
	
	private static final String START = "START";	
	
	public static void sendRequest(VmProperties vm, FlavorProperties flavor, String input_string, String user_id, int input_len, String flag) {
		Socket socket = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		System.out.println(vm.getFloating_ip());
		try {
			socket = new Socket(vm.getFloating_ip(), 9998);
		} catch(UnknownHostException e4) {
			e4.printStackTrace();
		} catch(IOException e4) {
			e4.printStackTrace();
		}
		
		//waits for the signal that vm is ready to take input
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//write to socket using ObjectOutputStream
        try {
			oos = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e3) {
			e3.printStackTrace();
		}
        
        String msg = "";
        try {
        	msg = (String) ois.readObject();
        } catch(Exception e5) {
        	e5.printStackTrace();
        }
        if (msg.equals("send")) {
        	// creating folders to store the user output files
            System.out.println("Sending request to socket server "+vm.getFloating_ip()+" "+input_string);
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("sh", "-c", "mkdir "+file+user_id);
    		try {
    			Process process = builder.start();
    		} catch (IOException e2) {
    			// TODO Auto-generated catch block
    			e2.printStackTrace();
    		}
    		// sending the user input   		
    		if (flag.equals("1")) {
    			 try {
//    	    			oos.writeObject(user_id+" 1 3H15");
    	            	oos.writeObject(flag);
    	            	oos.writeObject(user_id+" "+input_len+" "+input_string);
    	    		} catch (IOException e3) {
    	    			// TODO Auto-generated catch block
    	    			e3.printStackTrace();
    	    		}
    		} else if (flag.equals("2")) {
    			try {
//	    			oos.writeObject(user_id+" 1 3H15");
	            	oos.writeObject(flag);
	            	oos.writeObject(user_id);
	            	int bytes=0;
                    dos = new DataOutputStream(socket.getOutputStream());
                    File file = new File(input_string);
                    dos.writeLong(file.length());
                    try (FileInputStream fis = new FileInputStream(file)) {
						byte[] buffer = new byte[(int)file.length()];
						while ((bytes = fis.read(buffer)) != -1 ){
						    dos.write(buffer, 0, bytes);
						    dos.flush();
						}
					}
                    System.out.println("sent "+file.getName());

	    		} catch (IOException e3) {
	    			// TODO Auto-generated catch block
	    			e3.printStackTrace();
	    		}
    		}
        }   
        
        while (true) {
        	// checking the message type (monitor or output)
        	// if msg = 1 (output)
        	// if msg = 2 (monitor)
        	// else (break the loop)
        	try {
            	try {
    				msg = (String) ois.readObject();
    			} catch (ClassNotFoundException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
            } catch(IOException e10) {
            	e10.printStackTrace();
            }
        	
        	if (msg.equals("1")) {
        		for (int i=0; i<input_len*2+1; i++) {
                	String message="";
        			try {
              			message = (String) ois.readObject();
//              			System.out.println(message);
        			} catch (ClassNotFoundException | IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
        	    	String filename = message.split(" ")[0];
                	receiveFiles(socket, filename, user_id);
                	try {
        				oos.writeObject("received "+filename);
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
                	
                	String idle_vm = "";
            		try {
            			idle_vm = (String) ois.readObject();
            		} catch (ClassNotFoundException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		} catch (IOException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}
                    if (idle_vm.equals("3")) {
                    	startTimer(vm, START, flavor);   
                    } else {
                    	startTimer(vm, "0", flavor);
                    }
                    
                    int capacity = flavor.getVacant_vm_cap();
                    vm.setInput_cnt(capacity - input_len);           	
             
                	String signal = "";
                	try {
                		signal = (String) ois.readObject();
                	} catch (ClassNotFoundException | IOException e) {
                		e.printStackTrace();
                	}
                	if (signal.equals("3")) {
                		// break the loop
                		// start the timer 
                	} else {
                		// do nothing
                	}
                }
        	} else if (msg.equals("2")) {
        		String monitor = "";
        		try {
        			monitor = (String) ois.readObject();
        		} catch(ClassNotFoundException | IOException e) {
        			e.printStackTrace();
        		}
        		System.out.println(monitor);
        	} 
        }    
	}
	

	
private static void startTimer(VmProperties vm, String timer_tag, FlavorProperties flavor) {
	Timer t = new Timer();
	if (timer_tag.equals(START)) {
		TimerTask tt = new TimerTask() {
			public void run() {
				vm_timer.get(vm.getFloating_ip()).cancel();
			    vm_timer.remove(vm.getFloating_ip());
			    flavor.vacant_vms.remove(vm);
			    System.out.println(flavor.vacant_vms);
			    VM.deleteVM(vm);
			};
		};
		t.schedule(tt, 900000);
		vm_timer.put(vm.getFloating_ip(), t);
	} else {
		vm_timer.get(vm.getFloating_ip()).cancel();
	}
}

	
private static void receiveFiles(Socket socket, String filename, String user_id) {
	DataInputStream dis = null;
	DataOutputStream dos = null;
	
	try {
		dis = new DataInputStream(socket.getInputStream());
	} catch (IOException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	try {
		dos = new DataOutputStream(socket.getOutputStream());
	} catch (IOException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	// System.out.println("received file of size "+file_size);
	File f = new File(file+user_id+"/"+filename);
	int bytes = 0;
	FileOutputStream fos = null;
	try {
		fos = new FileOutputStream(f);
	} catch (FileNotFoundException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	long size=0;
	try {
		size = dis.readLong();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	byte[] buffer = new byte[4*1024];
	try {
		while (size>0 && (bytes = dis.read(buffer, 0, (int)Math.min(buffer.length,  size))) != -1) {
			try {
				fos.write(buffer, 0, bytes);
				size-=bytes;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("received "+filename);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
}

}