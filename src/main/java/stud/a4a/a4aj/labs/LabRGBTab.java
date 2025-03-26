package stud.a4a.a4aj.labs;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;

import java.io.File;

public class LabRGBTab extends Tab {

    private ImageView imageView;
    private BarChart<String, Number> redChart;
    private BarChart<String, Number> greenChart;
    private BarChart<String, Number> blueChart;
    private BarChart<String, Number> avgChart;

    public LabRGBTab() {
        // Инициализация компонентов
        imageView = new ImageView();
        imageView.setX(20);
        imageView.setY(20);

        this.setText("Анализ картинок");

        // Инициализация графиков
        redChart = createChart("Red");
        greenChart = createChart("Green");
        blueChart = createChart("Blue");
        avgChart = createChart("Average");

        // Создаем FlowPane для размещения графиков
        FlowPane flowPane = new FlowPane(redChart, greenChart, blueChart, avgChart);
        flowPane.setHgap(10); // Горизонтальный отступ между графиками
        flowPane.setVgap(10); // Вертикальный отступ между графиками

        // Обертываем FlowPane в ScrollPane
        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setMaxWidth(300);
        scrollPane.setMaxHeight(300);
        // Кнопка для выбора изображения
        Button button = new Button("Выбрать картинку");
        button.setOnAction(event -> {
            // Выбор изображения
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Image File");
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
                processRGB(image);
            }
        });

        // Размещаем компоненты в AnchorPane
        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(imageView, 10.0);
        AnchorPane.setLeftAnchor(imageView, 10.0);
        AnchorPane.setTopAnchor(button, 10.0);
        AnchorPane.setRightAnchor(button, 10.0);
        AnchorPane.setTopAnchor(scrollPane, 200.0);
        AnchorPane.setLeftAnchor(scrollPane, 10.0);
        AnchorPane.setRightAnchor(scrollPane, 10.0);
        AnchorPane.setBottomAnchor(scrollPane, 10.0);

        anchorPane.getChildren().addAll(imageView, button, scrollPane);
        this.setContent(anchorPane);
        this.setClosable(false);
    }

    private BarChart<String, Number> createChart(String title) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle(title);
        chart.setLegendVisible(false);
        return chart;
    }

    public void processRGB(Image image) {
        int[] redHistogram = new int[256];
        int[] greenHistogram = new int[256];
        int[] blueHistogram = new int[256];
        int[] avgHistogram = new int[256];

        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                int red = (argb >> 16) & 0xff;
                int green = (argb >> 8) & 0xff;
                int blue = argb & 0xff;
                int avg = (red + green + blue) / 3;

                redHistogram[red]++;
                greenHistogram[green]++;
                blueHistogram[blue]++;
                avgHistogram[avg]++;
            }
        }

        updateChart(redChart, redHistogram);
        updateChart(greenChart, greenHistogram);
        updateChart(blueChart, blueHistogram);
        updateChart(avgChart, avgHistogram);
    }

    private void updateChart(BarChart<String, Number> chart, int[] histogram) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < histogram.length; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), histogram[i]));
        }
        chart.getData().clear();
        chart.getData().add(series);
    }
}