package io.github.jezreal.controller;

import io.github.jezreal.database.Database;
import io.github.jezreal.model.Book;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.sql.SQLException;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class EditItem {

    @FXML
    private TextField descriptionTextfield;

    @FXML
    private TextField quantityTextField;

    @FXML
    private Button deleteButton;

    @FXML
    private Button saveButton;

    @FXML
    private AnchorPane editItem;

    private Book book;
    private Home home;

    public void initialize() {
        setUpListeners();
    }

    public void setData(Book book) {
        if (book == null) {
            dismissWindow();
        }

        descriptionTextfield.setText(book.getDescription());
        quantityTextField.setText(String.valueOf(book.getQuantity()));

        this.book = book;
    }


    private void setUpListeners() {
        deleteButton.setVisible(false);

        deleteButton.setOnAction(action -> {
            try {
                Database.deleteBook(book.getId());
            } catch (SQLException | ClassNotFoundException e) {
                Alert alert = new Alert(ERROR);
                alert.setHeaderText("SQL Error!");
                alert.setContentText("An unexpected sql error has occurred. Please report the issue to the developers");
                alert.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Success!");
            alert.setContentText("Record has been deleted successfully");
            alert.showAndWait();

            home.loadBooksTableData();

            dismissWindow();
        });

        saveButton.setOnAction(action -> {
            if (validateInput()) {
                Book updatedBook = new Book(descriptionTextfield.getText(), Integer.parseInt(quantityTextField.getText()));
                updatedBook.setId(book.getId());

                try {
                    Database.updateBook(updatedBook);
                } catch (SQLException | ClassNotFoundException e) {
                    Alert alert = new Alert(ERROR);
                    alert.setHeaderText("SQL Error!");
                    alert.setContentText("An unexpected sql error has occurred. Please report the issue to the developers");
                    alert.showAndWait();
                    return;
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Success!");
                alert.setContentText("Record has been added successfully");
                alert.showAndWait();

                home.loadBooksTableData();

                dismissWindow();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Invalid input!");
                alert.setContentText("There is an error in your input. Please try again");
                alert.showAndWait();

            }
        });
    }

    private boolean validateInput() {
        if (descriptionTextfield.getText().isEmpty() || quantityTextField.getText().isEmpty()) {
            return false;
        }

        return true;
    }

    private void dismissWindow() {
        Stage stage = (Stage) editItem.getScene().getWindow();
        stage.close();
    }

    public void setHomeController(Home controller) {
        this.home = controller;
    }

}
