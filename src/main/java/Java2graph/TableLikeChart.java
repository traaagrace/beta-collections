package Java2graph;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TableLikeChart {

    // ===== 超清渲染倍率 =====
    private static final int SCALE = 3;

    private static final int PADDING = 8;
    private static final int ROW_HEIGHT = 25;
    private static final int HEADER_HEIGHT = 35;
    private static final int CORNER_RADIUS = 12;

    private static final Color HEADER_PRIMARY = new Color(139, 130, 245);   // 更淡的紫蓝色
    private static final Color HEADER_SECONDARY = new Color(159, 162, 251); // 更淡的蓝色
    private static final Color TEXT_COLOR = new Color(17, 24, 39);
    private static final Color ALERT_RED = new Color(220, 38, 38);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private static Font cachedFont;

    private static Font loadFont(float size) {
        if (cachedFont == null) {
            try (InputStream is =
                         TableLikeChart.class.getResourceAsStream("/fonts/NotoSansCJK-Regular.ttc")) {
                cachedFont = Font.createFont(Font.TRUETYPE_FONT, is);
            } catch (Exception e) {
                throw new RuntimeException("字体加载失败", e);
            }
        }
        return cachedFont.deriveFont(Font.BOLD, size);
    }

    // ================= 数据结构 =================

    public static class Cell {
        public String text;
        public Color color;

        public Cell(String text, Color color) {
            this.text = text;
            this.color = color;
        }

        public Cell(String text) {
            this(text, TEXT_COLOR);
        }
    }

    public static class TableData {
        public List<String> title;
        public List<List<Cell>> rows;
        public String chartTitle;

        public TableData(List<String> title, List<List<Cell>> rows, String chartTitle) {
            this.title = title;
            this.rows = rows;
            this.chartTitle = chartTitle;
        }
    }

    // ================= 图片生成 =================

    public static void generate(TableData data, String output) throws Exception {

        int cols = data.title.size();
        int rows = data.rows.size();

        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tg = tmp.createGraphics();
        Font headerFont = loadFont(16f);
        Font cellFont = loadFont(15f);
        tg.setFont(headerFont);
        FontMetrics hfm = tg.getFontMetrics();
        tg.setFont(cellFont);
        FontMetrics cfm = tg.getFontMetrics();

        int[] colWidths = new int[cols];
        for (int i = 0; i < cols; i++) {
            int max = hfm.stringWidth(data.title.get(i));
            for (List<Cell> r : data.rows) {
                max = Math.max(max, cfm.stringWidth(r.get(i).text));
            }
            colWidths[i] = max + PADDING * 2;
        }
        tg.dispose();

        int tableWidth = 0;
        for (int w : colWidths) tableWidth += w;

        int titleHeight = data.chartTitle != null ? 60 : 0;
        int tableHeight = titleHeight + HEADER_HEIGHT + rows * ROW_HEIGHT;

        int margin = 6;

        BufferedImage img = new BufferedImage(
                (tableWidth + margin * 2) * SCALE,
                (tableHeight + margin * 2) * SCALE,
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics2D g = img.createGraphics();
        g.scale(SCALE, SCALE);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // ===== 背景 =====
        g.setColor(Color.WHITE);
        g.fillRoundRect(margin, margin, tableWidth, tableHeight,
                CORNER_RADIUS, CORNER_RADIUS);

        int y = margin;

        // ===== 标题 =====
        if (data.chartTitle != null) {
            g.setFont(loadFont(20f));
            FontMetrics fm = g.getFontMetrics();
            int x = margin + (tableWidth - fm.stringWidth(data.chartTitle)) / 2;
            g.setColor(TEXT_COLOR);
            g.drawString(data.chartTitle, x, y + titleHeight / 2 + fm.getAscent() / 2);
            y += titleHeight;
        }

        // ===== 表头 =====
        GradientPaint gp = new GradientPaint(
                margin, y, HEADER_PRIMARY,
                margin, y + HEADER_HEIGHT, HEADER_SECONDARY
        );
        g.setPaint(gp);
        g.fillRect(margin, y, tableWidth, HEADER_HEIGHT);

        g.setFont(headerFont);
        g.setColor(Color.WHITE);

        int x = margin;
        for (int i = 0; i < cols; i++) {
            String t = data.title.get(i);
            int tx = x + (colWidths[i] - hfm.stringWidth(t)) / 2;
            int ty = y + HEADER_HEIGHT / 2 + hfm.getAscent() / 2;
            g.drawString(t, tx, ty);
            x += colWidths[i];
        }

        // ===== 数据行 =====
        y += HEADER_HEIGHT;
        g.setFont(cellFont);

        for (List<Cell> r : data.rows) {
            x = margin;
            for (int i = 0; i < cols; i++) {
                Cell c = r.get(i);
                g.setColor(c.color);
                FontMetrics fm = g.getFontMetrics();
                int tx = (i == 0)
                        ? x + PADDING
                        : x + (colWidths[i] - fm.stringWidth(c.text)) / 2;
                int ty = y + ROW_HEIGHT / 2 + fm.getAscent() / 2;
                g.drawString(c.text, tx, ty);
                x += colWidths[i];
            }
            y += ROW_HEIGHT;
        }

        // ===== 行分隔线 =====
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(1f));

        int lineY = margin + titleHeight + HEADER_HEIGHT;
        for (int i = 1; i < rows; i++) {
            lineY += ROW_HEIGHT;
            g.drawLine(margin, lineY, margin + tableWidth, lineY);
        }

        // ===== 列分隔线 =====
        int lineX = margin;
        for (int i = 0; i < cols - 1; i++) {
            lineX += colWidths[i];
            g.drawLine(
                    lineX,
                    margin + titleHeight,
                    lineX,
                    margin + tableHeight
            );
        }

        // ===== 外层边框 =====
        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(margin, margin, tableWidth, tableHeight,
                CORNER_RADIUS, CORNER_RADIUS);

        g.dispose();
        ImageIO.write(img, "PNG", new File(output));
    }

    public static void main(String[] args) throws Exception {

        List<String> title = List.of(
                "真实原因", "15:32", "15:33", "15:34", "15:35",
                "15:36", "15:37", "15:38", "15:39", "15:40", "15:41"
        );

        List<List<Cell>> rows = List.of(
                List.of(
                        new Cell("调用SPA未返回报价"),
                        new Cell("0"),
                        new Cell("2", ALERT_RED),
                        new Cell("0"),
                        new Cell("0"),
                        new Cell("0"),
                        new Cell("0"),
                        new Cell("2", ALERT_RED),
                        new Cell("0"),
                        new Cell("0"),
                        new Cell("6", ALERT_RED)
                ),
                List.of(
                        new Cell("华住可订校验接口失败-spa"),
                        new Cell("0"),
                        new Cell("2", ALERT_RED),
                        new Cell("0"),
                        new Cell("1"),
                        new Cell("0"),
                        new Cell("1"),
                        new Cell("0"),
                        new Cell("0"),
                        new Cell("0"),
                        new Cell("2")
                )
        );

        TableData data = new TableData(title, rows, "进订失败原因统计表");
        generate(data, "table_output_hd.png");
    }
}
