package UDP_mode;

import java.io.*;
import java.net.*;
import java.util.*;

public class ScoreUDPServer {
    private static final int PORT = 1234;
    private static final int BUFSIZE = 1024;   // max size of a message

  
    private DatagramSocket serverDatagramSocket;
    private byte [] buffer = new byte[BUFSIZE];


    public ScoreUDPServer(DatagramSocket datagramSocket) {
        this.serverDatagramSocket = datagramSocket;
        System.out.println("Server started on port " + PORT);
    }

    public void receiveThenSend(){
        while(true){
            try{
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                serverDatagramSocket.receive(datagramPacket);
                InetAddress inetAddress = datagramPacket.getAddress();
                int port = datagramPacket.getPort();
                String messageFromClient = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println("Message from client : "+ messageFromClient);
                datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, port);
                serverDatagramSocket.send(datagramPacket);

            }
            catch (IOException io) {
                io.printStackTrace();
                break;
            }
        }
    }


    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket(1234);
        ScoreUDPServer scoreUDPServer = new ScoreUDPServer(datagramSocket);
        scoreUDPServer.receiveThenSend();
    }

    
}
