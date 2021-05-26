package io.github.jezreal.controller;

import io.github.jezreal.database.Database;
import io.github.jezreal.model.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;

public class BooksDataByPersonController {

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
        ObservableList<Transaction> transaction = Database.getTransactionsByName(firstName, lastName);
        booksDataByPersonTable.setItems(transaction);

    }
}
