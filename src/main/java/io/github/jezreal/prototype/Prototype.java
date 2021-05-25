package io.github.jezreal.prototype;

import io.github.jezreal.database.Database;
import io.github.jezreal.model.Book;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class Prototype {

    @FXML
    private TableView<Book> tableView;

    @FXML
    private TableColumn<Book, String> descriptionColumn;

    @FXML
    private TableColumn<Book, Integer> quantityColumn;

    @FXML
    private TextField descriptionInput;

    @FXML
    private TextField quantityInput;

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button cancelButton;


    private ObservableList<Book> books;

    public void initialize() {
//        System.out.println("Observable");
//        Observable<Integer> emissionsSource = Observable.just(1, 2, 3, 4, 5);
//        emissionsSource.subscribe(number -> System.out.println("Number=" + number));

//        Database.names.subscribe(name -> System.out.println("Name: " + name));

        initializeTable();
        setupListeners();
        updateButton.setDisable(true);
    }

    private void initializeTable() {
        books = Database.getAllBooks();

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );

        quantityColumn.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        tableView.setItems(books);
    }

    private void setupListeners() {
        books.addListener((ListChangeListener<? super Book>) change -> {
            updateTable();
        });

        addButton.setOnAction(actionEvent -> {
            if (validateInput()) {
                String description = descriptionInput.getText();
                int quantity = Integer.parseInt(quantityInput.getText());

                Book newBook = new Book(description, quantity);

                books.add(newBook);

                Database.addBook(newBook);

                clearInputFields();
            } else {
                //display error dialogue
                System.out.println("Error");
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {
                descriptionInput.setText(newSelection.getDescription());
                quantityInput.setText(String.valueOf(newSelection.getQuantity()));
                updateButton.setDisable(false);
                addButton.setDisable(true);
            }
        }));

        updateButton.setOnAction(actionEvent -> {
            if (validateInput()) {
                int id = tableView.getSelectionModel().getSelectedItem().getId();
                String description = descriptionInput.getText();
                int quantity = Integer.parseInt(quantityInput.getText());

                Book newBook = new Book(id, description, quantity);

                Database.updateBook(newBook);
                updateTable(newBook);
            }
        });

        cancelButton.setOnAction(actionEvent -> {
            tableView.getSelectionModel().clearSelection();
            addButton.setDisable(false);
            updateButton.setDisable(true);
            clearInputFields();
        });
    }

    private void updateTable() {
        tableView.setItems(books);
    }

    private void updateTable(Book book) {
        for (Book r : books) {
            if (r.getId() == book.getId()) {
                r.setDescription(book.getDescription());
                r.setQuantity(book.getQuantity());
                break;
            }
        }
        updateTable();
//        tableView.setItems(records);
    }

    private boolean validateInput() {
        if (descriptionInput.getText().isEmpty() || quantityInput.getText().isEmpty()) {
            return false;
        }

        return true;
    }

    private void clearInputFields() {
        descriptionInput.clear();
        quantityInput.clear();
    }
}
