package stud.a4a.a4aj.labs;

import javafx.animation.AnimationTimer;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Lab4Tab extends Tab {

    public Lab4Tab() {
        super("Вращение кубика");

        // Вершины куба (изначально)
        Point3D[] points = {
                new Point3D(-50, -50, -50), new Point3D(50, -50, -50),
                new Point3D(50, 50, -50), new Point3D(-50, 50, -50),
                new Point3D(-50, -50, 50), new Point3D(50, -50, 50),
                new Point3D(50, 50, 50), new Point3D(-50, 50, 50)
        };

        // Соединения рёбер
        int[][] edgesIndexes = {
                {0, 1}, {1, 2}, {2, 3}, {3, 0},
                {4, 5}, {5, 6}, {6, 7}, {7, 4},
                {0, 4}, {1, 5}, {2, 6}, {3, 7}
        };

        // Создаём линии для куба
        Line[] edges = new Line[edgesIndexes.length];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = new Line();
            edges[i].setStroke(Color.RED);
        }

        Group cube = new Group(edges);
        SubScene subScene = new SubScene(cube, 400, 400, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.LIGHTGRAY);

        // Контроллеры вращения
        CheckBox cbX = new CheckBox("X");
        CheckBox cbY = new CheckBox("Y");
        CheckBox cbZ = new CheckBox("Z");

        VBox controls = new VBox(10, cbX, cbY, cbZ);
        HBox content = new HBox(subScene, controls);
        this.setContent(content);
        this.setClosable(false);

        // Углы вращения
        final double speed = Math.toRadians(1);
        final double[] angleX = {0};
        final double[] angleY = {0};
        final double[] angleZ = {0};

        // Таймер анимации
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (cbX.isSelected()) angleX[0] += speed;
                if (cbY.isSelected()) angleY[0] += speed;
                if (cbZ.isSelected()) angleZ[0] += speed;

                Point3D[] transformedPoints = new Point3D[points.length];

                for (int i = 0; i < points.length; i++) {
                    Point3D p = points[i];

                    // Вращение вокруг оси X
                    double y1 = p.getY() * Math.cos(angleX[0]) - p.getZ() * Math.sin(angleX[0]);
                    double z1 = p.getY() * Math.sin(angleX[0]) + p.getZ() * Math.cos(angleX[0]);

                    // Вращение вокруг оси Y
                    double x2 = p.getX() * Math.cos(angleY[0]) + z1 * Math.sin(angleY[0]);
                    double z2 = -p.getX() * Math.sin(angleY[0]) + z1 * Math.cos(angleY[0]);

                    // Вращение вокруг оси Z
                    double x3 = x2 * Math.cos(angleZ[0]) - y1 * Math.sin(angleZ[0]);
                    double y3 = x2 * Math.sin(angleZ[0]) + y1 * Math.cos(angleZ[0]);

                    transformedPoints[i] = new Point3D(x3, y3, z2);
                }

                // Отображаем проекцию на 2D
                for (int i = 0; i < edgesIndexes.length; i++) {
                    Point3D p1 = transformedPoints[edgesIndexes[i][0]];
                    Point3D p2 = transformedPoints[edgesIndexes[i][1]];

                    edges[i].setStartX(p1.getX() + 200);
                    edges[i].setStartY(p1.getY() + 200);
                    edges[i].setEndX(p2.getX() + 200);
                    edges[i].setEndY(p2.getY() + 200);
                }
            }
        }.start();
    }
}
