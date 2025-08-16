package P2P_MODE;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

public class MultiTimeClient {

     private static final String MHOST = "228.5.6.7";
     private static final int PORT = 6789;

     public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getByName(MHOST);
        NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
        MulticastSocket msock = new MulticastSocket(PORT);
         SocketAddress socketAddress = msock.getLocalSocketAddress();
        msock.joinGroup(inetAddress);


        DatagramPacket dpacket;

        byte [] buffer = new byte[1024];
        dpacket = new DatagramPacket(buffer, buffer.length);
        String string_received;

        while(true){

            msock.receive(dpacket);
            string_received = new String(dpacket.getData()).trim();
            System.out.println("Date received from the server : "+ dpacket.getAddress()+ "   " + string_received);


            
        }




     }
    
}
