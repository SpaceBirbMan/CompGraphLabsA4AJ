package stud.a4a.a4aj;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

public class Visuals {
    /// Окраска границ сетки, визуальный сахар
    public static void drawGrid(GridPane canvas) {
        for (int i = 0; i < 35; i++) {
            for (int j = 0; j < 30; j++) {
                Pane cell = new Pane();
                cell.setStyle("-fx-border-color: black; -fx-border-width: 0.5;");
                cell.setMinSize(10, 10);
                cell.setMaxSize(10, 10);
                canvas.add(cell, i, j);
            }
        }
    }

    /// Настройка пиксельного холста, размер фиксирован (30х35)
    public static GridPane setupCanvas() {
        GridPane canvas = new GridPane();

        for (int i = 0; i < 35; i++) {
            ColumnConstraints cc = new ColumnConstraints(10); // Фиксируем размер ячеек
            canvas.getColumnConstraints().add(cc);
        }
        for (int j = 0; j < 30; j++) {
            RowConstraints rc = new RowConstraints(10); // Фиксируем размер ячеек
            canvas.getRowConstraints().add(rc);
        }

        canvas.setLayoutX(10);
        canvas.setLayoutY(10);

        return canvas;
    }

    /// Вставляет цветной квадрат в клетку
    public static Pane drawPixel(int x, int y, Color color, GridPane canvas) {
        Pane pixel = new Pane();
        pixel.setStyle("-fx-background-color: " + toRgbString(color) + ";");
        pixel.setMinSize(10, 10);
        pixel.setMaxSize(10, 10);

        // Очистка клетки перед покраской, так как это не покраска, а наслоение объекта
        canvas.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y);
        canvas.add(pixel, x, y);
        return pixel;
    }

    public static String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
}
