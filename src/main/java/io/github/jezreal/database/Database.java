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

        PreparedStatement statement = connection.prepareStatement("SELECT  * FROM books_table WHERE id=?");
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();

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

        PreparedStatement statement = connection.prepareStatement("INSERT INTO books_table (description, quantity) VALUES (?, ?)");
        statement.setString(1, book.getDescription());
        statement.setInt(2, book.getQuantity());

        statement.executeUpdate();
    }

    public static void updateBook(Book book) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement("UPDATE books_table SET description=?, quantity=? WHERE id=?");
        statement.setString(1, book.getDescription());
        statement.setInt(2, book.getQuantity());
        statement.setInt(3, book.getId());

        statement.executeUpdate();
    }

    public static void updateBook(int bookId, int quantity) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();

        int existingQuantity = getBook(bookId).getQuantity();
        int newQuantity = existingQuantity + quantity;

        PreparedStatement statement = connection.prepareStatement("UPDATE books_table SET quantity=? WHERE id=?");
        statement.setInt(1, newQuantity);
        statement.setInt(2, bookId);

        statement.executeUpdate();
    }

    public static void deleteBook(int id) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement("DELETE FROM books_table WHERE id=?");
        statement.setInt(1, id);

        statement.execute();
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

        PreparedStatement statement = connection.prepareStatement("INSERT INTO transactions_table (book_id, first_name, last_name, date_borrowed, quantity_borrowed) VALUES (?,?,?,?,?)");
        statement.setInt(1, id);
        statement.setString(2, firstName);
        statement.setString(3, lastName);
        statement.setDate(4, java.sql.Date.valueOf(date.toString()));
        statement.setInt(5, quantity);
        statement.executeUpdate();

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

        PreparedStatement statement = connection.prepareStatement("UPDATE transactions_table SET date_returned=? WHERE transaction_id=?");
        statement.setDate(1, java.sql.Date.valueOf(LocalDate.now().toString()));
        statement.setInt(2, transactionId);

        statement.executeUpdate();
    }

    public static ObservableList<BookToReturn> getBooksToReturn(String firstName, String lastName) throws SQLException, ClassNotFoundException {
        ObservableList<BookToReturn> booksToReturn = FXCollections.observableArrayList();

        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement("SELECT transactions_table.transaction_id, transactions_table.book_id, books_table.description, transactions_table.quantity_borrowed FROM books_table JOIN transactions_table ON transactions_table.book_id=books_table.id WHERE transactions_table.first_name=? AND transactions_table.last_name=? AND transactions_table.date_returned is NULL");
        statement.setString(1, firstName);
        statement.setString(2, lastName);

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            int transactionId = resultSet.getInt("transaction_id");
            int bookId = resultSet.getInt("book_id");
            String description = resultSet.getString("description");
            int quantityBorrowed = resultSet.getInt("quantity_borrowed");

            booksToReturn.add(new BookToReturn(transactionId, bookId, description, quantityBorrowed));
        }

        return booksToReturn;
    }

    public static void returnBook(String firstName, String lastName, int bookId, int quantity) throws SQLException, ClassNotFoundException {
        updateBook(bookId, quantity);
        updateTransactionByName(firstName, lastName, bookId);
    }

    private static void updateTransactionByName(String firstName, String lastName, int bookId) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();

        LocalDate dateReturned = LocalDate.now();

        PreparedStatement statement = connection.prepareStatement("UPDATE transactions_table SET date_returned = ? WHERE first_name = ? AND last_name = ? AND book_id = ?");
        statement.setDate(1, Date.valueOf(dateReturned));
        statement.setString(2, firstName);
        statement.setString(3, lastName);
        statement.setInt(4, bookId);

        statement.executeUpdate();
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

        PreparedStatement statement = connection.prepareStatement("INSERT INTO articles_table (date_acquired, article_name, property_number, quantity, unit_cost, total_cost, remarks) VALUES (?, ?,?,?,?,?,?)");
        statement.setDate(1, java.sql.Date.valueOf(dateAcquired.toString()));
        statement.setString(2, articleName);
        statement.setString(3, propertyNumber);
        statement.setInt(4, quantity);
        statement.setDouble(5, unitCost);
        statement.setDouble(6, totalCost);
        statement.setString(7, remarks);
        statement.executeUpdate();
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

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM transactions_table WHERE first_name=? AND last_name=?");
        statement.setString(1, firstName);
        statement.setString(2, lastName);

        ResultSet resultSet = statement.executeQuery();

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

    public static int getTotalQuantityOfBorrowedBook(String firstName, String lastName, int bookId) throws SQLException, ClassNotFoundException {
        int sum = 0;

        Connection connection = getConnection();
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder);

        PreparedStatement statement = connection.prepareStatement("SELECT quantity_borrowed FROM transactions_table WHERE first_name=? AND last_name=? AND book_id=? AND date_returned is NULL");
        statement.setString(1, firstName);
        statement.setString(2, lastName);
        statement.setInt(3, bookId);

        ResultSet resultSet = statement.executeQuery();

        while(resultSet.next()) {
            int currentQuantity = resultSet.getInt("quantity_borrowed");
            sum += currentQuantity;
        }

        return sum;
    }
}
