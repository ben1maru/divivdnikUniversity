package guide.guidetouniversities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CRUDFacultate {
    @FXML
    private FlowPane scrollPanel;
    private VBox universityList;
    private int currentPage = 0;
    private int universitiesPerPage = 5;
    private List<String> universityNames;

    @FXML
    private Button previousButton;

    @FXML
    private Button nextButton;

    @FXML
    void initialize() {
        universityList = new VBox();
        HBox contentBox = new HBox();
        contentBox.getChildren().add(universityList);
        scrollPanel.getChildren().add(contentBox);
        universityList.setSpacing(10);

        universityNames = new ArrayList<>();
        loadUniversities(); // Завантаження списку університетів
        displayUniversities();
    }

    DB db = new DB();

    private void loadUniversities() {
        try {
            String query = "SELECT name FROM facultet"; // Запит до бази даних
            PreparedStatement statement = db.getDbConnection().prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                universityNames.add(resultSet.getString("name"));
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void nextPage() {
        if (currentPage < universityNames.size() / universitiesPerPage) {
            currentPage++;
            displayUniversities();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayUniversities();
        }
    }

    private void displayUniversities() {
        universityList.getChildren().clear(); // Очищаємо флов панель перед додаванням нових елементів
        int startIndex = currentPage * universitiesPerPage;
        int endIndex = Math.min(startIndex + universitiesPerPage, universityNames.size());

        for (int i = startIndex; i < endIndex; i++) {
            addButton(universityNames.get(i));
        }

        // Перевіряємо, чи потрібно відображати кнопки "Вперед" і "Назад"
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable(currentPage >= universityNames.size() / universitiesPerPage);
    }

    private void deleteFacultate(String facultateName) {
        // SQL query to delete the university based on its name
        String deleteQuery = "DELETE FROM `facultet` WHERE `name` = ?";

        try (PreparedStatement preparedStatement = db.getDbConnection().prepareStatement(deleteQuery)) {
            // Set the university name as the parameter in the prepared statement
            preparedStatement.setString(1, facultateName);

            // Execute the delete statement
            int affectedRows = preparedStatement.executeUpdate();

            // Check if any rows were affected
            if (affectedRows > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Успіх");
                alert.setContentText("Дані було успішно видалено");
                alert.show();
                System.out.println("University '" + facultateName + "' deleted successfully.");
            } else {
                System.out.println("Failed to delete university '" + facultateName + "'.");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Упс");
                alert.setContentText("Щось пішло не так");
                alert.show();
            }

            // Clear and reload universities after deletion
            universityNames.clear();
            loadUniversities();
            displayUniversities();

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void addButton(String universityName) {
        Button editButton = new Button();
        editButton.setStyle("-fx-background-color: transparent; -fx-border-color: green; -fx-border-width: 2px;");
        Image editImage = new Image(getClass().getResourceAsStream("/photo/edit-content-svgrepo-com.png"));
        ImageView editImageView = new ImageView(editImage);
        editImageView.setFitWidth(30);
        editImageView.setFitHeight(30);
        editButton.setGraphic(editImageView);
        editButton.setPrefSize(30, 30);
        editButton.setOnAction(event -> {
            HBox parentBox = (HBox) editButton.getParent();
            Label nameLabel = (Label) parentBox.getChildren().get(0);
            String selectedUniversityName = nameLabel.getText();
            System.out.println(selectedUniversityName);
            Stage stage = (Stage) editButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("EditFacultate.fxml")); // Виправлено шлях до FXML
            try {
                loader.load();
                EditFacultate controller = loader.getController(); // Отримуємо контролер іншої форми
                controller.setName(selectedUniversityName); // Передаємо ім'я університету на іншу форму
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            stage.setScene(new Scene(root));

        });
        String yellowBorderColor = "-fx-border-color: yellow; -fx-border-width: 2px;";
        Button deleteButton = new Button();
        deleteButton.setStyle("-fx-background-color: transparent; -fx-border-color: red; -fx-border-width: 2px;");
        Image deleteImage = new Image(getClass().getResourceAsStream("/photo/trash-svgrepo-com.png"));
        ImageView deleteImageView = new ImageView(deleteImage);
        deleteImageView.setFitWidth(30);
        deleteImageView.setFitHeight(30);
        deleteButton.setGraphic(deleteImageView);
        deleteButton.setPrefSize(30, 30);
        deleteButton.setOnAction(event -> deleteFacultate(universityName));

        Label nameLabel = new Label(universityName);
        nameLabel.setPrefWidth(300); // Задаємо бажану ширину для Label
        nameLabel.setWrapText(true); // Дозволяємо тексту врапитися
        nameLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;"); // Задаємо розмір тексту та колір

        HBox universityBox = new HBox();
        universityBox.setStyle(yellowBorderColor);
        universityBox.setSpacing(5);
        universityBox.getChildren().addAll(nameLabel, editButton, deleteButton);
        universityList.getChildren().add(universityBox);
    }

}
