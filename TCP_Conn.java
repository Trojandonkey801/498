import java.net.*;
import java.io.*;
import java.util.*;
public class TCP_Conn extends Thread{
	static ServerSocket ss;
	static int count;
	static int tcp_port = 29999;
	private Socket conn_socket;
	public TCP_Conn(Socket toassign){
		conn_socket = toassign;
		System.out.println("assigned");
	}
	public void run(Socket clientSide){
		try{
			DataInputStream inStream  = new DataInputStream(clientSide.getInputStream());
			DataOutputStream outStream = new DataOutputStream(clientSide.getOutputStream());
			int counter;
			byte[] buffer = new byte[4096];
			while((counter = inStream.read(buffer)) > 0){
			}
			String toprint = new String(buffer,"UTF-8");
			System.out.println(toprint);
		}catch(IOException e){
			System.out.println(e);
		}
	}
	public static void main(String[] args) {
		try{
			ss = new ServerSocket(tcp_port);
			System.out.println("Listening on " + ss.getLocalPort());

			while (true) {
				Socket sock = ss.accept();
                Thread t = new TCP_Conn(sock); 
			}
		}catch(IOException e){
			System.out.println(e);
		}
	}
}
