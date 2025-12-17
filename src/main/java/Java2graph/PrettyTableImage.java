package Java2graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PrettyTableImage {

    public static void main(String[] args) throws Exception {

        // =============================
        //  1) 表格数据（你的 30×10 数据）
        // =============================
        String[][] data = {
                {"姓名", "语文", "数学", "英语", "物理", "化学", "生物", "地理", "历史", "政治"},

                {"李明就附近拉萨地方阿斯顿", "89", "95", "88", "92", "84", "91", "93", "87", "90"},
                {"王晓东发的说法加快立法", "78", "88", "90", "85", "79", "82", "91", "86", "80"},
                {"张小峰阿斯顿发送到发", "92", "90", "87", "93", "91", "89", "94", "88", "92"},
                {"陈一凡地方阿斯顿发射点", "85", "89", "84", "90", "78", "81", "83", "79", "82"},
                {"刘海涛就拉萨地方夹反馈", "76", "82", "88", "80", "75", "79", "77", "78", "81"},
                {"赵建国阿萨德回复发动机", "93", "92", "90", "95", "94", "93", "96", "90", "91"},
                {"孙立平附近拉萨的开发", "88", "77", "85", "82", "80", "84", "83", "86", "79"},
                {"周冬雨发动机撒旦法撒旦法", "91", "89", "90", "88", "87", "92", "93", "89", "88"},
                {"高俊杰阿斯顿发射点发射点", "83", "92", "88", "86", "84", "89", "90", "87", "85"},
                {"黄继光地方哈说的合法化", "95", "88", "91", "94", "92", "93", "95", "90", "89"},

                {"韩美林阿斯顿附近阿斯顿", "86", "90", "84", "89", "81", "83", "82", "85", "87"},
                {"冯佳琪发动机拉萨的开发", "79", "85", "83", "78", "76", "80", "81", "82", "79"},
                {"姚欣然地方啊是的发送到", "88", "91", "87", "92", "90", "89", "94", "91", "90"},
                {"温嘉怡阿斯顿发射点发射点", "82", "84", "79", "83", "77", "80", "81", "78", "82"},
                {"胡子健附近阿斯顿发", "90", "88", "91", "89", "87", "92", "90", "88", "86"},
                {"毛俊生拉萨的恢复的恢复的", "77", "86", "82", "80", "79", "81", "83", "82", "78"},
                {"谷天宇发动机阿斯顿发", "92", "90", "93", "94", "91", "90", "95", "93", "92"},
                {"郭子豪开发拉萨地方发", "84", "89", "86", "87", "82", "84", "88", "85", "83"},
                {"陶思源啊上岛咖啡阿斯顿发", "88", "87", "89", "90", "85", "88", "92", "91", "89"},
                {"程少杰发动机离开的积分", "91", "92", "90", "93", "89", "87", "94", "92", "91"},

                {"丁嘉乐发的说法加快立法", "83", "80", "79", "82", "76", "77", "81", "80", "78"},
                {"林小艺阿萨德回复的开发", "89", "91", "92", "90", "87", "88", "93", "90", "89"},
                {"罗辰宇附近拉萨的恢复", "81", "84", "83", "85", "79", "82", "84", "83", "82"},
                {"曹景浩发动机啊是的发射", "93", "89", "91", "94", "92", "93", "95", "90", "91"},
                {"蒋梓轩拉萨地方发动机", "78", "82", "85", "79", "77", "80", "81", "78", "80"},
                {"魏博文阿斯顿发送到的", "86", "90", "88", "87", "85", "89", "90", "88", "87"},
                {"康士瀚发动机啊发送到", "91", "92", "90", "93", "89", "88", "94", "91", "92"},
                {"郝宇航拉萨地方阿斯顿", "84", "87", "85", "86", "82", "83", "88", "87", "85"},
                {"石浩哲发动机撒旦法", "89", "88", "91", "90", "87", "89", "92", "90", "88"},
                {"邓嘉明地方阿斯顿发射点", "92", "91", "93", "95", "94", "93", "96", "94", "92"}
        };

        int cols = data[0].length;

        // ===========================================
        // 2) 列宽自动匹配（不会再出现越界）
        // ===========================================
        int[] colWidth = new int[cols];
        colWidth[0] = 250; // 姓名列宽一些
        for (int i = 1; i < cols; i++) colWidth[i] = 120;

        // ===========================================
        // 3) 图表配置
        // ===========================================
        int baseRowHeight = 60;
        int padding = 12;

        Font headerFont = new Font("宋体", Font.BOLD, 22);
        Font normalFont = new Font("宋体", Font.PLAIN, 20);

        // ============ 先计算每一行的真实高度（自动换行） ============
        List<Integer> rowHeights = new ArrayList<>();

        BufferedImage temp = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D tg = temp.createGraphics();

        for (int r = 0; r < data.length; r++) {
            int maxHeight = baseRowHeight;

            for (int c = 0; c < cols; c++) {
                tg.setFont(r == 0 ? headerFont : normalFont);

                List<String> lines = wrapText(
                        tg, data[r][c],
                        colWidth[c] - padding * 2
                );
                int h = lines.size() * tg.getFontMetrics().getHeight() + padding * 2;

                if (h > maxHeight) maxHeight = h;
            }
            rowHeights.add(maxHeight);
        }
        tg.dispose();

        // 总尺寸
        int totalWidth = 0;
        for (int w : colWidth) totalWidth += w;
        int totalHeight = rowHeights.stream().mapToInt(Integer::intValue).sum();

        BufferedImage image = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(245, 245, 245));
        g.fillRect(0, 0, totalWidth, totalHeight);

        Color headerBg = new Color(230, 230, 230);
        Color rowBg = new Color(240, 248, 255);
        Color gridColor = new Color(200, 200, 200);

        int y = 0;

        // ============ 绘制每一行 ============
        for (int r = 0; r < data.length; r++) {

            int rowHeight = rowHeights.get(r);

            // 背景色
            if (r == 0) g.setColor(headerBg);
            else if (r % 2 == 1) g.setColor(Color.WHITE);
            else g.setColor(rowBg);

            g.fillRect(0, y, totalWidth, rowHeight);

            int x = 0;

            for (int c = 0; c < cols; c++) {

                // 边框
                g.setColor(gridColor);
                g.drawRect(x, y, colWidth[c], rowHeight);

                String text = data[r][c];

                // 字体颜色规则
                if (r == 0) {
                    g.setFont(headerFont);
                    g.setColor(Color.BLACK);
                } else if (isNumber(text) && Integer.parseInt(text) < 90) {
                    g.setFont(normalFont);
                    g.setColor(new Color(220, 20, 60)); // 红色
                } else {
                    g.setFont(normalFont);
                    g.setColor(new Color(40, 40, 40));
                }

                // 自动换行
                List<String> lines = wrapText(
                        g, text,
                        colWidth[c] - padding * 2
                );

                int lineHeight = g.getFontMetrics().getHeight();
                int startY = y + (rowHeight - lineHeight * lines.size()) / 2
                        + g.getFontMetrics().getAscent();

                // 居中绘制
                for (int i = 0; i < lines.size(); i++) {
                    int textWidth = g.getFontMetrics().stringWidth(lines.get(i));
                    int textX = x + (colWidth[c] - textWidth) / 2;
                    g.drawString(lines.get(i), textX, startY + i * lineHeight);
                }

                x += colWidth[c];
            }

            y += rowHeight;
        }

        g.dispose();

        ImageIO.write(image, "png", new File("pretty_table.png"));

        System.out.println("生成完毕：pretty_table.png");
    }

    // 自动换行
    public static List<String> wrapText(Graphics2D g, String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        for (char c : text.toCharArray()) {
            sb.append(c);
            if (g.getFontMetrics().stringWidth(sb.toString()) > maxWidth) {
                sb.deleteCharAt(sb.length() - 1);
                lines.add(sb.toString());
                sb = new StringBuilder().append(c);
            }
        }

        if (!sb.toString().isEmpty()) lines.add(sb.toString());
        return lines;
    }

    // 判断数字
    public static boolean isNumber(String str) {
        return str.matches("\\d+");
    }
}
