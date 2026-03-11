# Changelog - CSV to JSON Migration

## Summary of Changes

This document outlines all modifications made to convert the Movie Ticket Booking System from CSV to JSON storage.

---

## 1. Import Changes

**REMOVED:**
```java
import java.io.*;
```

**ADDED:**
```java
import java.io.*;
import java.nio.file.*;
import org.json.*;
```

---

## 2. File Name Constant Update

**BEFORE:**
```java
private static final String CSV_FILE = "bookings.csv";
```

**AFTER:**
```java
private static final String JSON_FILE = "bookings.json";
```

---

## 3. Constructor Changes

**BEFORE:**
```java
loadBookingsFromCSV();
```

**AFTER:**
```java
loadBookingsFromJSON();
```

---

## 4. confirmBooking() Method Changes

**BEFORE:**
```java
saveBookingToCSV(currentBooking); // ✅ Save to CSV
```

**AFTER:**
```java
saveBookingToJSON(currentBooking); // ✅ Save to JSON
```

---

## 5. saveBookingToCSV() → saveBookingToJSON() Method

### Old Implementation (CSV):
```java
private void saveBookingToCSV(Booking booking) {
    try (FileWriter writer = new FileWriter(CSV_FILE, true)) {
        for (Seat s : booking.bookedSeats) {
            writer.write(booking.movie.title + "," + s.row + "," + s.col + "," + booking.movie.price + "\n");
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

### New Implementation (JSON):
```java
private void saveBookingToJSON(Booking booking) {
    try {
        JSONObject bookingObj = new JSONObject();
        bookingObj.put("movieTitle", booking.movie.title);
        bookingObj.put("price", booking.movie.price);
        bookingObj.put("totalPrice", booking.totalPrice);
        bookingObj.put("timestamp", new Date().toString());

        JSONArray seatsArray = new JSONArray();
        for (Seat s : booking.bookedSeats) {
            JSONObject seatObj = new JSONObject();
            seatObj.put("row", s.row);
            seatObj.put("col", s.col);
            seatsArray.put(seatObj);
        }
        bookingObj.put("seats", seatsArray);

        JSONObject rootObj;
        File file = new File(JSON_FILE);
        if (file.exists()) {
            String content = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
            rootObj = new JSONObject(content);
        } else {
            rootObj = new JSONObject();
            rootObj.put("bookings", new JSONArray());
        }

        rootObj.getJSONArray("bookings").put(bookingObj);

        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            writer.write(rootObj.toString(2)); // Pretty print
        }
    } catch (IOException | JSONException e) {
        // ... error handling
    }
}
```

**Key Improvements:**
- Structured hierarchical format
- Includes timestamp for each booking
- Stores total price per booking
- Pretty-printed for readability
- Better error handling (JSONException)
- Preserves existing bookings when adding new ones

---

## 6. loadBookingsFromCSV() → loadBookingsFromJSON() Method

### Old Implementation (CSV):
```java
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
```

### New Implementation (JSON):
```java
private void loadBookingsFromJSON() {
    try {
        File file = new File(JSON_FILE);
        if (!file.exists()) return;

        String content = new String(Files.readAllBytes(Paths.get(JSON_FILE)));
        JSONObject rootObj = new JSONObject(content);
        JSONArray bookingsArray = rootObj.getJSONArray("bookings");

        for (int i = 0; i < bookingsArray.length(); i++) {
            JSONObject bookingObj = bookingsArray.getJSONObject(i);
            String movieTitle = bookingObj.getString("movieTitle");
            JSONArray seatsArray = bookingObj.getJSONArray("seats");

            Show show = shows.get(movieTitle);
            if (show != null) {
                for (int j = 0; j < seatsArray.length(); j++) {
                    JSONObject seatObj = seatsArray.getJSONObject(j);
                    int row = seatObj.getInt("row");
                    int col = seatObj.getInt("col");

                    if (row < show.seats.length && col < show.seats[0].length) {
                        show.seats[row][col].isBooked = true;
                    }
                }
            }
        }
    } catch (IOException | JSONException e) {
        e.printStackTrace();
    }
}
```

**Key Improvements:**
- Parses structured JSON instead of comma-separated values
- Handles nested arrays properly
- Better error handling (JSONException)
- More robust and maintainable code

---

## 7. New Features Added

### ✅ Timestamp Tracking
Each booking now includes a timestamp of when it was made

### ✅ Total Price Storage
Tracks the total price paid for each booking session

### ✅ Pretty-Printed JSON
Output JSON is formatted with 2-space indentation for easy readability

### ✅ Hierarchical Structure
All bookings are organized under a "bookings" array at the root level

### ✅ Better Error Handling
Added JSONException handling in addition to IOException

---

## 8. Data Structure Comparison

### CSV Output (Old):
```
Avengers: Endgame,0,1,200
Avengers: Endgame,0,2,200
Interstellar,1,3,250
```

### JSON Output (New):
```json
{
  "bookings": [
    {
      "movieTitle": "Avengers: Endgame",
      "price": 200.0,
      "totalPrice": 400.0,
      "timestamp": "Wed Mar 11 10:30:45 IST 2026",
      "seats": [
        {"row": 0, "col": 1},
        {"row": 0, "col": 2}
      ]
    }
  ]
}
```

---

## 9. Dependencies Added

- **org.json** (version 20231013)
  - Used for JSON parsing and serialization
  - Located in Maven Central Repository

---

## 10. Files Modified

| File | Changes |
|------|---------|
| MovieBookingSystem.java | All CSV methods replaced with JSON, added imports, updated variable names |
| bookings.csv | REMOVED |
| bookings.json | NEW - Sample format provided |
| pom.xml | NEW - Maven configuration |
| README.md | NEW - Setup and usage guide |
| compile_and_run.bat | NEW - Windows batch script |

---

## 11. Backward Compatibility

⚠️ **Important**: The new JSON-based system is NOT backward compatible with the old CSV format.
- If you have existing `bookings.csv` files, you'll need to manually migrate data or delete them to start fresh.
- The application will create a new `bookings.json` file automatically.

---

## 12. Testing

After migration, verify:
- ✅ Application starts without errors
- ✅ JSON file is created on first booking
- ✅ Bookings persist across application restarts
- ✅ JSON file is properly formatted and readable
- ✅ All movie seats are marked correctly on reload

---

## Summary

The migration from CSV to JSON provides:
- **Better Data Organization**: Hierarchical structure
- **Richer Information**: Timestamps and totals included
- **Improved Readability**: Pretty-printed JSON format
- **Enhanced Maintainability**: Easier to parse and extend
- **Better Error Handling**: JSONException support

Total lines of code changed: ~60 lines
New functionality added: Timestamp tracking, JSON serialization
