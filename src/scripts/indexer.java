package scripts;

import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;

public class indexer {

    private final String data_path;
    private final String output_file = "./index.post";

    public indexer(String path) {
        this.data_path = path;
    }

    public void makeIndexPost() throws Exception {
        File indexXml = new File(data_path);
        NodeList nodeList = createNodeList(indexXml);
        HashMap<String, int[]> map = makeFrequencyMap(nodeList);
        HashMap<String, String> storeMap = makeStoreMap(map, nodeList.getLength());
        creatFile(storeMap);
        printFile();
        System.out.println("4주차 실행완료");
    }

    private NodeList createNodeList(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();
        return document.getElementsByTagName("doc");
    }

    private HashMap<String, int[]> makeFrequencyMap(NodeList nodeList) {
        HashMap<String, int[]> map = new HashMap<>();
        int totalDocNum = nodeList.getLength();
        for (int i = 0; i < nodeList.getLength(); i++) {
            int id = Integer.parseInt(nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue());

            NodeList childNodes = nodeList.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); j++) {
                if ("body".equals(childNodes.item(j).getNodeName())) {
                    String[] bodyWord = childNodes.item(j).getTextContent().split("#");
                    for (String str : bodyWord) {
                        String[] split = str.split(":");
                        String word = split[0];
                        int frequency = Integer.parseInt(split[1]);
                        if (map.containsKey(word)) {
                            int[] list = map.get(word);
                            list[id] += frequency;
                            list[totalDocNum] += 1;
                        } else {
                            int[] list = new int[totalDocNum + 1];
                            list[id] = frequency;
                            list[totalDocNum] = 1;
                            map.put(word, list);
                        }
                    }
                }
            }
        }
        return map;
    }

    private HashMap<String, String> makeStoreMap(HashMap<String, int[]> map, int totalDocNum) {
        HashMap<String, String> storeMap = new HashMap<>();
        for (String key : map.keySet()) {
            int[] list = map.get(key);
            for (int i = 0; i < totalDocNum; i++) {
                double w = list[i] * (Math.log(totalDocNum) - Math.log(list[totalDocNum]));
                w = Math.round(w * 100) / 100.0;
                if (storeMap.containsKey(key)) storeMap.put(key, storeMap.get(key) + " " + i + " " + w);
                else storeMap.put(key, i + " " + w);
            }
        }
        return storeMap;
    }

    private void creatFile(HashMap<String, String> storeMap) throws IOException {
        FileOutputStream fileStream = new FileOutputStream(output_file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
        objectOutputStream.writeObject(storeMap);
        objectOutputStream.close();
    }

    private void printFile() throws IOException, ClassNotFoundException {
        FileInputStream fileStream = new FileInputStream(output_file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileStream);

        Object object = objectInputStream.readObject();
        objectInputStream.close();

        HashMap map = (HashMap) object;

        for (String key : (Iterable<String>) map.keySet()) {
            System.out.println(key + " -> " + map.get(key));
        }
    }
}
