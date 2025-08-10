package Multiplex;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.*;
import java.util.*;

public class SelectScoreServer {
    private static final int PORT = 12345;
    private static final int BUFSIZ = 1024;

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private Map<SocketChannel, ClientInfo> clients = new HashMap<>();
    private HighScores hs = new HighScores();

    public SelectScoreServer() throws IOException {
        // Setup server socket channel nonblocking
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(PORT));

        // Open selector and register serverChannel for accept events
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started on port " + PORT);
    }

    public void run() throws IOException {
        while (true) {
            selector.select();  // blocking until events
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (key.isAcceptable()) {
                    acceptClient(key);
                } else if (key.isReadable()) {
                    readFromClient(key);
                } else if (key.isWritable()) {
                    finishSend(key);
                } else {
                    System.out.println("Unknown key: " + key);
                }
            }
        }
    }

    private void acceptClient(SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = server.accept();
        clientChannel.configureBlocking(false);

        clientChannel.register(selector, SelectionKey.OP_READ);
        clients.put(clientChannel, new ClientInfo(clientChannel, this));

        System.out.println("Accepted new client: " + clientChannel.getRemoteAddress());
    }

    private void readFromClient(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientInfo ci = clients.get(clientChannel);

        if (ci == null) {
            System.out.println("No client info for channel " + clientChannel);
            clientChannel.close();
            return;
        }

        String msg = ci.readMessage();

        if (msg != null) {
            System.out.println("Received: " + msg.trim());
            if (msg.trim().equalsIgnoreCase("bye")) {
                ci.closeDown();
                clients.remove(clientChannel);
                System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
            } else {
                doRequest(msg.trim(), ci);
            }
        }
    }

    private void doRequest(String line, ClientInfo ci) {
        if (line.equalsIgnoreCase("get")) {
            System.out.println("Sending high scores to client.");
            ci.sendMessage(hs.toString());
        } else if (line.toLowerCase().startsWith("score ")) {
            String scoreData = line.substring(6).trim();
            hs.addScore(scoreData);
            System.out.println("Added score: " + scoreData);
            ci.sendMessage("Score added");
        } else {
            System.out.println("Ignoring input line: " + line);
        }
    }

    private void finishSend(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ClientInfo ci = clients.get(clientChannel);
        if (ci != null) {
            ci.trySendMessage();
        }
    }

    public void removeChannel(SocketChannel channel) {
        try {
            System.out.println("Removing channel: " + channel.getRemoteAddress());
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        clients.remove(channel);
    }

    // Main method to start the server
    public static void main(String[] args) throws IOException {
        SelectScoreServer server = new SelectScoreServer();
        server.run();
    }

    // Simple HighScores class for demo
    static class HighScores {
        private final List<String> scores = new ArrayList<>();

        public void addScore(String score) {
            scores.add(score);
        }

        public String toString() {
            if (scores.isEmpty()) return "No scores yet";
            StringBuilder sb = new StringBuilder("High Scores:\n");
            for (String s : scores) sb.append(s).append("\n");
            return sb.toString();
        }
    }

    // ClientInfo inner class
    static class ClientInfo {
        private static final int BUFSIZ = 1024;
        private SocketChannel channel;
        private SelectScoreServer server;
        private ByteBuffer inBuffer;
        private ByteBuffer outBuffer = null;
        private CharsetDecoder decoder;
        private Charset charset;
        private boolean sending = false;

        public ClientInfo(SocketChannel chan, SelectScoreServer server) {
            this.channel = chan;
            this.server = server;
            this.inBuffer = ByteBuffer.allocateDirect(BUFSIZ);
            this.inBuffer.clear();
            this.charset = Charset.forName("ISO-8859-1");
            this.decoder = charset.newDecoder();
        }

        public String readMessage() {
            String msg = null;
            try {
                int numBytes = channel.read(inBuffer);
                if (numBytes == -1) {
                    closeDown();
                    server.removeChannel(channel);
                    return null;
                }
                msg = getMessage(inBuffer);
            } catch (IOException e) {
                System.out.println("Read error: " + e);
                server.removeChannel(channel);
            }
            return msg;
        }

        private String getMessage(ByteBuffer buf) {
            String msg = null;
            int pos = buf.position();
            int limit = buf.limit();

            buf.position(0);
            buf.limit(pos);

            try {
                CharBuffer cb = decoder.decode(buf);
                msg = cb.toString();
            } catch (CharacterCodingException e) {
                e.printStackTrace();
            }
            buf.limit(limit);
            buf.position(pos);

            if (msg != null && msg.endsWith("\n")) {
                buf.clear();
                return msg;
            }
            return null;
        }

        public boolean sendMessage(String msg) {
            if (sending) {
                // Already sending something, reject new sends in this simple example
                System.out.println("Send in progress, skipping new message");
                return false;
            }

            try {
                String fullMsg = msg + "\r\n";
                outBuffer = ByteBuffer.allocateDirect(BUFSIZ);
                outBuffer.clear();
                outBuffer.put(fullMsg.getBytes(charset));
                outBuffer.flip();
                sending = true;
                trySendMessage();
                return true;
            } catch (Exception e) {
                System.out.println("Send error: " + e);
                server.removeChannel(channel);
                return false;
            }
        }

        public void trySendMessage() {
            try {
                if (outBuffer == null) return;

                channel.write(outBuffer);

                if (!outBuffer.hasRemaining()) {
                    // All sent, reset interest ops
                    outBuffer = null;
                    sending = false;
                    channel.register(server.selector, SelectionKey.OP_READ);
                } else {
                    // Not all sent, keep OP_WRITE interest to continue later
                    channel.register(server.selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }
            } catch (IOException e) {
                System.out.println("Write error: " + e);
                server.removeChannel(channel);
            }
        }

        public void closeDown() {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

