package stud.a4a.a4aj;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import stud.a4a.a4aj.labs.Lab1Tab;

public class HelloController {

    @FXML public Pane workspace;
    private TabPane labs;

    @FXML public void initialize() {
        labs = new TabPane();
        workspace.getChildren().add(labs);
        addLabTabs();
    }

    private void addLabTabs() {
        labs.getTabs().add(new Lab1Tab());
    }
}
