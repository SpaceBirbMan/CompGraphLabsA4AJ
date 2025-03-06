package stud.a4a.a4aj.labs;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Affine;

import java.util.ArrayList;
import java.util.Objects;

public class Lab3Tab extends Tab {

    private final Pane pane = new Pane(); // Изображение для вращения
    private final Circle pivotPoint = new Circle(5, Color.RED); // Красная точка (ось вращения)
    private final Circle startPoint = new Circle(5, Color.BLUE); // Синяя точка – показывает, куда поворачиваем
    private final Circle endPoint = new Circle(5, Color.GREEN); // Зеленая точка – показывает, откуда начат поворот
    private final Line startLine = new Line(); // Линия от оси (pivot) до синей точки
    private final Line endLine = new Line();   // Линия от оси (pivot) до зеленой точки

    private double targetAngle = 0; // Целевой угол поворота (в градусах)
    private double currentAngle = 0; // Текущий угол поворота (в градусах)
    private double rotationSpeed = 1; // Скорость поворота (градусов за кадр)

    // Флаг первого поворота
    private boolean firstRotation = true;
    // Текущая «стрелка» (начальная позиция для вычисления поворота) – изначально равна положению зелёной точки
    private double currentArrowX;
    private double currentArrowY;
    // Для отслеживания смещения оси (pivot) при её перемещении
    private double prevPivotX;
    private double prevPivotY;

    ArrayList<Double> fdot = new ArrayList();

    Polyline polyline = new Polyline();
    boolean isFirst = true;

    public Lab3Tab() {
        super("Карусель карусель");

        pane.setLayoutX(0);
        pane.setLayoutY(0);

        pane.setMaxSize(300, 300);
        pane.setPrefSize(300, 300);
        pane.setMinSize(300,300);

        pane.setOnMouseClicked(mouseEvent -> {
            System.out.println(polyline.getPoints().size());
                ArrayList<Double> dot = new ArrayList<>();
                dot.add(mouseEvent.getX());
                dot.add(mouseEvent.getY());
                polyline.getPoints().addAll(dot);
                if (fdot.isEmpty()) {
                    fdot = dot;
                }
            System.out.println(polyline.getPoints().size());
            if (isFirst) { // yanderedev moment
                pane.getChildren().add(polyline);
                System.out.println(polyline.getPoints().size());
                isFirst = false;
            }
            if (polyline.getPoints().size() == 6) {
                polyline.getPoints().addAll(fdot);
                System.out.println(polyline.getPoints());
            }
            else if (polyline.getPoints().size() >= 8) {
                pane.getChildren().remove(polyline);
                fdot.clear();
                polyline.getPoints().clear();
                isFirst = true;
            }
        });


        // рисуем квадрат путём растягивания, как в фотошопе

        // Изначальный центр картинки
        double centerX = 150;
        double centerY = 150;

        // Красная точка (ось вращения) изначально в центре
        pivotPoint.setCenterX(centerX);
        pivotPoint.setCenterY(centerY);
        prevPivotX = centerX;
        prevPivotY = centerY;

        // Зелёная точка
        double defaultGreenY = centerY - 50;
        endPoint.setCenterX(centerX);
        endPoint.setCenterY(defaultGreenY);

        // Текущая «стрелка» (будущая синяя точка) изначально равна положению зелёной
        currentArrowX = centerX;
        currentArrowY = defaultGreenY;
        startPoint.setCenterX(currentArrowX);
        startPoint.setCenterY(currentArrowY);

        // Инициализация линий (от оси до точек)
        startLine.setStartX(pivotPoint.getCenterX());
        startLine.setStartY(pivotPoint.getCenterY());
        startLine.setEndX(startPoint.getCenterX());
        startLine.setEndY(startPoint.getCenterY());

        endLine.setStartX(pivotPoint.getCenterX());
        endLine.setStartY(pivotPoint.getCenterY());
        endLine.setEndX(endPoint.getCenterX());
        endLine.setEndY(endPoint.getCenterY());

        // Элементы управления – вводим координаты оси (красной точки)
        TextField angleField = new TextField("0"); // Угол поворота в градусах
        TextField speedField = new TextField("1"); // Время поворота в секундах
        Label label = new Label("Координаты красной точки (pivot)");
        TextField dotX = new TextField(String.valueOf(centerX));
        TextField dotY = new TextField(String.valueOf(centerY));
        Button button = new Button("Повернуть");

        VBox vBox = new VBox(angleField, speedField, label, dotX, dotY, button);
        this.setClosable(false);

        Pane imagePane = new Pane(pane, pivotPoint, startPoint, endPoint, startLine, endLine);
        HBox content = new HBox(imagePane, vBox);
        this.setContent(content);

        button.setOnAction(event -> {
            try {
                double angleDeg = Double.parseDouble(angleField.getText()); // угол в градусах
                double speed = Double.parseDouble(speedField.getText());
                // Новые координаты оси (красной точки)
                double newPivotX = Double.parseDouble(dotX.getText());
                double newPivotY = Double.parseDouble(dotY.getText());

                // Если ось перемещается, сдвигаем текущую стрелку и зеленую точку на ту же дельту
                double deltaX = newPivotX - pivotPoint.getCenterX();
                double deltaY = newPivotY - pivotPoint.getCenterY();
                pivotPoint.setCenterX(newPivotX);
                pivotPoint.setCenterY(newPivotY);
                if (firstRotation) {
                    // Если ещё не было поворота, текущая стрелка определяется как [новая ось, -50 по Y]
                    currentArrowX = newPivotX;
                    currentArrowY = newPivotY - 50;
                    endPoint.setCenterX(currentArrowX);
                    endPoint.setCenterY(currentArrowY);
                } else {
                    currentArrowX += deltaX;
                    currentArrowY += deltaY;
                    endPoint.setCenterX(endPoint.getCenterX() + deltaX);
                    endPoint.setCenterY(endPoint.getCenterY() + deltaY);
                }

                // Обновляем начало линий (точка вращения)
                startLine.setStartX(newPivotX);
                startLine.setStartY(newPivotY);
                endLine.setStartX(newPivotX);
                endLine.setStartY(newPivotY);

                // Вычисляем новую позицию для синей точки: поворот текущей стрелки относительно новой оси на заданный угол
                double angleRad = Math.toRadians(angleDeg);
                double cosA = Math.cos(angleRad);
                double sinA = Math.sin(angleRad);
                double newBlueX = newPivotX + (currentArrowX - newPivotX) * cosA - (currentArrowY - newPivotY) * sinA;
                double newBlueY = newPivotY + (currentArrowX - newPivotX) * sinA + (currentArrowY - newPivotY) * cosA;

                if (firstRotation) {
                    // При первом повороте: синяя точка устанавливается в вычисленное место,
                    // а зелёная остаётся на прежнем месте (показывая начальное положение)
                    startPoint.setCenterX(newBlueX);
                    startPoint.setCenterY(newBlueY);
                    currentArrowX = newBlueX;
                    currentArrowY = newBlueY;
                    firstRotation = false;
                } else {
                    // При последующих: зелёная точка перемещается на место предыдущей синей,
                    // а синяя устанавливается в новую позицию
                    endPoint.setCenterX(currentArrowX);
                    endPoint.setCenterY(currentArrowY);
                    startPoint.setCenterX(newBlueX);
                    startPoint.setCenterY(newBlueY);
                    currentArrowX = newBlueX;
                    currentArrowY = newBlueY;
                }

                // Обновляем концы линий
                startLine.setEndX(startPoint.getCenterX());
                startLine.setEndY(startPoint.getCenterY());
                endLine.setEndX(endPoint.getCenterX());
                endLine.setEndY(endPoint.getCenterY());

                // Устанавливаем целевой угол и скорость (накопительный поворот)
                targetAngle = currentAngle + angleDeg;
                rotationSpeed = angleDeg / (speed * 60);

            } catch (NumberFormatException e) {
                System.err.println("Ошибка ввода данных");
            }
        });

        // Анимация поворота изображения вокруг оси (красной точки) с помощью аффинного преобразования
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (Math.abs(currentAngle - targetAngle) > 0.1) {
                    currentAngle += rotationSpeed;
                    if (Math.abs(currentAngle - targetAngle) <= 0.1) {
                        currentAngle = targetAngle;
                    }
                    double radAngle = Math.toRadians(currentAngle);
                    double cosA = Math.cos(radAngle);
                    double sinA = Math.sin(radAngle);
                    double pivotX = pivotPoint.getCenterX();
                    double pivotY = pivotPoint.getCenterY();
                    Affine affine = new Affine(
                            cosA, -sinA, pivotX * (1 - cosA) + pivotY * sinA,
                            sinA, cosA, pivotY * (1 - cosA) - pivotX * sinA
                    );
                    pane.getTransforms().setAll(affine);
                }
            }
        };
        timer.start();
    }
}
