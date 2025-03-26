package stud.a4a.a4aj;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import stud.a4a.a4aj.labs.*;

public class HelloController {

    @FXML public Pane workspace;
    private TabPane labs;

    @FXML public void initialize() {
        labs = new TabPane();
        workspace.getChildren().add(labs);
        addLabTabs();
    }

    private void addLabTabs() {
        labs.getTabs().addAll(
                new Lab1Tab(),
                new Lab2Tab(),
                new Lab3Tab(),
                new Lab4Tab(),
                new Lab5Tab(),
                new Lab6Tab(),
                new Lab7Tab(),
                new LabRGBTab()
        );
    }
}
