package utils;

import lombok.Data;

import java.util.List;

@Data
public class PromotionInfo {
    private Long promotionId;
    private String couponCode;
    private String displayName;
    private String remark;
    private List<UrlInfoList> urlInfoList;

    @Data
    public static class UrlInfoList {
        private String url;
        private String type;
    }

}