package UDP_mode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.Scanner;
import java.io.*;

public class ScoreUDPClient {

    DatagramSocket clienDatagramSocket;
    private InetAddress inetAddress;
    private int PORT_NUMBER = 1234;
    private int BUFFER_SIZE = 1024;
    private byte[] buffer = new byte[BUFFER_SIZE];

    ScoreUDPClient(InetAddress inetAddress, DatagramSocket datagramSocket){
        this.clienDatagramSocket = datagramSocket;
        this.inetAddress = inetAddress;
    }

    // work in tandem with the receivethensend method from ScoreUDPServer class

    public void sendAndReceive(){

        Scanner scanner = new Scanner(System.in);
        while(true){


            try {
                String messageClient = scanner.nextLine();
                buffer = messageClient.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, PORT_NUMBER);
                clienDatagramSocket.send(datagramPacket);
                clienDatagramSocket.receive(datagramPacket);
                System.out.println("The server said that you sent the following message " + new String(datagramPacket.getData(), 0, datagramPacket.getLength()));


            }
            catch (IOException e){
                e.printStackTrace();
                break;
            }

        }
        scanner.close();
    }










    public static void main(String[] args) throws SocketException, UnknownHostException {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress inetAddress = InetAddress.getByName("localhost");
        ScoreUDPClient scoreUDPClient = new ScoreUDPClient(inetAddress, datagramSocket);
        System.out.println("Send datagram packets and receive...");
        scoreUDPClient.sendAndReceive();

    }
}
