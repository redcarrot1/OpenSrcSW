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
    int docNum = 5; // 수정 필요

    public searcher(String input_file, String query) {
        this.query = query;
        this.input_file = input_file;
    }

    private HashMap<String, String> openInputFile() throws IOException, ClassNotFoundException {
        FileInputStream fileStream = new FileInputStream(input_file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        return (HashMap<String, String>) object;
    }

    private List<Pair> calcSim(List<List<Double>> index, List<Integer> queryIndex, int keywordNumber) {
        List<Pair> calc = new ArrayList<>();
        for (int i = 0; i < docNum; i++) {
            double w = 0;
            for (int k = 0; k < keywordNumber; k++) {
                w += (queryIndex.get(k) * index.get(k).get(i));
            }
            calc.add(new Pair(w, i));
        }
        calc.sort(new PairComparator());
        return calc;
    }

    public void search() throws Exception {
        HashMap<String, String> map = openInputFile();
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true);

        List<List<Double>> index = new ArrayList<>();
        List<Integer> queryIndex = new ArrayList<>();
        for (Keyword kwrd : kl) {
            String word = kwrd.getString();
            int weight = kwrd.getCnt();
            queryIndex.add(weight);

            String wStr = map.get(word);
            String[] strList = wStr.split(" ");
            List<Double> temp = new ArrayList<>();
            for (int j = 0; j < strList.length / 2; j++) {
                temp.add(Double.parseDouble(strList[j * 2 + 1]));
            }
            index.add(temp);
        }

        List<Pair> calc = calcSim(index, queryIndex, kl.size());
        HashMap<Integer, String> title = makeTitleMapById();

        for (int i = 0; i < 3; i++) {
            if (calc.get(i).value != 0)
                System.out.println("문서 title = " + title.get(calc.get(i).id) + ",  유사도 = " + Math.round(calc.get(i).value * 100) / 100.0);
            else break;
        }
    }

    private HashMap<Integer, String> makeTitleMapById() throws ParserConfigurationException, IOException, SAXException {
        HashMap<Integer, String> title = new HashMap<>();
        File collectionXml = new File("./collection.xml"); // 수정 필요
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element docsElement = doc.createElement("docs");
        doc.appendChild(docsElement);

        NodeList nodeList = createNodeList(docBuilder, collectionXml);

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

    static class Pair {
        Double value;
        Integer id;

        public Pair(Double value, Integer id) {
            this.value = value;
            this.id = id;
        }
    }

    static class PairComparator implements Comparator<Pair> {
        @Override
        public int compare(Pair f1, Pair f2) {
            if (f1.value > f2.value) return -1;
            else if (f1.value < f2.value) return 1;
            return 0;
        }
    }
}
