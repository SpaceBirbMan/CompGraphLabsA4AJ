package stud.a4a.a4aj.labs;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tab;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Lab7Tab extends Tab {
    private final Canvas canvas;
    private double rectX1, rectY1, rectX2, rectY2; // Координаты прямоугольника
    private boolean drawingRect = true; // Режим рисования прямоугольника
    private final List<Line> lines = new ArrayList<>();
    private Line currentLine = null;

    public Lab7Tab() {
        super("CutThe...");
        canvas = new Canvas(800, 600);
        setContent(canvas);

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(this::handleMouseDragged);
        canvas.setOnMouseReleased(this::handleMouseReleased);
    }

    /** Обработка нажатия кнопки мыши */
    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            clearCanvas(); // ПКМ очищает поле
            return;
        }

        if (drawingRect) {
            if (rectX1 == 0 && rectY1 == 0) {
                rectX1 = event.getX();
                rectY1 = event.getY();
            } else {
                rectX2 = event.getX();
                rectY2 = event.getY();
                drawingRect = false; // Завершаем рисование прямоугольника
            }
        } else if (event.getButton() == MouseButton.PRIMARY) {
            currentLine = new Line(event.getX(), event.getY(), event.getX(), event.getY());
        }
        redraw();
    }

    /** Обработка движения мыши при зажатой кнопке */
    private void handleMouseDragged(MouseEvent event) {
        if (!drawingRect && currentLine != null) {
            currentLine.x2 = event.getX();
            currentLine.y2 = event.getY();
        }
        redraw();
    }

    /** Обработка отпускания кнопки мыши */
    private void handleMouseReleased(MouseEvent event) {
        if (!drawingRect && currentLine != null) {
            lines.add(currentLine);
            currentLine = null;
        }
        redraw();
    }

    /** Очистка поля */
    private void clearCanvas() {
        rectX1 = rectY1 = rectX2 = rectY2 = 0;
        drawingRect = true;
        lines.clear();
        redraw();
    }

    /** Очистка и перерисовка холста */
    private void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (!drawingRect) {
            double xmin = Math.min(rectX1, rectX2);
            double ymin = Math.min(rectY1, rectY2);
            double xmax = Math.max(rectX1, rectX2);
            double ymax = Math.max(rectY1, rectY2);

            gc.setStroke(Color.BLUE);
            gc.strokeRect(xmin, ymin, xmax - xmin, ymax - ymin);

            gc.setStroke(Color.RED);
            for (Line line : lines) {
                double[] clipped = liangBarsky(line.x1, line.y1, line.x2, line.y2, xmin, ymin, xmax, ymax);
                if (clipped != null) {
                    gc.strokeLine(clipped[0], clipped[1], clipped[2], clipped[3]);
                }
            }

            if (currentLine != null) {
                double[] clipped = liangBarsky(currentLine.x1, currentLine.y1, currentLine.x2, currentLine.y2, xmin, ymin, xmax, ymax);
                if (clipped != null) {
                    gc.strokeLine(clipped[0], clipped[1], clipped[2], clipped[3]);
                }
            }
        }
    }

    /** Алгоритм Лианга-Барски */
    private double[] liangBarsky(double x1, double y1, double x2, double y2, double xmin, double ymin, double xmax, double ymax) {
        double dx = x2 - x1, dy = y2 - y1;
        double t0 = 0, t1 = 1;
        double[] p = {-dx, dx, -dy, dy};
        double[] q = {x1 - xmin, xmax - x1, y1 - ymin, ymax - y1};

        for (int i = 0; i < 4; i++) {
            if (p[i] == 0 && q[i] < 0) return null;
            double t = q[i] / p[i];
            if (p[i] < 0) {
                t0 = Math.max(t0, t);
            } else {
                t1 = Math.min(t1, t);
            }
        }

        if (t0 > t1) return null;
        return new double[]{x1 + t0 * dx, y1 + t0 * dy, x1 + t1 * dx, y1 + t1 * dy};
    }

    /** Класс для хранения линий */
    private static class Line {
        double x1, y1, x2, y2;
        Line(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }
}
