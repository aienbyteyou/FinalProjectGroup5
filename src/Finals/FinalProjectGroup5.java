/*
Date: December 03, 2025
Group Members:
1. Bungulan, Chaste
2. Cimatu, Ernst
3. Give, Aura Vienn
4. Javier, Chad
5. Manzano, Jay Archie
6. Velasco, Brian
7. Vidal, Giovanni

Algorithm:
1. Start program.
2. Initialize all books: for each category (4) and each book (10), set status = “Available” and clear borrower, access type, days, cost.
3. Repeat until user chooses Exit:
3.1. Show menu: Borrow, Return, Check Available, Search, Exit.
3.2. Read user choice.
3.3. If Borrow → go to step 4.
3.4. If Return → go to step 5.
3.5. If Check Available → go to step 6.
3.6. If Search → go to step 7.
3.7. If Exit → end loop.

4. Borrow Book:
4.1. Ask for category; if invalid, stop this operation.
4.2. List only “Available” books in that category with new numbers.
4.3. If none, stop this operation.
4.4. Ask user to choose a listed book; validate.
4.5. Ask for access type: Overnight, Multi-Day (ask days), or Monthly; compute cost.
4.6. Ask for borrower name; if empty, stop.
4.7. Confirm book still “Available”; if not, stop.
4.8. Show summary; call payment.
4.9. If payment fails, stop.
4.10. If payment succeeds, set status = “Borrowed” and save borrower, access type, days, cost.

5. Return Book:
5.1. Ask for borrower name.
5.2. Search all books for status “Borrowed” with that name.
5.3. If not found, inform user.
5.4. If found, show book, then reset that slot to “Available” and clear data.

6. Check Available Books:
6.1. Ask for category; validate.
6.2. List all books in that category with status “Available”; if none, say so.

7. Search Book:
7.1. Ask for full/partial title.
7.2. Search all books whose titles contain the term (case-insensitive).
7.3. For each match, show title, category, and status (with borrower if borrowed).
7.4. If no matches, say so.

8. End program. */

package Finals;

import java.util.Scanner;

public class FinalProjectGroup5 {

    // Shared Scanner for all user keyboard input
    static Scanner kbd = new Scanner(System.in);

    // Book categories (row index in 2D arrays)
    static String[] categories = {"Fiction", "Non-Fiction", "Science", "History"};

    // Book titles per category (4 categories x 10 books)
    static String[][] bookTitles = {
            {"The Silent Library", "Starlight Kingdom", "Echoes of Tomorrow", "The Lost Cartographer",
                    "Moonlit Chronicles", "Forest of Whispers", "The Glass Voyager", "House of Falling Stars",
                    "A Tale of Two Voyagers", "The Ember Archive"},
            {"Age of AI", "A Brief History of Time", "Bio Ethics Today.", "Global Warming: The Facts",
                    "The Space Race Chronicle", "Art Theory and Practice", "Economics 101", "The Human Brain",
                    "Anatomy of the Body", "Practical Philosophy"},
            {"Quantum Realities", "Astrobiology Frontier", "The Particle Puzzle", "Evolution of Life",
                    "Deep Ocean Mysteries", "Chemical Foundations", "Solar System Odyssey", "Applied Robotics",
                    "Introduction to Genetics", "Environmental Science Today"},
            {"Medieval Ages", "Ancient Civilizations", "The Roman Empire Legacy", "World War Chronicles",
                    "Rise of Nations", "The Silk Road Story", "History of Inventions", "The Age of Exploration",
                    "Revolutions That Shaped the World", "The Modern Century"}
    };

    // Parallel arrays to track status and borrowing details
    static String[][] status = new String[4][10];        // "Available" or "Borrowed"
    static String[][] borrowerName = new String[4][10];  // name of borrower
    static String[][] accessType = new String[4][10];    // "Overnight", "Multi-Day", or "Monthly"
    static int[][] daysBorrowed = new int[4][10];        // number of days for Multi-Day
    static int[][] cost = new int[4][10];                // last rent cost paid for that book

    // Main method: shows menu and routes user to chosen operation
    public static void main(String[] args) {

        initializeBooks();  // set all books to Available with cleared data

        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=== City Library System ===");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. Check Available Books");
            System.out.println("4. Search Book");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");

            int choice = readInt();  // safe integer input with validation

            // validate main menu choice range
            while (choice < 1 || choice > 5) {
                System.out.print("Invalid choice. Please enter 1-5: ");
                choice = readInt();
            }

            switch (choice) {
                case 1:
                    borrowBook();        // handle borrowing flow
                    break;
                case 2:
                    returnBook();        // handle return flow
                    break;
                case 3:
                    checkAvailableBooks(); // show available books in a category
                    break;
                case 4:
                    searchBooks();       // search books by title
                    break;
                case 5:
                    System.out.println("Thank you for using the Library Management System. Goodbye!");
                    running = false;     // exit loop and end program
                    break;
            }
        }

        kbd.close();  // close Scanner when program ends
    }

    // initializeBooks: sets all book entries to default "Available" and clears borrower data
    static void initializeBooks() {
        for (int c = 0; c < 4; c++) {
            for (int b = 0; b < 10; b++) {
                status[c][b] = "Available";
                borrowerName[c][b] = "";
                accessType[c][b] = "";
                daysBorrowed[c][b] = 0;
                cost[c][b] = 0;
            }
        }
    }

    // borrowBook: lets user choose a category, pick an available book, choose access type, pay, then mark as borrowed
    static void borrowBook() {

        System.out.println();
        System.out.println("Select Book Category");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        System.out.print("Enter category number: ");
        int catChoice = readInt();
        while (catChoice < 1 || catChoice > categories.length) {
            System.out.print("Invalid category. Please enter 1-" + categories.length + ": ");
            catChoice = readInt();
        }
        int categoryIndex = catChoice - 1;

        System.out.println();
        System.out.println("Available " + categories[categoryIndex] + " Books:");

        // mapIndex stores the real book index for each displayed number
        int[] mapIndex = new int[10];
        int displayCount = 0;

        for (int i = 0; i < 10; i++) {
            if (status[categoryIndex][i].equals("Available")) {
                displayCount++;
                mapIndex[displayCount - 1] = i;
                System.out.println(displayCount + ". " + bookTitles[categoryIndex][i]);
            }
        }

        if (displayCount == 0) {
            System.out.println("No available books in this category.");
            return;
        }

        System.out.print("Enter book number to borrow (1-" + displayCount + "): ");
        int chosenDisplay = readInt();
        while (chosenDisplay < 1 || chosenDisplay > displayCount) {
            System.out.print("Invalid book number. Enter 1-" + displayCount + ": ");
            chosenDisplay = readInt();
        }

        int bookIndex = mapIndex[chosenDisplay - 1];

        System.out.println();
        System.out.println("Choose Access Type");
        System.out.println("1. Overnight (₱50 flat rate)");
        System.out.println("2. Multi-Day (₱100 per day)");
        System.out.println("3. Monthly (₱1000 flat rate)");
        System.out.print("Enter access type number: ");
        int accessChoice = readInt();
        while (accessChoice < 1 || accessChoice > 3) {
            System.out.print("Invalid type. Please enter 1-3: ");
            accessChoice = readInt();
        }

        String access;
        int totalCost = 0;
        int days = 0;

        if (accessChoice == 1) {
            access = "Overnight";
            totalCost = 50;
        } else if (accessChoice == 2) {
            access = "Multi-Day";
            System.out.print("Enter number of days: ");
            days = readInt();
            while (days <= 0) {
                System.out.print("Please enter a positive number of days: ");
                days = readInt();
            }
            totalCost = 100 * days;
        } else {
            access = "Monthly";
            totalCost = 1000;
        }

        System.out.print("Enter Borrower Name: ");
        String borrower = kbd.nextLine().trim();
        if (borrower.isEmpty()) {
            System.out.println("Borrower name cannot be empty.");
            return;
        }

        // Block double-borrow of the same book by the same borrower
        if (status[categoryIndex][bookIndex].equals("Borrowed") &&
                borrowerName[categoryIndex][bookIndex].equalsIgnoreCase(borrower)) {
            System.out.println("You already borrowed this book.");
            return;
        }

        // Check if borrower already has other books (warning only)
        boolean hasOther = false;
        for (int c = 0; c < 4; c++) {
            for (int b = 0; b < 10; b++) {
                if (status[c][b].equals("Borrowed") &&
                        borrowerName[c][b].equalsIgnoreCase(borrower)) {
                    hasOther = true;
                    break;
                }
            }
            if (hasOther) break;
        }
        if (hasOther) {
            System.out.println("Note: You already have other borrowed book(s).");
        }

        // Final availability check before confirming
        if (!status[categoryIndex][bookIndex].equals("Available")) {
            System.out.println("Sorry, that book is no longer available.");
            return;
        }

        // Show summary of the planned borrowing
        System.out.println();
        System.out.println("Borrowing Summary");
        System.out.println("Book: " + bookTitles[categoryIndex][bookIndex]);
        System.out.println("Category: " + categories[categoryIndex]);
        if (access.equals("Multi-Day")) {
            System.out.println("Access Type: Multi-Day (" + days + " days)");
            System.out.println("Borrower: " + borrower);
            System.out.println("Total Cost: ₱100 * " + days + " = ₱" + totalCost);
        } else {
            System.out.println("Access Type: " + access);
            System.out.println("Borrower: " + borrower);
            System.out.println("Total Cost: ₱" + totalCost);
        }

        // Process payment; only proceed if successful
        boolean paid = processPayment(totalCost);

        if (!paid) {
            System.out.println("Borrowing NOT confirmed. Book remains available.");
            return;
        }

        // Save borrowing details into parallel arrays
        status[categoryIndex][bookIndex] = "Borrowed";
        borrowerName[categoryIndex][bookIndex] = borrower;
        accessType[categoryIndex][bookIndex] = access;
        daysBorrowed[categoryIndex][bookIndex] = days;
        cost[categoryIndex][bookIndex] = totalCost;

        System.out.println();
        System.out.println("Borrowing Confirmed");
        System.out.println("Book '" + bookTitles[categoryIndex][bookIndex]
                + "' is now borrowed by " + borrower + ".");
    }

    // processPayment: asks for payment, checks amount, simulates 10% system error, returns true on success
    static boolean processPayment(int costAmount) {
        System.out.println();
        System.out.println("Payment");
        System.out.print("Please enter payment amount (₱" + costAmount + "): ");
        int payment = readInt();

        // Reject negative payment amounts
        while (payment < 0) {
            System.out.print("Amount cannot be negative. Enter again: ");
            payment = readInt();
        }

        // Not enough money to pay the required cost
        if (payment < costAmount) {
            System.out.println("Payment Failed: Insufficient amount!");
            System.out.println("You are short of ₱" + (costAmount - payment));
            return false;
        }

        // Random chance to simulate a system error
        double chance = Math.random();
        if (chance > 0.1) {
            int change = payment - costAmount;
            System.out.println("**Payment Successful!**");
            if (change > 0) {
                System.out.println("Change Due: ₱" + change);
            }
            return true;
        } else {
            System.out.println("Payment Failed: System Error");
            System.out.println("Borrowing NOT Confirmed. Refunded ₱" + payment);
            return false;
        }
    }

    // returnBook: finds all books borrowed by a name, lets user choose which book to return, then resets its data
    static void returnBook() {
        System.out.print("\nEnter borrower's full name: ");
        String borrower = kbd.nextLine().trim();

        if (borrower.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        // Arrays to store found borrowed books (up to 40 total)
        int[] catIndex = new int[40];
        int[] bookIndex = new int[40];
        int count = 0;

        // Collect all books currently borrowed by this person
        for (int c = 0; c < 4; c++) {
            for (int b = 0; b < 10; b++) {
                if (status[c][b].equals("Borrowed") &&
                        borrowerName[c][b].equalsIgnoreCase(borrower)) {
                    catIndex[count] = c;
                    bookIndex[count] = b;
                    count++;
                }
            }
        }

        if (count == 0) {
            System.out.println("No borrowed book found under that name.");
            return;
        }

        System.out.println("Borrowed books:");
        for (int i = 0; i < count; i++) {
            System.out.println((i + 1) + ". " + bookTitles[catIndex[i]][bookIndex[i]] +
                    " (Category: " + categories[catIndex[i]] + ")");
        }

        // If there is more than one book, ask which one to return
        int choice;
        if (count == 1) {
            choice = 1;
        } else {
            System.out.print("Enter which book number to return (1-" + count + "): ");
            choice = readInt();
            while (choice < 1 || choice > count) {
                System.out.print("Invalid choice. Enter 1-" + count + ": ");
                choice = readInt();
            }
        }

        int c = catIndex[choice - 1];
        int b = bookIndex[choice - 1];

        // Reset all data for that book slot
        status[c][b] = "Available";
        borrowerName[c][b] = "";
        accessType[c][b] = "";
        daysBorrowed[c][b] = 0;
        cost[c][b] = 0;

        System.out.println("Book successfully returned by " + borrower + ".");
        System.out.println("Book slot is now available.");
    }

    // checkAvailableBooks: lets user pick a category and lists all books that are still "Available"
    static void checkAvailableBooks() {
        System.out.println();
        System.out.println("Select Category");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        System.out.print("Enter category number: ");
        int catChoice = readInt();
        while (catChoice < 1 || catChoice > categories.length) {
            System.out.print("Invalid category. Please enter 1-" + categories.length + ": ");
            catChoice = readInt();
        }
        int categoryIndex = catChoice - 1;

        System.out.println();
        System.out.println("Available " + categories[categoryIndex] + " Books:");
        boolean anyAvailable = false;

        // Print all titles still marked as Available
        for (int i = 0; i < 10; i++) {
            if (status[categoryIndex][i].equals("Available")) {
                anyAvailable = true;
                System.out.println((i + 1) + ". " + bookTitles[categoryIndex][i]);
            }
        }

        if (!anyAvailable) {
            System.out.println("No available books in this category.");
        }
    }

    // searchBooks: searches all titles for a case-insensitive match to a user-provided term and prints results
    static void searchBooks() {
        System.out.print("\nEnter book name (full or partial): ");
        String term = kbd.nextLine().trim();

        if (term.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        String lowerTerm = term.toLowerCase();
        System.out.println();
        System.out.println("Search Results for '" + term + "'");
        boolean found = false;

        // Scan all titles and print details for each match
        for (int c = 0; c < 4; c++) {
            for (int b = 0; b < 10; b++) {
                if (bookTitles[c][b].toLowerCase().contains(lowerTerm)) {
                    found = true;
                    System.out.print("Book: " + bookTitles[c][b]);
                    System.out.print(" | Category: " + categories[c]);
                    if (status[c][b].equals("Available")) {
                        System.out.println(" | Status: Available");
                    } else {
                        System.out.println(" | Status: Borrowed by " + borrowerName[c][b]);
                    }
                }
            }
        }

        if (!found) {
            System.out.println("No books found matching the search term.");
        }
    }

    // readInt: reads a whole line from the user and converts it to int, re-asking on invalid input
    static int readInt() {
        while (true) {
            String line = kbd.nextLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }
}
