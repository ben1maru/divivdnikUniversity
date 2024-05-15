package guide.guidetouniversities;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditUniverse {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField nameTxt;
    @FXML
    private TextField idTxt;
    @FXML
    private TextField photoTxt;

    @FXML
    private TextField ratingTxt;

    @FXML
    private ChoiceBox<String> regionChoiceBox;

    @FXML
    private Button updateButton;

    @FXML
    private TextField yearCreateTxt;
    DB db =new DB();
    private String name;

    public void setName(String name) {
        this.name = name;
        if (nameTxt != null) {
            nameTxt.setText(name);
            System.out.println(nameTxt.getText());
        }
        // Викликаємо метод fetchDataFromDatabase після встановлення імені
        fetchDataFromDatabase(name);
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
                    //fetchFacultiesFromDatabase(id);
                    // Отримання назви регіону за його ідентифікатором
                    idTxt.setText(String.valueOf(id));
                    String regionName = getRegionName(regionId);
                    descriptionArea.setText(description);
                    nameTxt.setText(name);
                    photoTxt.setText(photo);
                    yearCreateTxt.setText(yearCreated);
                    regionChoiceBox.setValue(regionName);
                    ratingTxt.setText(rating);

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
    public void updateUniversity(int universId) {
        String updateQuery = "UPDATE `universe` SET `name` = ?, `photo` = ?, `description` = ?, `yearcreate` = ?, `id_region` = ?, `rating` = ? WHERE `id` = ?";

        try (PreparedStatement preparedStatement = db.getDbConnection().prepareStatement(updateQuery)) {
            preparedStatement.setString(1, nameTxt.getText());
            preparedStatement.setString(2, photoTxt.getText());
            preparedStatement.setString(3, descriptionArea.getText());
            preparedStatement.setString(4, yearCreateTxt.getText());
            String regionName = regionChoiceBox.getValue(); // Get the selected region name
            int regionId = getRegionId(regionName); // Get the region ID based on the name
            preparedStatement.setInt(5, regionId);
            preparedStatement.setString(6, ratingTxt.getText());
            preparedStatement.setInt(7, universId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Успіх");
                alert.setContentText("Дані було успішно оновлено");
                alert.show();
                System.out.println("University updated successfully.");
            } else {
                System.out.println("Failed to update university.");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private int getRegionId(String regionName) {
        String regionQuery = "SELECT `id` FROM `region` WHERE `name` = ?";
        try (PreparedStatement preparedStatement = db.getDbConnection().prepareStatement(regionQuery)) {
            preparedStatement.setString(1, regionName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // Return a default value if the region ID is not found
        return 0;
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
    public void addCategories() {

        try {
            PreparedStatement statement = db.getDbConnection().prepareStatement("SELECT id,name FROM region");
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                regionChoiceBox.getItems().add(set.getString("name"));
            }
        }
        catch (Exception ignored) { }
    }
    @FXML
    void initialize() {
addCategories();
updateButton.setOnAction(event -> {
    int id = Integer.parseInt(idTxt.getText());
    updateUniversity(id);
    Stage stage = (Stage) updateButton.getScene().getWindow();
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(getClass().getResource("CRUDUniverse.fxml"));
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
