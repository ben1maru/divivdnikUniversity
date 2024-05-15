module guide.guidetouniversities {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.commons.codec;


    opens guide.guidetouniversities to javafx.fxml;
    exports guide.guidetouniversities;
}