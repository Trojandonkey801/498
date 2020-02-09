import java.net.*;
import java.io.*;
import java.util.*;

public class manager extends Thread{
	public manager(){
	}
	public void AgentMonitor(){
	}
	public void BeaconListener(){
	}
	/**
	 * Open this specific port to expose to TCP
	 *
	 */
	public void openTCP(int port){
		try{
			ServerSocket ss = new ServerSocket(port);
		}catch(IOException e){
			System.out.println(e);
		}
	}
	public static void main(String[] args) {
	}
}

