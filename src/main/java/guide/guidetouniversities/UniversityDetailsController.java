package guide.guidetouniversities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniversityDetailsController {
    @FXML
    private ListView facultetList;
    @FXML
    private Button isBackBtn;
    @FXML
    private Text descritionUniversity;

    @FXML
    private ImageView imageUniversity;

    @FXML
    private Label locationRegion;

    @FXML
    private Label nameLabel;

    @FXML
    private Label ratingUniversity;

    @FXML
    private Label yearUniversity;

    private String name; // Поле для зберігання імені університету

    // Метод для встановлення імені університету та оновлення Label
    public void setName(String name) {
        this.name = name;
        if (nameLabel != null) {
            nameLabel.setText(name);
        }
        // Викликаємо метод fetchDataFromDatabase після встановлення імені
        fetchDataFromDatabase(name);
    }

    DB db = new DB();
    public void fetchFacultiesFromDatabase(int universityId) {
        // Отримання ідентифікаторів факультетів для даного університету
        String universityFacultiesQuery = "SELECT `id_fakulteta` FROM `universefacultate` WHERE `id_universe` = ?";
        try (PreparedStatement preparedStatement = db.getDbConnection().prepareStatement(universityFacultiesQuery)) {
            preparedStatement.setInt(1, universityId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<Integer> facultyIds = new ArrayList<>();
                while (resultSet.next()) {
                    int facultyId = resultSet.getInt("id_fakulteta");
                    facultyIds.add(facultyId);
                }
                if (!facultyIds.isEmpty()) { // Перевірка, чи список не є порожнім
                    // Отримання даних про факультети за їх ідентифікаторами
                    String placeholders = String.join(",", Collections.nCopies(facultyIds.size(), "?"));
                    String facultiesQuery = "SELECT name FROM `facultet` WHERE `id` IN (" + placeholders + ")";
                    try (PreparedStatement facultiesStatement = db.getDbConnection().prepareStatement(facultiesQuery)) {
                        int i = 1;
                        for (Integer id : facultyIds) {
                            facultiesStatement.setInt(i++, id);
                        }
                        try (ResultSet facultiesResultSet = facultiesStatement.executeQuery()) {
                            List<String> facultyNames = new ArrayList<>();
                            while (facultiesResultSet.next()) {
                                String facultyName = facultiesResultSet.getString("name");
                                facultyNames.add(facultyName);
                            }
                            // Оновлення ListView зі списком факультетів
                            facultetList.getItems().addAll(facultyNames);
                        }
                    }
                } else {
                    // Якщо список факультетів порожній, виведіть повідомлення про це
                    System.out.println("No faculties found for university with ID: " + universityId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }



    public void fetchDataFromDatabase(String universityName) {
        // Отримання даних про університети
        String universitiesQuery = "SELECT `id`, `name`, `photo`, `description`, `yearcreate`, `id_region`, `rating` FROM `universe` where `name`=?";
        try (PreparedStatement preparedStatement = db.getDbConnection().prepareStatement(universitiesQuery)) {
            // Встановлюємо параметр запиту як ім'я університету
            preparedStatement.setString(1, universityName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String photo = resultSet.getString("photo");
                    String description = resultSet.getString("description");
                    String yearCreated = resultSet.getString("yearcreate");
                    int regionId = resultSet.getInt("id_region");
                    String rating = resultSet.getString("rating");
                    fetchFacultiesFromDatabase(id);
                    // Отримання назви регіону за його ідентифікатором
                    String regionName = getRegionName(regionId);
                    Image image = new Image(photo);
                    imageUniversity.setImage(image);
                    ratingUniversity.setText("Рейтинг університету: "+rating);
                    nameLabel.setText(name);
                    descritionUniversity.setText(description);
                    yearUniversity.setText("Рік заснування: "+yearCreated);
                    locationRegion.setText("Розташування університету " + regionName);
                } else {
                    // Університета з вказаним ім'ям не знайдено
                    System.out.println("University with name '" + universityName + "' not found.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String getRegionName(int regionId) throws SQLException {
        String regionQuery = "SELECT `name` FROM `region` WHERE `id` = ?";
        try (PreparedStatement preparedStatement = db.getDbConnection().prepareStatement(regionQuery)) {
            preparedStatement.setInt(1, regionId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");

                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // Метод, який викликається при ініціалізації контролера
    @FXML
    public void initialize() {
        isBackBtn.setOnAction(event-> {
            Stage stage = (Stage) isBackBtn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Universe.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            stage.setScene(new Scene(root));
        });

    }
}

