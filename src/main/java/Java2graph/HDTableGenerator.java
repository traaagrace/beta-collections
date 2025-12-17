package Java2graph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.AttributedString;

public class HDTableGenerator {

    public static void main(String[] args) throws IOException {
        int rows = 20;  // 30 行
        int cols = 6;  // 10 列
        int cellWidth = 180;
        int cellHeight = 60;

        int imgWidth = cols * cellWidth + 2;
        int imgHeight = rows * cellHeight + 2;

        BufferedImage image = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();

        // ========= 高清抗锯齿增强 ==========
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // ========= 自动选择最清晰字体 ==========
        String fontName = detectBestFont();
        Font headerFont = new Font(fontName, Font.BOLD, 24);
        Font normalFont = new Font(fontName, Font.PLAIN, 22);

        // 背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, imgWidth, imgHeight);

        // 画表格线
        g.setColor(new Color(180, 180, 180));
        for (int i = 0; i <= rows; i++) {
            g.drawLine(0, i * cellHeight, imgWidth, i * cellHeight);
        }
        for (int j = 0; j <= cols; j++) {
            g.drawLine(j * cellWidth, 0, j * cellWidth, imgHeight);
        }

        // ========= 绘制内容 ==========
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                // 第一行用加粗字体
                g.setFont(r == 0 ? headerFont : normalFont);

                String text = "R" + (r + 1) + "C" + (c + 1);

                // ———— 字体增强：黑色描边 ————
                AttributedString as = new AttributedString(text);
                as.addAttribute(TextAttribute.FONT, g.getFont());
                as.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);

                // 文字位置居中
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int x = c * cellWidth + (cellWidth - textWidth) / 2;
                int y = r * cellHeight + (cellHeight + fm.getAscent()) / 2 - 5;

                // 阴影增强（浅灰）
                g.setColor(new Color(0, 0, 0, 90));
                g.drawString(text, x + 1, y + 1);

                // 主体文字（深色）
                g.setColor(new Color(30, 30, 30));
                g.drawString(text, x, y);
            }
        }

        g.dispose();

        File file = new File("hd_table.png");
        ImageIO.write(image, "png", file);

        System.out.println("生成成功：hd_table.png");
    }

    /**
     * 自动检测最清晰的可用字体
     */
    private static String detectBestFont() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fonts = ge.getAvailableFontFamilyNames();

        String[] preferred = {
                "Microsoft YaHei",
                "Microsoft YaHei UI",
                "PingFang SC",
                "Helvetica",
                "Arial",
                "SimHei"
        };

        for (String f : preferred) {
            for (String installed : fonts) {
                if (installed.equalsIgnoreCase(f)) {
                    return installed;
                }
            }
        }
        return "SansSerif";
    }
}
