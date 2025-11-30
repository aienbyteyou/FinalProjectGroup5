package Finals;

import java.util.Scanner;

public class LibraryManagementSystemG5 {
 
    // One shared Scanner for all user input
    static Scanner kbd = new Scanner(System.in);

    // List of categories. Index 0 = Fiction, 1 = Non-Fiction, etc.
    static String[] categories = {"Fiction", "Non-Fiction", "Science", "History"};

    // 2D array: [category][bookIndex]
    // Each row corresponds to one category, each column to a specific book
    static String[][] bookTitles = {
            // Fiction (index 0)
            {"The Silent Library", "Starlight Kingdom", "Echoes of Tomorrow", "The Lost Cartographer",
                    "Moonlit Chronicles", "Forest of Whispers", "The Glass Voyager", "House of Falling Stars",
                    "A Tale of Two Voyagers", "The Ember Archive"},
            // Non-Fiction (index 1)
            {"Age of AI", "A Brief History of Time", "Bio Ethics Today.", "Global Warming: The Facts",
                    "The Space Race Chronicle", "Art Theory and Practice", "Economics 101", "The Human Brain",
                    "Anatomy of the Body", "Practical Philosophy"},
            // Science (index 2)
            {"Quantum Realities", "Astrobiology Frontier", "The Particle Puzzle", "Evolution of Life",
                    "Deep Ocean Mysteries", "Chemical Foundations", "Solar System Odyssey", "Applied Robotics",
                    "Introduction to Genetics", "Environmental Science Today"},
            // History (index 3)
            {"Medieval Ages", "Ancient Civilizations", "The Roman Empire Legacy", "World War Chronicles",
                    "Rise of Nations", "The Silk Road Story", "History of Inventions", "The Age of Exploration",
                    "Revolutions That Shaped the World", "The Modern Century"}
    };

    // These arrays store info for each [category][book]
    static String[][] status = new String[4][10];        // "Available" or "Borrowed"
    static String[][] borrowerName = new String[4][10];  // who borrowed it
    static String[][] accessType = new String[4][10];    // "Overnight", "Multi-Day", or "Monthly"
    static int[][] daysBorrowed = new int[4][10];        // only used for Multi-Day
    static int[][] cost = new int[4][10];                // last rent cost for that book

    public static void main(String[] args) {

        // Set initial values for all books
        initializeBooks();

        boolean running = true; // controls the main menu loop

        // Main program loop – keeps showing menu until user chooses Exit
        while (running) {
            System.out.println();
            System.out.println("Welcome to the City Library System");
            System.out.println("1. Borrow Book");
            System.out.println("2. Return Book");
            System.out.println("3. Check Available Books");
            System.out.println("4. Search Book");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");

            int choice = readInt(); // safe integer input

            // Decide what to do based on user choice
            switch (choice) {
                case 1:
                    borrowBook(); // handle borrowing flow
                    break;
                case 2:
                    returnBook(); // handle return flow
                    break;
                case 3:
                    checkAvailableBooks(); // show available books for one category
                    break;
                case 4:
                    searchBooks(); // search by name
                    break;
                case 5:
                    System.out.println("Thank you for using the Library Management System. Goodbye!");
                    running = false; // exit loop
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1–5.");
            }
        }

        // Good practice: close Scanner when done
        kbd.close();
    }

    // Initialize all book slots to default values
    static void initializeBooks() {
        // Loop through each category
        for (int c = 0; c < 4; c++) {
            // Loop through each book in that category
            for (int b = 0; b < 10; b++) {
                status[c][b] = "Available";
                borrowerName[c][b] = "";
                accessType[c][b] = "";
                daysBorrowed[c][b] = 0;
                cost[c][b] = 0;
            }
        }
    }

    // ===================== BORROW BOOK =====================
    // Full flow: pick category → pick book → pick access type → pay → update arrays
    static void borrowBook() {

        System.out.println();
        System.out.println("Select Book Category");
        // Show numbered category list (1-based for user)
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        System.out.print("Enter category number: ");

        int categoryIndex = readInt() - 1; // convert 1–4 to 0–3

        // Validate category index
        if (categoryIndex < 0 || categoryIndex >= categories.length) {
            System.out.println("Invalid category.");
            return; // stop borrow process
        }

        // Now show only the books that are currently Available in that category
        System.out.println();
        System.out.println("Available " + categories[categoryIndex] + " Books:");

        // mapIndex will store the real array index of each displayed book
        // Example: user sees 1,2,3 but maybe real indexes are 0,4,7
        int[] mapIndex = new int[10];
        int displayCount = 0; // how many books we actually showed

        for (int i = 0; i < 10; i++) {
            // Check if this book is available
            if (status[categoryIndex][i].equals("Available")) {
                displayCount++;
                // store real index at position (displayCount - 1)
                mapIndex[displayCount - 1] = i;
                System.out.println(displayCount + ". " + bookTitles[categoryIndex][i]);
            }
        }

        // If nothing was displayed, no books are available in this category
        if (displayCount == 0) {
            System.out.println("No available books in this category.");
            return;
        }

        // Ask user which of the displayed books they want to borrow
        System.out.print("Enter book number to borrow (1-" + displayCount + "): ");
        int chosenDisplay = readInt();

        // Validate display choice
        if (chosenDisplay < 1 || chosenDisplay > displayCount) {
            System.out.println("Invalid book number.");
            return;
        }

        // Convert chosen display number back to the actual book index
        int bookIndex = mapIndex[chosenDisplay - 1];

        // Ask for access type (rental plan)
        System.out.println();
        System.out.println("Choose Access Type");
        System.out.println("1. Overnight (₱50 flat rate)");
        System.out.println("2. Multi-Day (₱100 per day)");
        System.out.println("3. Monthly (₱1000 flat rate)");
        System.out.print("Enter access type number: ");
        int accessChoice = readInt();

        String access;   // will store "Overnight", "Multi-Day", or "Monthly"
        int totalCost = 0;
        int days = 0;    // only used if Multi-Day

        // Determine access type and cost
        switch (accessChoice) {
            case 1:
                access = "Overnight";
                totalCost = 50;
                break;
            case 2:
                access = "Multi-Day";
                System.out.print("Enter number of days: ");
                days = readInt();
                if (days <= 0) {
                    System.out.println("Invalid number of days.");
                    return;
                }
                totalCost = 100 * days; // 100 per day
                break;
            case 3:
                access = "Monthly";
                totalCost = 1000;
                break;
            default:
                System.out.println("Invalid access type.");
                return;
        }

        // Get borrower name, must not be empty
        System.out.print("Enter Borrower Name: ");
        String borrower = kbd.nextLine().trim();
        if (borrower.isEmpty()) {
            System.out.println("Borrower name cannot be empty.");
            return;
        }

        // Safety check: ensure book is still available
        if (!status[categoryIndex][bookIndex].equals("Available")) {
            System.out.println("Sorry, that book is no longer available.");
            return;
        }

        // Show a summary so user can confirm mentally before paying
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

        // Call payment method. If this returns false, booking will not proceed.
        boolean paid = processPayment(totalCost);

        if (!paid) {
            System.out.println("Borrowing NOT confirmed. Book remains available.");
            return;
        }

        // Payment succeeded → update all related arrays for this book
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

    // ===================== PAYMENT =====================
    // Asks the user to pay costAmount. There is a 10% chance of "system error".
    public static boolean processPayment(int costAmount) {
        System.out.println();
        System.out.println("Payment");
        System.out.print("Please enter payment amount (₱" + costAmount + "): ");

        int payment = readInt();

        // If user pays less than required amount
        if (payment < costAmount) {
            System.out.println("Payment Failed: Insufficient amount!");
            System.out.println("You are short of ₱" + (costAmount - payment));
            return false;
        }

        // Generate a random number from 0.0 to <1.0
        double chance = Math.random();

        // 90% chance of success (chance > 0.1)
        if (chance > 0.1) {
            int change = payment - costAmount;
            System.out.println("**Payment Successful!**");
            if (change > 0) {
                System.out.println("Change Due: ₱" + change);
            }
            return true;
        } else {
            // 10% of the time, simulate a system error
            System.out.println("Payment Failed: System Error");
            System.out.println("Borrowing NOT Confirmed");
            return false;
        }
    }

    // ===================== RETURN BOOK =====================
    // Finds a book by borrower name and marks it as available again
    static void returnBook() {
        System.out.print("\nEnter borrower's full name: ");
        String borrower = kbd.nextLine().trim();

        if (borrower.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        int foundCategory = -1; // will hold category index of found book
        int foundBook = -1;     // will hold book index of found book

        // Search through all categories and books
        for (int c = 0; c < 4; c++) {
            for (int b = 0; b < 10; b++) {
                // Match both status and borrower name (case-insensitive)
                if (status[c][b].equals("Borrowed") &&
                        borrowerName[c][b].equalsIgnoreCase(borrower)) {
                    foundCategory = c;
                    foundBook = b;
                    break; // break inner loop
                }
            }
            if (foundCategory != -1) break; // break outer loop if found
        }

        // If we didn't find any matching borrowed book
        if (foundCategory == -1) {
            System.out.println("No borrowed book found under that name.");
            return;
        }

        // Show which book was found
        System.out.println("Found book: '" + bookTitles[foundCategory][foundBook] +
                "' (Category: " + categories[foundCategory] + ")");
        System.out.println("Return Processed:");
        System.out.println("Book successfully returned by " + borrower + ".");
        System.out.println("Book slot is now available.");

        // Reset arrays to default values for that slot
        status[foundCategory][foundBook] = "Available";
        borrowerName[foundCategory][foundBook] = "";
        accessType[foundCategory][foundBook] = "";
        daysBorrowed[foundCategory][foundBook] = 0;
        cost[foundCategory][foundBook] = 0;
    }

    // ===================== CHECK AVAILABLE BOOKS =====================
    // Lets user pick a category and then shows all available books in that category
    static void checkAvailableBooks() {
        System.out.println();
        System.out.println("Select Category");
        for (int i = 0; i < categories.length; i++) {
            System.out.println((i + 1) + ". " + categories[i]);
        }
        System.out.print("Enter category number: ");
        int categoryIndex = readInt() - 1;

        // Validate category index
        if (categoryIndex < 0 || categoryIndex >= categories.length) {
            System.out.println("Invalid category.");
            return;
        }

        System.out.println();
        System.out.println("Available " + categories[categoryIndex] + " Books:");
        boolean anyAvailable = false;

        // Loop through all books in selected category
        for (int i = 0; i < 10; i++) {
            if (status[categoryIndex][i].equals("Available")) {
                anyAvailable = true;
                System.out.println((i + 1) + ". " + bookTitles[categoryIndex][i]);
            }
        }

        // If we never printed any book
        if (!anyAvailable) {
            System.out.println("No available books in this category.");
        }
    }

    // ===================== SEARCH BOOKS =====================
    // User can type full or partial name, search across all categories
    static void searchBooks() {
        System.out.print("\nEnter book name (full or partial): ");
        String term = kbd.nextLine().trim();

        if (term.isEmpty()) {
            System.out.println("Search term cannot be empty.");
            return;
        }

        // Work with lowercase to make search case-insensitive
        String lowerTerm = term.toLowerCase();

        System.out.println();
        System.out.println("Search Results for '" + term + "'");
        boolean found = false;

        // Check every title in the 2D array
        for (int c = 0; c < 4; c++) {
            for (int b = 0; b < 10; b++) {
                // If title contains the search term (e.g. "History" in "Ancient History")
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

    // ===================== SAFE INT INPUT =====================
    // Reads an int from the user safely using nextLine(), to avoid Scanner bugs with mixing nextInt and nextLine
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
