package guide.guidetouniversities;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Autentification {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button btnLogIn;

    @FXML
    private Button btnSgnIn;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    void initialize() {
        btnLogIn.setOnAction(event -> {
            String loginText = txtEmail.getText().trim();
            String loginPassword = txtPassword.getText().trim();

            if (!loginText.equals("") && !loginPassword.equals(""))
                loginUser(loginText, loginPassword);
            else
                System.out.println("login is empty ");
        });

        btnSgnIn.setOnAction(event -> {
            Stage stage = (Stage) btnSgnIn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Registration.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            stage.setScene(new Scene(root));
        });
    }
    /**
     * Авторизація користувача
     * @param loginText
     * @param passwordTxt
     */
    public void loginUser(String loginText, String passwordTxt) {
       Autorize autorize = new Autorize();
        User userFromDatabase = autorize.getUser(loginText, passwordTxt);
        if (userFromDatabase != null) {
            Const.user = userFromDatabase;
            System.out.println(Const.user.getIsAdmin());
            Stage stage = (Stage) btnLogIn.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainViews.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            stage.setScene(new Scene(root));
            System.out.println("login");
        }
    }

}
