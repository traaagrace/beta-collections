package Java2graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 表格图片生成器 - 零依赖版本
 * 支持高级样式、条件格式化、渐变效果
 */
public class TableImageGenerator {

    // 预定义配色方案
    public enum ColorScheme {
        BLUE(new Color(66, 139, 202), new Color(52, 152, 219)),
        GREEN(new Color(40, 167, 69), new Color(46, 204, 113)),
        PURPLE(new Color(108, 92, 231), new Color(155, 89, 182)),
        ORANGE(new Color(255, 133, 27), new Color(243, 156, 18)),
        RED(new Color(220, 53, 69), new Color(231, 76, 60)),
        DARK(new Color(52, 58, 64), new Color(73, 80, 87));

        final Color primary;
        final Color secondary;

        ColorScheme(Color primary, Color secondary) {
            this.primary = primary;
            this.secondary = secondary;
        }
    }

    // 单元格样式类
    public static class CellStyle {
        public Color textColor = Color.BLACK;
        public Color backgroundColor = null;
        public boolean bold = false;
        public boolean italic = false;
        public int fontSize = 11;
        public int alignment = 0; // 0=center, -1=left, 1=right

        public CellStyle textColor(Color color) {
            this.textColor = color;
            return this;
        }

        public CellStyle backgroundColor(Color color) {
            this.backgroundColor = color;
            return this;
        }

        public CellStyle bold() {
            this.bold = true;
            return this;
        }

        public CellStyle italic() {
            this.italic = true;
            return this;
        }

        public CellStyle fontSize(int size) {
            this.fontSize = size;
            return this;
        }

        public CellStyle alignLeft() {
            this.alignment = -1;
            return this;
        }

        public CellStyle alignCenter() {
            this.alignment = 0;
            return this;
        }

        public CellStyle alignRight() {
            this.alignment = 1;
            return this;
        }
    }

    // 单元格类
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
    }

    // 表格配置类
    public static class TableConfig {
        public ColorScheme colorScheme = ColorScheme.BLUE;
        public boolean alternateRowColors = true;
        public boolean showBorders = true;
        public boolean showShadow = false;
        public int headerHeight = 40;
        public int rowHeight = 30;
        public int firstColumnWidth = 250;
        public int columnWidth = 100;
        public String fontName = "Microsoft YaHei";
        public int padding = 15;

        public TableConfig colorScheme(ColorScheme scheme) {
            this.colorScheme = scheme;
            return this;
        }

        public TableConfig alternateRowColors(boolean alternate) {
            this.alternateRowColors = alternate;
            return this;
        }

        public TableConfig showShadow(boolean shadow) {
            this.showShadow = shadow;
            return this;
        }

        public TableConfig columnWidth(int width) {
            this.columnWidth = width;
            return this;
        }

        public TableConfig firstColumnWidth(int width) {
            this.firstColumnWidth = width;
            return this;
        }
    }

    // 表格数据类
    public static class TableData {
        public List<String> headers;
        public List<List<Cell>> rows;
        public TableConfig config;

        public TableData(List<String> headers, List<List<Cell>> rows) {
            this.headers = headers;
            this.rows = rows;
            this.config = new TableConfig();
        }

        public TableData(List<String> headers, List<List<Cell>> rows, TableConfig config) {
            this.headers = headers;
            this.rows = rows;
            this.config = config;
        }
    }

    /**
     * 生成表格图片
     */
    public static void generateTableImage(TableData data, String outputPath) throws IOException {
        int columnCount = data.headers.size();
        int rowCount = data.rows.size();

        // 计算尺寸
        int totalWidth = data.config.firstColumnWidth +
                (columnCount - 1) * data.config.columnWidth;
        int headerHeight = data.config.headerHeight;
        int rowHeight = data.config.rowHeight;
        int totalHeight = headerHeight + (rowCount * rowHeight);

        // 创建图像
        BufferedImage image = new BufferedImage(
                totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = image.createGraphics();

        // 设置高质量渲染
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        // 背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, totalWidth, totalHeight);

        // 绘制表头 - 使用渐变效果
        GradientPaint headerGradient = new GradientPaint(
                0, 0, data.config.colorScheme.primary,
                0, headerHeight, data.config.colorScheme.secondary
        );
        g2d.setPaint(headerGradient);
        g2d.fillRect(0, 0, totalWidth, headerHeight);

        // 表头文字
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(data.config.fontName, Font.BOLD, 13));
        FontMetrics fm = g2d.getFontMetrics();

        int x = 0;
        for (int i = 0; i < columnCount; i++) {
            int colWidth = (i == 0) ? data.config.firstColumnWidth : data.config.columnWidth;
            String text = data.headers.get(i);
            int textWidth = fm.stringWidth(text);
            int textX = x + (colWidth - textWidth) / 2;
            int textY = (headerHeight + fm.getAscent()) / 2 - 2;

            // 添加文字阴影效果
            if (data.config.showShadow) {
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(text, textX + 1, textY + 1);
            }
            g2d.setColor(Color.WHITE);
            g2d.drawString(text, textX, textY);

            x += colWidth;
        }

        // 绘制数据行
        int y = headerHeight;
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            List<Cell> row = data.rows.get(rowIndex);

            x = 0;
            for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                int colWidth = (colIndex == 0) ? data.config.firstColumnWidth : data.config.columnWidth;
                Cell cell = (colIndex < row.size()) ? row.get(colIndex) : new Cell("");

                // 背景色
                Color bgColor;
                if (cell.style.backgroundColor != null) {
                    bgColor = cell.style.backgroundColor;
                } else if (data.config.alternateRowColors && rowIndex % 2 == 1) {
                    bgColor = new Color(248, 249, 250);
                } else {
                    bgColor = Color.WHITE;
                }
                g2d.setColor(bgColor);
                g2d.fillRect(x, y, colWidth, rowHeight);

                // 文字样式
                int fontStyle = Font.PLAIN;
                if (cell.style.bold) fontStyle |= Font.BOLD;
                if (cell.style.italic) fontStyle |= Font.ITALIC;
                g2d.setFont(new Font(data.config.fontName, fontStyle, cell.style.fontSize));
                g2d.setColor(cell.style.textColor);

                fm = g2d.getFontMetrics();
                String text = cell.text;
                int textWidth = fm.stringWidth(text);
                int textX;

                // 对齐方式
                if (colIndex == 0) {
                    textX = x + data.config.padding; // 第一列左对齐
                } else {
                    if (cell.style.alignment == -1) {
                        textX = x + data.config.padding;
                    } else if (cell.style.alignment == 1) {
                        textX = x + colWidth - textWidth - data.config.padding;
                    } else {
                        textX = x + (colWidth - textWidth) / 2;
                    }
                }

                int textY = y + (rowHeight + fm.getAscent()) / 2 - 2;
                g2d.drawString(text, textX, textY);

                x += colWidth;
            }

            y += rowHeight;
        }

        // 绘制边框
        if (data.config.showBorders) {
            g2d.setColor(new Color(222, 226, 230));
            g2d.setStroke(new BasicStroke(1));

            // 横线
            for (int i = 0; i <= rowCount; i++) {
                int lineY = headerHeight + (i * rowHeight);
                g2d.drawLine(0, lineY, totalWidth, lineY);
            }

            // 竖线
            x = 0;
            for (int i = 0; i <= columnCount; i++) {
                if (i > 0) {
                    x += (i == 1) ? data.config.firstColumnWidth : data.config.columnWidth;
                }
                g2d.drawLine(x, 0, x, totalHeight);
            }

            // 外边框加粗
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(173, 181, 189));
            g2d.drawRect(0, 0, totalWidth - 1, totalHeight - 1);
        }

        g2d.dispose();

        // 保存图像
        File outputFile = new File(outputPath);
        ImageIO.write(image, "PNG", outputFile);
        System.out.println("✅ 表格图片已生成: " + outputPath);
    }

    /**
     * 根据数值返回格式化样式（示例）
     */
    public static CellStyle getStyleForNumber(String value) {
        try {
            int num = Integer.parseInt(value);
            CellStyle style = new CellStyle();

            if (num == 0) {
                // 0 显示为灰色
                style.textColor(new Color(173, 181, 189));
            } else if (num >= 8) {
                // 高值：红色粗体 + 浅红背景
                style.textColor(new Color(220, 53, 69))
                        .backgroundColor(new Color(255, 235, 238))
                        .bold();
            } else if (num >= 5) {
                // 中高值：橙色 + 浅橙背景
                style.textColor(new Color(253, 126, 20))
                        .backgroundColor(new Color(255, 243, 224));
            } else if (num >= 3) {
                // 中值：黄色
                style.textColor(new Color(255, 193, 7));
            } else {
                // 低值：绿色
                style.textColor(new Color(40, 167, 69));
            }

            return style;
        } catch (NumberFormatException e) {
            return new CellStyle();
        }
    }

    /**
     * 从字符串数组创建表格数据（简化版）
     */
    public static TableData createFromStrings(
            List<String> headers,
            List<List<String>> dataRows
    ) {
        List<List<Cell>> rows = new ArrayList<>();

        for (List<String> dataRow : dataRows) {
            List<Cell> row = new ArrayList<>();
            for (int i = 0; i < dataRow.size(); i++) {
                String value = dataRow.get(i);
                // 第一列使用默认样式，其他列使用数字格式化
                CellStyle style = (i == 0) ?
                        new CellStyle().alignLeft() :
                        getStyleForNumber(value);
                row.add(new Cell(value, style));
            }
            rows.add(row);
        }

        return new TableData(headers, rows);
    }

    // ============= 使用示例 =============
    public static void main(String[] args) {
        try {
            // 方式1: 从字符串数组快速创建（推荐用于你的JSON数据）
            List<String> headers = List.of(
                    "真实原因", "15:32", "15:33", "15:34", "15:35",
                    "15:36", "15:37", "15:38", "15:39", "15:40", "15:41"
            );

            List<List<String>> dataRows = List.of(
                    List.of("调用SPA未返回报价", "0", "2", "0", "0", "0", "0", "2", "0", "0", "6"),
                    List.of("携程可订接口返回错误信息", "8", "2", "9", "6", "3", "3", "7", "9", "6", "5"),
                    List.of("卖价小于底价-sirius", "1", "3", "2", "2", "1", "0", "3", "2", "0", "4"),
                    List.of("房型满房-spa", "0", "1", "3", "2", "3", "2", "5", "1", "0", "3"),
                    List.of("标准代理商接口room节点为空-spa", "2", "1", "2", "2", "5", "3", "1", "4", "1", "3")
            );

            // 使用默认配置
            TableData data1 = createFromStrings(headers, dataRows);
            generateTableImage(data1, "table_default.png");

            // 使用自定义配置
            TableConfig config = new TableConfig()
                    .colorScheme(ColorScheme.PURPLE)
                    .showShadow(true)
                    .columnWidth(80)
                    .firstColumnWidth(300);

            TableData data2 = createFromStrings(headers, dataRows);
            data2.config = config;
            generateTableImage(data2, "table_custom.png");

            // 方式2: 完全自定义样式
            List<List<Cell>> customRows = List.of(
                    List.of(
                            new Cell("重要提示", new CellStyle()
                                    .textColor(Color.WHITE)
                                    .backgroundColor(new Color(220, 53, 69))
                                    .bold()
                                    .alignLeft()),
                            new Cell("100", new CellStyle()
                                    .textColor(new Color(220, 53, 69))
                                    .fontSize(14)
                                    .bold()),
                            new Cell("99.5%", new CellStyle()
                                    .textColor(new Color(40, 167, 69))
                                    .fontSize(12))
                    )
            );

            TableData data3 = new TableData(
                    List.of("状态", "数量", "占比"),
                    customRows
            );
            generateTableImage(data3, "table_advanced.png");

            System.out.println("\n🎉 所有表格已生成！");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}