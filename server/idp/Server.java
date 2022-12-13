package idp;

import idp.BiomasaServer;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.lang.Thread;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

// server establishes connection with the client after accepting the client's request
public class Server {
    //static ServerSocket variable
    private static ServerSocket server;
    //socket server port on which it will listen
    private static int port = 9998;

    public static void main(String args[]) throws IOException, ClassNotFoundException {
           System.out.println("hello");
           server = new ServerSocket(port);
           Socket socket = server.accept();
           ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
           String flag = "";
           String input_string = "";
           String user_id = "";
           DataInputStream dis = null;
           try {
              dis = new DataInputStream(socket.getInputStream());
           } catch(IOException e2) {
              e2.printStackTrace();
           }
           System.out.println("hello");
           oos.writeObject("send");
           System.out.println("Waiting for the client...");
           ObjectInputStream ois = null;
           while (true) {
                      try {
                           ois = new ObjectInputStream(socket.getInputStream());
                      } catch (IOException e){
                           e.printStackTrace();
                      }
                      //convert ObjectInputStream object to String
                      try {
                          flag = (String) ois.readObject();
                      } catch(IOException e10){
                          e10.printStackTrace();
                      }
                      System.out.println(flag);
                      // if flag is 1 normal rcsb ids
                      // if flag is 2 patent protein strucuture input is file
                      if (flag.equals("1")) {
                          try {
                              input_string = (String) ois.readObject();
                          } catch(IOException e10){
                              e10.printStackTrace();
                          }
                          System.out.println(input_string);
                      } else {
                         // taking user id as input
                         try {
                              user_id = (String) ois.readObject();
                         } catch(IOException e11){
                              e11.printStackTrace();
                         }
                         Process folder_create = Runtime.getRuntime().exec("mkdir "+user_id);
                         System.out.println("folder created");

                         File f = new File("/home/ubuntu/"+user_id+"/"+user_id);
                         int bytes = 0;
                         FileOutputStream fos = null;
                         try {
                             fos = new FileOutputStream(f);
                         } catch(FileNotFoundException e12) {
                             e12.printStackTrace();
                         }
                         // receiving file size
                         long size = 0;
                         try {
                             size = dis.readLong();
                         } catch(IOException e13) {
                             e13.printStackTrace();
                         }
                         byte[] buffer = new byte[4*1024];
                         try {
                            while (size > 0 && (bytes = dis.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                               try {
                                   fos.write(buffer, 0, bytes);
                                   size-=bytes;
                              } catch(IOException e) {
                                   e.printStackTrace();
                               }
                            }
                            System.out.println("received "+user_id);
                         } catch (IOException e14) {
                           e14.printStackTrace();
                         }
                      }
				// input is sent for processing of the protein id
              BiomasaServer client_obj = new BiomasaServer(socket, user_id, input_string, ois, flag);
              client_obj.start();
           }
    }
}
