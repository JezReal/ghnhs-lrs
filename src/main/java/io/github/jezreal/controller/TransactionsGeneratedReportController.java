package io.github.jezreal.controller;

import io.github.jezreal.model.Transaction;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.time.LocalDate;

public class TransactionsGeneratedReportController {

    @FXML
    private TextArea reportArea;


    public void initialize() {
        reportArea.appendText("LRS generated report\n");
        reportArea.appendText("Date: " + LocalDate.now() + "\n\n");
        reportArea.appendText("Name\t\t\tBook borrowed\t\tDate issued\t\tDate returned\n\n");
    }

    public void loadData(ObservableList<Transaction> transactions) {
        String repeatingName = transactions.get(0).getFirstName() + " " +transactions.get(0).getLastName();
        boolean isRepeatingNameWritten = false;

        for (Transaction transaction : transactions) {
            String currentName = transaction.getFirstName() + " " + transaction.getLastName();
            if (currentName.equals(repeatingName)) {
                if (!isRepeatingNameWritten) {
                    reportArea.appendText(currentName + "\t\t" + transaction.getBookBorrowed() + "\t\t\t\t");
                    isRepeatingNameWritten = true;
                } else {
                    reportArea.appendText("\t\t\t\t" + transaction.getBookBorrowed() + "\t\t\t\t");
                }
            } else {
                repeatingName = currentName;

                reportArea.appendText(currentName + "\t\t\t" + transaction.getBookBorrowed() + "\t\t");
            }

            reportArea.appendText(transaction.getDateBorrowed() + "\t\t");

            if (transaction.getDateReturned() != null) {
                reportArea.appendText(transaction.getDateReturned() + "\n");
            } else {
                reportArea.appendText("\n");
            }
        }
    }
}
