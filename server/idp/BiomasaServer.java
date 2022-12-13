package idp;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.Thread;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.lang.Thread;

// python file for the molecular analysis is invoked
public class BiomasaServer extends Thread{
           private static int REQUEST_CNT = 0;

           private Socket socket;
           private String message;
           private ObjectInputStream ois;
           private String flag;
           private String user_id;

           public BiomasaServer(Socket s, String user_id, String message, ObjectInputStream ois, String flag) {
               this.socket = s;
               this.message = message;
               this.ois = ois;
               this.flag = flag;
               this.user_id = user_id;
           }

           public void run() {
           if (flag.equals("1")){
             try {
                  System.out.println("New client connected..");
					// REQUEST CNT is the value that the no of clients, server is dealing with.
                  REQUEST_CNT++;

                      String foldername=message.split(" ")[0];
                      int input_len = Integer.parseInt(message.split(" ")[1]);
                      String filename=foldername+".txt";
                      File dir = new File("/home/ubuntu/"+foldername);
						// a folder to store the output files is created
                      Process folder_create = Runtime.getRuntime().exec("mkdir "+foldername);
                      System.out.println("folder created");
						// a file with protein ids in it is created
                      Process file_create = Runtime.getRuntime().exec("touch /home/ubuntu/"+foldername+"/"+filename);
                      System.out.println("file created");
                      Path fileName1 = Path.of("/home/ubuntu/"+foldername+"/"+filename);
                      Files.writeString(fileName1, message);
                      System.out.println("started executing "+foldername);
						// protein analysis python file is invoked
                      execute_idp_process(socket, foldername, filename, dir, flag, input_len);
                      sendFiles(dir, socket, ois, flag);

               } catch(IOException e11) {
                 e11.printStackTrace();
               }
            } else {
                File dir = new File("/home/ubuntu/"+user_id);
                execute_idp_process(socket, user_id, user_id, dir, flag, 1);
                sendFiles(dir, socket, ois, flag);
            }
          }

    private static void sendFiles(File dir, Socket socket, ObjectInputStream ois, String flag) {
             // sending files to the client
            System.out.println("started sending");
            ObjectOutputStream oos = null;
            DataOutputStream dos = null;
            DataInputStream dis = null;
            try {
                oos = new ObjectOutputStream(socket.getOutputStream());
            } catch(IOException ei) {
                ei.printStackTrace();
            }

            try{
                File[] directoryListing = dir.listFiles();
                if (directoryListing != null) {
                    for (File child: directoryListing) {
                       int bytes=0;
                       oos.writeObject(child.getName()+" "+String.valueOf((int)child.length()));
                       dos = new DataOutputStream(socket.getOutputStream());
                       FileInputStream fis = new FileInputStream(child);
                       dos.writeLong(child.length());
                       byte[] buffer = new byte[(int)child.length()];
                       while ((bytes = fis.read(buffer)) != -1 ){
                           dos.write(buffer, 0, bytes);
                           dos.flush();
                       }
                       System.out.println("sent "+child.getName()
                       String ack = (String)ois.readObject();
                       System.out.println("client acknowledged: "+ack);
                    }
                    REQUEST_CNT--;
                    if (REQUEST_CNT == 0){
                        oos.writeObject("3");
                  } else {
                        oos.writeObject("0");
                    }
                }
            } catch (Exception e9){
                 e9.printStackTrace();
            }

    }

    private static void execute_idp_process(Socket socket, String foldername, String filename, File dir, String flag, int input_len) {
               // monitoring
               ObjectOutputStream oos = null;
               try {
                    oos = new ObjectOutputStream(socket.getOutputStream());
               } catch(IOException ei) {
                    ei.printStackTrace();
               }
               FileWatcher watcher = new FileWatcher(socket, "/home/ubuntu/"+foldername, input_len);
               new Thread(new Runnable(){
                   public void run() {
                       watcher.watch();
                   }
               });
       // executing idp project.py
               System.out.println("executing idp");
               Process idp_process = null;
                   try {
                       idp_process = Runtime.getRuntime().exec("python3 /home/ubuntu/idp_project.py "+"/home/ubuntu/"+foldername+"/"+filename+" "+flag, null, dir);
                   } catch(IOException e3) {
                       e3.printStackTrace();
                   }
                   BufferedReader stdError = new BufferedReader(new InputStreamReader(idp_process.getErrorStream()));
                   BufferedReader stdInput = new BufferedReader(new InputStreamReader(idp_process.getInputStream()));
                   String s = null;
                   String e = null;
                   try {
                       while ((s=stdInput.readLine()) != null ) {
                           System.out.println(s);
                       }
                   } catch(IOException e4) {
                      e4.printStackTrace();
                   }
                   try {
                       while ((e=stdError.readLine()) != null) {
                           System.out.println(e);
                       }
                   } catch(IOException e5) {
                     e5.printStackTrace();
                   }
              System.out.println("files created");

    }

}

