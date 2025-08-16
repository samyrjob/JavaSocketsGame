package P2P_MODE;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.Date;

public class MultiTimeServer {

     private static final String MHOST = "228.5.6.7";
     private static final int PORT = 6789;

     public static void main(String[] args) throws Exception {

        InetAddress inetAddress = InetAddress.getByName(MHOST);
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
        MulticastSocket multicastSocket = new MulticastSocket(PORT);
        SocketAddress socketAddress = multicastSocket.getLocalSocketAddress();
        multicastSocket.joinGroup(inetAddress);

        DatagramPacket datagramPacket;
        System.out.println("Ticking");

        while(true){

            Thread.sleep(1000);
            String stringDate = (new Date()).toString();
            datagramPacket = new DatagramPacket(stringDate.getBytes(), stringDate.getBytes().length, inetAddress, PORT);
            multicastSocket.send(datagramPacket);
        }


        
     }
    
}
