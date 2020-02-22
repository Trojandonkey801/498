import java.net.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class GetLocalTime extends executor{
	c_char OS;
	c_char valid;
	public  void execute(String IP, int PORT){
		int length = time.getSize()+valid.getSize();
		byte[] buf = new byte[100+4+length];
		setrange(0,99,buf,"GetLocalOS");
		setrange(100,103,buf,length);
		offset = 104;
		int OS_size = OS.getSize();
		setrange(offset,OS_size,buf,OS.toByte());
		offset += OS_size;
		int valid_size = valid.getSize();
		setrange(offset,valid_size,buf,valid.toByte());
		offset += valid_size;
		try{
			clientSocket = new Socket(IP, PORT); 
			SendPacket(clientSocket, buf, buf.length);
			RecvPacket(clientSocket, buf, buf.length);
			OS.setValue(Arrays.copyOfRange(buf,104,104 + OS.getSize()));
			valid.setValue(Arrays.copyOfRange(buf,104 + OS.getSize(),
						104+OS.getSize()+valid.getSize()));
		}catch(UnknownHostException e){
			System.out.println(e);
		}catch(IOException e){
			System.out.println(e);
		}
	}
}
