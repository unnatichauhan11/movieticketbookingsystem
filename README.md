# FlickBooK

### Key Updates:
- Removed CSV file handling code
- Implemented JSON-based serialization using `org.json` library
- Created structured JSON format for better data organization
- Added pretty-printing for readable JSON output
- Each booking now includes: movieTitle, price, totalPrice, timestamp, and seats array

## JSON File Structure

```json
{
  "bookings": [
    {
      "movieTitle": "Movie Name",
      "price": 200.0,
      "totalPrice": 400.0,
      "timestamp": "Date and Time",
      "seats": [
        {"row": 0, "col": 1},
        {"row": 0, "col": 2}
      ]
    }
  ]
}
```

## Setup & Compilation

### Prerequisites:
- Java JDK 8 or higher
- org.json library (json-20231013.jar or latest version)

### Step 1: Download org.json Library
```bash
# Download from Maven Central Repository
# Link: https://repo1.maven.org/maven2/org/json/json/20231013/json-20231013.jar
```

### Step 2: Compile the Java File

**For Windows (with json.jar in the same directory):**
```bash
javac -cp json-20231013.jar MovieBookingSystem.java
```

**For Linux/Mac:**
```bash
javac -cp json-20231013.jar:. MovieBookingSystem.java
```

### Step 3: Run the Application

**For Windows:**
```bash
java -cp .;json-20231013.jar MovieBookingSystem
```

**For Linux/Mac:**
```bash
java -cp .:json-20231013.jar MovieBookingSystem
```

### Alternative: Using Maven (pom.xml)

If you have Maven installed, use the provided `pom.xml` file:

```bash
# Compile
mvn compile

# Run
mvn exec:java -Dexec.mainClass="MovieBookingSystem"
```

## Features

✅ Movie selection dropdown
✅ Interactive seat selection (Green = Available, Red = Booked)
✅ Real-time price calculation
✅ Booking confirmation dialog
✅ JSON-based persistent storage
✅ Automatic booking loading on startup
✅ Timestamp tracking for each booking

## File Structure

```
movie ticket booking system/
├── MovieBookingSystem.java      (Main application)
├── bookings.json               (Stores all bookings)
├── json-20231013.jar           (Required dependency)
├── pom.xml                     (Maven configuration - optional)
└── README.md                   (This file)
```

## JSON vs CSV Changes

### Old CSV Format:
```
Movie Title,Row,Col,Price
Avengers: Endgame,0,1,200
Avengers: Endgame,0,2,200
```

### New JSON Format:
Structured, hierarchical format with complete booking details including timestamp and total price per booking.

## Troubleshooting

**Issue**: "ClassNotFoundException: org.json.JSONObject"
- **Solution**: Ensure json.jar is in the classpath during compilation and execution

**Issue**: "bookings.json not found"
- **Solution**: The application will create it automatically on first booking

**Issue**: Malformed JSON error
- **Solution**: Delete bookings.json and restart - a new one will be created

