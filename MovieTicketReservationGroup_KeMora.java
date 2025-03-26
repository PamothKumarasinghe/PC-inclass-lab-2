import java.io.*;
import java.util.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;


//for custom exceptions as the problem asks//
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
/** This is the Movie class for all the objects that belongs to Movie */
class Movie {
    String code, name, showtime;
    int totalSeats, availableSeats;
    double ticketPrice;

    public Movie(String code, String name, String showtime, int totalSeats, int availableSeats, double ticketPrice, String language, String genre) {
        this.code = code;
        this.name = name;
        this.showtime = showtime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
        this.ticketPrice = ticketPrice;
    }
}

public class MovieTicketReservationGroup_KeMora {
    private static final List<Movie> movieList = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadMoviesFromCSV("Movie Reservation Dataset.csv"); // to read the csv file
        bookTickets();
    }

    /** This method will load and read the csv file from the computer */
    private static void loadMoviesFromCSV(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); //split the data by comma
                Movie movie = new Movie(data[0].trim(), data[1].trim(), data[3].trim(),Integer.parseInt(data[4].trim()), Integer.parseInt(data[5].trim()),
                Double.parseDouble(data[6].trim()), data[7].trim(), data[8].trim()); //Movie object to add to the list//
                movieList.add(movie);
            }
        } 
        catch (IOException e) {
            System.out.println("Error loading movies: " + e.getMessage());
        }
    }

    /** This method will book the tickets for the movie */
    private static void bookTickets() {
        Movie selectedMovie = null;
        String showtime = "";
        int tickets = 0;


        while (selectedMovie == null) { // until the movie code is valid //
            try {
                System.out.print("Enter The Movie Code: ");
                String movieCode = sc.nextLine().trim().toUpperCase();
                selectedMovie = validateMovieCode(movieCode);
            } 
            catch (InvalidMovieCodeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        while (true) {
            try {
                System.out.print("Enter Showtime : ");
                showtime = sc.nextLine().trim();
                validateShowtime(selectedMovie, showtime);
                break;
            } 
            catch (InvalidDateOrShowtimeException e) {
                System.out.println(" Error: " + e.getMessage());
            }
        }


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

        confirmBooking(selectedMovie, tickets); //If everything is ok--? then? book is confirmed //
    }

    private static Movie validateMovieCode(String code) throws InvalidMovieCodeException {
        for (Movie movie : movieList) {
            if (movie.code.equalsIgnoreCase(code)) {
                return movie;
            }
        }
        throw new InvalidMovieCodeException("Movie code is not found! plz enter a valid code");
    }
    
    /** This method will check the validity of showtimes  */
    private static void validateShowtime(Movie movie, String showtime) throws InvalidDateOrShowtimeException {
        List<String> validShowtimes = Arrays.asList("Morning", "Afternoon", "Evening");
        if (!validShowtimes.contains(showtime)) throw new InvalidDateOrShowtimeException("Invalid showtime! Please enter from Morning, Afternoon, or Evening.");
        
        if (!movie.showtime.equalsIgnoreCase(showtime)) throw new InvalidDateOrShowtimeException("The movie selected is not available at the selected showtime!");
        
    }
    /** This method will check the validity of ticket quantity */
    private static int validateTicketQuantity(String input, Movie movie) throws InvalidTicketQuantityException, OverbookingException {
        try {
            int tickets = Integer.parseInt(input);
            if (tickets <= 0) throw new InvalidTicketQuantityException("Ticket quantity must be a positive integer.");
            
            if (tickets > movie.availableSeats) throw new OverbookingException("No seats! plz enter lower amount of seats.");
            return tickets;
        } catch (NumberFormatException e) {
            throw new InvalidTicketQuantityException("Invalid number! Please enter a positive integer.");
        }
    }

    private static void confirmBooking(Movie movie, int tickets) {
        double totalCost = tickets * movie.ticketPrice;
        
        System.out.println("\n Booking Confirmed!");
        System.out.println(" Movie: " + movie.name);
        System.out.println(" Showtime: " + movie.showtime);
        System.out.println(" Tickets: " + tickets);
        System.out.println(" Total Cost: " + totalCost);

        System.out.print("\nEnter Email to sent the PDF Bill: ");
        String email = sc.nextLine().trim();

        generatePDFBill(movie, tickets, totalCost, email);
    }

    private static void generatePDFBill(Movie movie, int tickets, double totalCost, String email) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("Ticket_Bill.pdf"));
            document.open();
            
            document.add(new Paragraph("------  Movie Ticket Reservation Bill ------"));
            document.add(new Paragraph("Movie: " + movie.name));
            document.add(new Paragraph("Showtime: " + movie.showtime));
            document.add(new Paragraph("Tickets: " + tickets));
            document.add(new Paragraph("Total Cost: $" + totalCost));
            document.add(new Paragraph("\n--------Thank you for booking with us!-------"));
            
            document.close();
            System.out.println("*****PDF Bill Generated: Ticket_Bill.pdf (Sent to " + email + ")******");
        } 
        catch (Exception e) {
            System.out.println("Error generating PDF: " + e.getMessage());
        }
    }
}
