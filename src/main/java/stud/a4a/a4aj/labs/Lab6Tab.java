package stud.a4a.a4aj.labs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Lab6Tab extends Tab {

    private static final int GRID_SIZE = 200;
    private static final int PIXEL_SIZE = 5;
    private static final int GRID_CELLS = GRID_SIZE / PIXEL_SIZE;

    private GridPane gridPane;
    private List<List<Rectangle>> pixels;
    private Button fill4Button;
    private Button fill8Button;
    private Button clearButton;
    private Button buildButton;
    private TextField x1Field, y1Field, x2Field, y2Field, x3Field, y3Field, x4Field, y4Field;
    private TextField fillXField, fillYField;

    public Lab6Tab() {
        setText("Неквадрат");

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
        x4Field = new TextField();
        y4Field = new TextField();

        fillXField = new TextField();
        fillYField = new TextField();

        buildButton = new Button("Build Quadrilateral");
        buildButton.setOnAction(e -> buildQuadrilateral());

        fill4Button = new Button("Fill (4-connected)");
        fill4Button.setOnAction(e -> {
            int x = Integer.parseInt(fillXField.getText()) / PIXEL_SIZE;
            int y = Integer.parseInt(fillYField.getText()) / PIXEL_SIZE;
            floodFill4(x, y, Color.RED);
        });
        fill4Button.setDisable(true);

        fill8Button = new Button("Fill (8-connected)");
        fill8Button.setOnAction(e -> {
            int x = Integer.parseInt(fillXField.getText()) / PIXEL_SIZE;
            int y = Integer.parseInt(fillYField.getText()) / PIXEL_SIZE;
            floodFill8(x, y, Color.BLUE);
        });
        fill8Button.setDisable(true);

        clearButton = new Button("Clear");
        clearButton.setOnAction(e -> clearGrid());

        VBox vbox = new VBox(
                new Label("Vertex 1 (x, y):"), new VBox(x1Field, y1Field),
                new Label("Vertex 2 (x, y):"), new VBox(x2Field, y2Field),
                new Label("Vertex 3 (x, y):"), new VBox(x3Field, y3Field),
                new Label("Vertex 4 (x, y):"), new VBox(x4Field, y4Field),
                buildButton,
                new Label("Fill Point (x, y):"), new VBox(fillXField, fillYField),
                fill4Button, fill8Button, clearButton, gridPane
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

    private void buildQuadrilateral() {
        clearGrid();

        int x1 = Integer.parseInt(x1Field.getText()) / PIXEL_SIZE;
        int y1 = Integer.parseInt(y1Field.getText()) / PIXEL_SIZE;
        int x2 = Integer.parseInt(x2Field.getText()) / PIXEL_SIZE;
        int y2 = Integer.parseInt(y2Field.getText()) / PIXEL_SIZE;
        int x3 = Integer.parseInt(x3Field.getText()) / PIXEL_SIZE;
        int y3 = Integer.parseInt(y3Field.getText()) / PIXEL_SIZE;
        int x4 = Integer.parseInt(x4Field.getText()) / PIXEL_SIZE;
        int y4 = Integer.parseInt(y4Field.getText()) / PIXEL_SIZE;

        drawLine(x1, y1, x2, y2);
        drawLine(x2, y2, x3, y3);
        drawLine(x3, y3, x4, y4);
        drawLine(x4, y4, x1, y1);

        fill4Button.setDisable(false);
        fill8Button.setDisable(false);
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

    private void floodFill4(int startX, int startY, Color fillColor) {
        if (startX < 0 || startX >= GRID_CELLS || startY < 0 || startY >= GRID_CELLS) {
            return;
        }

        Color targetColor = (Color) pixels.get(startY).get(startX).getFill();
        if (targetColor.equals(fillColor)) {
            return;
        }

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});

        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);

        while (!stack.isEmpty()) {
            int[] point = stack.pop();
            int x = point[0];
            int y = point[1];

            if (x >= 0 && x < GRID_CELLS && y >= 0 && y < GRID_CELLS && pixels.get(y).get(x).getFill().equals(targetColor)) {
                pixels.get(y).get(x).setFill(fillColor);

                stack.push(new int[]{x + 1, y});
                stack.push(new int[]{x - 1, y});
                stack.push(new int[]{x, y + 1});
                stack.push(new int[]{x, y - 1});

                // Добавляем задержку для анимации
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(1000 * stack.size()), e -> {
                    // Обновляем UI
                }));
            }
        }

        timeline.play();
    }

    private void floodFill8(int startX, int startY, Color fillColor) {
        if (startX < 0 || startX >= GRID_CELLS || startY < 0 || startY >= GRID_CELLS) {
            return;
        }

        Color targetColor = (Color) pixels.get(startY).get(startX).getFill();
        if (targetColor.equals(fillColor)) {
            return;
        }

        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{startX, startY});

        Timeline timeline = new Timeline();
        timeline.setCycleCount(1);

        while (!stack.isEmpty()) {
            int[] point = stack.pop();
            int x = point[0];
            int y = point[1];

            if (x >= 0 && x < GRID_CELLS && y >= 0 && y < GRID_CELLS && pixels.get(y).get(x).getFill().equals(targetColor)) {
                pixels.get(y).get(x).setFill(fillColor);

                stack.push(new int[]{x + 1, y});
                stack.push(new int[]{x - 1, y});
                stack.push(new int[]{x, y + 1});
                stack.push(new int[]{x, y - 1});
                stack.push(new int[]{x + 1, y + 1});
                stack.push(new int[]{x - 1, y - 1});
                stack.push(new int[]{x + 1, y - 1});
                stack.push(new int[]{x - 1, y + 1});

                // Добавляем задержку для анимации
                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(10 * stack.size()), e -> {
                    // Обновляем UI
                }));
            }
        }

        timeline.play();
    }

    private void clearGrid() {
        for (int i = 0; i < GRID_CELLS; i++) {
            for (int j = 0; j < GRID_CELLS; j++) {
                pixels.get(i).get(j).setFill(Color.WHITE);
            }
        }
    }
}