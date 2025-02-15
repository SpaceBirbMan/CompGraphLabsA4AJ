package stud.a4a.a4aj.labs;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;

public class Lab1Tab extends Tab {

    private GridPane canvas;
    private double[] line1 = {1, 1, 20, 20};  // Линия Брезенхема
    private double[] line2 = {15, 1, 12, 28}; // Линия ЦДА
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
        lineB.toFront();
        lineD.toFront();
    }


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

    private Pane drawPixel(int x, int y, Color color) {
        Pane pixel = new Pane();
        pixel.setStyle("-fx-background-color: " + toRgbString(color) + ";");
        pixel.setMinSize(10, 10);
        pixel.setMaxSize(10, 10);

        // Удаляем существующий элемент, если он есть
        canvas.getChildren().removeIf(node -> GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y);

        // Добавляем новый пиксель
        canvas.add(pixel, x, y);
        return pixel;
    }


    /// Реализация алгоритма Брезенхема (пиксельная версия)
    private void drawBresenhamLine(int x1, int y1, int x2, int y2, Color color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = (x1 < x2) ? 1 : -1;
        int sy = (y1 < y2) ? 1 : -1;
        int err = dx - dy;

        while (true) {
            var pixel = drawPixel(x1, y1, color);
            pixel.toBack();
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

        double startX = line1[0] * 10 + canvas.getLayoutX() + 5;
        double startY = line1[1] * 10 + canvas.getLayoutY() + 5;
        double endX = line1[2] * 10 + canvas.getLayoutX() + 5;
        double endY = line1[3] * 10 + canvas.getLayoutY() + 5;

        if (lineB == null) {
            lineB = new Line(startX, startY, endX, endY);
            lineB.setStroke(Color.CYAN);
            lineB.setStrokeWidth(2);
            //contentU.getChildren().add(lineB);
        } else {
            lineB.setStartX(startX);
            lineB.setStartY(startY);
            lineB.setEndX(endX);
            lineB.setEndY(endY);
        }
    }

    Line lineB;
    Line lineD;

    // Реализация метода ЦДА (DDA) (пиксельная версия)
    private void drawDDALine(double x1, double y1, double x2, double y2, Color color) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double steps = Math.max(Math.abs(dx), Math.abs(dy));

        double xInc = dx / steps;
        double yInc = dy / steps;

        double x = x1;
        double y = y1;
        for (int i = 0; i <= steps; i++) {
            var pixel = drawPixel((int) Math.round(x), (int) Math.round(y), color);
            pixel.toBack();
            x += xInc;
            y += yInc;
        }

        double startX = line2[0] * 10 + canvas.getLayoutX() + 5;
        double startY = line2[1] * 10 + canvas.getLayoutY() + 5;
        double endX = line2[2] * 10 + canvas.getLayoutX() + 5;
        double endY = line2[3] * 10 + canvas.getLayoutY() + 5;

        if (lineD == null) {
            lineD = new Line(startX, startY, endX, endY);
            lineD.setStroke(Color.CYAN);
            lineD.setStrokeWidth(2);
            //contentU.getChildren().add(lineD);
        } else {
            lineD.setStartX(startX);
            lineD.setStartY(startY);
            lineD.setEndX(endX);
            lineD.setEndY(endY);
        }
    }

    // Метод для преобразования цвета в CSS-формат
    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

}