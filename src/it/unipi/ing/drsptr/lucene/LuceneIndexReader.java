package it.unipi.ing.drsptr.lucene;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.FSDirectory;


public class LuceneIndexReader {

	private DirectoryReader indexReader;
	private List<String> storedFields;
	private List<String> termVectorFields;

	public LuceneIndexReader(String luceneIndexPath, List<String> storedFields, List<String> termVectorFields) throws IOException {
		indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(luceneIndexPath, "")));
		this.storedFields = storedFields;
		this.termVectorFields = termVectorFields;
	}

	public Map<String, Object> readDocument(int docId) throws IOException {
		Map<String, Object> result = new HashMap<>();
		Document document = indexReader.document(docId);

		for(int i=0; i<storedFields.size(); i++) {
			String fieldName = storedFields.get(i);
			String fieldValue = document.get(fieldName);
			result.put(fieldName, fieldValue);
		}

		for(int i=0; i<termVectorFields.size(); i++) {
			String fieldName = termVectorFields.get(i);
			Map<String, Long> termVector = new HashMap<>();
			TermsEnum termEnum = indexReader.getTermVector(docId, fieldName).iterator();
			while(termEnum.next() != null)
				termVector.put(termEnum.term().utf8ToString(), termEnum.totalTermFreq());
			result.put(fieldName, termVector);
		}

		return result;
	}

	public int getNumDocuments() {
		return indexReader.numDocs();
	}

	public static String getTextFromTermVector(Map<String, Long> termVector) {
		String result = "";

		for(String term : termVector.keySet())
			result += new String(new char[(int)(long)termVector.get(term)]).replace("\0", term + " ");

		return result;
	}
}
