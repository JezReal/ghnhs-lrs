package io.github.jezreal.model;

public class BookToReturn {

    private int transactionId;
    private int bookId;
    private String description;
    private int quantityBorrowed;

    public BookToReturn(int transactionId, int bookId, String description, int quantityBorrowed) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.description = description;
        this.quantityBorrowed = quantityBorrowed;
    }

    public BookToReturn(int bookId, String description, int quantityBorrowed) {
        this.bookId = bookId;
        this.description = description;
        this.quantityBorrowed = quantityBorrowed;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantityBorrowed() {
        return quantityBorrowed;
    }

    public void setQuantityBorrowed(int quantityBorrowed) {
        this.quantityBorrowed = quantityBorrowed;
    }

    @Override
    public String toString() {
        return "BookToReturn{" +
                "transactionId=" + transactionId +
                ", bookId=" + bookId +
                ", description='" + description + '\'' +
                ", quantityBorrowed=" + quantityBorrowed +
                '}';
    }
}
