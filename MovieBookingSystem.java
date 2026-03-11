import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    private static final String JSON_FILE = "bookings.json";

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

        loadBookingsFromJSON();

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

        saveBookingToJSON(currentBooking); // ✅ Save to JSON

        JOptionPane.showMessageDialog(this,
                "🎫 Booking Confirmed!\nMovie: " + currentBooking.movie.title +
                        "\nSeats: " + currentBooking.bookedSeats.size() +
                        "\nTotal: ₹" + currentBooking.totalPrice);

        updateSeatLayout();
    }

    private void saveBookingToJSON(Booking booking) {
        try {
            List<Map<String, Object>> bookings = new ArrayList<>();
            File file = new File(JSON_FILE);
            
            // Load existing bookings if file exists
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath()));
                bookings = parseBookingsFromJSON(content);
            }
            
            // Create new booking object
            Map<String, Object> bookingMap = new LinkedHashMap<>();
            bookingMap.put("movieTitle", booking.movie.title);
            bookingMap.put("price", booking.movie.price);
            bookingMap.put("totalPrice", booking.totalPrice);
            bookingMap.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            List<Map<String, Integer>> seatsList = new ArrayList<>();
            for (Seat s : booking.bookedSeats) {
                Map<String, Integer> seatMap = new LinkedHashMap<>();
                seatMap.put("row", s.row);
                seatMap.put("col", s.col);
                seatsList.add(seatMap);
            }
            bookingMap.put("seats", seatsList);
            
            // Add to bookings list
            bookings.add(bookingMap);
            
            // Write to JSON file
            String jsonContent = toJSON(bookings);
            try (FileWriter writer = new FileWriter(JSON_FILE)) {
                writer.write(jsonContent);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving booking: " + e.getMessage());
        }
    }

    private void loadBookingsFromJSON() {
        try {
            File file = new File(JSON_FILE);
            if (!file.exists()) return;

            String content = new String(Files.readAllBytes(file.toPath()));
            List<Map<String, Object>> bookings = parseBookingsFromJSON(content);

            for (Map<String, Object> bookingMap : bookings) {
                String movieTitle = (String) bookingMap.get("movieTitle");
                @SuppressWarnings("unchecked")
                List<Map<String, Integer>> seatsList = (List<Map<String, Integer>>) bookingMap.get("seats");

                Show show = shows.get(movieTitle);
                if (show != null && seatsList != null) {
                    for (Map<String, Integer> seatMap : seatsList) {
                        int row = seatMap.get("row");
                        int col = seatMap.get("col");

                        if (row < show.seats.length && col < show.seats[0].length) {
                            show.seats[row][col].isBooked = true;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MovieBookingSystem::new);
    }

    // JSON Parsing Helper Methods
    private List<Map<String, Object>> parseBookingsFromJSON(String jsonContent) {
        List<Map<String, Object>> bookings = new ArrayList<>();
        try {
            // Simple JSON array parsing
            jsonContent = jsonContent.trim();
            if (!jsonContent.startsWith("[")) {
                return bookings; // Not an array
            }
            
            // Remove outer brackets
            String content = jsonContent.substring(1, jsonContent.length() - 1).trim();
            
            // Split by top-level booking objects
            int braceCount = 0;
            StringBuilder currentObj = new StringBuilder();
            
            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                
                if (c == ',' && braceCount == 0) {
                    if (currentObj.toString().trim().length() > 0) {
                        bookings.add(parseBookingObject(currentObj.toString().trim()));
                        currentObj = new StringBuilder();
                    }
                } else {
                    currentObj.append(c);
                }
            }
            
            // Parse last booking
            if (currentObj.toString().trim().length() > 0) {
                bookings.add(parseBookingObject(currentObj.toString().trim()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookings;
    }

    private Map<String, Object> parseBookingObject(String objStr) {
        Map<String, Object> result = new LinkedHashMap<>();
        
        // Extract movieTitle
        result.put("movieTitle", extractStringValue(objStr, "movieTitle"));
        
        // Extract price
        String priceStr = extractNumberValue(objStr, "price");
        result.put("price", priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr));
        
        // Extract totalPrice
        String totalPriceStr = extractNumberValue(objStr, "totalPrice");
        result.put("totalPrice", totalPriceStr.isEmpty() ? 0.0 : Double.parseDouble(totalPriceStr));
        
        // Extract timestamp
        result.put("timestamp", extractStringValue(objStr, "timestamp"));
        
        // Extract seats array
        result.put("seats", parseSeatsArray(objStr));
        
        return result;
    }

    private List<Map<String, Integer>> parseSeatsArray(String objStr) {
        List<Map<String, Integer>> seats = new ArrayList<>();
        try {
            // Find seats array
            int startIdx = objStr.indexOf("\"seats\"");
            if (startIdx == -1) return seats;
            
            int arrayStart = objStr.indexOf("[", startIdx);
            int arrayEnd = objStr.indexOf("]", arrayStart);
            
            if (arrayStart == -1 || arrayEnd == -1) return seats;
            
            String seatsArray = objStr.substring(arrayStart + 1, arrayEnd);
            
            // Parse each seat object
            int braceCount = 0;
            StringBuilder currentSeat = new StringBuilder();
            
            for (int i = 0; i < seatsArray.length(); i++) {
                char c = seatsArray.charAt(i);
                if (c == '{') braceCount++;
                if (c == '}') braceCount--;
                
                if (c == ',' && braceCount == 0) {
                    if (currentSeat.toString().trim().length() > 0) {
                        seats.add(parseSeatObject(currentSeat.toString().trim()));
                        currentSeat = new StringBuilder();
                    }
                } else if (c != ',' || braceCount > 0) {
                    currentSeat.append(c);
                }
            }
            
            // Parse last seat
            if (currentSeat.toString().trim().length() > 0) {
                seats.add(parseSeatObject(currentSeat.toString().trim()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seats;
    }

    private Map<String, Integer> parseSeatObject(String seatStr) {
        Map<String, Integer> seat = new LinkedHashMap<>();
        
        String rowStr = extractNumberValue(seatStr, "row");
        seat.put("row", rowStr.isEmpty() ? 0 : Integer.parseInt(rowStr));
        
        String colStr = extractNumberValue(seatStr, "col");
        seat.put("col", colStr.isEmpty() ? 0 : Integer.parseInt(colStr));
        
        return seat;
    }

    private String extractStringValue(String str, String key) {
        int keyIdx = str.indexOf("\"" + key + "\"");
        if (keyIdx == -1) return "";
        
        int colonIdx = str.indexOf(":", keyIdx);
        int quoteStart = str.indexOf("\"", colonIdx);
        int quoteEnd = str.indexOf("\"", quoteStart + 1);
        
        if (quoteStart == -1 || quoteEnd == -1) return "";
        
        return str.substring(quoteStart + 1, quoteEnd);
    }

    private String extractNumberValue(String str, String key) {
        int keyIdx = str.indexOf("\"" + key + "\"");
        if (keyIdx == -1) return "";
        
        int colonIdx = str.indexOf(":", keyIdx);
        int numStart = colonIdx + 1;
        
        // Skip whitespace
        while (numStart < str.length() && Character.isWhitespace(str.charAt(numStart))) {
            numStart++;
        }
        
        int numEnd = numStart;
        while (numEnd < str.length() && (Character.isDigit(str.charAt(numEnd)) || str.charAt(numEnd) == '.')) {
            numEnd++;
        }
        
        if (numStart >= numEnd) return "";
        
        return str.substring(numStart, numEnd);
    }

    private String toJSON(List<Map<String, Object>> bookings) {
        StringBuilder json = new StringBuilder("[\n");
        
        for (int i = 0; i < bookings.size(); i++) {
            Map<String, Object> booking = bookings.get(i);
            json.append("  {\n");
            json.append("    \"movieTitle\": \"").append(booking.get("movieTitle")).append("\",\n");
            json.append("    \"price\": ").append(booking.get("price")).append(",\n");
            json.append("    \"totalPrice\": ").append(booking.get("totalPrice")).append(",\n");
            json.append("    \"timestamp\": \"").append(booking.get("timestamp")).append("\",\n");
            json.append("    \"seats\": [\n");
            
            @SuppressWarnings("unchecked")
            List<Map<String, Integer>> seats = (List<Map<String, Integer>>) booking.get("seats");
            for (int j = 0; j < seats.size(); j++) {
                Map<String, Integer> seat = seats.get(j);
                json.append("      {\"row\": ").append(seat.get("row")).append(", \"col\": ").append(seat.get("col")).append("}");
                if (j < seats.size() - 1) json.append(",");
                json.append("\n");
            }
            
            json.append("    ]\n");
            json.append("  }");
            if (i < bookings.size() - 1) json.append(",");
            json.append("\n");
        }
        
        json.append("]");
        return json.toString();
    }
}
