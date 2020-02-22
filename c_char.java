public class c_char{
	byte[] buf = new byte[16];

	public int getSize(){
		return buf.length;
	}

	// Written in  [T][h][i][s][O][r][d][e][r]
	public void setValue(String val){
		//If it exceeds 16 it gets truncated.
		for (int i = 0; i < 16 && i < val.length(); i++) {
			buf[i] = (byte)val.charAt(i);
		}
	}

	public void setValue(byte[] b){
		for (int i = 0; i < 16; i++) {
			buf[i] = b[i];
		}
	}

	public String getValue(){
		String toreturn = "";
		for (int i = 0; i < 16; i++) {
			toreturn += (char)buf[i];
		}
		return toreturn;
	}

	public byte[] toByte(){
		return buf;
	}

	public static void main(String[] args) {
		c_char c = new c_char();
		c.setValue("123456");
		System.out.println(c.getValue());
	}
}
