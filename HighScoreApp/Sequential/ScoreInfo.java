public class ScoreInfo {
    public String name;
    public int score;

    public ScoreInfo(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String toString() {
        return name + " & " + score + " & ";
    }
}
