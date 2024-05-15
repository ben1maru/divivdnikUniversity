package guide.guidetouniversities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditFacultate {
    @FXML
    private TextField nameTxt;
    @FXML
    private TextField idTxt;
    private String name;
    @FXML
    private Button updateButton;
    DB db= new DB();
    @FXML
    void initialize() {

        updateButton.setOnAction(event -> {
            int id = Integer.parseInt(idTxt.getText());
            updateFacultate(id);
            Stage stage = (Stage) updateButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("CRUDFacultate.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            stage.setScene(new Scene(root));
        });

    }
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
        String universitiesQuery = "SELECT `id`, `name` FROM `facultet` where `name`=?";
        try (PreparedStatement preparedStatement = db.getDbConnection().prepareStatement(universitiesQuery)) {
            // Встановлюємо параметр запиту як ім'я університету
            preparedStatement.setString(1, universityName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");

                    idTxt.setText(String.valueOf(id));

                    nameTxt.setText(name);

                } else {
                    // Університета з вказаним ім'ям не знайдено

                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateFacultate(int universId) {
        String updateQuery = "UPDATE `facultet` SET `name` = ? WHERE `id` = ?";

        try (PreparedStatement preparedStatement = db.getDbConnection().prepareStatement(updateQuery)) {
            preparedStatement.setString(1, nameTxt.getText());
            preparedStatement.setInt(2, universId);

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
}
