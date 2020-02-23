import java.net.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class GetLocalOS extends executor{
	c_char OS;
	c_char valid;
	public GetLocalOS(){
		time = new c_int();
		valid = new c_char();
	}

	public  void execute(String IP, int PORT){
		int length = time.getSize()+valid.getSize();
		byte[] buf = new byte[100+4+length];
		setrange(0,99,buf,"GetLocalOS");
		setrange(100,103,buf,length);
		offset = 104;
		int OS_size = time.getSize();
		setrange(offset,OS_size,buf,time.toByte());
		offset += OS_size;
		int valid_size = valid.getSize();
		setrange(offset,valid_size,buf,valid.toByte());
		offset += valid_size;
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
		GetLocalOS temp = new GetLocalOS();
		temp.valid.setValue("FALSE");
		temp.execute("127.0.0.1",1234);
	}

}
