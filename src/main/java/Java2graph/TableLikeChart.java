package Java2graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 现代化表格图片生成器
 * 特点: 渐变表头、圆角、阴影、现代配色
 */
public class TableLikeChart {

    private static final int PADDING = 15;
    private static final int ROW_HEIGHT = 38;
    private static final int HEADER_HEIGHT = 50;
    private static final int CORNER_RADIUS = 12; // 圆角半径

    // 现代化配色
    private static final Color HEADER_PRIMARY = new Color(79, 70, 229);     // 靛蓝
    private static final Color HEADER_SECONDARY = new Color(99, 102, 241);  // 淡靛蓝
    private static final Color HEADER_TEXT = Color.WHITE;
    private static final Color ROW_BG_1 = Color.WHITE;
    private static final Color ROW_BG_2 = new Color(249, 250, 251);
    private static final Color TEXT_COLOR = new Color(17, 24, 39);          // 加深默认文字
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 15);

    // 单元格样式类
    public static class CellStyle {
        public Color textColor;
        public Color backgroundColor;
        public Font font;
        public boolean bold;

        public CellStyle() {
            this.textColor = TEXT_COLOR;
            this.backgroundColor = null;
            this.bold = false;
        }

        public CellStyle(Color textColor) {
            this.textColor = textColor;
            this.backgroundColor = null;
        }

        public CellStyle(Color textColor, Color backgroundColor) {
            this.textColor = textColor;
            this.backgroundColor = backgroundColor;
        }

        public CellStyle(Color textColor, Color backgroundColor, boolean bold) {
            this.textColor = textColor;
            this.backgroundColor = backgroundColor;
            this.bold = bold;
        }
    }

    // 单元格数据类
    public static class Cell {
        public String text;
        public CellStyle style;

        public Cell(String text) {
            this.text = text;
            this.style = new CellStyle();
        }

        public Cell(String text, CellStyle style) {
            this.text = text;
            this.style = style;
        }

        public Cell(String text, Color textColor) {
            this.text = text;
            this.style = new CellStyle(textColor);
        }

        public Cell(String text, Color textColor, Color backgroundColor) {
            this.text = text;
            this.style = new CellStyle(textColor, backgroundColor);
        }

        public Cell(String text, Color textColor, Color backgroundColor, boolean bold) {
            this.text = text;
            this.style = new CellStyle(textColor, backgroundColor, bold);
        }
    }

    public static class TableData {
        public List<String> title;
        public List<List<Cell>> preSubmit;
        public String chartTitle; // 新增: 图片标题

        public TableData(List<String> title, List<List<Cell>> preSubmit) {
            this.title = title;
            this.preSubmit = preSubmit;
            this.chartTitle = null;
        }

        public TableData(List<String> title, List<List<Cell>> preSubmit, String chartTitle) {
            this.title = title;
            this.preSubmit = preSubmit;
            this.chartTitle = chartTitle;
        }
    }

    public static void generateTableImage(TableData data, String outputPath) throws IOException {
        int columnCount = data.title.size();
        int rowCount = data.preSubmit.size();

        // 创建临时图像来测量文本宽度
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG2d = tempImage.createGraphics();
        Font headerFont = new Font("Microsoft YaHei", Font.BOLD, 16);
        Font cellFont = new Font("Microsoft YaHei", Font.BOLD, 15); // 数字字体调大
        tempG2d.setFont(headerFont);
        FontMetrics headerFm = tempG2d.getFontMetrics();
        tempG2d.setFont(cellFont);
        FontMetrics cellFm = tempG2d.getFontMetrics();

        // 计算每列的宽度
        int[] columnWidths = new int[columnCount];
        for (int i = 0; i < columnCount; i++) {
            int maxWidth = headerFm.stringWidth(data.title.get(i));

            for (List<Cell> row : data.preSubmit) {
                if (i < row.size()) {
                    int cellWidth = cellFm.stringWidth(row.get(i).text);
                    maxWidth = Math.max(maxWidth, cellWidth);
                }
            }
            columnWidths[i] = maxWidth + PADDING * 2;
        }
        tempG2d.dispose();

        // 计算总宽度和高度
        int totalWidth = 0;
        for (int width : columnWidths) {
            totalWidth += width;
        }

        // 计算标题高度
        int titleHeight = 0;
        FontMetrics titleFm = null;
        if (data.chartTitle != null && !data.chartTitle.isEmpty()) {
            titleHeight = 60; // 标题区域高度
            Font titleFont = new Font("Microsoft YaHei", Font.BOLD, 20);
            tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            tempG2d = tempImage.createGraphics();
            tempG2d.setFont(titleFont);
            titleFm = tempG2d.getFontMetrics();
            tempG2d.dispose();
        }

        int totalHeight = titleHeight + HEADER_HEIGHT + (rowCount * ROW_HEIGHT);

        // 添加边距和阴影空间
        int margin = 5;
        int canvasWidth = totalWidth + margin * 2;
        int canvasHeight = totalHeight + margin * 2;

        // 创建图像
        BufferedImage image = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 设置高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // 填充透明背景
        g2d.setColor(new Color(255, 255, 255, 0));
        g2d.fillRect(0, 0, canvasWidth, canvasHeight);

        // 绘制阴影效果（多层）
        for (int i = 0; i < 3; i++) {
            g2d.setColor(new Color(0, 0, 0, 8 - i * 2));
            g2d.fillRoundRect(margin + i, margin + i + 4,
                    totalWidth, totalHeight, CORNER_RADIUS, CORNER_RADIUS);
        }

        // 绘制表格主体背景（白色圆角矩形）
        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(margin, margin, totalWidth, totalHeight, CORNER_RADIUS, CORNER_RADIUS);

        // 裁剪区域为圆角矩形
        g2d.setClip(margin, margin, totalWidth, totalHeight);
        Shape roundRect = new java.awt.geom.RoundRectangle2D.Float(
                margin, margin, totalWidth, totalHeight, CORNER_RADIUS, CORNER_RADIUS);
        g2d.setClip(roundRect);

        // 绘制标题区域（如果有标题）
        int contentStartY = margin;
        if (data.chartTitle != null && !data.chartTitle.isEmpty()) {
            // 标题背景 - 浅灰色
            g2d.setColor(new Color(248, 250, 252));
            g2d.fillRect(margin, margin, totalWidth, titleHeight);

            // 标题文字
            Font titleFont = new Font("Microsoft YaHei", Font.BOLD, 20);
            g2d.setFont(titleFont);
            g2d.setColor(new Color(30, 41, 59)); // 深灰色

            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(data.chartTitle);
            int textX = margin + (totalWidth - textWidth) / 2; // 居中
            int textY = margin + (titleHeight + fm.getAscent()) / 2 - 2;
            g2d.drawString(data.chartTitle, textX, textY);

            contentStartY = margin + titleHeight;
        }

        // 绘制表头 - 渐变效果
        GradientPaint headerGradient = new GradientPaint(
                margin, contentStartY, HEADER_PRIMARY,
                margin, contentStartY + HEADER_HEIGHT, HEADER_SECONDARY
        );
        g2d.setPaint(headerGradient);
        g2d.fillRect(margin, contentStartY, totalWidth, HEADER_HEIGHT);

        // 绘制表头文字
        g2d.setFont(headerFont);
        g2d.setColor(HEADER_TEXT);
        int x = margin;
        for (int i = 0; i < columnCount; i++) {
            String headerText = data.title.get(i);
            int textWidth = headerFm.stringWidth(headerText);
            int textX = x + (columnWidths[i] - textWidth) / 2;
            int textY = contentStartY + (HEADER_HEIGHT + headerFm.getAscent()) / 2 - 2;

            // 文字阴影
            g2d.setColor(new Color(0, 0, 0, 40));
            g2d.drawString(headerText, textX + 1, textY + 1);
            g2d.setColor(HEADER_TEXT);
            g2d.drawString(headerText, textX, textY);

            x += columnWidths[i];
        }

        // 绘制数据行
        g2d.setFont(new Font("Microsoft YaHei", Font.BOLD, 15)); // 数字字体调大
        int y = contentStartY + HEADER_HEIGHT;

        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            List<Cell> row = data.preSubmit.get(rowIndex);

            x = margin;
            for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                Cell cell = (colIndex < row.size()) ? row.get(colIndex) : new Cell("");

                // 绘制单元格背景 - 全白色
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, columnWidths[colIndex], ROW_HEIGHT);

                // 绘制单元格文本 - 加粗字体
                Font displayFont = new Font("Microsoft YaHei", Font.BOLD, 15); // 数字字体调大
                g2d.setFont(displayFont);
                g2d.setColor(cell.style.textColor);

                String cellText = cell.text;
                FontMetrics fm = g2d.getFontMetrics();

                // 第一列左对齐,其他列居中
                int textX;
                if (colIndex == 0) {
                    textX = x + PADDING;
                } else {
                    int textWidth = fm.stringWidth(cellText);
                    textX = x + (columnWidths[colIndex] - textWidth) / 2;
                }

                int textY = y + (ROW_HEIGHT + fm.getAscent()) / 2 - 2;
                g2d.drawString(cellText, textX, textY);

                x += columnWidths[colIndex];
            }

            y += ROW_HEIGHT;
        }

        // 重置裁剪区域
        g2d.setClip(null);

        // 绘制细边框
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(1.5f));

        // 水平分割线
        y = contentStartY + HEADER_HEIGHT;
        for (int i = 0; i <= rowCount; i++) {
            g2d.drawLine(margin, y, margin + totalWidth, y);
            y += ROW_HEIGHT;
        }

        // 垂直分割线
        x = margin;
        for (int width : columnWidths) {
            x += width;
            if (x < margin + totalWidth) { // 不画最右边的线
                g2d.drawLine(x, contentStartY, x, margin + totalHeight);
            }
        }

        // 绘制圆角外边框
        g2d.setColor(new Color(209, 213, 219));
        g2d.setStroke(new BasicStroke(2f));
        g2d.drawRoundRect(margin, margin, totalWidth - 1, totalHeight - 1,
                CORNER_RADIUS, CORNER_RADIUS);

        g2d.dispose();

        // 保存图像
        File outputFile = new File(outputPath);
        ImageIO.write(image, "PNG", outputFile);
        System.out.println("✨ 现代化表格图片已生成: " + outputPath);
    }

    // 使用示例
    public static void main(String[] args) {
        try {
            List<String> title = List.of(
                    "真实原因", "15:32", "15:33", "15:34", "15:35",
                    "15:36", "15:37", "15:38", "15:39", "15:40", "15:41"
            );

            // 字体颜色: 黑色或红色
            Color blackText = Color.BLACK;                  // 黑色
            Color redText = new Color(220, 38, 38);         // 红色

            List<List<Cell>> preSubmit = List.of(
                    List.of(
                            new Cell("调用SPA未返回报价"),
                            new Cell("0", blackText),
                            new Cell("2", redText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("2", redText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("6", redText)
                    ),
                    List.of(
                            new Cell("华住可订校验接口失败-spa"),
                            new Cell("0", blackText),
                            new Cell("2", redText),
                            new Cell("0", blackText),
                            new Cell("1", blackText),
                            new Cell("0", blackText),
                            new Cell("1", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("2", blackText)
                    ),
                    List.of(
                            new Cell("动态直减产品已下线-sirius"),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("1", blackText),
                            new Cell("1", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("1", blackText),
                            new Cell("1", blackText)
                    ),
                    List.of(
                            new Cell("最小间数大于最大间数-spa"),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("1", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText)
                    ),
                    List.of(
                            new Cell("酒店大促wrapper报价为空-sirius"),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("1", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText),
                            new Cell("0", blackText)
                    )
            );

            // 创建带标题的表格数据
            TableData data = new TableData(title, preSubmit, "进订失败原因统计表");
            generateTableImage(data, "table_output.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}