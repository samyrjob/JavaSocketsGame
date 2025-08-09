
//imports from java.io package
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

// imports from java.nio API libraries
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

// import streams
import java.util.stream.Collectors;



public class BufferReaderClass {


    public static void main(String[] args) throws FileNotFoundException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("scores.txt"), 16859);



        // try with resources to close the stream (close() method)
        try (
            BufferedReader bufferedReader2 = Files.newBufferedReader(Paths.get("scores.txt"), StandardCharsets.UTF_8);

        ){
             System.out.println(readAllLines(bufferedReader2));
        }


    


    }


    public static String readAllLines(BufferedReader bufferedReader) throws IOException{


        StringBuilder content = new StringBuilder();
        String line;


        while ((line = bufferedReader.readLine()) != null){
            content.append(line);
            content.append(System.lineSeparator());
        }

        return content.toString();
    }


    // method using streams

    public static String readAllLinesWithStream(BufferedReader bufferedReader){

      

       return bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

}