package test;

import it.unipi.ing.drsptr.lucene.LuceneIndexReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Pietro on 07/11/2016.
 */
public class ImporterTester {

    public static final String LUCENE_PATH = "C://Users//Pietro//Desktop//DeepLuceneYFCC100M_Q30_merged";

    public static final List<String> storedFields = new ArrayList<String>(){
        {
            add("ID");
            add("TXT");
            add("URI");
        }
    };

    public static final List<String> termVectorFields = new ArrayList<String>(){
        {
            add("DEEP");
        }
    };

    public static void main(String[] args) throws IOException {

        LuceneIndexReader importer = new LuceneIndexReader(LUCENE_PATH, storedFields, termVectorFields);
        Map<String, Object> result = importer.readDocument(0);
        System.out.println("DOCUMENT #0\n" + "\n" +
                            "docId:\t" + (String)result.get("ID") + "\n" +
                            "tags:\t" + (String)result.get("TXT") + "\n" +
                            "uri:\t" + (String)result.get("URI") + "\n" +
                            "Tv:\t" + "\n");

        Map<String, Long> tv = ( Map<String, Long>)result.get("DEEP");
        for(String s : tv.keySet())
            System.out.println("\t" + s + ", " + tv.get(s));

        System.out.println("\n\n" + LuceneIndexReader.getTextFromTermVector(tv));
    }
}
