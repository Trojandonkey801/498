import java.net.*;
import java.io.*;
import java.util.*;
public class manager{
	public static int SIZE = 4192;
	public static int PORT = 30000;
	public DatagramSocket in_socket;
	ArrayList<agent> entities = new ArrayList<agent>();

	public void run(){
		byte[] buffer = new byte[20];
		DatagramPacket temp_packet = new DatagramPacket(buffer, buffer.length);
		while(true){
			try{
				in_socket.receive(temp_packet);
				char[] temp = copy_buf(temp_packet.getData());
				agent ttemp = handler_buf(temp);
			}catch(IOException e){
				System.out.println(e);
			}
		}
	}

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
					ID = toInteger32(construct);
					break;
				case 1: 
					IP = construct;
					break;
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
		entities.add(toreturn);
		return toreturn;
	}
	public static void main(String[] args) throws SocketException, UnknownHostException{
		manager torun = new manager();
		torun.in_socket = new DatagramSocket(PORT);
		torun.run();
	}
	int toInteger32(char[] bytes)
	{
		int tmp = (bytes[3] << 20) + 
			(bytes[2] << 16) + 
			(bytes[1] << 8) + 
			bytes[0];
		return tmp;
	}
	private class agent{
		int 	ID;                     // randomly generated during startup
		int 	startUpTime; // the time when the client starts
		int     timeInterval; // the time period that this beacon will be repeated
		char[]  	IP;	            // the IP address of this client
		int		cmdPort;       // the client listens to this port for manager commands
		public agent(int ID,int startUpTime, int timeInterval, char[] IP,int cmdPort){
			this.ID = ID;
			this.startUpTime = startUpTime;
			this.timeInterval = timeInterval;
			this.IP = IP;
			this.cmdPort = cmdPort;
		}
		public void printData(){
			System.out.println("ID is" + ID);
			System.out.println("startUpTime is" + startUpTime);
			System.out.println("timeInterval is" + timeInterval);
			System.out.println("cmdPort is" + cmdPort);
		}
	}
	private class TCP_Conn{
		private ServerSocket ss;
		public TCP_Conn(int port){
			try {
				ss = new ServerSocket(port);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void run() {
			while (true) {
				try {
					Socket clientSock = ss.accept();
					System.out.println("accepted");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		public void connections(Socket clientSide){
			DataInputStream inStream  = new DataInputStream(clientSide.getInputStream());
			DataOutputStream outStream = new DataOutputStream(clientSide.getOutputStream());
			byte[] buffer = new byte[4096];
		}
	}
	private static HashMap<Thread,Integer> map = new HashMap<Thread,Integer>();
	static int count;
	void connection(){
		//This newserver runs on port 21839 by default
		//If args[0] is given it replaces the defailt
		int port = 29999;
		ServerSocket ssock = new ServerSocket(port);
		System.out.println("Listening on " + ssock.getLocalPort());

		while (true) {
			Socket sock = ssock.accept();
			count++;
			System.out.println("Connected");
			Thread t = new Thread(new FileServer(sock));//.start();
			map.put(t,Integer.valueOf(count));
			t.start();
		}
	}
}
