package stud.a4a.a4aj.labs;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Lab1Tab extends Tab {

    private Canvas canvas;
    private GraphicsContext gc;
    private double[] line1 = {50, 50, 200, 200};  // Линия Брезенхема
    private double[] line2 = {100, 200, 300, 100}; // Линия ЦДА

    public Lab1Tab() {
        super("Лабораторная 1");
        Pane content = new Pane();
        this.setClosable(false);
        setupCanvas();
        setupControls(content);
        setContent(content);
    }

    /// Установка холста
    private void setupCanvas() {
        canvas = new Canvas(400, 300);
        gc = canvas.getGraphicsContext2D();
        drawLines();
        canvas.setLayoutX(10);
        canvas.setLayoutY(10);
    }

    /// Установка ввода координат
    private void setupControls(Pane content) {
        VBox controls = new VBox(5);
        controls.setPrefWidth(150);
        TextField[] fields = new TextField[8];

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
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawLines();
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

    /// Рисовалка (здесь можно добавить линий)
    private void drawLines() {
        drawBresenhamLine((int) line1[0], (int) line1[1], (int) line1[2], (int) line1[3]);
        drawDDALine(line2[0], line2[1], line2[2], line2[3]);
    }

    /// Реализация алгоритма Брезенхема (целочисленный)
    private void drawBresenhamLine(int x1, int y1, int x2, int y2) {
        // дельты есть разность координат
        int dx = Math.abs(x2 - x1); // дельта раз
        int dy = Math.abs(y2 - y1); // дельта два
        int sx = (x1 < x2) ? 1 : -1; // направление движения по оси вправо/влево
        int sy = (y1 < y2) ? 1 : -1; // направление движения вверх/вниз
        int err = dx - dy; // считаем разницу (ошибку)

        // Рисовалка для точек
        while (true) {
            gc.fillRect(x1, y1, 1, 1);

            if (x1 == x2 && y1 == y2) break; // прям попали
            int e2 = 2 * err;
            if (e2 > -dy) { // высоковато
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) { // слишком влево
                err += dx;
                y1 += sy;
            }
        }
    }

    // Реализация метода ЦДА (DDA)
    private void drawDDALine(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1; // дельта раз
        double dy = y2 - y1; // дельта два
        double steps = Math.max(Math.abs(dx), Math.abs(dy)); // берём максимальную разницу из X или Y, столько будет шагов

        // Считаем приращения (насколько надо чего куда двигать, грубо говоря)
        double xInc = dx / steps;
        double yInc = dy / steps;

        double x = x1;
        double y = y1;
        for (int i = 0; i <= steps; i++) {
            gc.fillRect(x, y, 1, 1); // Рисуем точку
            x += xInc;
            y += yInc;
        }
    }
}
