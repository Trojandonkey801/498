import java.io.IOException;
import java.net.*;

/**
 *
 * @author hp
 */
public class testend {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnknownHostException, SocketException, IOException  {
        // TODO code application logic here
        byte[] buffer = {10,23,12,31,43,32,24};
        byte [] IP={-64,-88,1,106};
        InetAddress address = InetAddress.getByAddress(IP);
        DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, address, 30000 
                );
        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.send(packet);
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }
}
