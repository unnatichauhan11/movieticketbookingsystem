# Railway Ticket Booking System

A modular C-based railway ticket booking system with seat management, passenger tracking, and a waiting list.

## Project Structure

```
.
├── include/          # Header files
│   ├── common.h      # Utility functions and constants
│   ├── passenger.h   # Passenger data structures
│   ├── queue.h       # Waiting queue structures
│   └── booking.h     # Booking operations
├── src/              # Source files
│   ├── main.c        # Main program loop
│   ├── common.c      # Utility implementations
│   ├── passenger.c   # Passenger management
│   ├── queue.c       # Queue management
│   ├── payment.c     # Payment processing
│   ├── file_io.c     # CSV save/load operations
│   └── booking.c     # Booking logic
├── Makefile          # Build configuration
└── README.md         # This file
```

## Features

- **Modular Architecture**: Clean separation of concerns with dedicated modules
- **Seat Management**: Track seat availability (Empty, On Hold, Confirmed)
- **Passenger Tracking**: Manage booked passengers with unique PNR codes
- **Waiting List**: Queue system for fully booked scenarios
- **Persistent Storage**: CSV-based data persistence
- **Payment Processing**: Simulate multiple payment methods

## Building the Project

### Prerequisites
- GCC compiler
- Make utility

### Compile
```bash
make
```

### Run
```bash
make run
```

### Clean Build
```bash
make rebuild
```

## Menu Options

1. **Book Ticket** - Reserve a seat with payment
2. **View Confirmed Passengers** - List all booked passengers
3. **View Waiting List** - Show passengers in queue
4. **Cancel Ticket** - Remove a booking and auto-promote from waiting list
5. **Show Seat Map** - Display seat availability
6. **Exit** - Save and close the application

## Data Storage

- `confirmed.csv` - Confirmed passenger bookings
- `waiting.csv` - Waiting list entries

## License

MIT License
