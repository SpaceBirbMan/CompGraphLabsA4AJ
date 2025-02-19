package stud.a4a.a4aj.labs;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class Lab1Tab extends Tab {

    private GridPane canvas;
    private double[] line1 = {1, 1, 30, 20};  // Линия Брезенхема
    private double[] line2 = {1, 5, 30, 24}; // Линия ЦДА
    private Pane content = new Pane();
    private Pane contentU = new Pane();

    public Lab1Tab() {
        super("Полоски");

        this.setClosable(false);
        setupCanvas();
        setupControls(content);
        setContent(content);
        canvas.setGridLinesVisible(true);
        content.getChildren().add(contentU);
        contentU.toFront();
    }

    /// Установка ввода координат
    private void setupControls(Pane content) {
        VBox controls = new VBox(5);
        controls.setPrefWidth(150);
        TextField[] fields = new TextField[8]; // Поля для ввода координат двух линий

        for (int i = 0; i < fields.length; i++) {
            fields[i] = new TextField(String.valueOf(i < 4 ? line1[i] : line2[i - 4]));
            int index = i;
            fields[i].setOnAction(e -> {
                try {
                    double newValue = Double.parseDouble(fields[index].getText());
                    if (index < 4) {
                        line1[index] = newValue;
                    } else {
                        line2[index - 4] = newValue;
                    }
                    drawLines();
                    canvas.setGridLinesVisible(true);
                } catch (NumberFormatException ignored) {}
            });
        }

        controls.getChildren().addAll(
                new Label("Линия 1 (Брезенхем): X1 Y1 X2 Y2"),
                fields[0], fields[1], fields[2], fields[3],
                new Label("Линия 2 (ЦДА): X1 Y1 X2 Y2"),
                fields[4], fields[5], fields[6], fields[7]
        );

        ScrollPane scrollBox = new ScrollPane(controls);
        scrollBox.setFitToWidth(true);
        scrollBox.setPrefHeight(300);
        scrollBox.setLayoutX(420);
        scrollBox.setLayoutY(10);

        content.getChildren().addAll(canvas, scrollBox);
    }

    /// Окраска границ сетки, визуальный сахар
    private void drawGrid() {
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

    /// Рисование линий
    private void drawLines() {
        canvas.getChildren().clear(); // Очистка перед отрисовкой
        drawGrid(); // Восстановление сетки

        drawBresenhamLine((int) line1[0], (int) line1[1], (int) line1[2], (int) line1[3], Color.RED);
        drawDDALine(line2[0], line2[1], line2[2], line2[3], Color.GREEN);
    }

    /// Настройка "холста"
    private void setupCanvas() {
        canvas = new GridPane();

        for (int i = 0; i < 35; i++) {
            ColumnConstraints cc = new ColumnConstraints(10); // Фиксируем размер ячеек
            canvas.getColumnConstraints().add(cc);
        }
        for (int j = 0; j < 30; j++) {
            RowConstraints rc = new RowConstraints(10); // Фиксируем размер ячеек
            canvas.getRowConstraints().add(rc);
        }

        drawLines();
        canvas.setLayoutX(10);
        canvas.setLayoutY(10);
    }

    /// Вставляет цветной квадрат в клетку
    private Pane drawPixel(int x, int y, Color color) {
        Pane pixel = new Pane();
        pixel.setStyle("-fx-background-color: " + toRgbString(color) + ";");
        pixel.setMinSize(10, 10);
        pixel.setMaxSize(10, 10);

        // Очистка клетки перед покраской, так как это не покраска, а наслоение объекта
        canvas.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y);
        canvas.add(pixel, x, y);
        return pixel;
    }


    /// Отрисовка линии через алгоритм Брезенхема
    private void drawBresenhamLine(int x1, int y1, int x2, int y2, Color color) {
        // Дельты (разности)
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        // Смещение
        int sx = (x1 < x2) ? 1 : -1; // правее либо левее соответственно
        int sy = (y1 < y2) ? 1 : -1; // ниже либо выше
        // На сколько ошиблись
        int err = dx - dy;

        while (true) {
            // Рисуем пиксель на текущих координатах (x1, y1) с заданным цветом
            var pixel = drawPixel(x1, y1, color);

            pixel.toBack();

            // Проверяем, достигли ли мы конечной точки (x2, y2)
            if (x1 == x2 && y1 == y2) break;

            // Вычисляем удвоенную ошибку
            int e2 = 2 * err;

            // Если ошибка больше, чем -dy, то корректируем ошибку и увеличиваем x1
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;  // sx - это направление движения по оси X (1 или -1)
            }

            // Если ошибка меньше, чем dx, то корректируем ошибку и увеличиваем y1
            if (e2 < dx) {
                err += dx;
                y1 += sy;  // sy - это направление движения по оси Y (1 или -1)
            }
        }

    }

    /// Отрисовка линии методом ЦДА
    private void drawDDALine(double x1, double y1, double x2, double y2, Color color) {
        // Дельты
        double dx = x2 - x1;
        double dy = y2 - y1;
        double steps = Math.max(Math.abs(dx), Math.abs(dy)); // Большая из двух = количество шагов

        // Инкременты
        double xInc = dx / steps;
        double yInc = dy / steps;

        // Старт
        double x = x1;
        double y = y1;
        for (int i = 0; i <= steps; i++) {
            var pixel = drawPixel((int) Math.round(x), (int) Math.round(y), color); // Просто округляем в большую сторону получившееся число
                                                                                    // для получения нормальной координаты для установки
            pixel.toBack();
            x += xInc;
            y += yInc;
        }
    }

    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

}