module stud.a4a.a4aj {
    requires javafx.controls;
    requires javafx.fxml;


    opens stud.a4a.a4aj to javafx.fxml;
    exports stud.a4a.a4aj;
}