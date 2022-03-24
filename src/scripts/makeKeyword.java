package scripts;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 3주차 실습 코드
 * <p>
 * kkma 형태소 분석기를 이용하여 index.xml 파일을 생성하세요.
 * <p>
 * index.xml 파일 형식은 아래와 같습니다.
 * (키워드1):(키워드1에 대한 빈도수)#(키워드2):(키워드2에 대한 빈도수)#(키워드3):(키워드3에 대한 빈도수) ...
 * e.g., 라면:13#밀가루:4#달걀:1 ...
 * <p>
 * input : collection.xml
 * output : index.xml
 */

public class makeKeyword {

    private final String input_file;
    private final String output_file = "./index.xml";

    public makeKeyword(String file) {
        this.input_file = file;
    }

    public void convertXml() throws Exception {
        File collectionXml = new File(input_file);
        org.w3c.dom.Document indexXmlData = createIndexXmlData(collectionXml);
        creatFile(indexXmlData);
        System.out.println("3주차 실행완료");
    }

    private void creatFile(org.w3c.dom.Document doc) throws TransformerException, FileNotFoundException {
        Transformer transformer = makeTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(output_file));
        transformer.transform(source, result);
    }

    private Transformer makeTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        return transformer;
    }

    private org.w3c.dom.Document createIndexXmlData(File xmlFile) throws Exception {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element docsElement = doc.createElement("docs");
        doc.appendChild(docsElement);

        NodeList nodeList = createNodeList(docBuilder, xmlFile);

        String title = "", body = "", id = "";
        for (int i = 0; i < nodeList.getLength(); i++) {
            id = nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue();

            NodeList childNodes = nodeList.item(i).getChildNodes();
            for (int j = 0; j < childNodes.getLength(); ++j) {
                if ("title".equals(childNodes.item(j).getNodeName()))
                    title = childNodes.item(j).getTextContent();
                if ("body".equals(childNodes.item(j).getNodeName()))
                    body = convertBody(childNodes.item(j).getTextContent());
            }
            makeElement(doc, docsElement, title, body, id);
        }
        return doc;
    }

    private NodeList createNodeList(DocumentBuilder builder, File xmlFile) throws IOException, SAXException {
        org.w3c.dom.Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();
        return document.getElementsByTagName("doc");
    }

    private String convertBody(String bodyContent) {
        StringBuilder body = new StringBuilder();
        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(bodyContent, true);

        for (Keyword kwrd : kl)
            body.append(kwrd.getString()).append(":").append(kwrd.getCnt()).append("#");
        return body.toString();
    }

    private void makeElement(org.w3c.dom.Document doc, org.w3c.dom.Element docsElement, String title, String content, String id) {
        org.w3c.dom.Element docElement = doc.createElement("doc");
        docsElement.appendChild(docElement);
        docElement.setAttribute("id", id);

        org.w3c.dom.Element titleElement = doc.createElement("title");
        titleElement.appendChild(doc.createTextNode(title));
        docElement.appendChild(titleElement);

        org.w3c.dom.Element bodyElement = doc.createElement("body");
        bodyElement.appendChild(doc.createTextNode(content));
        docElement.appendChild(bodyElement);
    }

}
