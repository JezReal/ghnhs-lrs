package io.github.jezreal.controller;

import io.github.jezreal.model.Book;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.time.LocalDate;

public class BooksGeneratedReportController {

    @FXML
    private TextArea reportArea;


    public void initialize() {
        reportArea.appendText("LRS generated report\n");
        reportArea.appendText("Date: " + LocalDate.now() + "\n\n");
        reportArea.appendText("Description\t\t\tQuantity\n\n");
    }

    public void loadData(ObservableList<Book> books) {
        for (Book book : books) {
            reportArea.appendText(book.getDescription() + "\t\t\t\t");
            reportArea.appendText(book.getQuantity() + "\n");
        }
    }


}
