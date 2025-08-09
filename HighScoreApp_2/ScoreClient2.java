package HighScoreApp_2;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;

public class ScoreClient2 extends JFrame implements ActionListener {
    private static final int PORT = 1234;
    private static final String HOST = "localhost";

    private Socket sock;
    private BufferedReader in;
    private PrintWriter out;

    private JTextField jtfName, jtfScore;
    private JTextArea jtaMesgs;
    private JButton jbGetScores;

    public ScoreClient2() {
        super("High Score Client");
        initializeGUI();
        makeContact();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeLink();
            }
        });

        setSize(300, 400);
        setVisible(true);
    }

    private void initializeGUI() {
        jtaMesgs = new JTextArea();
        jtaMesgs.setEditable(false);
        add(new JScrollPane(jtaMesgs), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridLayout(3, 1));
        jtfName = new JTextField();
        jtfScore = new JTextField();
        jtfScore.addActionListener(this);
        jbGetScores = new JButton("Get Scores");
        jbGetScores.addActionListener(this);

        bottomPanel.add(new JLabel("Name:"));
        bottomPanel.add(jtfName);
        bottomPanel.add(new JLabel("Score:"));
        bottomPanel.add(jtfScore);
        bottomPanel.add(jbGetScores);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void makeContact() {
        try {
            sock = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
        } catch (IOException e) {
            jtaMesgs.append("Unable to connect to server\n");
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jbGetScores) {
            sendGet();
        } else if (e.getSource() == jtfScore) {
            sendScore();
        }
    }

    private void sendGet() {
        try {
            out.println("get");
            String line = in.readLine();
            if (line.startsWith("HIGH$$")) {
                jtaMesgs.setText(line.substring(6).replace("&", "\n"));
            }
        } catch (IOException e) {
            jtaMesgs.append("Problem getting scores\n");
        }
    }

   private void sendScore() {
        String name = jtfName.getText().trim();
        String score = jtfScore.getText().trim();
        if (!name.isEmpty() && !score.isEmpty()) {
            out.println("score " + name + " & " + score + " &");
            jtfScore.setText("");
        }
    }

    private void closeLink() {
        try {
            out.println("bye");
            sock.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        System.exit(0);
    }

    public static void main(String[] args) {
        new ScoreClient2();
    }
}

