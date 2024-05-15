package guide.guidetouniversities;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddFacultate {
    @FXML
    private TextField nameTxt;
    @FXML
    private Button addButton;
    DB db= new DB();

    @FXML
    void initialize(){
addButton.setOnAction(event -> {
    String facultate = nameTxt.getText();
    insertFacultet(facultate);
});
    }

    private void insertFacultet(String facultetName) {
        // SQL запит для вставки нового факультету у базу даних
        String insertQuery = "INSERT INTO `facultet` (`name`) VALUES (?)";

        try (PreparedStatement preparedStatement = db.getDbConnection().prepareStatement(insertQuery)) {
            // Встановлюємо ім'я факультету як параметр у підготовленому запиті
            preparedStatement.setString(1, facultetName);

            // Виконуємо запит на вставку
            int affectedRows = preparedStatement.executeUpdate();

            // Перевіряємо, чи були змінені які-небудь рядки
            if (affectedRows > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Успіх");
                alert.setContentText("Дані було успішно додано");
                alert.show();
                System.out.println("Факультет '" + facultetName + "' успішно додано.");
            } else {
                System.out.println("Не вдалося додати факультет '" + facultetName + "'.");
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Помилка");
                alert.setContentText("Щось пішло не так");
                alert.show();
            }


        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
