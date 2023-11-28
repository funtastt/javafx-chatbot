module com.example.exam_2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.exam_2 to javafx.fxml;
    exports com.example.exam_2;
    exports chat;
}