import java.util.*;
import java.io.*;

public class HighScores {
    private ArrayList<ScoreInfo> scores;

    public HighScores() {
        scores = new ArrayList<>();
        loadScores();
    }

    public void addScore(String line) {
        String[] parts = line.split("&");
        if (parts.length >= 2) {
            String name = parts[0].trim();
            int score = Integer.parseInt(parts[1].trim());
            scores.add(new ScoreInfo(name, score));
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("HIGH$$ ");
        for (ScoreInfo si : scores) {
            sb.append(si.toString());
        }
        return sb.toString();
    }

    public void loadScores() {
        try (BufferedReader br = new BufferedReader(new FileReader("scores.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                addScore(line);
            }
        } catch (IOException e) {
            System.out.println("No scores to load.");
        }
    }

    public void saveScores() {
        try (PrintWriter pw = new PrintWriter(new FileWriter("scores.txt"))) {
            for (ScoreInfo si : scores) {
                pw.println(si.name + " & " + si.score);
            }
        } catch (IOException e) {
            System.out.println("Unable to save scores.");
        }
    }
}
