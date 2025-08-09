import java.net.*;
import java.io.*;

public class ThreadedScoreServer {
    private static final int PORT = 1234;
    private HighScores hs;

    public ThreadedScoreServer() {
        hs = new HighScores();
        try {
            ServerSocket serverSock = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            Socket clientSock;
            String cliAddr;


            while (true) {
                System.out.println("waiting for a client...");
                clientSock = serverSock.accept();
                cliAddr = clientSock.getInetAddress().getHostAddress();


              


                new ThreadedScoreServerHandler(clientSock, cliAddr, hs).start();

                hs.saveScores();
            }


        } catch (IOException e) {
            System.out.println(e);
        }
    }




public class ThreadedScoreServerHandler extends Thread{


    Socket clientSocket;
    HighScores hs;
    String cliAddr;

    ThreadedScoreServerHandler(Socket clientSocket, String cliAddr, HighScores hs){

       this.clientSocket = clientSocket;
       this.cliAddr = cliAddr;
       this.hs = hs;

    }




    private void processClient(BufferedReader in, PrintWriter out) {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                if (line.trim().equalsIgnoreCase("bye")) break;
                doRequest(line, out);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void doRequest(String line, PrintWriter out) {
        if (line.trim().equalsIgnoreCase("get")) {
            out.println(hs.toString());
        } else if (line.toLowerCase().startsWith("score")) {
            hs.addScore(line.substring(5));
        }
    }


    public void run(){

        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);) {


            processClient(in, out);

             // Close client connection
            clientSocket.close( );
            System.out.println("Client (" + cliAddr +
                                    ") connection closed\n");

        }
        catch (Exception e) {
            System.out.print(e);

        }



    }



}
    

    public static void main(String[] args) {
        new ThreadedScoreServer();
    }
}
