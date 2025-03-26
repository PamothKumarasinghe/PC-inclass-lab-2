import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class InvalidMovieCodeException extends Exception {
    public InvalidMovieCodeException(String message) {
        super(message);
    }
}
class InvalidDateOrShowtimeException extends Exception {
    public InvalidDateOrShowtimeException(String message) {
        super(message);
    }
}
class InvalidTicketQuantityException extends Exception {
    public InvalidTicketQuantityException(String message) {
        super(message);
    }
}
class OverbookingException extends Exception {
    public OverbookingException(String message) {
        super(message);
    }
}

public class in_lab_2 {
    public static void main() {
        
    }

    private static void loadMovies() {
        try (BufferedReader br = new BufferedReader(new FileReader("movies.csv"))) {
            String line;
            br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] details = line.split(",");
                movies.add(new Movie(
                    details[0], details[1], details[2], details[3],
                    Integer.parseInt(details[4]), Integer.parseInt(details[5]),
                    Double.parseDouble(details[6]), details[7], details[8]
                ));
            }
            System.out.println("Movies loaded successfully!");
        } catch (IOException e) {
            System.out.println("Error loading movie data.");
        }
    }

}
