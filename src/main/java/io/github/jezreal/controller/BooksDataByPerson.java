package io.github.jezreal.controller;

import io.github.jezreal.database.Database;
import io.github.jezreal.model.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class BooksDataByPerson {

    @FXML
    private TableView<Transaction> booksDataByPersonTable;

    @FXML
    private TableColumn<Transaction, String> bookBorrowedColumn;

    @FXML
    private TableColumn<Transaction, LocalDate> dateBorrowedColumn;

    @FXML
    private TableColumn<Transaction, LocalDate> dateReturnedColumn;

    @FXML
    private TableColumn<Transaction, Integer> quantityColumn;

    public void initialize() {
        bookBorrowedColumn.setCellValueFactory(
                new PropertyValueFactory<>("bookBorrowed")
        );

        dateBorrowedColumn.setCellValueFactory(
                new PropertyValueFactory<>("dateBorrowed")
        );

        dateReturnedColumn.setCellValueFactory(
                new PropertyValueFactory<>("dateReturned")
        );

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );
    }


    public void setData(String firstName, String lastName) {
        ObservableList<Transaction> transaction = null;

        try {
            transaction = Database.getTransactionsByName(firstName, lastName);
        } catch (SQLException | ClassNotFoundException e) {
            Alert alert = new Alert(ERROR);
            alert.setHeaderText("SQL Error!");
            alert.setContentText("An unexpected sql error has occurred. Please report the issue to the developers");
            alert.showAndWait();
        }

        booksDataByPersonTable.setItems(transaction);
    }
}
