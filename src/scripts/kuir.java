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
            case "-s" -> {
                if (args[2].equals("-q")) {
                    String query = args[3];
                    searcher searcher = new searcher(path, query);
                    searcher.cosineSimilarity();
                } else System.out.println("Input query(-q {yourQuery})");
            }
            case "-m" -> {
                if (args[2].equals("-q")) {
                    String query = args[3];
                    MidTerm midTerm = new MidTerm(path, query);
                    midTerm.showSnippet();
                } else System.out.println("Input query(-q {yourQuery})");
            }
        }
    }
}
