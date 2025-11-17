import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

class Movie {
    String title;
    double price;

    public Movie(String title, double price) {
        this.title = title;
        this.price = price;
    }
}

class Seat {
    int row, col;
    boolean isBooked;

    public Seat(int row, int col) {
        this.row = row;
        this.col = col;
        this.isBooked = false;
    }
}

class Show {
    Movie movie;
    Seat[][] seats;

    public Show(Movie movie, int rows, int cols) {
        this.movie = movie;
        seats = new Seat[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                seats[i][j] = new Seat(i, j);
    }
}

class Booking {
    Movie movie;
    ArrayList<Seat> bookedSeats;
    double totalPrice;

    public Booking(Movie movie) {
        this.movie = movie;
        this.bookedSeats = new ArrayList<>();
    }

    public void addSeat(Seat s) {
        bookedSeats.add(s);
        totalPrice = bookedSeats.size() * movie.price;
    }
}

public class MovieBookingSystem extends JFrame {
    private JComboBox<String> movieList;
    private JPanel seatPanel;
    private JLabel billLabel;
    private JButton bookButton;

    private ArrayList<Movie> movies = new ArrayList<>();
    private HashMap<String, Show> shows = new HashMap<>();
    private Booking currentBooking;

    private static final String CSV_FILE = "bookings.csv";

    public MovieBookingSystem() {
        setTitle("🎥 Movie Ticket Booking System");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Movies and shows
        movies.add(new Movie("Avengers: Endgame", 200));
        movies.add(new Movie("Interstellar", 250));
        movies.add(new Movie("Inception", 180));
        movies.add(new Movie("Spiderman: Homecoming", 300));

        for (Movie m : movies) {
            shows.put(m.title, new Show(m, 5, 8)); // 5 rows × 8 seats
        }


        loadBookingsFromCSV();


        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Select Movie:"));
        movieList = new JComboBox<>(shows.keySet().toArray(new String[0]));
        topPanel.add(movieList);
        add(topPanel, BorderLayout.NORTH);

        seatPanel = new JPanel();
        seatPanel.setLayout(new GridLayout(5, 8, 5, 5));
        add(seatPanel, BorderLayout.CENTER);
        updateSeatLayout();

        JPanel bottomPanel = new JPanel();
        bookButton = new JButton("Book Selected Seats");
        billLabel = new JLabel("Total: ₹0.00");
        bottomPanel.add(bookButton);
        bottomPanel.add(billLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        movieList.addActionListener(e -> updateSeatLayout());
        bookButton.addActionListener(e -> confirmBooking());

        setVisible(true);
    }

    private void updateSeatLayout() {
        seatPanel.removeAll();
        String selectedMovie = (String) movieList.getSelectedItem();
        Show show = shows.get(selectedMovie);
        currentBooking = new Booking(show.movie);

        for (int i = 0; i < show.seats.length; i++) {
            for (int j = 0; j < show.seats[0].length; j++) {
                Seat seat = show.seats[i][j];
                JButton seatBtn = new JButton((i + 1) + "-" + (j + 1));
                seatBtn.setBackground(seat.isBooked ? Color.RED : Color.GREEN);
                seatBtn.setEnabled(!seat.isBooked);

                seatBtn.addActionListener(e -> {
                    seat.isBooked = true;
                    currentBooking.addSeat(seat);
                    seatBtn.setBackground(Color.RED);
                    seatBtn.setEnabled(false);
                    billLabel.setText("Total: ₹" + currentBooking.totalPrice);
                });

                seatPanel.add(seatBtn);
            }
        }

        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void confirmBooking() {
        if (currentBooking.bookedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No seats selected!");
            return;
        }

        saveBookingToCSV(currentBooking); // ✅ Save to CSV

        JOptionPane.showMessageDialog(this,
                "🎫 Booking Confirmed!\nMovie: " + currentBooking.movie.title +
                        "\nSeats: " + currentBooking.bookedSeats.size() +
                        "\nTotal: ₹" + currentBooking.totalPrice);

        updateSeatLayout();
    }

    private void saveBookingToCSV(Booking booking) {
        try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
            for (Seat s : booking.bookedSeats) {
                writer.write(booking.movie.title + "," + s.row + "," + s.col + "," + booking.movie.price + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBookingsFromCSV() {
        File file = new File(CSV_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 4) continue;

                String movieTitle = data[0];
                int row = Integer.parseInt(data[1]);
                int col = Integer.parseInt(data[2]);

                Show show = shows.get(movieTitle);
                if (show != null && row < show.seats.length && col < show.seats[0].length) {
                    show.seats[row][col].isBooked = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MovieBookingSystem::new);
    }
}
