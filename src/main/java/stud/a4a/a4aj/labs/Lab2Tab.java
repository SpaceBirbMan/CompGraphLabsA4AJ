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

        // Добыча координат из углов
        int x1 = (int) Math.min(ellipse[0], ellipse[2]);
        int y1 = (int) Math.min(ellipse[1], ellipse[3]);
        int x2 = (int) Math.max(ellipse[0], ellipse[2]);
        int y2 = (int) Math.max(ellipse[1], ellipse[3]);

        int a = (x2 - x1) / 2; // радиус 1
        int b = (y2 - y1) / 2; // радиус 2
        int xCenter = x1 + a; // центр
        int yCenter = y1 + b; // центр

        drawBresenhamEllipse(xCenter, yCenter, a, b, Color.RED);
    }

    /// Отрисовка эллипса методом Брезенхэма
    private void drawBresenhamEllipse(int xCenter, int yCenter, int a, int b, Color color) {
        // Инициализация начальных значений
        int x = 0;          // Текущая координата x (начинаем с 0)
        int y = b;          // Текущая координата y (начинаем с радиуса по оси y)

        int a2 = a * a;     // Квадрат радиуса по оси x
        int b2 = b * b;     // Квадрат радиуса по оси y

        int fx = 0;         // Накопление для вычисления ошибки по оси x
        int fy = 2 * a2 * y; // Накопление для вычисления ошибки по оси y

        // Начальное значение параметра решения (ошибки)
        int p = (int) Math.round(b2 - (a2 * b) + (0.25 * a2));

        // Первая часть алгоритма: рисуем дугу от (0, b) до точки, где наклон равен -1
        while (fx < fy) {
            // Рисуем точки эллипса в текущей позиции (x, y)
            plotEllipsePoints(xCenter, yCenter, x, y, color);

            x++;            // Увеличиваем x
            fx += 2 * b2;   // Обновляем накопление для ошибки по оси x

            // Обновляем параметр решения (ошибку)
            if (p < 0) {
                p += b2 + fx; // Если ошибка отрицательная, продолжаем движение по x
            } else {
                y--;        // Уменьшаем y
                fy -= 2 * a2; // Обновляем накопление для ошибки по оси y
                p += b2 + fx - fy; // Обновляем параметр решения с учетом изменения y
            }
        }

        // Вторая часть алгоритма: рисуем дугу от точки, где наклон равен -1, до (a, 0)
        p = (int) Math.round(b2 * (x + 0.5) * (x + 0.5) + a2 * (y - 1) * (y - 1) - a2 * b2);
        while (y >= 0) {
            // Рисуем точки эллипса в текущей позиции (x, y)
            plotEllipsePoints(xCenter, yCenter, x, y, color);

            y--;            // Уменьшаем y
            fy -= 2 * a2;   // Обновляем накопление для ошибки по оси y

            // Обновляем параметр решения (ошибку)
            if (p >= 0) {
                p += a2 - fy; // Если ошибка положительная, продолжаем движение по y
            } else {
                x++;        // Увеличиваем x
                fx += 2 * b2; // Обновляем накопление для ошибки по оси x
                p += a2 - fy + fx; // Обновляем параметр решения с учетом изменения x
            }
        }
    }

    /// Вспомогательный метод для симметричного рисования точек эллипса
    private void plotEllipsePoints(int xCenter, int yCenter, int x, int y, Color color) {
        // Используя координаты центра эллипса и координаты точки, можно нарисовать ещё 3 точки, в ещё трёх четвертях, симметрично первой в первой четверти
        Visuals.drawPixel(xCenter + x, yCenter + y, color, canvas).toBack();
        Visuals.drawPixel(xCenter - x, yCenter + y, color, canvas).toBack();
        Visuals.drawPixel(xCenter + x, yCenter - y, color, canvas).toBack();
        Visuals.drawPixel(xCenter - x, yCenter - y, color, canvas).toBack();
    }
}
