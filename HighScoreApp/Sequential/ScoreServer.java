import java.net.*;
import java.io.*;

public class ScoreServer {
    private static final int PORT = 1234;
    private HighScores hs;

    public ScoreServer() {
        hs = new HighScores();
        try {
            ServerSocket serverSock = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSock = serverSock.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
                PrintWriter out = new PrintWriter(clientSock.getOutputStream(), true);
                processClient(in, out);
                clientSock.close();
                hs.saveScores();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
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

    public static void main(String[] args) {
        new ScoreServer();
    }
}
