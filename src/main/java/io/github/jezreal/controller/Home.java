package io.github.jezreal.controller;

import io.github.jezreal.App;
import io.github.jezreal.database.Database;
import io.github.jezreal.model.Book;
import io.github.jezreal.model.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

import static javafx.scene.control.Alert.AlertType.*;

public class Home {

    @FXML
    private AnchorPane addRecordPage;

    @FXML
    private AnchorPane viewRecordsPage;

    @FXML
    private AnchorPane aboutPage;

    @FXML
    private Button addRecordPageButton;

    @FXML
    private AnchorPane borrowBooksPage;

    @FXML
    private Button viewRecordsPageButton;

    @FXML
    private Button aboutPageButton;

    @FXML
    private TableView<Book> recordsTable;

    @FXML
    private TableColumn<Book, String> descriptionColumn;

    @FXML
    private TableColumn<Book, Integer> quantityColumn;

    @FXML
    private Button addRecordButton;

    @FXML
    private TextField descriptionInput;

    @FXML
    private TextField quantityInput;

    @FXML
    private Button editButton;

    @FXML
    private Button showBooksReportButton;

    @FXML
    private Button showTransactionsReportButton;

    @FXML
    private Button borrowBooksButton;

    @FXML
    private Button selectBooksButton;

    @FXML
    private TextField firstNameInput;

    @FXML
    private TextField lastNameInput;

    @FXML
    private DatePicker datePickerInput;

    @FXML
    private Button selectReturnButton;

    @FXML
    private ComboBox<String> namesCombobox;

    @FXML
    private TableView<Transaction> borrowedBooksTable;

    @FXML
    private TableColumn<Transaction, String> firstNameColumn;

    @FXML
    private TableColumn<Transaction, String> lastNameColumn;

    @FXML
    private TableColumn<Transaction, String> bookBorrowedColumn;

    @FXML
    private TableColumn<Transaction, LocalDate> dateBorrowedColumn;

    @FXML
    private TableColumn<Transaction, LocalDate> dateReturnedColumn;

    @FXML
    private TableColumn<Transaction, Integer> quantityBorrowedColumn;

    private ObservableList<Transaction> unreturnedTransactions;
    private ObservableList<String> unreturnedTransactionNames;

    private ObservableList<Transaction> transactions;

    private ObservableList<Book> books;

    public void initialize() {

        addRecordPage.setVisible(false);
        aboutPage.setVisible(false);
        borrowBooksPage.setVisible(false);

        viewRecordsPageButton.fire();

        setupListeners();

        addRecordPageButton.setOnAction(actionEvent -> {
            addRecordPage.setVisible(true);
            borrowBooksPage.setVisible(false);
            viewRecordsPage.setVisible(false);
            aboutPage.setVisible(false);
        });

        borrowBooksButton.setOnAction(actionEvent -> {
            addRecordPage.setVisible(false);
            borrowBooksPage.setVisible(true);
            viewRecordsPage.setVisible(false);
            aboutPage.setVisible(false);

            loadNamesComboBox();
        });

        viewRecordsPageButton.setOnAction(actionEvent -> {
            addRecordPage.setVisible(false);
            borrowBooksPage.setVisible(false);
            viewRecordsPage.setVisible(true);
            aboutPage.setVisible(false);
            loadBooksTableData();
            loadBorrowedBooksTableData();
        });

        aboutPageButton.setOnAction(actionEvent -> {
            addRecordPage.setVisible(false);
            borrowBooksPage.setVisible(false);
            viewRecordsPage.setVisible(false);
            aboutPage.setVisible(true);
        });

        loadBooksTableData();
        loadBorrowedBooksTableData();
        loadNamesComboBox();
        populateTable();
    }

    private void populateTable() {
        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        firstNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("firstName")
        );

        lastNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastName")
        );

        bookBorrowedColumn.setCellValueFactory(
                new PropertyValueFactory<>("bookBorrowed")
        );

        dateBorrowedColumn.setCellValueFactory(
                new PropertyValueFactory<>("dateBorrowed")
        );

        dateReturnedColumn.setCellValueFactory(
                new PropertyValueFactory<>("dateReturned")
        );

        quantityBorrowedColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );
    }

    public void loadBooksTableData() {
        books = Database.getAllBooks();
        recordsTable.setItems(books);
    }

    public void loadBorrowedBooksTableData() {
        transactions = Database.getAllTransactions();
        borrowedBooksTable.setItems(transactions);
    }

    public void loadNamesComboBox() {
        unreturnedTransactions = Database.getUniqueUnreturnedTransactions();
        unreturnedTransactionNames = FXCollections.observableArrayList();

        for (Transaction transaction : unreturnedTransactions) {
            String lastName = transaction.getLastName();
            String firstName = transaction.getFirstName();

            String name = lastName + ", " + firstName;

            unreturnedTransactionNames.add(name);
        }

        namesCombobox.setItems(unreturnedTransactionNames);
    }

    private boolean validateAddBookInput() {
        if (descriptionInput.getText().isEmpty() || quantityInput.getText().isEmpty()) {
            return false;
        }

        try {
            //check if quantity is of valid type
            Integer.parseInt(quantityInput.getText());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    private void setupListeners() {
        addRecordButton.setOnAction(event -> {
            if (validateAddBookInput()) {
                String description = descriptionInput.getText();
                int quantity = Integer.parseInt(quantityInput.getText());

                Database.addBook(new Book(description, quantity));

                descriptionInput.clear();
                quantityInput.clear();

                Alert alert = new Alert(INFORMATION);
                alert.setHeaderText("Success!");
                alert.setContentText("Record has been added successfully");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(ERROR);
                alert.setHeaderText("Invalid input!");
                alert.setContentText("There is an error in your input. Please try again");
                alert.showAndWait();
            }
        });

        recordsTable.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                editButton.setDisable(false);
            }
        });

        editButton.setOnAction(actionEvent -> loadEditRecordDialog());

        selectBooksButton.setOnAction(mouseAction -> loadSelectBooksDialog());

        selectReturnButton.setOnAction(action -> loadReturnBooksDialog());

        showBooksReportButton.setOnAction(action -> generateBooksReport());

        showTransactionsReportButton.setOnAction(action -> generateTransactionsReport());
    }

    private void loadEditRecordDialog() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ui/edit_item.fxml"));
            Parent root = fxmlLoader.load();

            EditItemController editItemController = fxmlLoader.getController();
            Book selectedBook = recordsTable.getSelectionModel().getSelectedItem();

            if (selectedBook != null) {
                Scene scene = new Scene(root);
                Stage stage = new Stage();

                editItemController.setData(selectedBook);
                editItemController.setHomeController(this);
                stage.setScene(scene);
                stage.setTitle("Edit");
                stage.setResizable(false);
                stage.show();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSelectBooksDialog() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ui/select_books.fxml"));
            Parent root = fxmlLoader.load();

            SelectBooksController selectBooksController = fxmlLoader.getController();

            ObservableList<Book> availableBooks = Database.getAvailableBooks();

            if (availableBooks.isEmpty()) {
                Alert alert = new Alert(INFORMATION);
                alert.setHeaderText("Empty");
                alert.setContentText("There are no available books to be borrowed");
                alert.showAndWait();
            } else if (!validateBorrowBookInput()) {
                Alert alert = new Alert(ERROR);
                alert.setHeaderText("Invalid input");
                alert.setContentText("Invalid input. Please check your input");
                alert.showAndWait();
            } else {
                selectBooksController.setData(availableBooks, firstNameInput.getText(), lastNameInput.getText(), datePickerInput.getValue());
                selectBooksController.setHomeController(this);

                Scene scene = new Scene(root);
                Stage stage = new Stage();

                stage.setScene(scene);
                stage.setTitle("Select books");
                stage.setResizable(false);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateBorrowBookInput() {

        if (firstNameInput.getText().isEmpty() || lastNameInput.getText().isEmpty() || datePickerInput.getValue() == null) {
            return false;
        }

        return true;
    }

    public void clearBorrowBooksInput() {
        firstNameInput.clear();
        lastNameInput.clear();
    }

    private void loadReturnBooksDialog() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ui/return_books.fxml"));
            Parent root = fxmlLoader.load();

            if (unreturnedTransactionNames.isEmpty()) {
                Alert alert = new Alert(INFORMATION);
                alert.setHeaderText("Empty");
                alert.setContentText("No books to return");
                alert.showAndWait();
            } else if (namesCombobox.getSelectionModel().getSelectedItem() == null) {
                Alert alert = new Alert(ERROR);
                alert.setHeaderText("Invalid input");
                alert.setContentText("Please select a name");
                alert.showAndWait();
            } else {

                ReturnBooksController returnBooksController = fxmlLoader.getController();

                int index = namesCombobox.getSelectionModel().getSelectedIndex();
                Transaction transaction = unreturnedTransactions.get(index);

                returnBooksController.setData(transaction.getFirstName(), transaction.getLastName());
                returnBooksController.setHomeController(this);

                Scene scene = new Scene(root);
                Stage stage = new Stage();

                stage.setScene(scene);
                stage.setTitle("Return books");
                stage.setResizable(false);
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateBooksReport() {
        String userHome = System.getProperty("user.home");
        //escape backslashes because backslash is considered a regular expression
        String path = userHome.replaceAll("\\\\", "\\\\\\\\");

        LocalDate localDate = LocalDate.now();
        String pdfPath = path.concat("\\\\Desktop\\\\lrs-books-generated-report-" + localDate + ".pdf");

//        try {
//            PdfDocument pdf = new PdfDocument(new PdfWriter(pdfPath));
//            Document document = new Document(pdf);
//
//            ObservableList<Book> books = Database.getAllBooks();
//
//            String line = "Generated report by lrs";
//
//            Paragraph paragraph = new Paragraph(line);
//            paragraph.setHorizontalAlignment(HorizontalAlignment.LEFT);
//            paragraph.setVerticalAlignment(VerticalAlignment.TOP);
//
//            document.setMargins(1, 1, 1, 1);
//
//            document.add(paragraph);
//
//            Table table = new Table(3);
//
//            table.setVerticalAlignment(VerticalAlignment.BOTTOM);
//            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
//
//            table.addHeaderCell("");
//            table.addHeaderCell("Description");
//            table.addHeaderCell("Quantity");
//
//            for (int i = 0; i < books.size(); i++) {
//                String description = books.get(i).getDescription();
//                int quantity = books.get(i).getQuantity();
//
//                table.addCell(String.valueOf(i + 1));
//                table.addCell(description);
//                table.addCell(String.valueOf(quantity));
//            }
//
//            document.add(table);
//
//            document.close();
//
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setHeaderText("Success!");
//            alert.setContentText("Records has been exported as PDF to your desktop");
//            alert.showAndWait();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

//        try {
//            report()
//                    .columns(
//                            col.column("Iten", "item", type.stringType()),
//                            col.column("Quantity", "quantity", type.integerType()),
//                            col.column("Unit price", "unitprice", type.bigDecimalType())
//                    )
//                    .title(cmp.text("Getting started"))
//                    .pageFooter(cmp.pageXofY())
//                    .show();
//        } catch (DRException e) {
//            e.printStackTrace();
//        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ui/books_generated_report.fxml"));
            Parent root = fxmlLoader.load();

            BooksGeneratedReportController controller = fxmlLoader.getController();

            if (books.isEmpty()) {
                Alert alert = new Alert(INFORMATION);
                alert.setHeaderText("Empty data");
                alert.setContentText("Cannot generate report. Empty data");
                alert.showAndWait();
            } else {
                controller.loadData(books);

                Scene scene = new Scene(root);
                Stage stage = new Stage();

                stage.setScene(scene);
                stage.setTitle("Books generated report");
                stage.setResizable(false);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateTransactionsReport() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("ui/transactions_generated_report.fxml"));
            Parent root = fxmlLoader.load();

            TransactionsGeneratedReportController controller = fxmlLoader.getController();

            if (transactions.isEmpty()) {
                Alert alert = new Alert(INFORMATION);
                alert.setHeaderText("Empty data");
                alert.setContentText("Cannot generate report. Empty data");
                alert.showAndWait();
            } else {
                controller.loadData(transactions);

                Scene scene = new Scene(root);
                Stage stage = new Stage();

                stage.setScene(scene);
                stage.setTitle("Books generated report");
                stage.setResizable(false);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateReport(String report) {
        FXMLLoader fxmlLoader = null;
        Parent root = null;
        Scene scene = null;
        Stage stage= null;

        if (report.equals("books")) {
            try {
                fxmlLoader = new FXMLLoader(App.class.getResource("ui/books_generated_report.fxml"));
                root = fxmlLoader.load();

                BooksGeneratedReportController controller = fxmlLoader.getController();

                if (books.isEmpty()) {
                    Alert alert = new Alert(INFORMATION);
                    alert.setHeaderText("Empty data");
                    alert.setContentText("Cannot generate report. Empty data");
                    alert.showAndWait();
                } else {
                    controller.loadData(books);

                    scene = new Scene(root);
                    stage = new Stage();

                    stage.setScene(scene);
                    stage.setTitle("Books generated report");
                    stage.setResizable(false);
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                fxmlLoader = new FXMLLoader(App.class.getResource("ui/transactions_generated_report.fxml"));
                root = fxmlLoader.load();

                TransactionsGeneratedReportController controller = fxmlLoader.getController();

                if (transactions.isEmpty()) {
                    Alert alert = new Alert(INFORMATION);
                    alert.setHeaderText("Empty data");
                    alert.setContentText("Cannot generate report. Empty data");
                    alert.showAndWait();
                } else {
                    controller.loadData(transactions);

                    scene = new Scene(root);
                    stage = new Stage();

                    stage.setScene(scene);
                    stage.setTitle("Transactions generated report");
                    stage.setResizable(false);
                    stage.show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
