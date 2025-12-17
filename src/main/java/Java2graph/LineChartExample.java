package Java2graph;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.IOException;

public class LineChartExample {
    public static void main(String[] args) throws IOException {
        // 创建数据集
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1, "2025", "1月");
        dataset.addValue(3, "2025", "2月");
        dataset.addValue(2, "2025", "3月");
        dataset.addValue(5, "2025", "4月");

        // 创建图表
        JFreeChart chart = ChartFactory.createLineChart(
                "销售数据折线图",
                "月份",
                "销售额",
                dataset
        );

        // 导出成图片
        ChartUtils.saveChartAsPNG(new File("linechart.png"), chart, 800, 600);
    }
}
