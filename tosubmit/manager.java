import java.net.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
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
	 * It additionally creates threads of the required functions to run
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
	 * It periodically checks each agent to see if they are dead
	 */
	public void AgentMonitor(){
		for (int i = 0; i < entities.size(); i++) {
			if(entities.get(i).timeOver())
				handleTimeOver(entities.get(i));
		}
	}
	/**
	 * A function to handle an agent dying.
	 * The user is notified, as well as removing the agent
	 */
	public void handleTimeOver(agent a){
		System.out.println("A UDP connection has been lost.");
		System.out.println("Agent " + a.ID + "will be deleted" );
		for (int i = 0; i < entities.size(); i++) {
			if(entities.get(i).ID == a.ID)
				entities.remove(i);
		}
	}

	/**
	 * Function to keep a UDP port open to listen to incoming UDP 
	 * It prints the incoming beacon.
	 * It also starts a new instance of TCP once a new agent is detected
	 */
	public void BeaconListener(){
		byte[] buffer = new byte[20];
		DatagramPacket temp_packet = new DatagramPacket(buffer, buffer.length);
		while(true){
			try{
				in_socket.receive(temp_packet);
				char[] temp_char = copy_buf(buffer);
				agent temp = handler_buf(temp_char);
				temp.printData();
				//This code exists to ensure that upon receiving a new UDP,
				//If this port is already used, it doest not try to reconnect
				if(!ports.contains(temp.cmdPort)){
					ports.add(temp.cmdPort);
					Thread t3 = new Thread(){
						public void run(){
							start_TCP(temp.cmdPort);
						}
					};
					t3.start();
				}
			}catch(IOException e){
				System.out.println(e);
			}
		}
	}
	/**
	 * Function to start the TCP with given port
	 * It opens a new Serversocket with the given port
	 * Once the agent that sent the cmdPort tries connecting, it accepts
	 * Once it has concluded, it prints out ot notify the user that it has beed
	 * disconnected
	 *
	 */
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
				} 
			} 
		}catch(IOException e){
			System.out.println(e);
		} }

	/**
	 * Handle a new agent that has come in
	 * If the new agent has the same ID, we notify that 
	 * an agent has disconnected and reconnected
	 */
	public void addAgent(agent A){
		for (int i = 0; i < entities.size(); i++) {
			if(A.ID == entities.get(i).ID){ // This agent already exists but the time is different
				if(A.startUpTime != entities.get(i).startUpTime){
					entities.get(i).upDateTime(A.startUpTime);
					System.out.println("This entity has started again somehow");
				}
				else{
					System.out.println("The beacon is checking in");
				}
			}
			else// New entitiy has appeared
				entities.add(A);
		}
	}

	/**
	 * Provide a copy of a buffer
	 * It converts the bytes into chars on the way.
	 */
	public char[] copy_buf(byte[] tocopy){
		char[] toreturn = new char[20];
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
		} agent toreturn = new agent(ID,startUpTime,timeInterval,IP,cmdPort);
		return toreturn;
	}
	public static void main(String[] args) throws SocketException, UnknownHostException{
		manager torun = new manager();
	}
	int toInteger32(char[] bytes)
	{
		int tmp = (bytes[3] << 20) + 
			(bytes[2] << 16) + 
			(bytes[1] << 8) + 
			bytes[0];
		return tmp;
	}
}

/**
 * A seperate class
 * is created to handle each tcp connection
 *
 */
class TCP_Conn extends Thread  
{ 
	//Data input and output streams
	DataInputStream DataInputS; 
	DataOutputStream DataOutputS; 
	final Socket s; 

	/**
	 * The constructor for TCP_Conn
	 * This takes in the socket that was accepted in the main tcp loop
	 */
	public TCP_Conn(Socket s)  
	{ 
		this.s = s; 
		try{
			DataInputS = new DataInputStream(s.getInputStream()); 
			DataOutputS = new DataOutputStream(s.getOutputStream());
		}catch(IOException e){
			System.out.println(e);
		}
	} 

	/**
	 * This function currently sends the Command to the agent.
	 * The command is specified by what command is included in the string.
	 * If the string contains "Time", it returns the local time
	 * If the string contains "OS", it returns the operating system
	 * If the string contains "end", it ends the TCP connection
	 *
	 */
	ArrayList<String> tosend = new ArrayList<String>();
	public void startstuff()  
	{ 
		tosend.add("Time_OS_end");
		int counter;
		int bufsize = 0;
		String command = "OS";
		byte[] buffer = new byte[4096];
		try{
			/**
			 * This is responsible for sending the different commands
			 */
			for (int i = 0; i < tosend.size(); i++) {
				buffer = new byte[4096];
				byte[] buf = tosend.get(i).getBytes();
				DataOutputS.write(buf, 0, buf.length);
				DataOutputS.flush();
			}
			/**
			 * Afterwards, this TCP connection listens for return values
			 *
			 */
			while((counter = DataInputS.read(buffer)) > 0){
				String toprint = new String(buffer,"UTF-8");
				System.out.println(toprint);
			}
			//After the TCP connection disappears, the user is notified
			System.out.println("TCP connection has been disconnected");
			s.close();
		}catch(IOException e){
			System.out.println(e);
		}
	} 
} 

/**
 * A seperate class agent for instantiating agent objects
 */
class agent{
	int CurrentTime;
	int ID;                     // randomly generated during startup
	int lastConnected; // The last time this was heard from
	int startUpTime; // the time when the client starts
	int timeInterval; // the time period that this beacon will be repeated
	char[] IP;	            // the IP address of this client
	int	cmdPort;       // the client listens to this port for manager commands
	//Constructor
	public agent(int ID,int startUpTime, int timeInterval, char[] IP,int cmdPort){
		lastConnected = CurrentTime;
		this.ID = ID;
		this.startUpTime = startUpTime;
		this.timeInterval = timeInterval;
		this.IP = IP;
		this.cmdPort = cmdPort;
	}
	/**
	 * This function checks if the agent has crossed its notification time
	 *
	 */
	public boolean timeOver(){
		long currentTime = Instant.now().toEpochMilli();
		if(currentTime - startUpTime > timeInterval * 2)
			return true;
		return false;
	}
	/**
	 * This updates the last time this agent was connected
	 */
	public void upDateTime(int Time){
		lastConnected = Time;
	}
	/**
	 * Helper function for printing out the values of the various things.
	 *
	 */
	public void printData(){
		System.out.println("Printing UDP DATA---------");
		System.out.println("ID is " + ID);
		System.out.println("Port number is" + cmdPort);
		System.out.print("IP address is ");
		for (int i = 0; i < 4; i++) {
			System.out.print(IP[i]-'0'+'0');
			System.out.print(".");
		}
		System.out.println();
		System.out.println("--------------");
	}
}
