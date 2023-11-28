module com.example.exam_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;


    opens com.example.exam_2 to javafx.fxml;
    exports chat;
}