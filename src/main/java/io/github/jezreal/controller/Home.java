package io.github.jezreal.controller;

import io.github.jezreal.App;
import io.github.jezreal.database.Database;
import io.github.jezreal.model.Article;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

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
    private DatePicker articlesDatePickerInput;

    @FXML
    private TextField articleNameInput;

    @FXML
    private TextField propertyNumberInput;

    @FXML
    private TextField articleQuantityInput;

    @FXML
    private TextField unitCostInput;

    @FXML
    private TextArea remarksInput;

    @FXML
    private Button addArticleButton;


    @FXML
    private ComboBox<String> namesCombobox;

    @FXML
    private TableView<Article> articlesTable;

    @FXML
    private TableColumn<Article, LocalDate> dateAcquiredColumn;

    @FXML
    private TableColumn<Article, String> articleNameColumn;

    @FXML
    private TableColumn<Article, String> propertyNumberColumn;

    @FXML
    private TableColumn<Article, Integer> articleQuantityColumn;

    @FXML
    private TableColumn<Article, Double> unitCostColumn;

    @FXML
    private TableColumn<Article, Double> totalCostColumn;

    @FXML
    private TableColumn<Article, String> remarksColumn;

    private ObservableList<Transaction> unreturnedTransactions;
    private ObservableList<String> unreturnedTransactionNames;

    private ObservableList<Transaction> transactions;

    private ObservableList<Book> books;

    private ObservableList<Article> articles;

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
            loadArticlesData();
        });

        aboutPageButton.setOnAction(actionEvent -> {
            addRecordPage.setVisible(false);
            borrowBooksPage.setVisible(false);
            viewRecordsPage.setVisible(false);
            aboutPage.setVisible(true);
        });

        loadBooksTableData();
        loadBorrowedBooksTableData();
        loadArticlesData();
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

        dateAcquiredColumn.setCellValueFactory(
                new PropertyValueFactory<>("dateAcquired")
        );

        articleNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("articleName")
        );

        propertyNumberColumn.setCellValueFactory(
                new PropertyValueFactory<>("propertyNumber")
        );

        articleQuantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        unitCostColumn.setCellValueFactory(
                new PropertyValueFactory<>("unitCost")
        );

        totalCostColumn.setCellValueFactory(
                new PropertyValueFactory<>("totalCost")
        );

        remarksColumn.setCellValueFactory(
                new PropertyValueFactory<>("remarks")
        );
    }

    public void loadBooksTableData() {
        books = Database.getAllBooks();
        recordsTable.setItems(books);
    }

    public void loadBorrowedBooksTableData() {
//        transactions = Database.getAllTransactions();
//        borrowedBooksTable.setItems(transactions);
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
                alert.setContentText("Book has been added successfully");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(ERROR);
                alert.setHeaderText("Invalid input!");
                alert.setContentText("There is an error in your input. Please try again");
                alert.showAndWait();
            }
        });

        addArticleButton.setOnAction(event -> {
            if (validateAddArticleInput()) {
                int quantity = Integer.parseInt(articleQuantityInput.getText());
                double unitCost = Double.parseDouble(unitCostInput.getText());
                double totalCost = quantity * unitCost;

                Article article = new Article(
                        articlesDatePickerInput.getValue(),
                        articleNameInput.getText(),
                        propertyNumberInput.getText(),
                        quantity,
                        unitCost,
                        totalCost,
                        remarksInput.getText()
                );

                Database.addArticle(article);
                Alert alert = new Alert(INFORMATION);
                alert.setHeaderText("Success!");
                alert.setContentText("Article has been added successfully");
                alert.showAndWait();

                clearArticlesInput();
            } else {
                Alert alert = new Alert(ERROR);
                alert.setHeaderText("Invalid input!");
                alert.setContentText("There is an error in your input. Please try again");
                alert.showAndWait();
            }
        });

        selectBooksButton.setOnAction(mouseAction -> loadSelectBooksDialog());

        selectReturnButton.setOnAction(action -> loadReturnBooksDialog());

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

    private boolean validateAddArticleInput() {
        if (articlesDatePickerInput.getValue() == null || articleNameInput.getText().isEmpty() || propertyNumberInput.getText().isEmpty() || remarksInput.getText().isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(articleQuantityInput.getText());
            Double.parseDouble(unitCostInput.getText());
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    private void clearArticlesInput() {
        articleNameInput.clear();
        propertyNumberInput.clear();
        articleQuantityInput.clear();
        unitCostInput.clear();
        remarksInput.clear();
    }

    private void loadArticlesData() {
        articles = Database.getAllArticles();
        articlesTable.setItems(articles);
    }
}
