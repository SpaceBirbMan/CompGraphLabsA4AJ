package stud.a4a.a4aj.labs;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import stud.a4a.a4aj.Visuals;

public class Lab2Tab extends Tab {

    private final GridPane canvas;
    private final double[] ellipse = {1, 1, 20, 20}; // x1, y1, x2, y2

    public Lab2Tab() {
        super("Эллипс по углам");

        this.setClosable(false);
        canvas = Visuals.setupCanvas();
        drawLines();
        Pane content = new Pane();
        setupControls(content);
        setContent(content);
        canvas.setGridLinesVisible(true);
        Pane contentU = new Pane();
        content.getChildren().add(contentU);
        contentU.toFront();
        content.getChildren().addAll(canvas);
    }

    /// Установка ввода координат для эллипса
    private void setupControls(Pane content) {
        VBox controls = new VBox(5);
        controls.setPrefWidth(150);
        TextField[] fields = new TextField[4]; // Поля для ввода углов эллипса

        for (int i = 0; i < fields.length; i++) {
            fields[i] = new TextField(String.valueOf(ellipse[i]));
            int index = i;
            fields[i].setOnAction(e -> {
                try {
                    double newValue = Double.parseDouble(fields[index].getText());
                    ellipse[index] = newValue;
                    drawLines();
                    canvas.setGridLinesVisible(true);
                } catch (NumberFormatException ignored) {}
            });
        }

        controls.getChildren().addAll(
                new Label("Углы эллипса (x1, y1, x2, y2):"),
                fields[0], fields[1], fields[2], fields[3]
        );

        ScrollPane scrollBox = new ScrollPane(controls);
        scrollBox.setFitToWidth(true);
        scrollBox.setPrefHeight(200);
        scrollBox.setLayoutX(420);
        scrollBox.setLayoutY(10);

        content.getChildren().addAll(scrollBox);
    }

    /// Рисование эллипса
    private void drawLines() {
        canvas.getChildren().clear(); // Очистка перед отрисовкой
        Visuals.drawGrid(canvas); // Восстановление сетки

        int x1 = (int) Math.min(ellipse[0], ellipse[2]);
        int y1 = (int) Math.min(ellipse[1], ellipse[3]);
        int x2 = (int) Math.max(ellipse[0], ellipse[2]);
        int y2 = (int) Math.max(ellipse[1], ellipse[3]);

        int a = (x2 - x1) / 2;
        int b = (y2 - y1) / 2;
        int xCenter = x1 + a;
        int yCenter = y1 + b;

        drawBresenhamEllipse(xCenter, yCenter, a, b, Color.RED);
    }

    /// Отрисовка эллипса методом Брезенхэма
    private void drawBresenhamEllipse(int xCenter, int yCenter, int a, int b, Color color) {
        int x = 0;
        int y = b;

        int a2 = a * a;
        int b2 = b * b;

        int fx = 0;
        int fy = 2 * a2 * y;

        int p = (int) Math.round(b2 - (a2 * b) + (0.25 * a2));

        while (fx < fy) {
            plotEllipsePoints(xCenter, yCenter, x, y, color);
            x++;
            fx += 2 * b2;
            if (p < 0) {
                p += b2 + fx;
            } else {
                y--;
                fy -= 2 * a2;
                p += b2 + fx - fy;
            }
        }

        p = (int) Math.round(b2 * (x + 0.5) * (x + 0.5) + a2 * (y - 1) * (y - 1) - a2 * b2);
        while (y >= 0) {
            plotEllipsePoints(xCenter, yCenter, x, y, color);
            y--;
            fy -= 2 * a2;
            if (p >= 0) {
                p += a2 - fy;
            } else {
                x++;
                fx += 2 * b2;
                p += a2 - fy + fx;
            }
        }
    }

    /// Вспомогательный метод для симметричного рисования точек эллипса
    private void plotEllipsePoints(int xCenter, int yCenter, int x, int y, Color color) {
        Visuals.drawPixel(xCenter + x, yCenter + y, color, canvas).toBack();
        Visuals.drawPixel(xCenter - x, yCenter + y, color, canvas).toBack();
        Visuals.drawPixel(xCenter + x, yCenter - y, color, canvas).toBack();
        Visuals.drawPixel(xCenter - x, yCenter - y, color, canvas).toBack();
    }
}
