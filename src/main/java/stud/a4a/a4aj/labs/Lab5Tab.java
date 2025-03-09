package stud.a4a.a4aj.labs;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Lab5Tab extends Tab {

    private static final int GRID_SIZE = 200;
    private static final int PIXEL_SIZE = 5;
    private static final int GRID_CELLS = GRID_SIZE / PIXEL_SIZE;

    private GridPane gridPane;
    private List<List<Rectangle>> pixels;
    private Button fillButton;
    private Button clearButton;
    private Button buildButton;
    private TextField x1Field, y1Field, x2Field, y2Field, x3Field, y3Field;

    public Lab5Tab() {
        setText("Нарисуй значок ютуба");

        gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        pixels = new ArrayList<>();

        initializeGrid();

        x1Field = new TextField();
        y1Field = new TextField();
        x2Field = new TextField();
        y2Field = new TextField();
        x3Field = new TextField();
        y3Field = new TextField();

        buildButton = new Button("Build Triangle");
        buildButton.setOnAction(e -> buildTriangle());

        fillButton = new Button("Fill");
        fillButton.setOnAction(e -> fillTriangle());
        fillButton.setDisable(true); // Initially disabled

        clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearGrid());

        VBox vbox = new VBox(
                new Label("Vertex 1 (x, y):"), new VBox(x1Field, y1Field),
                new Label("Vertex 2 (x, y):"), new VBox(x2Field, y2Field),
                new Label("Vertex 3 (x, y):"), new VBox(x3Field, y3Field),
                buildButton, gridPane, fillButton, clearButton
        );
        setContent(vbox);
    }

    private void initializeGrid() {
        for (int i = 0; i < GRID_CELLS; i++) {
            List<Rectangle> row = new ArrayList<>();
            for (int j = 0; j < GRID_CELLS; j++) {
                Rectangle pixel = new Rectangle(PIXEL_SIZE, PIXEL_SIZE, Color.WHITE);
                pixel.setStroke(Color.LIGHTGRAY);
                gridPane.add(pixel, j, i);
                row.add(pixel);
            }
            pixels.add(row);
        }
    }

    private void buildTriangle() {
        clearGrid();

        int x1 = Integer.parseInt(x1Field.getText()) / PIXEL_SIZE;
        int y1 = Integer.parseInt(y1Field.getText()) / PIXEL_SIZE;
        int x2 = Integer.parseInt(x2Field.getText()) / PIXEL_SIZE;
        int y2 = Integer.parseInt(y2Field.getText()) / PIXEL_SIZE;
        int x3 = Integer.parseInt(x3Field.getText()) / PIXEL_SIZE;
        int y3 = Integer.parseInt(y3Field.getText()) / PIXEL_SIZE;

        drawLine(x1, y1, x2, y2);
        drawLine(x2, y2, x3, y3);
        drawLine(x3, y3, x1, y1);

        fillButton.setDisable(false); // Enable fill button after triangle is built
    }

    private void drawLine(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x1 >= 0 && x1 < GRID_CELLS && y1 >= 0 && y1 < GRID_CELLS) {
                pixels.get(y1).get(x1).setFill(Color.BLACK);
            }

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private void fillTriangle() {
        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            private int y = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 10_000_000) { // 10ms delay
                    if (y < GRID_CELLS) {
                        fillScanLine(y);
                        y++;
                        lastUpdate = now;
                    } else {
                        this.stop();
                    }
                }
            }
        };
        timer.start();
    }

    private void fillScanLine(int y) {
        List<Integer> intersections = new ArrayList<>();

        // Находим все пересечения с границами треугольника на текущей строке
        for (int x = 0; x < GRID_CELLS; x++) {
            if (pixels.get(y).get(x).getFill() == Color.BLACK) {
                intersections.add(x);
            }
        }

        // Заливаем между парами пересечений
        for (int i = 0; i < intersections.size(); i += 2) {
            if (i + 1 >= intersections.size()) {
                break; // Если нечётное количество пересечений, игнорируем последнее
            }
            int startX = intersections.get(i);
            int endX = intersections.get(i + 1);

            for (int x = startX + 1; x < endX; x++) {
                if (pixels.get(y).get(x).getFill() != Color.BLACK) {
                    pixels.get(y).get(x).setFill(Color.RED);
                }
            }
        }
    }
    private void clearGrid() {
        for (int i = 0; i < GRID_CELLS; i++) {
            for (int j = 0; j < GRID_CELLS; j++) {
                pixels.get(i).get(j).setFill(Color.WHITE);
            }
        }
    }
}