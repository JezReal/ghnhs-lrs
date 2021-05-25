package io.github.jezreal.controller;

import io.github.jezreal.database.Database;
import io.github.jezreal.model.BookToReturn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.time.LocalDate;

public class ReturnBooksController {

    @FXML
    private AnchorPane returnBooks;

    @FXML
    private TextArea previewTextArea;

    @FXML
    private ComboBox<String> booksCombobox;

    @FXML
    private Button addButton;

    @FXML
    private Button returnButton;

    private Home homeController;
    private ObservableList<BookToReturn> booksToReturn;
    private ObservableList<String> booksToReturnNames;

    //contains list of books that must be updated
    private ObservableList<BookToReturn> returnees;

    private String firstName, lastName;


    public void initialize() {
        booksToReturnNames = FXCollections.observableArrayList();
        returnees = FXCollections.observableArrayList();
    }

    public void setData(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

        preloadTextArea();
        loadCombobox();
        setListeners();
    }

    private void loadCombobox() {
        booksToReturn = Database.getBooksToReturn(firstName, lastName);


        for (BookToReturn book : booksToReturn) {
            booksToReturnNames.add(book.getDescription());
        }

        booksCombobox.setItems(booksToReturnNames);
        booksCombobox.getSelectionModel().select(0);
    }

    public void setHomeController(Home controller) {
        homeController = controller;
    }

    private void preloadTextArea() {
        previewTextArea.appendText("Name: " + firstName + lastName + "\n");
        previewTextArea.appendText("Date: " + LocalDate.now() + "\n");
        previewTextArea.appendText("Book/s to return:\n\n");
    }

    private void setListeners() {
        addButton.setOnAction(event -> {
            int index = booksCombobox.getSelectionModel().getSelectedIndex();
            BookToReturn book = booksToReturn.get(index);

            booksToReturnNames.remove(index);
            booksCombobox.setItems(booksToReturnNames);

            returnees.add(book);

            previewTextArea.appendText(book.getDescription() + "\n");
        });

        returnButton.setOnAction(event -> {

            if (returnees.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Error");
                alert.setContentText("You did not select a book to return");
                alert.showAndWait();
            } else {
                for (BookToReturn book : returnees) {
                    int transactionId = book.getTransactionId();
                    int bookId = book.getBookId();
                    int quantity = book.getQuantityBorrowed();

                    Database.returnBook(transactionId, bookId, quantity);
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Success");
                alert.setContentText("Book/s have been returned");
                alert.showAndWait();

                homeController.loadBooksTableData();
                homeController.loadBorrowedBooksTableData();
                homeController.loadNamesComboBox();
                dismissWindow();
            }
        });
    }

    private void dismissWindow() {
        Stage stage = (Stage) returnBooks.getScene().getWindow();
        stage.close();
    }
}
