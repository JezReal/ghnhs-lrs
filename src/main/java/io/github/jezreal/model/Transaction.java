package io.github.jezreal.model;

import io.github.jezreal.database.Database;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.time.LocalDate;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class Transaction {

    private int transactionId;
    private int bookId;
    private String firstName;
    private String lastName;
    private LocalDate dateBorrowed;
    private LocalDate dateReturned;
    private int quantity;

    private String bookBorrowed;

    public Transaction(
            int transactionId,
            int bookId,
            String firstName,
            String lastName,
            LocalDate dateBorrowed,
            LocalDate dateReturned,
            int quantity
    ) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
        this.quantity = quantity;

        try {
            this.bookBorrowed = Database.getBook(bookId).getDescription();
        } catch (SQLException | ClassNotFoundException e) {
            this.bookBorrowed = "SQL error please report the issue to the developers";
        }
    }

    public Transaction(
            int transactionId,
            int bookId,
            String firstName,
            String lastName,
            LocalDate dateBorrowed,
            int quantity
    ) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateBorrowed = dateBorrowed;
        this.quantity = quantity;

        try {
            this.bookBorrowed = Database.getBook(bookId).getDescription();
        } catch (SQLException | ClassNotFoundException e) {
            this.bookBorrowed = "SQL error please report the issue to the developers";
        }
    }

    public Transaction(int transactionId, int bookId, String firstName, String lastName) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Transaction(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Transaction(int transactionId, int bookId, LocalDate dateBorrowed, LocalDate dateReturned, int quantity) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.dateBorrowed = dateBorrowed;
        this.dateReturned = dateReturned;
        this.quantity = quantity;

        try {
            this.bookBorrowed = Database.getBook(bookId).getDescription();
        } catch (SQLException | ClassNotFoundException e) {
            this.bookBorrowed = "SQL error please report the issue to the developers";
        }
    }

    public Transaction(int transactionId, int bookId, LocalDate dateBorrowed, int quantity) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.dateBorrowed = dateBorrowed;
        this.quantity = quantity;

        try {
            this.bookBorrowed = Database.getBook(bookId).getDescription();
        } catch (SQLException | ClassNotFoundException e) {
            this.bookBorrowed = "SQL error please report the issue to the developers";
        }
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateBorrowed() {
        return dateBorrowed;
    }

    public void setDateBorrowed(LocalDate dateBorrowed) {
        this.dateBorrowed = dateBorrowed;
    }

    public LocalDate getDateReturned() {
        return dateReturned;
    }

    public void setDateReturned(LocalDate dateReturned) {
        this.dateReturned = dateReturned;
    }

    public String getBookBorrowed() {
        return bookBorrowed;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", bookId=" + bookId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", dateBorrowed=" + dateBorrowed +
                ", dateReturned=" + dateReturned +
                ", quantity=" + quantity +
                ", bookBorrowed='" + bookBorrowed + '\'' +
                '}';
    }
}
