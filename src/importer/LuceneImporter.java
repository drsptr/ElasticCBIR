package importer;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;

import it.unipi.ing.drsptr.elastic.txt.ElasticIndexManager;

public class LuceneImporter {

	public static void main(String[] args) throws IOException{
		
		DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(Parameters.LUCENE_PATH, "")));
		
		for(int i=0; i<3; i++) {
			Document doc = reader.document(i);
			String docId = doc.get(LuceneFields.ID);
			String tags = doc.get(LuceneFields.TAGS);
			String uri = doc.get(LuceneFields.URI);
			String termsVector = "";
			
			Terms imageTermVector = reader.getTermVector(i, LuceneFields.IMG);
			TermsEnum termsEnum = imageTermVector.iterator();
			while(termsEnum.next() != null) {
				String term = termsEnum.term().utf8ToString();
				long freq = termsEnum.totalTermFreq();
				termsVector += "\t" + term + ", " + freq + "\n";
			}
			
			
			System.out.println("DOCUMENT #" + i + "\n" +
								" Id:\t" + docId + "\n" +
								" Tags:\t" + tags + "\n" +
								" Uri:\t" + uri + "\n" +
								" TV :\n" + termsVector + "\n");
		}
		
		//ElasticIndexManager indexManager = new ElasticIndexManager();
	}

}
