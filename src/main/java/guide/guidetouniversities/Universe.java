package guide.guidetouniversities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.*;

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import static javafx.scene.text.TextAlignment.CENTER;

public class Universe {

    @FXML
    private FlowPane cardss;
    @FXML
    private Button nextButton;
    @FXML
    private Button isBackBtn;
    @FXML
    private Button previousButton;
       @FXML
    private ChoiceBox<String> regionChioceBox;
    @FXML
    private TextField serchTxt;
    DB db = new DB();
    @FXML

    void initialize() {
        addCategories();
        regionChioceBox.setValue("Всі");
        generateUniversityCards(regionChioceBox.getValue(), 0,serchTxt.getText());

        regionChioceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            generateUniversityCards(newValue, 0,serchTxt.getText());
        });

        serchTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            searchUniversities(newValue);
        });
        isBackBtn.setOnAction(event-> {
            Stage stage = (Stage) isBackBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainViews.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            stage.setScene(new Scene(root));
        });
    }

    private HBox createNewHBox() {
        HBox hBox = new HBox();
        hBox.setSpacing(10); // Задаємо відстань між карточками
        hBox.setPadding(new Insets(10));
        return hBox;
    }
    private void searchUniversities(String searchText) {
        generateUniversityCards(regionChioceBox.getValue(), 0, searchText);
    }

    public void addCategories() {
        regionChioceBox.getItems().add("Всі");
        try {
            PreparedStatement statement = db.getDbConnection().prepareStatement("SELECT id,name FROM region");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                regionChioceBox.getItems().add(set.getString("name"));
            }
        }
        catch (Exception ignored) { }
    }
    private void generateUniversityCards(String selectedRegionName, int startIndex, String searchText) {
        double rightPosition = 0;
        System.out.println("Selected region: " + selectedRegionName);
        try {
            PreparedStatement statement;
            if ("Всі".equals(selectedRegionName)) {
                statement = db.getDbConnection().prepareStatement("SELECT * FROM universe WHERE name LIKE ? LIMIT ?, 8");
                statement.setString(1, "%" + searchText + "%");
                statement.setInt(2, startIndex);
            } else {
                int regionId = getRegionId(selectedRegionName);
                statement = db.getDbConnection().prepareStatement("SELECT * FROM universe WHERE id_region = ? AND name LIKE ? LIMIT ?, 8");
                statement.setInt(1, regionId);
                statement.setString(2, "%" + searchText + "%");
                statement.setInt(3, startIndex);
            }

            ResultSet resultSet = statement.executeQuery();
            cardss.getChildren().clear(); // Очищення вмісту FlowPane

            HBox currentHBox = createNewHBox(); // Створення першого HBox
            int universitiesPerPage = 4; // Кількість університетів на сторінці
            int universityCount = 0; // Лічильник університетів на поточній сторінці

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String photoUrl = resultSet.getString("photo");
                System.out.println(name + " " + " " + photoUrl);

                AnchorPane universityCard = new AnchorPane();
                universityCard.setPrefWidth(200);
                universityCard.setPrefHeight(280);
                universityCard.setStyle("-fx-background-color: #003366; -fx-border-color: yellow; -fx-border-image-width: 9;");

                Image image = new Image(photoUrl);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                AnchorPane.setTopAnchor(imageView, 10.0);
                AnchorPane.setLeftAnchor(imageView, 25.0);

                Label nameLabel = new Label(name);
                nameLabel.setWrapText(true);
                nameLabel.setPrefWidth(150);
                nameLabel.setMaxWidth(150);
                nameLabel.setAlignment(Pos.CENTER);
                nameLabel.setTextAlignment(CENTER);
                nameLabel.setStyle("-fx-text-fill:white");
                AnchorPane.setTopAnchor(nameLabel, 170.0);
                AnchorPane.setLeftAnchor(nameLabel, 25.0);

                universityCard.getChildren().addAll(imageView, nameLabel);

                universityCard.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
                        String universityName = nameLabel.getText(); // Отримуємо назву університету
                        Stage stage = (Stage) cardss.getScene().getWindow();
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("UniversityDetails.fxml"));
                        try {
                            loader.load();
                            UniversityDetailsController controller = loader.getController(); // Отримуємо контролер іншої форми
                            controller.setName(universityName); // Передаємо ім'я університету на іншу форму
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Parent root = loader.getRoot();
                        stage.setScene(new Scene(root));
                    }
                });
                currentHBox.getChildren().add(universityCard);
                universityCount++;

                if (universityCount >= universitiesPerPage) {
                    cardss.getChildren().add(currentHBox); // Додаємо поточний HBox до FlowPane

                    currentHBox = createNewHBox(); // Створюємо новий HBox
                    rightPosition = 0; // Починаємо новий рядок
                    universityCount = 0; // Скидаємо лічильник університетів на сторінці
                }
            }

            // Якщо залишилися університети, які не вміщуються на сторінці, додаємо останній HBox
            if (!currentHBox.getChildren().isEmpty()) {
                cardss.getChildren().add(currentHBox);
            }

            // Додамо кнопки пагінації
            addPaginationButtons();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean hasNextPage(String selectedRegionName, int startIndex) {
        try {
            PreparedStatement statement;
            if ("Всі".equals(selectedRegionName)) {
                statement = db.getDbConnection().prepareStatement("SELECT COUNT(*) FROM universe LIMIT ?, 8");
                statement.setInt(1, startIndex + 8);
            } else {
                int regionId = getRegionId(selectedRegionName);
                statement = db.getDbConnection().prepareStatement("SELECT COUNT(*) FROM universe WHERE id_region = ? LIMIT ?, 8");
                statement.setInt(1, regionId);
                statement.setInt(2, startIndex + 8);
            }
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addPaginationButtons() {
        previousButton.setOnAction(event -> previousPage());
        if (hasNextPage(regionChioceBox.getValue(), currentPage * 2 * 4)) {
            nextButton.setDisable(false);
            previousButton.setDisable(false);
            nextButton.setOnAction(event -> nextPage());
        } else {
            nextButton.setDisable(true);
            previousButton.setDisable(true);
        }
    }


    private int currentPage = 0;

    public void nextPage() {
        currentPage++;
        showPage(currentPage);
    }

    public void previousPage() {
        currentPage--;
        showPage(currentPage);
    }

    private void showPage(int page) {
        // Видалення всіх елементів з FlowPane
        cardss.getChildren().clear();

        // Отримання індексу початкового університету для поточної сторінки
        int startIndex = page * 2 * 4; // 2 - кількість рядків у FlowPane, 4 - кількість карточок у VBox на сторінці

        // Генерування карточок для поточної сторінки
        generateUniversityCards(regionChioceBox.getValue(), startIndex,serchTxt.getText());
    }


    // Метод для отримання ідентифікатора регіону за його назвою
    private int getRegionId(String regionName) {
        try {
            PreparedStatement statement = db.getDbConnection().prepareStatement("SELECT id FROM region WHERE name = ?");
            statement.setString(1, regionName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // Якщо не вдалося знайти ідентифікатор регіону, повертаємо -1
        return -1;
    }
}
