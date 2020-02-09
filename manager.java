import java.net.*;
import java.io.*;
import java.util.*;
public class manager extends Thread{
	public static int SIZE = 4192;
	public static int PORT = 30000;
	public int to_run;
	public DatagramSocket in_socket;
	public ServerSocket ss;
	ArrayList<agent> entities = new ArrayList<agent>();
	ArrayList<Integer> ports = new ArrayList<Integer>();
	/** 
	 * Constructor for the manager class
	 * This constructor instantiates the Datagram Socket
	 *
	 */
	public manager(){
		try{
			in_socket = new DatagramSocket(PORT);
		}catch(SocketException e){
			System.out.println(e);
		}
		Thread t1 = new Thread(){
			public void run(){
				BeaconListener();
			} };
		Thread t2 = new Thread(){
			public void run(){
				AgentMonitor();
			}
		};
		t1.start();
		t2.start();
	}
	/**
	 * AgentMonitor to listen to check if agents are dead
	 *
	 */
	public void AgentMonitor(){
		for (int i = 0; i < entities.size(); i++) {
			if(entities.get(i).timeOver())
				handleTimeOver();
		}
	}
	public void handleTimeOver(){}
	/**
	 * Function to keep a UDP port open to listen to incoming UDP 
	 *
	 */
	public void BeaconListener(){
		byte[] buffer = new byte[24];
		DatagramPacket temp_packet = new DatagramPacket(buffer, buffer.length);
		while(true){
			try{
				in_socket.receive(temp_packet);
				char[] temp_char = copy_buf(buffer);
				agent temp = handler_buf(temp_char);
				temp.printData();
				Thread t3 = new Thread(){
					public void run(){
						start_TCP(temp.cmdPort);
					}
				};
				t3.start();
			}catch(IOException e){
				System.out.println(e);
				System.out.println("Beacon");
			}
		}
	}

	public void start_TCP(int port){
		try{
			ServerSocket ss = new ServerSocket(port);
			while (true)  
			{ 
				Socket s = null; 
				try 
				{ 
					s = ss.accept(); 
					TCP_Conn temp = new	TCP_Conn(s); 
					temp.startstuff(); 
				} 
				catch (Exception e){ 
					e.printStackTrace(); 
				System.out.println("TCP");
				} 
			} 
		}catch(IOException e){
			System.out.println(e);
				System.out.println("TCP");
		}
	}

	/**
	 * Handle a new agent that has come in
	 *
	 */
	public void addAgent(agent A){
		for (int i = 0; i < entities.size(); i++) {
			if(A.ID == entities.get(i).ID){ // This agent already exists but the time is different
				if(A.startUpTime != entities.get(i).startUpTime){
					entities.get(i).upDateTime(A.startUpTime);
					System.out.println("This entity has started again somehow");
				}
				else{
				}
			}
			else
				entities.add(A);
		}
	}

	/**
	 * Provide a copy of a buffer
	 *
	 */
	public char[] copy_buf(byte[] tocopy){
		char[] toreturn = new char[24];
		for (int i = 0; i < tocopy.length; i++) {
			toreturn[i] = (char)tocopy[i];
		}
		return toreturn;
	}

	// To send ID | startUpTime | timeInterval | IP | cmdPort
	//Assume each byte is one char.
	//Use standard java encode/decode to convert to ascii
	public agent handler_buf(char[] toParse){
		int 	ID = 0;                     // randomly generated during startup
		int 	startUpTime = 0; // the time when the client starts
		int     timeInterval = 0; // the time period that this beacon will be repeated
		char[] 	IP = new char[4];	            // the IP address of this client
		int		cmdPort = 0;       // the client listens to this port for manager commands
		for (int i = 0; i < 5 ; i++) {
			char construct[] = new char[4];
			for (int k = 0; k < 4; k++) {
				construct[k] = toParse[i*4 + k];
			}
			switch(i){
				case 0: 
					cmdPort = toInteger32(construct);
					break;
				case 1: 
					for (int k = 0; k < 4; k++) {
						IP[k] = toParse[k+4];
					} break;
				case 2: 
					timeInterval = toInteger32(construct);
					break;
				case 3: 
					startUpTime = toInteger32(construct);
					break;
				case 4: 
					ID = toInteger32(construct);
					break;
			}
		}
		agent toreturn = new agent(ID,startUpTime,timeInterval,IP,cmdPort);
		return toreturn;
	}
	public static void main(String[] args) throws SocketException, UnknownHostException{
		manager torun = new manager();
		//torun.in_socket = new DatagramSocket(PORT);
		//torun.run();
	}
	int toInteger32(char[] bytes)
	{
		int tmp = (bytes[3] << 20) + 
			(bytes[2] << 16) + 
			(bytes[1] << 8) + 
			bytes[0];
		return tmp;
	}
	//------------------------------------------------- 
	// TCP related methods and classes
	//-------------------------------------------------- 
	public void CmdAgent(){
		try{
			ServerSocket ss = new ServerSocket(29999);
			while (true)  
			{ 
				Socket s = null; 
				try 
				{ 
					s = ss.accept(); 
					Thread t = new TCP_Conn(s); 
					t.start(); 
				} 
				catch (Exception e){ 
					e.printStackTrace(); 
				System.out.println("cmd");
				} 
			} 
		}catch(IOException e){
			System.out.println(e);
				System.out.println("cmd");
		}
	}
}

class TCP_Conn extends Thread  
{ 
	DataInputStream DataInputS; 
	DataOutputStream DataOutputS; 
	final Socket s; 
	// Constructor 
	public TCP_Conn(Socket s)  
	{ 
		System.out.println("here");
		this.s = s; 
		try{
			DataInputS = new DataInputStream(s.getInputStream()); 
			DataOutputS = new DataOutputStream(s.getOutputStream());
		}catch(IOException e){
			System.out.println(e);
		}
	} 

	ArrayList<String> tosend = new ArrayList<String>();
	public void startstuff()  
	{ 
		tosend.add("Time_OS_end");
		int counter;
		int bufsize = 0;
		String command = "OS";
		byte[] buffer = new byte[4096];
		try{
			for (int i = 0; i < tosend.size(); i++) {
				buffer = new byte[4096];
				byte[] buf = tosend.get(i).getBytes();
				DataOutputS.write(buf, 0, buf.length);
				DataOutputS.flush();
			}
			while((counter = DataInputS.read(buffer)) > 0){
				String toprint = new String(buffer,"UTF-8");
				System.out.println(toprint);
				if(toprint.contains("end")){
					System.out.println("broken");
					break;
				}
			}
			s.close();
		}catch(IOException e){
			System.out.println(e);
		}
	} 
	static private byte[] toBytes(int i)
	{
		byte[] result = new byte[4];
		result[0] = (byte) (i >> 24);
		result[1] = (byte) (i >> 16);
		result[2] = (byte) (i >> 8);
		result[3] = (byte) (i /*>> 0*/);

		return result;
	}
} 

class agent{
	int CurrentTime;
	int ID;                     // randomly generated during startup
	int lastConnected; // The last time this was heard from
	int startUpTime; // the time when the client starts
	int timeInterval; // the time period that this beacon will be repeated
	char[] IP;	            // the IP address of this client
	int	cmdPort;       // the client listens to this port for manager commands
	public agent(int ID,int startUpTime, int timeInterval, char[] IP,int cmdPort){
		lastConnected = CurrentTime;
		this.ID = ID;
		this.startUpTime = startUpTime;
		this.timeInterval = timeInterval;
		this.IP = IP;
		this.cmdPort = cmdPort;
	}
	public boolean timeOver(){
		int currentTime = 5;
		if(currentTime - startUpTime > timeInterval)
			return true;
		return false;
	}
	public void upDateTime(int Time){
		startUpTime = Time;
	}
	public void printData(){
		System.out.println("ID is " + ID);
		System.out.println("startUpTime is " + startUpTime);
		System.out.println("timeInterval is " + timeInterval);
		System.out.println("cmdPort is " + cmdPort);
		System.out.print("IP address is ");
		for (int i = 0; i < 4; i++) {
			System.out.print(IP[i]-'0'+'0');
			System.out.print(".");
		}
		System.out.println();
		System.out.println("--------------");
	}
}
