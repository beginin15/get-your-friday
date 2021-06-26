package com.toy.getyourfriday.jsoup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class JsoupParserTest {

    private static final String TEST_URL = "https://www.freitag.ch/en/f11";
    private static final String TEST_MODEL = "LASSIE";

    private Document doc;

    @BeforeEach
    void getDocument() {
        try {
            doc = Jsoup.connect(TEST_URL)
                    .header("accept-language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7") // 필수 헤더
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("url 응답 확인")
    void connect() throws IOException {
        assertThat(doc.title()).contains(TEST_MODEL);
    }

    @Test
    @DisplayName("제품 정보를 저장하는 컨테이너(변수)가 포함된 script 태그 가져오기")
    void getContainerScript() {
        String var = "window.variations";
        Optional<DataNode> optional = getDataByTagFromDOM(doc, "script", var);
        assertThat(optional.isPresent()).isTrue();
    }

    @Test
    @DisplayName("컨테이너 내 제품 개수 가져오기")
    void getProductInfo() throws JSONException {
        String var = "window.variations";
        String propertyName = "variations";
        Optional<DataNode> optional = getDataByTagFromDOM(doc, "script", var);
        if (optional.isPresent()) {
            String data = optional.get().getWholeData().replace(var + " = ", "");
            JSONArray arr = new JSONObject(data).getJSONArray(propertyName);
            assertThat(arr.length() > 0).isTrue();
            return;
        }
        fail("컨테이너 변수 가져오기 실패");
    }

    private Optional<DataNode> getDataByTagFromDOM(Document doc, String tag, String var) {
        return doc.getElementsByTag(tag).stream()
                .flatMap(element -> element.dataNodes().stream())
                .filter(node -> node.getWholeData().contains(var))
                .findFirst();
    }
}
