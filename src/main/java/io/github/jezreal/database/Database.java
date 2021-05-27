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

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null) {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DATABASE_URL, USER, PASSWORD);
        }

        return connection;
    }

    public static ObservableList<Book> getAllBooks() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        ObservableList<Book> books = FXCollections.observableArrayList();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM books_table");

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String description = resultSet.getString("description");
            int quantity = resultSet.getInt("quantity");

            books.add(new Book(id, description, quantity));
        }

        return books;
    }

    public static Book getBook(int id) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        Book book = null;

        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("SELECT * FROM books_table WHERE id='%d'", id);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(stringBuilder.toString());

        while (resultSet.next()) {
            int bookId = resultSet.getInt("id");
            String description = resultSet.getString("description");
            int quantity = resultSet.getInt("quantity");

            book = new Book(bookId, description, quantity);
        }

        return book;
    }

    public static void addBook(Book book) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("INSERT INTO books_table (description, quantity) VALUES ('%s', '%d')", book.getDescription(), book.getQuantity());

        Statement statement = connection.createStatement();
        statement.executeUpdate(stringBuilder.toString());
    }

    public static void updateBook(Book book) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("UPDATE books_table SET description='%s', quantity='%d' WHERE id='%d'", book.getDescription(), book.getQuantity(), book.getId());

        Statement statement = connection.createStatement();
        statement.executeUpdate(stringBuilder.toString());
    }

    public static void updateBook(int bookId, int quantity) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        int existingQuantity = getBook(bookId).getQuantity();
        int newQuantity = existingQuantity + quantity;

        formatter.format("UPDATE books_table SET quantity='%d' WHERE id='%d'", newQuantity, bookId);

        Statement statement = connection.createStatement();
        statement.executeUpdate(stringBuilder.toString());
    }

    public static void deleteBook(int id) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("DELETE FROM books_table WHERE id='%d'", id);

        Statement statement = connection.createStatement();
        statement.execute(stringBuilder.toString());
    }

    public static ObservableList<Book> getAvailableBooks() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        ObservableList<Book> availableBooks = FXCollections.observableArrayList();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM books_table WHERE quantity > 0");

        while (resultSet.next()) {
            int availableBookId = resultSet.getInt("id");
            String availableBookDescription = resultSet.getString("description");
            int availableBookQuantity = resultSet.getInt("quantity");

            availableBooks.add(new Book(availableBookId, availableBookDescription, availableBookQuantity));
        }

        return availableBooks;
    }

    public static void addBorrowedBook(String firstName, String lastName, LocalDate date, int id, int quantity, Book book) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        String query = "INSERT INTO transactions_table (book_id, first_name, last_name, date_borrowed, quantity_borrowed) VALUES('" + id + "','" + firstName + "','" + lastName + "','" + date + "','" + quantity + "')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);

        int borrowedQuantity = book.getQuantity();
        int originalQuantity = getBook(id).getQuantity();

        book.setQuantity(originalQuantity - borrowedQuantity);

        updateBook(book);
    }

    public static ObservableList<Transaction> getUniqueUnreturnedTransactions() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        ObservableList<Transaction> unreturnedTransactions = FXCollections.observableArrayList();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT DISTINCT first_name, last_name from transactions_table WHERE date_returned IS NULL");

        while (resultSet.next()) {
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");

            unreturnedTransactions.add(new Transaction(firstName, lastName));
        }

        return unreturnedTransactions;
    }

    public static ObservableList<Transaction> getAllTransactions() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM transactions_table");

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

        return transactions;
    }

    public static void updateTransaction(int transactionId) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();

        String query = "UPDATE transactions_table SET date_returned='" + LocalDate.now() + "'" + "WHERE transaction_id='" + transactionId + "'";

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public static ObservableList<BookToReturn> getBooksToReturn(String firstName, String lastName) throws SQLException, ClassNotFoundException {
        ObservableList<BookToReturn> booksToReturn = FXCollections.observableArrayList();

        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("SELECT transactions_table.transaction_id, transactions_table.book_id, books_table.description, transactions_table.quantity_borrowed FROM books_table JOIN transactions_table ON transactions_table.book_id=books_table.id WHERE transactions_table.first_name='%s' AND transactions_table.last_name='%s' AND transactions_table.date_returned is NULL", firstName, lastName);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(stringBuilder.toString());

        while (resultSet.next()) {
            int transactionId = resultSet.getInt("transaction_id");
            int bookId = resultSet.getInt("book_id");
            String description = resultSet.getString("description");
            int quantityBorrowed = resultSet.getInt("quantity_borrowed");

            booksToReturn.add(new BookToReturn(transactionId, bookId, description, quantityBorrowed));
        }

        return booksToReturn;
    }

    public static void returnBook(int transactionId, int bookId, int quantity) throws SQLException, ClassNotFoundException {
        updateBook(bookId, quantity);
        updateTransaction(transactionId);
    }

    public static void addArticle(Article article) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();

        LocalDate dateAcquired = article.getDateAcquired();
        String articleName = article.getArticleName();
        String propertyNumber = article.getPropertyNumber();
        int quantity = article.getQuantity();
        double unitCost = article.getUnitCost();
        double totalCost = article.getTotalCost();
        String remarks = article.getRemarks();

        String query = "INSERT INTO articles_table (date_acquired, article_name, property_number, quantity, unit_cost, total_cost, remarks) VALUES ('" + dateAcquired + "','" + articleName + "','" + propertyNumber + "','" + quantity + "','" + unitCost + "','" + totalCost + "','" + remarks + "')";

        Statement statement = connection.createStatement();
        statement.executeUpdate(query);
    }

    public static ObservableList<Article> getAllArticles() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        ObservableList<Article> articles = FXCollections.observableArrayList();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM articles_table");

        while (resultSet.next()) {
            int articleId = resultSet.getInt("article_id");
            LocalDate dateAcquired = resultSet.getDate("date_acquired").toLocalDate();
            String articleName = resultSet.getString("article_name");
            String propertyNumber = resultSet.getString("property_number");
            int quantity = resultSet.getInt("quantity");
            double unitCost = resultSet.getDouble("unit_cost");
            double totalCost = resultSet.getDouble("total_cost");
            String remarks = resultSet.getString("remarks");

            articles.add(new Article(articleId, dateAcquired, articleName, propertyNumber, quantity, unitCost, totalCost, remarks));
        }

        return articles;
    }

    public static ObservableList<Transaction> getUniqueTransactions() throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        ObservableList<Transaction> uniqueTransactions = FXCollections.observableArrayList();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT DISTINCT first_name, last_name from transactions_table");

        while (resultSet.next()) {
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");

            uniqueTransactions.add(new Transaction(firstName, lastName));
        }

        return uniqueTransactions;
    }

    public static ObservableList<Transaction> getTransactionsByName(String firstName, String lastName) throws SQLException, ClassNotFoundException {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();

        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        formatter.format("SELECT * FROM transactions_table WHERE first_name='%s' AND last_name='%s'", firstName, lastName);

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(stringBuilder.toString());

        while (resultSet.next()) {
            int transactionId = resultSet.getInt("transaction_id");
            int bookId = resultSet.getInt("book_id");
            LocalDate dateBorrowed = resultSet.getDate("date_borrowed").toLocalDate();
            LocalDate dateReturned = null;

            try {
                dateReturned = resultSet.getDate("date_returned").toLocalDate();
            } catch (NullPointerException ignored) {
            }

            int quantityBorrowed = resultSet.getInt("quantity_borrowed");

            Transaction transaction;

            if (dateReturned == null) {
                transaction = new Transaction(
                        transactionId,
                        bookId,
                        dateBorrowed,
                        quantityBorrowed
                );
            } else {
                transaction = new Transaction(
                        transactionId,
                        bookId,
                        dateBorrowed,
                        dateReturned,
                        quantityBorrowed
                );
            }

            transactions.add(transaction);
        }

        return transactions;
    }
}
