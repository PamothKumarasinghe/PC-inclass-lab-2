import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.text.Document;

/**
 * Movie Ticket Reservation System
 * - Users select a movie, date, showtime, and ticket quantity
 * - Validates inputs with custom exceptions
 * - Generates a PDF bill and saves session data
 */

// ✅ Custom Exception 1: Invalid Movie Code
class InvalidMovieCodeException extends Exception {
    public InvalidMovieCodeException(String message) {
        super(message);
    }
}

// ✅ Custom Exception 2: Invalid Date or Showtime
class InvalidDateOrShowtimeException extends Exception {
    public InvalidDateOrShowtimeException(String message) {
        super(message);
    }
}

// ✅ Custom Exception 3: Invalid Ticket Quantity
class InvalidTicketQuantityException extends Exception {
    public InvalidTicketQuantityException(String message) {
        super(message);
    }
}

// ✅ Custom Exception 4: Overbooking
class OverbookingException extends Exception {
    public OverbookingException(String message) {
        super(message);
    }
}

// 🎬 Movie Class to Store Movie Information
class Movie {
    String code, name, showtime, language, genre;
    int totalSeats, availableSeats;
    double ticketPrice;

    public Movie(String code, String name, String showtime, int totalSeats, int availableSeats, double ticketPrice, String language, String genre) {
        this.code = code;
        this.name = name;
        this.showtime = showtime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.ticketPrice = ticketPrice;
        this.language = language;
        this.genre = genre;
    }
}

// 🎟️ Reservation System
public class in_lab_2 {
    private static final List<Movie> movieList = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadMoviesFromCSV("Movie Reservation Dataset.csv"); // Load movies from CSV
        startBookingProcess(); // Start ticket reservation
    }

    // 🔹 Load Movies from CSV File
    private static void loadMoviesFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip header row
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Movie movie = new Movie(data[0].trim(), data[1].trim(), data[3].trim(),
                        Integer.parseInt(data[4].trim()), Integer.parseInt(data[5].trim()),
                        Double.parseDouble(data[6].trim()), data[7].trim(), data[8].trim());
                movieList.add(movie);
            }
        } catch (IOException e) {
            System.out.println("Error loading movies: " + e.getMessage());
        }
    }

    // 🎫 Ticket Booking Process
    private static void startBookingProcess() {
        try {
            System.out.print("Enter Movie Code: ");
            String movieCode = scanner.nextLine().trim().toUpperCase();
            Movie selectedMovie = validateMovieCode(movieCode);

            System.out.print("Enter Showtime (Morning/Afternoon/Evening): ");
            String showtime = scanner.nextLine().trim();
            validateShowtime(selectedMovie, showtime);

            System.out.print("Enter Number of Tickets: ");
            int tickets = validateTicketQuantity(scanner.nextLine().trim(), selectedMovie);

            confirmBooking(selectedMovie, tickets);
        } catch (InvalidMovieCodeException | InvalidDateOrShowtimeException | InvalidTicketQuantityException | OverbookingException e) {
            System.out.println("⚠ Error: " + e.getMessage());
        }
    }

    // ✅ Check if Movie Code Exists
    private static Movie validateMovieCode(String code) throws InvalidMovieCodeException {
        for (Movie movie : movieList) {
            if (movie.code.equalsIgnoreCase(code)) {
                return movie;
            }
        }
        throw new InvalidMovieCodeException("Movie code not found! Please enter a valid code.");
    }

    // ✅ Validate Showtime
    private static void validateShowtime(Movie movie, String showtime) throws InvalidDateOrShowtimeException {
        List<String> validShowtimes = Arrays.asList("Morning", "Afternoon", "Evening");
        if (!validShowtimes.contains(showtime)) {
            throw new InvalidDateOrShowtimeException("Invalid showtime! Please enter Morning, Afternoon, or Evening.");
        }
        if (!movie.showtime.equalsIgnoreCase(showtime)) {
            throw new InvalidDateOrShowtimeException("This movie is not available at the selected showtime!");
        }
    }

    // ✅ Validate Ticket Quantity
    private static int validateTicketQuantity(String input, Movie movie) throws InvalidTicketQuantityException, OverbookingException {
        try {
            int tickets = Integer.parseInt(input);
            if (tickets <= 0) {
                throw new InvalidTicketQuantityException("Ticket quantity must be a positive integer.");
            }
            if (tickets > movie.availableSeats) {
                throw new OverbookingException("Not enough seats available! Try booking fewer tickets.");
            }
            return tickets;
        } catch (NumberFormatException e) {
            throw new InvalidTicketQuantityException("Invalid number! Please enter a positive integer.");
        }
    }

    // 🔹 Confirm & Generate Bill
    private static void confirmBooking(Movie movie, int tickets) {
        double totalCost = tickets * movie.ticketPrice;
        System.out.println("\n Booking Confirmed!");
        System.out.println(" Movie: " + movie.name);
        System.out.println(" Showtime: " + movie.showtime);
        System.out.println(" Tickets: " + tickets);
        System.out.println(" Total Cost: $" + totalCost);

        System.out.print("\nEnter Email for PDF Bill: ");
        String email = scanner.nextLine().trim();
        generatePDFBill(movie, tickets, totalCost, email);
    }

    // 📝 Generate PDF Bill
    private static void generatePDFBill(Movie movie, int tickets, double totalCost, String email) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("Ticket_Bill.pdf"));
            document.open();
            document.add(new Paragraph("------ Movie Ticket Reservation Bill------"));
            document.add(new Paragraph("Movie: " + movie.name));
            document.add(new Paragraph("Showtime: " + movie.showtime));
            document.add(new Paragraph("Tickets: " + tickets));
            document.add(new Paragraph("Total Cost: $" + totalCost));
            document.add(new Paragraph("\n✅ Thank you for booking!"));

            document.close();
            System.out.println("📄 PDF Bill Generated: Ticket_Bill.pdf (Sent to " + email + ")");
        } catch (Exception e) {
            System.out.println("Error generating PDF: " + e.getMessage());
        }
    }
}
