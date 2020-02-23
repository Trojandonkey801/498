import java.net.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class GetLocalTime extends executor{
	c_int time;
	c_char valid;
	public GetLocalTime(){
		time = new c_int();
		valid = new c_char();
	}
	// Buffer structure:
	// "GetLocalTime" - 100 | length - 103 | time.toByte | valid.toByte
	public void execute(String IP, int PORT){
		int length = time.getSize()+valid.getSize();
		byte[] buf = new byte[100+4+length];
		setrange(0,99,buf,"GetLocalTime"); // set buffer range 0 to 99 to the String GetLocalTime
		System.out.println("setting length");
		setrange(100,103,buf,length); // set to length of this buffer
		offset = 104;
		int time_size = time.getSize();
		setrange(offset,time_size,buf,time.toByte());  // set data in buffer to time converted to Bytes
		offset += time_size;
		int valid_size = valid.getSize();
		setrange(offset,valid_size,buf,valid.toByte()); // set data in buffer to valid data
		offset += valid_size;
		printByteArr(buf,100+4+length);
		try{
			clientSocket = new Socket(IP, PORT); 
			SendPacket(clientSocket, buf, buf.length);
			RecvPacket(clientSocket, buf, buf.length);
			time.setValue(Arrays.copyOfRange(buf,104,104 + time.getSize()));
			valid.setValue(Arrays.copyOfRange(buf,104 + time.getSize(),
						104+time.getSize()+valid.getSize()));
		}catch(UnknownHostException e){
			System.out.println(e);
		}catch(IOException e){
			System.out.println(e);
		}
	}

	public static void main(String[] args) {
		GetLocalTime temp = new GetLocalTime();
		temp.valid.setValue("FALSE");
		temp.execute("127.0.0.1",1235);
	}
}
