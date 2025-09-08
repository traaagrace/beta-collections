package utils.java;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class Similary {
    public static void main(String[] args) {
        String text1 = "看了这么多天，我刚准备下单你给我玩涨价？？？\t\n" +
                "页面直接跳回告诉我价格波动？？\n" +
                "干什么欺负人！！几百块钱也是钱啊！！\n" +
                "谁的钱是大风刮来的！！\n" +
                "涨价我就不去了！！！\n" +
                "咽不下这口气！！！";
        String text2 = "看了这么多天，我刚准备下单去哪儿你就给我玩涨价？？？ 页面直接跳回告诉我价格波动？？ 干什么欺负人！！几百块钱也是钱啊！！ 谁的钱是大风刮来的！！ 涨价我就不去了！！！ 咽不下这口气！！！";
        System.out.println(text1.length());
        // 编辑距离相似度
        LevenshteinDistance ld = new LevenshteinDistance();
        int distance = ld.apply(text1, text2);
        double simRatio = 1.0 - (double) distance / Math.max(text1.length(), text2.length());
        System.out.println("Levenshtein 相似度: " + simRatio);

        // Jaccard 相似度（注意：是按字符，不是分词）
        JaccardSimilarity jaccard = new JaccardSimilarity();
        double jaccardSim = jaccard.apply(text1, text2);
        System.out.println("Jaccard 相似度: " + jaccardSim);
    }
}
