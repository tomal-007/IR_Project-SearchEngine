import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;



public class Lucene{
    public static void main(String[] args) throws IOException, ParseException {
        Analyzer analyzer = new StandardAnalyzer();
        Directory indexDirectory = FSDirectory.open(new File("indexfile").toPath());

        //Store the index in memory
        Directory directory = new RAMDirectory();
        // To store an index on disk, use this instead:
        // Directory directory = FSDirectory.open("path/to/directory");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter indexWriter = new IndexWriter(indexDirectory, config);
        String fileDirectoryPath = "/Users/sakshar5068/Desktop/CS242_Project/lucenedemo";
        File[] files = new File(fileDirectoryPath).listFiles();

        if(files!=null) {
            for (File file : files) {
                String str = FileUtils.readFileToString(file);
                String[] parts=str.split("\n");
                String body="";
                for(int i=0;i< parts.length;i++){
                    body+=parts[i]+"\n";
                }
                Document doc = new Document();
                doc.add(new TextField("title", parts[0], Field.Store.YES));
                doc.add(new TextField("content",  body, Field.Store.YES));
                indexWriter.addDocument(doc);

            }
            indexWriter.close();
        }
        /*String[] pages = {
                "Search engine is considered the most successful application of IR",
                "Collect your data and index with Lucene",
                "index your data using Hadoop and build Web interface"
        };
        for (String page: pages){
            Document doc = new Document();
            doc.add(new TextField("title", page, Field.Store.YES));
            doc.add(new TextField("content", page, Field.Store.YES));
            indexWriter.addDocument(doc);
        }*/
        indexWriter.close();

        // Now search the index:
        DirectoryReader indexReader = DirectoryReader.open(indexDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        String[] fields={"content","title"};
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer);
        Query query = parser.parse("biography non-fiction");

        System.out.println(query.toString());
        int topHitCount = 50;
        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;

        // Iterate through the results:
        for(int rank = 0; rank < hits.length; ++rank){
            Document hitDoc = indexSearcher.doc(hits[rank].doc);
            System.out.println((rank+1)+"(score:"+hits[rank].score+")-->"+hitDoc.get("content"));
            // System.out.println(indexSearcher.explain(query, hits[rank].doc));
        }
        indexReader.close();
        directory.close();
    }
}