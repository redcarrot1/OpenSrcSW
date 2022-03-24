package scripts;

public class kuir {

    public static void main(String[] args) throws Exception {

        String command = args[0];
        String path = args[1];

        switch (command) {
            case "-c" -> {
                makeCollection collection = new makeCollection(path);
                collection.makeXml();
            }
            case "-k" -> {
                makeKeyword keyword = new makeKeyword(path);
                keyword.convertXml();
            }
            case "-i" -> {
                indexer indexer = new indexer(path);
                indexer.makeIndexPost();
            }
        }

        //전체 코드 돌리기!!
        /*
        makeCollection collection = new makeCollection("./html");
        collection.makeXml();
        makeKeyword keyword = new makeKeyword("./collection.xml");
        keyword.convertXml();
        indexer indexer = new indexer("index.xml");
        indexer.makeIndexPost();
         */
    }
}
