package scripts;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MidTerm {
    private final String inputFilePath;
    private final String query;
    KeywordExtractor ke;
    HashMap<String, Boolean> map = new HashMap<>();

    public MidTerm(String inputFile, String query) {
        this.inputFilePath = inputFile;
        this.query = query;
    }

    private void makeQueryMap() {
        KeywordList kl = ke.extractKeyword(query, true);

        for (Keyword kwrd : kl) {
            map.put(kwrd.getString(), true);
        }
    }

    void showSnippet() throws Exception {
        // 스니펫 = 질의어에 포함된 키워드를 가장 많이 포함하는 30음절
        // 매칭점수 = 스닛펫에 포함된 키워드 수
        // 최대 매칭 점수가 0인 문서는 출력 x, 여러개면 앞에꺼 출력
        ke = new KeywordExtractor();
        makeQueryMap();

        File collectionXml = new File(inputFilePath);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document document = docBuilder.parse(collectionXml);
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName("doc");

        String title = "", body = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList childNodes = nodeList.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); ++j) {
                if ("title".equals(childNodes.item(j).getNodeName()))
                    title = childNodes.item(j).getTextContent();
                if ("body".equals(childNodes.item(j).getNodeName()))
                    body = childNodes.item(j).getTextContent();
            }
            printResult(title, body);
        }
    }

    private void printResult(String title, String body) {
        int maxIndex = -1, maxScore = 0;
        for (int startIndex = 0; startIndex + 30 < body.length(); startIndex++) {
            String substring = body.substring(startIndex, startIndex + 30);
            KeywordList kl = ke.extractKeyword(substring, true);

            int score = 0;
            for (Keyword kwrd : kl) {
                if (map.containsKey(kwrd.getString())) score += 1;
            }
            if (score > maxScore) {
                maxIndex = startIndex;
                maxScore = score;
            }
        }

        if (maxScore != 0) {
            String substring = body.substring(maxIndex, maxIndex + 30);
            System.out.println(title + ", " + substring + ", " + maxScore);
        }
    }
}
