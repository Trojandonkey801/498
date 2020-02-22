public class c_int{
	byte[] buf = new byte[4];

	public int getSize(){
		return buf.length;
	}

	public int getValue(){
		return (buf[3] << 24) +
		(buf[2] << 16) +
		(buf[1] << 8) +
		(buf[0]) ;
	}

	public void setValue(int val){
		buf[3] = (byte)(val >> 24);
		buf[2] = (byte)(val >> 16);
		buf[1] = (byte)(val >> 8);
		buf[0] = (byte)(val);
	}

	public void setValue(byte[] b){
		for (int i = 0; i < 4; i++) {
			buf[i] = b[i];
		}
	}

	public byte[] toByte(){
		return buf;
	}

	public static void main(String[] args) {
		c_int temp = new c_int();
		temp.setValue(50);
		System.out.println();
		System.out.println(temp.getValue());
	}

}

