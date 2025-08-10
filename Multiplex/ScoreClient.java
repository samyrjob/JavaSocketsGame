package Multiplex;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ScoreClient {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to server at " + HOST + ":" + PORT);

            Thread receiver = new Thread(() -> {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("Server says: " + line);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            });
            receiver.setDaemon(true);
            receiver.start();

            System.out.println("Type messages to send to server ('bye' to quit):");
            while (true) {
                String input = scanner.nextLine();
                writer.write(input + "\n");
                writer.flush();

                if (input.equalsIgnoreCase("bye")) {
                    break;
                }
            }

            System.out.println("Client exiting.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

