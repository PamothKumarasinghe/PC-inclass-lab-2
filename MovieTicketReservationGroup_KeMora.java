import java.io.*;
import java.util.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

class InvalidMovieCodeException extends Exception {
    public InvalidMovieCodeException(String message) {
        super(message);
    }
}

class InvalidShowtimeException extends Exception {
    public InvalidShowtimeException(String message) {
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

class Movie {
    String code, name, date, showtime, language, genre;
    int totalSeats, availableSeats;
    double ticketPrice;

    public Movie(String code, String name, String date, String showtime, int totalSeats, int availableSeats, double ticketPrice, String language, String genre) {
        this.code = code;
        this.name = name;
        this.date = date;
        this.showtime = showtime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.ticketPrice = ticketPrice;
        this.language = language;
        this.genre = genre;
    }
}

public class MovieTicketReservationGroup_KeMora {
    private static final List<Movie> movieList = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadMoviesFromCSV("Movie Reservation Dataset.csv");
        bookTickets();
    }
    /** This method will load and read the csv file from the computer */
    private static void loadMoviesFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Movie movie = new Movie(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim(),
                        Integer.parseInt(data[4].trim()), Integer.parseInt(data[5].trim()),
                        Double.parseDouble(data[6].trim()), data[7].trim(), data[8].trim());
                movieList.add(movie);
            }
        } 
        catch (IOException e) {
            System.out.println("Error loading movies: " + e.getMessage());
        }
    }
    /** This method will book the tickets if it is valid */
    private static void bookTickets() {
        List<Movie> selectedMovies = null;
        Movie selectedMovie = null;
        int tickets = 0;

        while (selectedMovies == null || selectedMovies.isEmpty()) {
            try {
                System.out.print("Enter The Movie Code : ");
                String movieCode = sc.nextLine().trim().toUpperCase();
                selectedMovies = validateMovieCode(movieCode);
            } 
            catch (InvalidMovieCodeException e) {
                System.out.println("Error : " + e.getMessage());
            }
        }

        // Select Showtimes for the Movie
        while (selectedMovie == null) {
            try {
                System.out.println("Available Showtimes:");
                for (int i = 0; i < selectedMovies.size(); i++) {
                    System.out.println((i + 1) + ". " + selectedMovies.get(i).showtime);
                }
                System.out.print("Select Showtime (Enter Number): ");
                int choice = Integer.parseInt(sc.nextLine().trim());
                if (choice < 1 || choice > selectedMovies.size()) {
                    throw new InvalidShowtimeException("Invalid selection! Please choose a valid showtime.");
                }
                selectedMovie = selectedMovies.get(choice - 1);
            } 
            catch (NumberFormatException | InvalidShowtimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        // Get Valid Ticket Quantity
        while (true) {
            try {
                System.out.print("Enter Number of Tickets: ");
                tickets = validateTicketQuantity(sc.nextLine().trim(), selectedMovie);
                break;
            } 
            catch (InvalidTicketQuantityException | OverbookingException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        confirmBooking(selectedMovie, tickets);
    }
    /** Checks if the movie code is valid or not */
    private static List<Movie> validateMovieCode(String code) throws InvalidMovieCodeException {
        List<Movie> matchingMovies = new ArrayList<>();
        for (Movie movie : movieList) {
            if (movie.code.equalsIgnoreCase(code)) {
                matchingMovies.add(movie);
            }
        }
        if (matchingMovies.isEmpty()) {
            throw new InvalidMovieCodeException("Movie code cannot be found! plz enter a new code.");
        }
        return matchingMovies;
    }

    /** This method will checks if teh ticket quantity is valid or not */
    private static int validateTicketQuantity(String input, Movie movie) throws InvalidTicketQuantityException, OverbookingException {
        try {
            int tickets = Integer.parseInt(input);
            if (tickets <= 0) throw new InvalidTicketQuantityException("Ticket quantity must be a positive integer.");
            
            if (tickets > movie.availableSeats) throw new OverbookingException("Not enough seats available! Try booking fewer tickets.");
            
            return tickets;
        } 
        catch (NumberFormatException e) {
            throw new InvalidTicketQuantityException("Invalid number! Please enter a positive integer.");
        }
    }

    private static void confirmBooking(Movie movie, int tickets) {
        double totalCost = tickets * movie.ticketPrice;
        
        System.out.println("\n -----Booking Confirmed!----");
        System.out.println(" Movie: " + movie.name);
        System.out.println(" Date: " + movie.date);
        System.out.println(" Showtime: " + movie.showtime);
        System.out.println(" Tickets: " + tickets);
        System.out.println(" Total Cost: " + totalCost);

        System.out.print("\nEnter Email sent the PDF Bill: ");
        String email = sc.nextLine().trim();

        generatePDFBill(movie, tickets, totalCost, email);
    }

    private static void generatePDFBill(Movie movie, int tickets, double totalCost, String email) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("Ticket_Bill.pdf"));
            document.open();
            
            document.add(new Paragraph("------  Movie Ticket Reservation Bill ------"));
            document.add(new Paragraph("Movie : " + movie.name));
            document.add(new Paragraph("Date : " + movie.date));
            document.add(new Paragraph("Showtime : " + movie.showtime));
            document.add(new Paragraph("Tickets : " + tickets));
            document.add(new Paragraph("Total Cost: " + totalCost));
            document.add(new Paragraph("\n******Thank you for booking with us (KeMora)!*****"));
            
            document.close();
            System.out.println(" ----PDF Bill Generated: Ticket_Bill.pdf (Sent to " + email + ")----");
        } catch (Exception e) {
            System.out.println("Error generating PDF: " + e.getMessage());
        }
    }
}