package Java2graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * 多维度表格图片生成器（无维度标题版）
 * 第一列超长换行，第二列居中，第三列靠右且占固定比例
 */
public class Op1GraphGenerator {

    private static final int SCALE = 3;
    private static final int PADDING = 8;
    private static final int ROW_HEIGHT = 25;
    private static final int HEADER_HEIGHT = 18;  // 减小表头高度：28 -> 24
    private static final int CORNER_RADIUS = 12;

    private static final int CHARS_PER_LINE = 12;  // 每行固定字符数
    private static final int MAX_CHARS_LIMIT = 36;
    private static final int LINE_SPACING = 2;
    private static final int ROW_VERTICAL_PADDING = 4;
    private static final float UNIFIED_FONT_SIZE = 10f;  // 统一字体大小（从15改为13）

    private static final Color HEADER_PRIMARY = new Color(139, 130, 245);
    private static final Color HEADER_SECONDARY = new Color(159, 162, 251);
    private static final Color TEXT_COLOR = new Color(17, 24, 39);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    private static Font cachedFont;

    private static Font loadFont(float size) {
        if (cachedFont == null) {
            try (InputStream is =
                         Op1GraphGenerator.class.getResourceAsStream("/fonts/NotoSansCJK-Regular.ttc")) {
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

        public Cell(String text) { this(text, TEXT_COLOR); }
        public Cell(String text, Color color) { this.text = text; this.color = color; }
    }

    public static class TableData {
        public List<String> title;
        public List<List<Cell>> rows;

        public TableData(List<String> title, List<List<Cell>> rows) {
            this.title = title;
            this.rows = rows;
        }
    }

    public static class MultiTableData {
        public TableData tableData;
        public MultiTableData(TableData tableData) { this.tableData = tableData; }
    }

    // ================= 文本处理 =================
    private static String truncateText(String text) {
        if (text.length() > MAX_CHARS_LIMIT) {
            return text.substring(0, MAX_CHARS_LIMIT) + "...";
        }
        return text;
    }

    private static List<String> wrapTextByChars(String text, int charsPerLine) {
        List<String> lines = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += charsPerLine) {
            int end = Math.min(i + charsPerLine, length);
            lines.add(text.substring(i, end));
        }
        return lines;
    }

    private static List<String> wrapText(String text, int maxWidth, FontMetrics fm) {
        List<String> lines = new ArrayList<>();
        if (fm.stringWidth(text) <= maxWidth) { lines.add(text); return lines; }
        StringBuilder currentLine = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String testLine = currentLine.toString() + c;
            if (fm.stringWidth(testLine) > maxWidth) {
                if (currentLine.length() > 0) lines.add(currentLine.toString());
                currentLine = new StringBuilder();
                currentLine.append(c);
            } else { currentLine.append(c); }
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString());
        return lines;
    }

    // ================= 计算最大表格宽度 =================
    private static int calcMaxTableWidth(List<MultiTableData> tables) {
        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = tmp.createGraphics();
        Font unifiedFont = loadFont(UNIFIED_FONT_SIZE);  // 使用统一字体
        int maxWidth = 0;

        for (MultiTableData mt : tables) {
            TableData data = mt.tableData;
            int width = 0;
            g.setFont(unifiedFont);
            FontMetrics fm = g.getFontMetrics();

            for (int i = 0; i < data.title.size(); i++) {
                int max = fm.stringWidth(data.title.get(i));
                for (List<Cell> r : data.rows) {
                    String cellText = r.get(i).text;
                    if (i == 0 && cellText.length() > CHARS_PER_LINE) {
                        // 按固定字符数计算最大宽度
                        max = Math.max(max, fm.stringWidth("测".repeat(CHARS_PER_LINE)));
                    } else { max = Math.max(max, fm.stringWidth(cellText)); }
                }
                width += max + PADDING * 2;
            }
            maxWidth = Math.max(maxWidth, width);
        }
        g.dispose();
        return maxWidth;
    }

    // ================= 单表格渲染 =================
    private static BufferedImage renderSingleTable(TableData data, int targetTableWidth) {
        int cols = data.title.size();
        int rows = data.rows.size();

        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = tmp.createGraphics();
        Font unifiedFont = loadFont(UNIFIED_FONT_SIZE);  // 使用统一字体
        g2.setFont(unifiedFont);
        FontMetrics fm = g2.getFontMetrics();
        g2.dispose();

        // ===== 列宽按比例 =====
        int[] colWidths = new int[3];
        colWidths[0] = (int)(targetTableWidth * 0.6); // 第一列60%
        colWidths[1] = (int)(targetTableWidth * 0.2); // 数量20%
        colWidths[2] = targetTableWidth - colWidths[0] - colWidths[1]; // 占比20%

        // ===== 每行高度 =====
        int[] rowHeights = new int[rows];
        for (int i = 0; i < rows; i++) {
            List<Cell> r = data.rows.get(i);
            String text = truncateText(r.get(0).text);
            if (text.length() > CHARS_PER_LINE) {
                // 按固定字符数换行
                List<String> lines = wrapTextByChars(text, CHARS_PER_LINE);
                int contentHeight = lines.size() * fm.getHeight() + (lines.size() - 1) * LINE_SPACING;
                rowHeights[i] = contentHeight + ROW_VERTICAL_PADDING * 2;
            } else { rowHeights[i] = ROW_HEIGHT; }
        }

        int tableWidth = Arrays.stream(colWidths).sum();
        int tableHeight = HEADER_HEIGHT + Arrays.stream(rowHeights).sum();
        int margin = 2;

        BufferedImage img = new BufferedImage(
                (tableWidth + margin * 2) * SCALE,
                (tableHeight + margin * 2) * SCALE,
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = img.createGraphics();
        g.scale(SCALE, SCALE);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRoundRect(margin, margin, tableWidth, tableHeight, CORNER_RADIUS, CORNER_RADIUS);

        int y = margin;
        // ===== 表头 =====
        GradientPaint gp = new GradientPaint(margin, y, HEADER_PRIMARY, margin, y + HEADER_HEIGHT, HEADER_SECONDARY);
        g.setPaint(gp);
        g.fillRect(margin, y, tableWidth, HEADER_HEIGHT);

        g.setFont(unifiedFont);  // 使用统一字体
        g.setColor(Color.WHITE);
        int x = margin;
        FontMetrics hfm = g.getFontMetrics();
        for (int i = 0; i < cols; i++) {
            String t = data.title.get(i);
            int tx = x + (colWidths[i] - hfm.stringWidth(t)) / 2;
            int ty = y + HEADER_HEIGHT / 2 + hfm.getAscent() / 2;
            g.drawString(t, tx, ty);
            x += colWidths[i];
        }

        // ===== 数据行 =====
        y += HEADER_HEIGHT;
        for (int i = 0; i < rows; i++) {
            List<Cell> r = data.rows.get(i);
            x = margin;
            for (int j = 0; j < cols; j++) {
                Cell c = r.get(j);
                int colWidth = colWidths[j];
                String displayText = (j == 0) ? truncateText(c.text) : c.text;

                g.setColor(c.color);
                g.setFont(unifiedFont);  // 使用统一字体
                FontMetrics currentFm = g.getFontMetrics();

                if (j == 0 && displayText.length() > CHARS_PER_LINE) {
                    // 按固定字符数换行
                    List<String> lines = wrapTextByChars(displayText, CHARS_PER_LINE);
                    int lineY = y + ROW_VERTICAL_PADDING + currentFm.getAscent();
                    for (String line : lines) {
                        g.drawString(line, x + PADDING, lineY);
                        lineY += currentFm.getHeight() + LINE_SPACING;
                    }
                } else {
                    int tx;
                    if (j == 0) { // 第一列靠左
                        tx = x + PADDING;
                    } else if (j == 1) { // 数量居中
                        tx = x + (colWidth - currentFm.stringWidth(displayText)) / 2;
                    } else { // 占比靠右
                        tx = x + colWidth - PADDING - currentFm.stringWidth(displayText);
                    }
                    int ty = y + rowHeights[i]/2 + currentFm.getAscent()/2 - currentFm.getDescent()/2;
                    g.drawString(displayText, tx, ty);
                }
                x += colWidth;
            }
            y += rowHeights[i];
        }

        // ===== 分隔线 & 边框 =====
        g.setColor(BORDER_COLOR);
        int lineY = margin + HEADER_HEIGHT;
        for (int i = 0; i < rows; i++) {
            lineY += rowHeights[i];
            if (i < rows - 1) g.drawLine(margin, lineY, margin + tableWidth, lineY);
        }
        g.drawRoundRect(margin, margin, tableWidth, tableHeight, CORNER_RADIUS, CORNER_RADIUS);
        g.dispose();
        return img;
    }

    // ================= 多表格合成 =================
    public static void generateMultiTableImage(List<MultiTableData> tables, String output) throws Exception {
        int gap = 0;  // 减小表格间距：20 -> 12
        int padding = 0;
        int targetWidth = calcMaxTableWidth(tables);

        List<BufferedImage> images = new ArrayList<>();
        int totalHeight = padding;
        for (MultiTableData t : tables) {
            BufferedImage img = renderSingleTable(t.tableData, targetWidth);
            images.add(img);
            totalHeight += img.getHeight() + gap;
        }
        totalHeight += padding;

        BufferedImage finalImg = new BufferedImage(images.get(0).getWidth(), totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = finalImg.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, finalImg.getWidth(), finalImg.getHeight());

        int y = padding;
        for (BufferedImage img : images) {
            g.drawImage(img, 0, y, null);
            y += img.getHeight() + gap;
        }
        g.dispose();
        ImageIO.write(finalImg, "PNG", new File(output));
    }

    // ================= 构建数据 =================
    public static TableData buildTableData(String field, List<Map<String, Object>> list) {
        List<String> title = List.of(field, "数量", "占比");
        List<List<Cell>> rows = new ArrayList<>();
        for (Map<String, Object> m : list) {
            rows.add(List.of(
                    new Cell(String.valueOf(m.get("value"))),
                    new Cell(String.valueOf(m.get("count"))),
                    new Cell(String.valueOf(m.get("rate")))
            ));
        }
        return new TableData(title, rows);
    }

    public static void main(String[] args) throws Exception {
        List<Map<String, Object>> data = new ArrayList<>();
        data.add(Map.of("value", "飞书科技有限公司sdfdf超长测试文本用来验证换行功能是否正常工作asdfasdf的测试数据", "count", 128, "rate", "32%"));
        data.add(Map.of("value", "阿里巴巴集团-技术中台", "count", 96, "rate", "24%"));

        List<Map<String, Object>> data2 = new ArrayList<>();
        data2.add(Map.of("value", "beijing", "count", 128, "rate", "32.00%"));
        data2.add(Map.of("value", "shanghai", "count", 96, "rate", "24%"));

        List<MultiTableData> tables = List.of(
                new MultiTableData(buildTableData("系统", data)),
                new MultiTableData(buildTableData("城市", data2))
        );

        generateMultiTableImage(tables, "op1_table.png");
        System.out.println("图片生成成功");
    }
}