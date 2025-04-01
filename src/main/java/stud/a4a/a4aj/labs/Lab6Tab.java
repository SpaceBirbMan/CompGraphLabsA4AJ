package stud.a4a.a4aj.labs;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Lab6Tab extends Tab {

    private static final int GRID_SIZE = 400;
    private static final int PIXEL_SIZE = 5;
    private static final int GRID_CELLS = GRID_SIZE / PIXEL_SIZE;

    private GridPane gridPane;
    private List<List<Rectangle>> pixels;
    private Button fill4Button;
    private Button fill8Button;
    private Button clearButton;
    private Button buildButton;
    private Button finishButton;
    private List<int[]> vertices = new ArrayList<>();
    private Label statusLabel;

    public Lab6Tab() {
        setText("Заполнение многоугольников");

        gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        pixels = new ArrayList<>();

        initializeGrid();

        // Инициализация элементов управления
        fill4Button = new Button("Заливка (4-связная)");
        fill4Button.setOnAction(e -> {
            if (!vertices.isEmpty()) {
                int[] centroid = calculateCentroid();
                floodFill4(centroid[0], centroid[1], Color.RED);
            }
        });
        fill4Button.setDisable(true);

        fill8Button = new Button("Заливка (8-связная)");
        fill8Button.setOnAction(e -> {
            if (!vertices.isEmpty()) {
                int[] centroid = calculateCentroid();
                floodFill8(centroid[0], centroid[1], Color.BLUE);
            }
        });
        fill8Button.setDisable(true);

        clearButton = new Button("Очистить");
        clearButton.setOnAction(e -> clearAll());

        finishButton = new Button("Замкнуть многоугольник");
        finishButton.setOnAction(e -> finishPolygon());
        finishButton.setDisable(true);

        statusLabel = new Label("Кликните на сетке, чтобы добавить вершины многоугольника");

        // Обработка кликов мыши
        gridPane.setOnMouseClicked(event -> {
            int x = (int) (event.getX() / PIXEL_SIZE);
            int y = (int) (event.getY() / PIXEL_SIZE);

            if (x >= 0 && x < GRID_CELLS && y >= 0 && y < GRID_CELLS) {
                addVertex(x, y);
            }
        });

        VBox vbox = new VBox(
                statusLabel,
                new HBox(finishButton, fill4Button, fill8Button, clearButton),
                gridPane
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

    private void addVertex(int x, int y) {
        vertices.add(new int[]{x, y});
        pixels.get(y).get(x).setFill(Color.BLACK);
        statusLabel.setText("Вершин: " + vertices.size() + ". Кликните для добавления или нажмите 'Замкнуть'");
        finishButton.setDisable(vertices.size() < 2);
    }

    private void finishPolygon() {
        if (vertices.size() < 2) return;

        clearGrid();

        // Рисуем многоугольник
        for (int i = 0; i < vertices.size(); i++) {
            int[] current = vertices.get(i);
            int[] next = vertices.get((i + 1) % vertices.size());
            drawLine(current[0], current[1], next[0], next[1]);
        }

        fill4Button.setDisable(false);
        fill8Button.setDisable(false);
        statusLabel.setText("Многоугольник замкнут. Вершин: " + vertices.size());
    }

    private int[] calculateCentroid() {
        if (vertices.isEmpty()) return new int[]{0, 0};

        double sumX = 0, sumY = 0;
        for (int[] vertex : vertices) {
            sumX += vertex[0];
            sumY += vertex[1];
        }
        return new int[]{(int)(sumX / vertices.size()), (int)(sumY / vertices.size())};
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

                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(10 * stack.size()), e -> {}));
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

                timeline.getKeyFrames().add(new KeyFrame(Duration.millis(10 * stack.size()), e -> {}));
            }
        }

        timeline.play();
    }

    private void clearGrid() {
        for (int i = 0; i < GRID_CELLS; i++) {
            for (int j = 0; j < GRID_CELLS; j++) {
                if (pixels.get(i).get(j).getFill() != Color.BLACK) {
                    pixels.get(i).get(j).setFill(Color.WHITE);
                }
            }
        }
    }

    private void clearAll() {
        vertices.clear();
        for (int i = 0; i < GRID_CELLS; i++) {
            for (int j = 0; j < GRID_CELLS; j++) {
                pixels.get(i).get(j).setFill(Color.WHITE);
            }
        }
        fill4Button.setDisable(true);
        fill8Button.setDisable(true);
        finishButton.setDisable(true);
        statusLabel.setText("Кликните на сетке, чтобы добавить вершины многоугольника");
    }
}