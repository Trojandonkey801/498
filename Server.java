
// Java implementation of  Server side 
// It contains two classes : Server and TCP_Conn 
// Save file as Server.java 
  
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*; 
  
// Server class 
public class Server  
{ 
    public static void main(String[] args) throws IOException  
    { 
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
                s.close(); 
                e.printStackTrace(); 
            } 
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
		this.s = s; 
		try{
			DataInputS = new DataInputStream(s.getInputStream()); 
			DataOutputS = new DataOutputStream(s.getOutputStream()); 
		}catch(IOException e){
			System.out.println(e);
		}
	} 

	@Override
	public void run()  
	{ 
		int counter;
		int bufsize = 0;
		byte[] buffer = new byte[4096];
		try{
			while((counter = DataInputS.read(buffer)) > 0){
				String toprint = new String(buffer,"UTF-8");
				System.out.println(toprint);
				if(toprint.contains("end")){
					System.out.println("broken");
					break;
				}
			}
			s.close();
			System.out.println("asdfasdf");
		}catch(IOException e){
			System.out.println(e);
		}
	} 
} 

