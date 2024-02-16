import java.util.Arrays;
import java.util.Scanner;
import java.io.*;
class Main {
    // Purpose: allows the user to log in to the program
    // Pre: Scanner input, String[] accounts
    // Post: String name
    public static String login(Scanner input, String[] accounts) {
        while (true) {
            System.out.println("\nPlease enter your username");
            String name = input.nextLine().trim();
            if (!hasDuplicateName(name, accounts)) {
                System.out.println("Username does not exist in the library!");
                continue;
            }
            System.out.println("\nPlease enter your password");
            String password = input.nextLine().trim();
            for (String account : accounts) {
                String[] information = account.split(",");
                if (information[0].equals(name) && information[1].equals(password)) {
                    System.out.println("\nLogin successful!");
                    return name;
                }
            }
            System.out.println("Password is incorrect! Please try again.");
        }
    }
    // Purpose: allows the user to create a new account
    // Pre: Scanner input, String[] accounts
    // Post: String name + "," + password
    public static String register(Scanner input, String[] accounts) {
        System.out.println("\nPlease choose a username!");
        String name = input.nextLine().trim();
        while (hasDuplicateName(name, accounts)) {
            System.out.println("The username you chose already exists. Please choose another.");
            name = input.nextLine().trim();
        }
        System.out
                .println("\nPlease choose a password! (At least one number, a length of six, and a special character!)");
        String password = input.nextLine().trim();
        while (true) {
            if (password.length() < 6) {
                System.out.println("Your password must have at least six characters!");
            } else if (!hasDigit(password)) {
                System.out.println("Your password must have at least one digit!");
            } else if (!hasSpecial(password)) {
                System.out.println("Your password must have at least one special character!");
            } else {
                break;
            }
            password = input.nextLine().trim();
        }
        return name + "," + password;
    }
    // Purpose: gets the amount of lines of the text file
    // Pre: File file
    // Post: int rows
    public static int getSize(File file) {
        try (Scanner reader = new Scanner(file)) {
            int rows = 0;
            while (reader.hasNext()) {
                reader.nextLine();
                rows++;
            }
            return rows;
        } catch (IOException ioe) {
            System.out.println("File does not exist!");
        }
        return 0;
    }
    // Purpose: checks to see if the password has a digit or not
    // Pre: String phrase
    // Post: boolean value
    public static boolean hasDigit(String phrase) {
        for (char c : phrase.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
    // Purpose: checks to see if the password has a special character or not
    // Pre: String phrase
    // Post: boolean value
    public static boolean hasSpecial(String phrase) {
        String specialChars = "~`!@#$%^&*()-_=+{[}]|\\:;\"'<,>.?/';";
        for (char c : phrase.toCharArray()) {
            if (specialChars.indexOf(c) > -1) {
                return true;
            }
        }
        return false;
    }
    // Purpose: checks to see if the specified phrase already exists within an array
    // Pre: String phrase, String[] names
    // Post: boolean value
    public static boolean hasDuplicateName(String phrase, String[] names) {
        for (String name : names) {
            if (name.split(",")[0].equals(phrase)) {
                return true;
            }
        }
        return false;
    }
    // Purpose: loads the file into an array to be used within the program
    // Pre: File file
    // Post: String[] array
    public static String[] loadFile(File file) {
        String[] array = new String[getSize(file)];
        try (Scanner reader = new Scanner(file)) {
            for (int i = 0; reader.hasNext(); i++) {
                array[i] = reader.nextLine();
            }
        } catch (IOException ioe) {
            System.out.println("The file does not exist!");
        }
        return array;
    }
    // Purpose: checks to see if the password has a special character or not
    // Pre: String phrase
    // Post: boolean value
    public static String borrow(File holdsFile, Scanner input, String[] books, String[] userFile, String[] holdsArray, String username, int date) {
        System.out.println("\nPlease choose what genre of books you want to browse and input the corresponding number:");
        System.out.println("1.Programming, 2.SciFi, 3.Math, 4.Finance, 5.Transportation");
        int categoryResponse = input.nextInt();
        boolean check = true;
        while (check) {
            // checks the category response and prints each book that's part of the genre
            switch (categoryResponse) {
                case 1, 2, 3, 4, 5 -> {
                    printBooks(books, categoryResponse);
                    check = false;
                }
                default -> {
                    System.out.println("Invalid corresponding genre number. Please input a valid number.");
                    categoryResponse = input.nextInt();
                }
            }
        }
        input.nextLine();
        // An if else ladder to check all the restrictions before the book can be borrowed
        while (true) {
            System.out.println("\nWhich book do you want to borrow? (Book titles are CASE-SENSITIVE!)");
            String bookResponse = input.nextLine().trim();
            if (!hasDuplicateName(bookResponse, books)) {
                System.out.println("Sorry, but this book title does not exist in the database! Please browse for a different selection.");
                continue;
            } else if (hasDuplicateName(bookResponse, userFile)) {
                System.out.println("Sorry, but you have already borrowed this book! Please browse for a different selection.");
                continue;
            } else if (alreadyBorrowed(books, bookResponse)) {
                System.out.println("Sorry, but this book title has already been borrowed from another user! Please browse for a different selection.");
                continue;
            } else if (alreadyOnHold(holdsArray, bookResponse, username)) {
                System.out.println("Sorry, but someone already has a hold on this book! Please browse for a different selection.");
                continue;
            }
            // Borrows the book, and if the user borrowed a book that they held, it changes the status
            System.out.println("\nThank you for borrowing: " + bookResponse + "!");
            System.out.println("Your receipt will be printed shortly in a separate file.");
            for (String hold : holdsArray) {
                String[] formatted = hold.split(",");
                if (formatted.length == 2 && formatted[0].equals(bookResponse) && formatted[1].equals(username)) {
                    changeHoldStatus(holdsFile, holdsArray, bookResponse, username, true);
                }
            }
            printReceipt(bookResponse, username, date);
            return bookResponse;
        }
    }
    // Purpose: checks to see if the book is already borrowed or not
    // Pre: String[] books, String bookResponse
    // Post: boolean value
    public static boolean alreadyBorrowed(String[] books, String bookResponse) {
        for (String book : books) {
            String[] formattedBook = book.split(",");
            if (formattedBook[0].equals(bookResponse)) {
                return formattedBook[1].equals("true");
            }
        }
        return false;
    }
    // Purpose: prints a list of the books in the database
    // Pre: String[] books, int response
    // Post: none
    public static void printBooks(String[] books, int response) {
        System.out.println("\nPrinting a list of books...");
        for (int i = response * 5 - 5; i < response * 5; i++) {
            System.out.println(books[i].split(",")[0]);
        }
    }
    // Purpose: prints the receipt of what the user borrowed and the return date
    // Pre: String bookTitle, String username, int date
    // Post: none
    public static void printReceipt(String bookTitle, String username, int date) {
        try (PrintWriter pw = new PrintWriter("receipt.txt")) {
            pw.println("---------------------------------------------------------");
            pw.println("PNC LIBRARY DATABASE");
            pw.println("---------------------------------------------------------");
            pw.println("Account Name: " + username);
            pw.println("Day: " + date);
            pw.println("Book Borrowed: " + bookTitle);
            pw.println("Return Date: " + (date + 10));
            pw.println("Thank you, and have a good day!");
            pw.println("---------------------------------------------------------");
        } catch (FileNotFoundException fnfe) {
            System.out.println("Unable to print receipt!");
        }
    }
    // Purpose: actually returns the specified book back into the database
    // Pre: File userFile, File booksFile, Scanner input
    // Post: none
    public static void returnBook(File userFile, File booksFile, Scanner input) {
        if (!userFile.exists() || userFile.length() == 0) {
            System.out.println("Sorry, but you did not borrow a book yet to be able to return!");
            return;
        }
        while (true) {
            view(userFile);
            System.out.println("\nWhich book do you want to return?");
            String response = input.nextLine().trim();
            if (!hasDuplicateName(response, loadFile(userFile))) {
                System.out.println("Sorry, but you did not borrow specified book title!");
                continue;
            }
            returnBooks(userFile, booksFile, response);
            break;
        }
    }
    // Purpose: changes the status of the book to either borrowed, or not borrowed yet
    // Pre: File booksFile, String book, boolean borrowed
    // Post: none
    public static void changeBookStatus(File booksFile, String book, boolean borrowed) {
        StringBuilder sb = new StringBuilder();
        try (Scanner reader = new Scanner(booksFile)) {
            // the loop determines if the book is borrowed, and then appends either true or false based on the status
            while (reader.hasNextLine()) {
                String[] sentence = reader.nextLine().split(",");
                if (sentence[0].equals(book) && sentence[1].equals(String.valueOf(!borrowed))) {
                    sb.append(sentence[0]).append(",").append(borrowed);
                } else if (sentence[1].equals(String.valueOf(borrowed))) {
                    sb.append(sentence[0]).append(",").append(borrowed);
                } else {
                    sb.append(sentence[0]).append(",").append(!borrowed);
                }
                if (reader.hasNextLine()) {
                    sb.append(System.lineSeparator());
                }
            }
        } catch (IOException ioe) {
            System.out.println("Something wrong happened!");
        }
        try (PrintWriter pw = new PrintWriter(booksFile)) {
            pw.print(sb);
        } catch (FileNotFoundException fnfe) {
            System.out.println("File not found!");
        }
    }
    // Purpose: renews a specified book, given that there is no current hold on it yet
    // Pre: File userFile, Scanner input, String[] holdsArray, String username
    // Post: none
    public static void renew(File userFile, Scanner input, String[] holdsArray, String username) {
        // checks to see if the user file exists. If it does not, that means there is nothing to be renewed
        if (!userFile.exists() || userFile.length() == 0) {
            System.out.println("Sorry, but you did not borrow a book yet to be able to renew!");
            return;
        }
        while (true) {
            System.out.println("\nWhich book do you want to renew?");
            String response = input.nextLine().trim();
            // checks current restrictions
            if (!hasDuplicateName(response, loadFile(userFile))) {
                System.out.println("Sorry, but this book title does not exist!");
                continue;
            } else if (alreadyOnHold(holdsArray, response, username)) {
                System.out.println("Sorry, but someone has a hold on this book! You cannot renew at this time.");
                continue;
            }
            try {
                Scanner reader = new Scanner(userFile);
                StringBuilder sb = new StringBuilder();
                //Determines the next due date of the book and writes it to the user file
                while (reader.hasNextLine()) {
                    String sentence = reader.nextLine();
                    if (sentence.split(",")[0].equals(response)) {
                        int nextDate = Integer.parseInt(sentence.split(",")[1]) + 10;
                        sb.append(response).append(",").append(nextDate).append(System.lineSeparator());
                        System.out.println("Successfully renewed book! Next due date: " + nextDate);
                    } else {
                        sb.append(sentence).append(System.lineSeparator());
                    }
                }
                PrintWriter pw = new PrintWriter(userFile);
                pw.print(sb);
                pw.close();
                reader.close();
            } catch (IOException ioe) {
                System.out.println("Something wrong happened!");
            }
            break;
        }
    }
    // Purpose: Prints the books borrowed from the user as well as the next due date
    // Pre: File userFile
    // Post: none
    public static void view(File userFile) {
        if (!userFile.exists() || userFile.length() == 0) {
            System.out.println("Sorry, but you do not have any books to view!");
            return;
        }
        String[] userInfo = loadFile(userFile);
        for (String sentence : userInfo) {
            String[] formatted = sentence.split(",");
            System.out.println("\nBook: " + formatted[0] + "\t-\tNext Day Until Due: Day " + formatted[1]);
        }
    }
    // Purpose: checks the current status of the books to notify the user when it is due, or when a hold book is available
    // Pre: File userFile, File booksFile, String[] userInfo, String[] booksArray, String[] holdsArray, String username, int date
    // Post: none
    public static void checkStatus(File userFile, File booksFile, String[] userInfo, String[] booksArray, String[] holdsArray, String username, int date) {
        for (String sentence : userInfo) {
            String[] formatted = sentence.split(",");
            if (Integer.parseInt(formatted[1]) == date + 1) {
                System.out.println("Book: " + formatted[0] + " is due the next day!");
            } else if (Integer.parseInt(formatted[1]) == date) {
                System.out.println("Book: " + formatted[0] + " is due today!");
            } else if (Integer.parseInt(formatted[1]) == date - 1) {
                System.out.println("Automatically returning " + formatted[0]);
                returnBooks(userFile, booksFile, formatted[0]);
            }
        }
        for (String hold : holdsArray) {
            String[] formatted = hold.split(",");
            if (!alreadyBorrowed(booksArray, formatted[0]) && isHeldByUser(holdsArray, formatted[0], username)) {
                System.out.println(formatted[0] + " is ready to be borrowed now!");
            }
        }
    }
    // Purpose: returns the books that the user wants to return
    // Pre: String[] books, int response
    // Post: none
    public static void returnBooks(File userFile, File booksFile, String book) {
        try (Scanner reader = new Scanner(userFile); PrintWriter pw = new PrintWriter(userFile)) {
            StringBuilder sb = new StringBuilder();
            while (reader.hasNextLine()) {
                String sentence = reader.nextLine();
                if (!sentence.equals(book)) {
                    sb.append(sentence).append(System.lineSeparator());
                }
            }
            pw.print(sb);
            changeBookStatus(booksFile, book, false);
            System.out.println("Return successful!");
        } catch (IOException ioe) {
            System.out.println("Cannot find userFile file!");
        }
    }
    // Purpose: allows the user to hold a book to borrow in the future if someone already borrowed it
    // Pre: Scanner input, File holdsFile, String[] holdsArray, String[] booksArray, String username
    // Post: none
    public static void hold(Scanner input, File holdsFile, String[] holdsArray, String[] booksArray, String username) {
        System.out.println("Printing books in the database...");
        for (String book : booksArray) {
            System.out.println(book.split(",")[0]);
        }
        String response;
        while (true) {
            System.out.println("\nWhich book do you want to hold?");
            response = input.nextLine().trim();
            if (!hasDuplicateName(response, booksArray)) {
                System.out.println("Sorry, but this book title does not exist! Please try again.");
                continue;
            }
            break;
        }
        //checks current restrictions before allowing to hold a book
        for (String hold : holdsArray) {
            String[] formatted = hold.split(",");
            if (formatted[0].equals(response) && formatted.length == 1 && alreadyBorrowed(booksArray, response)) {
                System.out.println("Placing hold...");
                break;
            } else if (formatted[0].equals(response) && formatted.length == 2) {
                System.out.println("Someone already has a hold on the book! Check back later!");
                return;
            } else if (formatted[0].equals(response)) {
                System.out.println("Nobody borrowed the book yet! You can borrow it now!");
                return;
            }
        }
        changeHoldStatus(holdsFile, holdsArray, response, username, false);
        System.out.println("Successfully placed hold! We will notify you once the book is ready to be taken out!");
    }
    // Purpose: checks if a book is already on hold
    // Pre: String[] holdsArray, String book, String username
    // Post: boolean value
    public static boolean alreadyOnHold(String[] holdsArray, String book, String username) {
        for (String hold : holdsArray) {
            String[] formatted = hold.split(",");
            if (formatted.length == 2 && formatted[0].equals(book) && !formatted[1].equals(username)) {
                return true;
            }
        }
        return false;
    }
    // Purpose: checks to see if a book has been held by a user
    // Pre: String[] holdsArray, String book, String username
    // Post: boolean value
    public static boolean isHeldByUser(String[] holdsArray, String book, String username) {
        for (String holds : holdsArray) {
            if (holds.split(",").length == 2 && holds.split(",")[0].equals(book)
                    && holds.split(",")[1].equals(username)) {
                return true;
            }
        }
        return false;
    }
    // Purpose: changes the status of a book if it is either on hold, or not on hold yet and changes it accordingly
    // Pre: File holdsFile, String[] holdsArray, String response, String username, boolean removeName
    // Post: none
    public static void changeHoldStatus(File holdsFile, String[] holdsArray, String response, String username, boolean removeName) {
        StringBuilder sb = new StringBuilder();
        for (String hold : holdsArray) {
            String[] formatted = hold.split(",");
            if (formatted[0].equals(response) && removeName) {
                sb.append(response).append(",").append(System.lineSeparator());
            } else if (formatted[0].equals(response)) {
                sb.append(response).append(",").append(username).append(System.lineSeparator());
            } else {
                sb.append(Arrays.toString(formatted).replace("[", "").replace("]", "").trim())
                        .append(System.lineSeparator());
            }
        }
        try (PrintWriter pw = new PrintWriter(holdsFile)) {
            pw.print(sb);
        } catch (IOException ioe) {
            System.out.println("Error printing to file!");
        }
    }
    // Purpose: recommends a book to the user based on what they borrowed before
    // Pre: String[] userArray, String[] booksArray, String[] holdsArray, String username
    // Post: none
    public static void recommendBook(String[] userArray, String[] booksArray, String[] holdsArray, String username) {
        if (userArray.length == 0) {
            return;
        }
        String randomBook = userArray[(int)(Math.random()*(userArray.length - 1))].split(",")[0];
        int index = 0;
        for (String book : booksArray) {
            if (book.split(",")[0].equals(randomBook)) {
                break;
            } else {
                index++;
            }
        }
        // Since the genres are split by multiples of five in the text file, the switch case handles the genre check
        // and determines the new random index for the recommended book of the day
        int randomIndex = switch (index) {
            case 1, 2, 3, 4, 5 -> (int) (Math.random() * (5) + 1);
            case 6, 7, 8, 9, 10 -> (int) (Math.random() * (5) + 6);
            case 11, 12, 13, 14, 15 -> (int) (Math.random() * (5) + 11);
            case 16, 17, 18, 19, 20 -> (int) (Math.random() * (5) + 16);
            case 21, 22, 23, 24, 25 -> (int) (Math.random() * (5) + 21);
            default -> 0;
        };
        // Prints the suggestion and notifies if the suggestion is actually available or not
        String suggestedBook = booksArray[randomIndex].split(",")[0];
        boolean available = (!alreadyBorrowed(booksArray, suggestedBook) || !alreadyOnHold(holdsArray, suggestedBook, username));
        System.out.println("\nToday's suggestion based on what you read: " + suggestedBook);
        if (available) {
            System.out.println("Availability: Available");
        } else {
            System.out.println("Availability: Unavailable");
        }
    }
    public static void main(String[] args) throws IOException {
        // loads every file into its respective array
        File accountsFile = new File("accounts.txt");
        File booksFile = new File("books.txt");
        File dayFile = new File("date.txt");
        File holdsFile = new File("holds.txt");
        Scanner input = new Scanner(System.in);
        String[] accounts = loadFile(accountsFile);
        String[] books = loadFile(booksFile);
        String[] holds = loadFile(holdsFile);
        String username;
        int day = Integer.parseInt(loadFile(dayFile)[0]);
        while (true) {
            System.out.println("Welcome to the PNC Library Database.");
            System.out.println("Do you want to sign in, or register?");
            String response = input.nextLine();
            if (response.equalsIgnoreCase("Sign in")) {
                if (accounts.length == 0) {
                    System.out.println("There are no accounts in the database yet!");
                    continue;
                }
                username = login(input, accounts);
                break;
            } else if (response.equalsIgnoreCase("Register")) {
                PrintWriter pw = new PrintWriter(new FileWriter(accountsFile, true));
                String newAccount = register(input, accounts);
                accounts = Arrays.copyOf(accounts, accounts.length + 1);
                accounts[accounts.length - 1] = newAccount;
                System.out.println("\nAccount creation successful!");
                pw.println(newAccount);
                pw.close();
            } else {
                System.out.println("Invalid Response. Please retype your response.");
            }
        }
        // This section starts the session in the library database
        System.out.println("\nDAY: " + day);
        System.out.println("Welcome to the PNC Library Database, " + username + "!");
        File userFile = new File(username + ".txt");
        if (!userFile.createNewFile() && !userFile.exists()) {
            System.out.println("Error creating user data file!");
        }
        String[] userInfo = loadFile(userFile);
        checkStatus(userFile, booksFile, userInfo, books, holds, username, day);
        // This loop prompts the user to choose one of the actions provided
        while (true) {
            System.out.println("\nWhat do you want to do, borrow, hold, renew, return, view, or exit?");
            String response = input.nextLine().trim();
            if (response.equalsIgnoreCase("Borrow")) {
                if (!userFile.createNewFile() && !userFile.exists()) {
                    System.out.println("Error creating user data file!");
                    continue;
                }
                recommendBook(userInfo,books,holds, username);
                String book = borrow(holdsFile, input, books, userInfo, holds, username, day);
                PrintWriter pw = new PrintWriter(new FileWriter(userFile, true));
                pw.println(book + "," + (day + 10));
                pw.close();
                changeBookStatus(booksFile, book, true);
            } else if (response.equalsIgnoreCase("Hold")) {
                hold(input, holdsFile, holds, books, username);
            } else if (response.equalsIgnoreCase("Renew")) {
                renew(userFile, input, holds, username);
            } else if (response.equalsIgnoreCase("Return")) {
                returnBook(userFile, booksFile, input);
            } else if (response.equalsIgnoreCase("View")) {
                view(userFile);
            } else if (response.equalsIgnoreCase("Exit")) {
                System.out.println("\nThank you " + username + " for visiting and we hope you have a good day!");
                break;
            } else {
                System.out.println("Invalid response! Please retype your response again.");
            }
        }
        PrintWriter pw = new PrintWriter(dayFile);
        pw.print(day + 1);
        pw.close();
        input.close();
    }
}