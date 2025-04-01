package stud.a4a.a4aj.labs;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class Lab8Tab extends Tab {

    private static final double SIZE = 50;
    private static final double HEIGHT = 100;
    private int sides = 4;
    private List<Point3D> vertices = new ArrayList<>();
    private List<int[]> edges = new ArrayList<>();
    private List<int[]> faces = new ArrayList<>();
    private List<Polygon> facePolygons = new ArrayList<>();
    private List<Line> edgeLines = new ArrayList<>();
    private Group prismGroup = new Group();
    private Spinner<Integer> sidesSpinner;
    private CheckBox backfaceCullingCheckbox;
    private CheckBox wireframeCheckbox;
    private CheckBox invertNormalsCheckbox;
    private CheckBox showNormalsCheckbox;
    private List<Line> normalLines = new ArrayList<>();

    public Lab8Tab() {
        super("Вращение призмы");

        // Инициализация UI элементов
        sidesSpinner = new Spinner<>(3, 20, 4);
        sidesSpinner.setEditable(true);
        sidesSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            sides = newVal;
            rebuildPrism();
        });

        backfaceCullingCheckbox = new CheckBox("Удаление невидимых граней");
        backfaceCullingCheckbox.setSelected(true);

        wireframeCheckbox = new CheckBox("Каркасный режим");
        wireframeCheckbox.setSelected(false);

        invertNormalsCheckbox = new CheckBox("Инвертировать нормали");
        invertNormalsCheckbox.setSelected(false);

        showNormalsCheckbox = new CheckBox("Показать нормали");
        showNormalsCheckbox.setSelected(false);

        CheckBox cbX = new CheckBox("X");
        CheckBox cbY = new CheckBox("Y");
        CheckBox cbYClockwise = new CheckBox("Y по часовой");
        CheckBox cbZ = new CheckBox("Z");

        // Создаем сцену
        SubScene subScene = new SubScene(prismGroup, 600, 600, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.LIGHTGRAY);

        // Сборка интерфейса
        VBox controls = new VBox(10,
                new Label("Количество граней:"),
                sidesSpinner,
                backfaceCullingCheckbox,
                invertNormalsCheckbox,
                showNormalsCheckbox,
                wireframeCheckbox,
                new Label("Оси вращения:"),
                cbX, cbY, cbYClockwise, cbZ
        );

        HBox content = new HBox(subScene, controls);
        this.setContent(content);
        this.setClosable(false);

        // Инициализация призмы
        rebuildPrism();

        // Углы вращения
        final double speed = Math.toRadians(1);
        final double[] angleX = {0};
        final double[] angleY = {0};
        final double[] angleZ = {0};

        // Таймер анимации
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Обновляем углы вращения
                if (cbX.isSelected()) angleX[0] += speed;
                if (cbY.isSelected()) {
                    if (cbYClockwise.isSelected()) {
                        angleY[0] -= speed;
                    } else {
                        angleY[0] += speed;
                    }
                }
                if (cbZ.isSelected()) angleZ[0] += speed;

                // Преобразуем вершины
                Point3D[] transformedPoints = new Point3D[vertices.size()];
                for (int i = 0; i < vertices.size(); i++) {
                    Point3D p = vertices.get(i);

                    // Вращение вокруг X
                    double y1 = p.getY() * Math.cos(angleX[0]) - p.getZ() * Math.sin(angleX[0]);
                    double z1 = p.getY() * Math.sin(angleX[0]) + p.getZ() * Math.cos(angleX[0]);

                    // Вращение вокруг Y
                    double x2 = p.getX() * Math.cos(angleY[0]) + z1 * Math.sin(angleY[0]);
                    double z2 = -p.getX() * Math.sin(angleY[0]) + z1 * Math.cos(angleY[0]);

                    // Вращение вокруг Z
                    double x3 = x2 * Math.cos(angleZ[0]) - y1 * Math.sin(angleZ[0]);
                    double y3 = x2 * Math.sin(angleZ[0]) + y1 * Math.cos(angleZ[0]);

                    transformedPoints[i] = new Point3D(x3, y3, z2);
                }

                // Обновляем отображение граней и нормалей
                for (int i = 0; i < faces.size(); i++) {
                    int[] face = faces.get(i);
                    Polygon polygon = facePolygons.get(i);

                    // Вычисляем нормаль грани
                    Point3D p1 = transformedPoints[face[0]];
                    Point3D p2 = transformedPoints[face[1]];
                    Point3D p3 = transformedPoints[face[2]];

                    Point3D v1 = p2.subtract(p1);
                    Point3D v2 = p3.subtract(p1);
                    Point3D normal = v1.crossProduct(v2).normalize();

                    // Инвертируем нормаль при необходимости
                    if (invertNormalsCheckbox.isSelected()) {
                        normal = normal.multiply(-1);
                    }

                    // Вектор к камере (предполагаем, что камера смотрит вдоль оси Z)
                    Point3D viewVector = new Point3D(0, 0, 1);

                    // Проверяем, видна ли грань
                    boolean visible = !backfaceCullingCheckbox.isSelected() ||
                            normal.dotProduct(viewVector) > 0;

                    polygon.setVisible(visible);

                    if (visible) {
                        // Обновляем координаты полигона
                        polygon.getPoints().clear();
                        for (int vertexIndex : face) {
                            Point3D p = transformedPoints[vertexIndex];
                            polygon.getPoints().addAll(p.getX() + 300, p.getY() + 300);
                        }
                    }

                    // Обновляем нормали (если включено их отображение)
                    if (showNormalsCheckbox.isSelected() && i < normalLines.size()) {
                        Line normalLine = normalLines.get(i);
                        Point3D center = new Point3D(
                                (transformedPoints[face[0]].getX() + transformedPoints[face[1]].getX() + transformedPoints[face[2]].getX()) / 3,
                                (transformedPoints[face[0]].getY() + transformedPoints[face[1]].getY() + transformedPoints[face[2]].getY()) / 3,
                                (transformedPoints[face[0]].getZ() + transformedPoints[face[1]].getZ() + transformedPoints[face[2]].getZ()) / 3
                        );

                        Point3D normalEnd = center.add(normal.multiply(20));
                        normalLine.setStartX(center.getX() + 300);
                        normalLine.setStartY(center.getY() + 300);
                        normalLine.setEndX(normalEnd.getX() + 300);
                        normalLine.setEndY(normalEnd.getY() + 300);
                        normalLine.setVisible(visible);
                        normalLine.setStroke(Color.RED);
                    }
                }

                // Обновляем отображение рёбер
                for (int i = 0; i < edges.size(); i++) {
                    int[] edge = edges.get(i);
                    Line line = edgeLines.get(i);

                    Point3D p1 = transformedPoints[edge[0]];
                    Point3D p2 = transformedPoints[edge[1]];

                    line.setStartX(p1.getX() + 300);
                    line.setStartY(p1.getY() + 300);
                    line.setEndX(p2.getX() + 300);
                    line.setEndY(p2.getY() + 300);

                    // Показываем/скрываем рёбра в зависимости от режима
                    line.setVisible(wireframeCheckbox.isSelected());
                }
            }
        }.start();
    }

    private void rebuildPrism() {
        vertices.clear();
        edges.clear();
        faces.clear();
        prismGroup.getChildren().clear();
        facePolygons.clear();
        edgeLines.clear();
        normalLines.clear();

        // Создаем вершины для призмы
        // Нижнее основание (вершины 0..sides-1)
        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides;
            double x = SIZE * Math.cos(angle);
            double y = SIZE * Math.sin(angle);
            vertices.add(new Point3D(x, y, -HEIGHT/2));
        }

        // Верхнее основание (вершины sides..2*sides-1)
        for (int i = 0; i < sides; i++) {
            double angle = 2 * Math.PI * i / sides;
            double x = SIZE * Math.cos(angle);
            double y = SIZE * Math.sin(angle);
            vertices.add(new Point3D(x, y, HEIGHT/2));
        }

        // Создаем рёбра основания
        for (int i = 0; i < sides; i++) {
            // Ребра нижнего основания
            edges.add(new int[]{i, (i + 1) % sides});
            // Ребра верхнего основания
            edges.add(new int[]{sides + i, sides + (i + 1) % sides});
            // Вертикальные ребра
            edges.add(new int[]{i, sides + i});
        }

        // Создаем грани
        // Боковые грани (каждая боковая грань - это два треугольника)
        for (int i = 0; i < sides; i++) {
            int next = (i + 1) % sides;
            // Первый треугольник боковой грани (вершины упорядочены против часовой стрелки)
            faces.add(new int[]{i, next, sides + i});
            // Второй треугольник боковой грани
            faces.add(new int[]{next, sides + next, sides + i});
        }

// Нижнее основание (состоит из triangles-2 треугольников)
        for (int i = 1; i < sides - 1; i++) {
            // Вершины упорядочены по часовой стрелке при взгляде снизу (чтобы нормаль смотрела вниз)
            faces.add(new int[]{0, i + 1, i});
        }

// Верхнее основание (состоит из triangles-2 треугольников)
        for (int i = 1; i < sides - 1; i++) {
            // Вершины упорядочены против часовой стрелки при взгляде сверху (чтобы нормаль смотрела вверх)
            faces.add(new int[]{sides, sides + i, sides + i + 1});
        }

        // Создаем полигоны для граней
        for (int i = 0; i < faces.size(); i++) {
            Polygon polygon = new Polygon();
            // Разные цвета для разных типов граней
            if (i < 2 * sides) {
                // Боковые грани
                polygon.setFill(Color.hsb(i * 360.0 / (2 * sides), 0.7, 0.9, 0.7));
            } else if (i < 2 * sides + (sides - 2)) {
                // Нижнее основание
                polygon.setFill(Color.LIGHTGRAY);
            } else {
                // Верхнее основание
                polygon.setFill(Color.DARKGRAY);
            }
            polygon.setStroke(Color.BLACK);
            facePolygons.add(polygon);
            prismGroup.getChildren().add(polygon);

            // Создаем линии для нормалей
            Line normalLine = new Line();
            normalLine.setVisible(showNormalsCheckbox.isSelected());
            normalLines.add(normalLine);
            prismGroup.getChildren().add(normalLine);
        }

        // Создаем линии для рёбер
        for (int i = 0; i < edges.size(); i++) {
            Line line = new Line();
            line.setStroke(Color.BLACK);
            line.setVisible(false); // По умолчанию скрыты
            edgeLines.add(line);
            prismGroup.getChildren().add(line);
        }

        // Обновляем видимость в соответствии с настройками
        updateVisibility();
    }

    private void updateVisibility() {
        boolean wireframe = wireframeCheckbox.isSelected();
        for (Polygon p : facePolygons) {
            p.setVisible(!wireframe);
        }
        for (Line l : edgeLines) {
            l.setVisible(wireframe);
        }
        for (Line nl : normalLines) {
            nl.setVisible(showNormalsCheckbox.isSelected());
        }
    }
}