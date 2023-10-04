package io.github.jezreal.controller;

import io.github.jezreal.database.Database;
import io.github.jezreal.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class SelectBooks {

    @FXML
    private ComboBox<String> booksCombobox;

    @FXML
    private TextArea previewTextArea;

    @FXML
    private TextField quantityInput;

    @FXML
    private Button addButton;

    @FXML
    private Button borrowButton;

    @FXML
    private Text availableQuantityText;

    @FXML
    private AnchorPane selectBooks;


    private Home homeController;

    private ObservableList<Book> books;
    private List<Book> borrowedBooks;
    private List<Integer> borrowedIds;

    private String firstName;
    private String lastName;
    private LocalDate date;


    public void initialize() {
        borrowedBooks = new ArrayList<>();
        borrowedIds = new ArrayList<>();
        setListeners();
    }

    public void setData(ObservableList<Book> books, String firstName, String lastName, LocalDate date) {
        ObservableList<String> availableBooks = FXCollections.observableArrayList();
        this.books = books;

        this.firstName = firstName;
        this.lastName = lastName;
        this.date = date;


        for (Book book : books) {
            availableBooks.add(book.getDescription());
        }

        previewTextArea.appendText("Name: " + firstName + " " + lastName + "\n");
        previewTextArea.appendText("Date: " + date + "\n");

        previewTextArea.appendText("Book/s to borrow:\n\n");

        booksCombobox.setItems(availableBooks);
        booksCombobox.getSelectionModel().select(0);
    }

    private void setListeners() {
        addButton.setOnAction(action -> {
            if (validateInput()) {
                String selectedBook = booksCombobox.getSelectionModel().getSelectedItem();
                int quantity = Integer.parseInt(quantityInput.getText());
                int index = booksCombobox.getSelectionModel().getSelectedIndex();

                int currentQuantity = books.get(index).getQuantity();

                Book book = books.get(index);
                book.setQuantity(currentQuantity - quantity);
                setQuantityText();

                previewTextArea.appendText(selectedBook + " : " + quantity + "\n");
                //add to borrowed books

                int id = books.get(index).getId();

                borrowedIds.add(id);
                borrowedBooks.add(new Book(id, selectedBook, quantity));

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Invalid input!");
                alert.setContentText("Please check if your input is correct");
                alert.showAndWait();
            }
        });

        borrowButton.setOnAction(action -> {
            if (borrowedIds.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("You did not select a book to borrow");
                alert.showAndWait();
            } else {

                for (int i = 0; i < borrowedBooks.size(); i++) {
                    int currentId = borrowedIds.get(i);
                    Book book = borrowedBooks.get(i);
                    int quantity = borrowedBooks.get(i).getQuantity();

                    try {
                        Database.addBorrowedBook(firstName.trim(), lastName.trim(), date, currentId, quantity, book);
                    } catch (SQLException | ClassNotFoundException e) {
                        Alert alert = new Alert(ERROR);
                        alert.setHeaderText("SQL Error!");
                        alert.setContentText("An unexpected sql error has occurred. Please report the issue to the developers");
                        alert.showAndWait();
                        e.printStackTrace();
                        return;
                    }
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Success");
                alert.setContentText("Book/s have been borrowed");
                alert.showAndWait();

                dismissWindow();
                homeController.loadBooksTableData();
                homeController.clearBorrowBooksInput();
                homeController.loadNamesComboBox();
            }
        });

        booksCombobox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> setQuantityText());
    }

    private boolean validateInput() {
        int quantity;

        try {
            quantity = Integer.parseInt(quantityInput.getText());
        } catch (NumberFormatException e) {
            return false;
        }

        // two lists should have the same index so we get the selected index
        // and use that index to refer to the list containing the object
        // which we can use to query the database
        int selectedBook = booksCombobox.getSelectionModel().getSelectedIndex();
        Book book = books.get(selectedBook);

        return book.getQuantity() >= quantity;
    }

    private void setQuantityText() {
        int selectedBook = booksCombobox.getSelectionModel().getSelectedIndex();
        Book book = books.get(selectedBook);

        availableQuantityText.setText(String.valueOf(book.getQuantity()));
    }

    private void dismissWindow() {
        Stage stage = (Stage) selectBooks.getScene().getWindow();
        stage.close();
    }

    public void setHomeController(Home controller) {
        homeController = controller;
    }
}
