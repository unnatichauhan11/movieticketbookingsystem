CC = gcc
CFLAGS = -Wall -Wextra -std=c99 -I./include
SRCDIR = src
OBJDIR = obj
BINDIR = bin
TARGET = $(BINDIR)/booking_system

# Source files
SOURCES = $(SRCDIR)/main.c $(SRCDIR)/common.c $(SRCDIR)/passenger.c $(SRCDIR)/queue.c $(SRCDIR)/payment.c $(SRCDIR)/file_io.c $(SRCDIR)/booking.c
OBJECTS = $(SOURCES:$(SRCDIR)/%.c=$(OBJDIR)/%.o)

# Default target
all: $(BINDIR) $(OBJDIR) $(TARGET)

# Create directories
$(BINDIR):
	mkdir -p $(BINDIR)

$(OBJDIR):
	mkdir -p $(OBJDIR)

# Link object files
$(TARGET): $(OBJECTS)
	$(CC) $(CFLAGS) -o $@ $^
	@echo "Build successful! Run with: ./$(TARGET)"

# Compile source files
$(OBJDIR)/%.o: $(SRCDIR)/%.c
	$(CC) $(CFLAGS) -c $< -o $@

# Clean build artifacts
clean:
	rm -rf $(OBJDIR) $(BINDIR)
	@echo "Cleaned build artifacts"

# Rebuild everything
rebuild: clean all

# Run the program
run: $(TARGET)
	./$(TARGET)

# Phony targets
.PHONY: all clean rebuild run
