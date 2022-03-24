package scripts;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 2주차 실습 코드
 * 주어진 5개의 html 문서를 전처리하여 하나의 xml 파일을 생성하세요.
 * input : data 폴더의 html 파일들
 * output : collection.xml
 */

public class makeCollection {

    private final String data_path;
    private final String output_flie = "./collection.xml";

    public makeCollection(String path) {
        this.data_path = path;
    }

    public void makeXml() throws Exception {
        File[] fileList = new File(data_path).listFiles();
        org.w3c.dom.Document doc = createCollectXmlData(fileList);
        creatFile(doc);

        System.out.println("2주차 실행완료");
    }

    private org.w3c.dom.Document createCollectXmlData(File[] fileList) throws ParserConfigurationException, IOException {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        org.w3c.dom.Document doc = docBuilder.newDocument();
        org.w3c.dom.Element docsElement = doc.createElement("docs");
        doc.appendChild(docsElement);

        int id = 0;
        for (File file : fileList) {
            if (file.isFile()) {
                Document document = Jsoup.parse(file, "UTF-8");
                String title = document.title();
                String content = document.body().text();
                makeElement(doc, docsElement, title, content, String.valueOf(id));
            }
            id += 1;
        }
        return doc;
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

    private void creatFile(org.w3c.dom.Document doc) throws TransformerException, FileNotFoundException {
        Transformer transformer = makeTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new FileOutputStream(output_flie));
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

}
