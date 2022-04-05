package scripts;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class searcher {
    private final String query;
    private final String input_file;
    private final String collectionFile = "./index.xml"; // 수정 필요
    private int docNum;
    private int keywordSize;

    public searcher(String input_file, String query) {
        this.input_file = input_file;
        this.query = query;
    }

    public void calcSim() throws Exception {
        System.out.println("input query = " + query);
        List<List<Double>> docIndex = new ArrayList<>();
        List<Integer> queryIndex = new ArrayList<>();
        makeDocIndexAndQueryIndex(docIndex, queryIndex);

        List<Pair_similar_docId> similarity = calculateSimilarity(docIndex, queryIndex);
        if (keywordSize == 0) {
            System.out.println("검색된 문서가 없습니다.");
            return;
        }

        HashMap<Integer, String> title = makeTitleMapById();

        for (int i = 0; i < 3; i++) {
            if (similarity.get(i).similarity != 0)
                System.out.println("문서 title = " + title.get(similarity.get(i).id) + ",  유사도 = " + Math.round(similarity.get(i).similarity * 100) / 100.0);
            else break;
        }
        System.out.println("5주차 실행완료");
    }

    private void makeDocIndexAndQueryIndex(List<List<Double>> docIndex, List<Integer> queryIndex) throws IOException, ClassNotFoundException {
        HashMap<String, String> invertedFile = openInputFile();
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true);
        keywordSize = kl.size();
        for (Keyword kwrd : kl) {
            int queryWeight = kwrd.getCnt();
            queryIndex.add(queryWeight);

            String queryWord = kwrd.getString();
            if (invertedFile.containsKey(queryWord)) {
                String[] strList = invertedFile.get(queryWord).split(" ");
                docIndex.add(extractDocTF(strList));
                docNum = strList.length / 2;
            } else keywordSize -= 1;
        }
    }

    private List<Double> extractDocTF(String[] strList) {
        List<Double> docTF = new ArrayList<>();
        for (int j = 1; j < strList.length; j += 2) {
            docTF.add(Double.parseDouble(strList[j]));
        }
        return docTF;
    }

    private HashMap<String, String> openInputFile() throws IOException, ClassNotFoundException {
        FileInputStream fileStream = new FileInputStream(input_file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        if (object == null) System.out.println("input_file error!");
        return (HashMap<String, String>) object;
    }

    private List<Pair_similar_docId> calculateSimilarity(List<List<Double>> docIndex, List<Integer> queryIndex) {
        List<Pair_similar_docId> similarity = new ArrayList<>();
        for (int i = 0; i < docNum; i++) {
            double w = 0;
            for (int k = 0; k < keywordSize; k++) {
                w += (queryIndex.get(k) * docIndex.get(k).get(i));
            }
            similarity.add(new Pair_similar_docId(w, i));
        }
        similarity.sort(new PairComparator());
        return similarity;
    }


    private HashMap<Integer, String> makeTitleMapById() throws ParserConfigurationException, IOException, SAXException {
        File collectionXml = new File(collectionFile);
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        NodeList nodeList = createNodeList(docBuilder, collectionXml);

        HashMap<Integer, String> title = new HashMap<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            String id = nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue();
            NodeList childNodes = nodeList.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); ++j) {
                if ("title".equals(childNodes.item(j).getNodeName())) {
                    title.put(Integer.valueOf(id), childNodes.item(j).getTextContent());
                    break;
                }
            }
        }
        return title;
    }

    private NodeList createNodeList(DocumentBuilder builder, File xmlFile) throws IOException, SAXException {
        org.w3c.dom.Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();
        return document.getElementsByTagName("doc");
    }

    static class Pair_similar_docId {
        Double similarity;
        Integer id;

        public Pair_similar_docId(Double similarity, Integer id) {
            this.similarity = similarity;
            this.id = id;
        }
    }

    static class PairComparator implements Comparator<Pair_similar_docId> {
        @Override
        public int compare(Pair_similar_docId p1, Pair_similar_docId p2) {
            if (p1.similarity > p2.similarity) return -1;
            else if (p1.similarity < p2.similarity) return 1;
            return 0;
        }
    }
}
