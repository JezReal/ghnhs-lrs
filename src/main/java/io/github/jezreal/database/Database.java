package io.github.jezreal.database;

import io.github.jezreal.model.Article;
import io.github.jezreal.model.Book;
import io.github.jezreal.model.BookToReturn;
import io.github.jezreal.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.Formatter;

public class Database {

    private static Connection connection;
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://localhost/lrs_db?useTimezone=true&serverTimezone=UTC";
    private static final String USER = "lrs-client";
    private static final String PASSWORD = "";

    private static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName(DB_DRIVER);
                connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);

                System.out.println("Connected to database");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("cannot get connection");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println("driver class not found");
            }
        }

        return connection;
    }

    public static ObservableList<Book> getAllBooks() {
        Connection connection = getConnection();
        ObservableList<Book> books = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM books_table");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String description = resultSet.getString("description");
                int quantity = resultSet.getInt("quantity");

                books.add(new Book(id, description, quantity));
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong with the query");
        }


        return books;
    }

    public static Book getBook(int id) {
        Connection connection = getConnection();
        Book book = null;

        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("SELECT * FROM books_table WHERE id='%d'", id);

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(stringBuilder.toString());

            while (resultSet.next()) {
                int bookId = resultSet.getInt("id");
                String description = resultSet.getString("description");
                int quantity = resultSet.getInt("quantity");

                book = new Book(bookId, description, quantity);
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong with the query");
        }


        return book;
    }

    public static void addBook(Book book) {
        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("INSERT INTO books_table (description, quantity) VALUES ('%s', '%d')", book.getDescription(), book.getQuantity());

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(stringBuilder.toString());

        } catch (SQLException e) {
            System.out.println("Query error");
        }
    }

    public static void updateBook(Book book) {
        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("UPDATE books_table SET description='%s', quantity='%d' WHERE id='%d'", book.getDescription(), book.getQuantity(), book.getId());


        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(stringBuilder.toString());
        } catch (SQLException e) {
            System.out.println("Sql error");
            e.printStackTrace();
        }
    }

    public static void updateBook(int bookId, int quantity) {
        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        int existingQuantity = getBook(bookId).getQuantity();
        int newQuantity = existingQuantity + quantity;

        formatter.format("UPDATE books_table SET quantity='%d' WHERE id='%d'", newQuantity , bookId);

        System.out.println("Update book: " + stringBuilder);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(stringBuilder.toString());
        } catch (SQLException e) {
            System.out.println("Sql error");
            e.printStackTrace();
        }
    }

    public static void deleteBook(int id) {
        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("DELETE FROM books_table WHERE id='%d'", id);

        try {
            Statement statement = connection.createStatement();
            statement.execute(stringBuilder.toString());
        } catch (SQLException e) {
            System.out.println("Sql error");
            e.printStackTrace();
        }
    }

    public static ObservableList<Book> getAvailableBooks() {
        Connection connection = getConnection();
        ObservableList<Book> availableBooks = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM books_table WHERE quantity > 0");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String description = resultSet.getString("description");
                int quantity = resultSet.getInt("quantity");

                availableBooks.add(new Book(id, description, quantity));
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong with the query");
        }

        return availableBooks;
    }

    public static void addBorrowedBook(String firstName, String lastName, LocalDate date, int id, int quantity, Book book) {
        Connection connection = getConnection();
        String query = "INSERT INTO transaction_table (book_id, first_name, last_name, date_borrowed, quantity_borrowed) VALUES('" + id + "','" + firstName + "','" + lastName + "','" + date + "','" + quantity + "')";


        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Query error");
        }

        int borrowedQuantity = book.getQuantity();
        int originalQuantity = getBook(id).getQuantity();

        book.setQuantity(originalQuantity - borrowedQuantity);

        updateBook(book);
    }

    public static ObservableList<Transaction> getUniqueUnreturnedTransactions() {
        ObservableList<Transaction> unreturnedTransactions = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT first_name, last_name from transaction_table WHERE date_returned IS NULL");

            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");

                unreturnedTransactions.add(new Transaction(firstName, lastName));
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong with the query");
        }

        return unreturnedTransactions;
    }

    public static ObservableList<Transaction> getAllTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM transaction_table");

            while (resultSet.next()) {
                int transactionId = resultSet.getInt("transaction_id");
                int bookId = resultSet.getInt("book_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                LocalDate dateBorrowed = resultSet.getDate("date_borrowed").toLocalDate();

                int quantityBorrowed = resultSet.getInt("quantity_borrowed");
                LocalDate dateReturned = null;

                try {
                    dateReturned = resultSet.getDate("date_returned").toLocalDate();
                } catch (NullPointerException ignored) {

                }

                if (dateReturned == null) {
                    transactions.add(new Transaction(transactionId, bookId, firstName, lastName, dateBorrowed, quantityBorrowed));
                } else {
                    transactions.add(new Transaction(transactionId, bookId, firstName, lastName, dateBorrowed, dateReturned, quantityBorrowed));
                }
            }
        } catch (SQLException e) {
            System.out.println("Something went wrong with the query");
        }

        return transactions;
    }

    public static void updateTransaction(int transactionId) {
        Connection connection = getConnection();

        String query = "UPDATE transaction_table SET date_returned='" + LocalDate.now() + "'" + "WHERE transaction_id='" + transactionId + "'";

        System.out.println("Update transaction: " + query);
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
        } catch (SQLException e) {
            System.out.println("Sql error");
            e.printStackTrace();
        }
    }

    public static ObservableList<BookToReturn> getBooksToReturn(String firstName, String lastName) {
        ObservableList<BookToReturn> booksToReturn = FXCollections.observableArrayList();

        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("SELECT transaction_table.transaction_id, transaction_table.book_id, books_table.description, transaction_table.quantity_borrowed FROM books_table JOIN transaction_table ON transaction_table.book_id=books_table.id WHERE transaction_table.first_name='%s' AND transaction_table.last_name='%s' AND transaction_table.date_returned is NULL", firstName, lastName);

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(stringBuilder.toString());

            while(resultSet.next()) {
                int transactionId = resultSet.getInt("transaction_id");
                int bookId = resultSet.getInt("book_id");
                String description = resultSet.getString("description");
                int quantityBorrowed = resultSet.getInt("quantity_borrowed");

                booksToReturn.add(new BookToReturn(transactionId, bookId, description, quantityBorrowed));
            }
        } catch (SQLException e) {
            System.out.println("Sql error");
            e.printStackTrace();
        }

        return booksToReturn;
    }

    public static void returnBook(int transactionId, int bookId, int quantity) {
        updateBook(bookId, quantity);
        updateTransaction(transactionId);
    }

    public static void addArticle(Article article) {
        Connection connection = getConnection();

        LocalDate dateAcquired = article.getDateAcquired();
        String articleName = article.getArticleName();
        String propertyNumber = article.getPropertyNumber();
        int quantity = article.getQuantity();
        double unitCost = article.getUnitCost();
        double totalCost = article.getTotalCost();
        String remarks = article.getRemarks();

        String query = "INSERT INTO articles_table (date_acquired, article_name, property_number, quantity, unit_cost, total_cost, remarks) VALUES ('" + dateAcquired + "','" + articleName + "','" + propertyNumber + "','" + quantity + "','" + unitCost + "','" + totalCost + "','" + remarks + "')";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);

        } catch (SQLException e) {
            System.out.println("article query error");
        }


    }
}
