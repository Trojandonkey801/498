import java.net.*;
import java.io.*;
import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
public class executor{
	public  c_int time;
	public  c_char valid;
	public  int offset = 100;
	Socket clientSocket;
	String command;

	public void printByteArr(byte[] toprint, int length){
		System.out.println(length);
		for (int i = 0; i < length; i++) {
			System.out.print(toprint[i]);
			System.out.print(" ");
		}
	}
	public void SendPacket(Socket s, byte[] buf, int length){
		try{
		DataOutputStream outStream  = new DataOutputStream(s.getOutputStream());
		outStream.write(buf,0,length);
		}catch(IOException e){
			System.out.println(e);
		}
	}

	public void RecvPacket(Socket s, byte[] buf, int length){
		try{
			DataInputStream inStream  = new DataInputStream(s.getInputStream());
			inStream.readFully(buf);
		}catch(IOException e){
			System.out.println(e);
		}
	}
	public int getValue(byte[] buf){
		return (buf[3] << 24) +
			(buf[2] << 16) +
			(buf[1] << 8) +
			(buf[0]) ;
	}

	public void setValue(byte[] buf, int val){
		buf[3] = (byte)(val >> 24);
		buf[2] = (byte)(val >> 16);
		buf[1] = (byte)(val >> 8);
		buf[0] = (byte)(val);
	}

	public void setrange(int begin, int length, byte[] buf,byte[] toset){
		for (int i = 0; i < length;i++) {
			buf[i + offset] = toset[i];
			System.out.println(buf[i+offset]);
		}
		System.out.println("done printing byte assignment");
	}

	public void setrange(int begin, int end,byte[] b, int toset){
		System.out.println("Setting" + toset);
		byte[] temp = new byte[4];
		setValue(temp,toset);
		for (int i = begin; i <= end;i++) {
			b[i] = temp[i-100];
			System.out.println(b[i]);
		}
		System.out.println("done printing int assignment");
	}

	public void setrange(int begin, int end,byte[] b, String toset){
		for (int i = begin; i <= end && i < toset.length(); i++) {
			b[i] = (byte)toset.charAt(i);
			System.out.println(b[i]);
		}
		System.out.println("done printing String assignment");
	}
}
