package utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TestStream {
    public static void main(String[] args) {
        PromotionInfo promotionInfo = new PromotionInfo();
        PromotionInfo.UrlInfoList urlInfoList = new PromotionInfo.UrlInfoList();
        urlInfoList.setType("1");
        urlInfoList.setUrl(null);
        promotionInfo.setUrlInfoList(List.of(urlInfoList));
        String url = Optional.of(promotionInfo)
                .map(PromotionInfo::getUrlInfoList)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .filter(info -> "1" != null && "1".equals(info.getType()))
                .map(PromotionInfo.UrlInfoList::getUrl)
                .findFirst()
                .orElse("");
        System.out.println(url);
    }
}
