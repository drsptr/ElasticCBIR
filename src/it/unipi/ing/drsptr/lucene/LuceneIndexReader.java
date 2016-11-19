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

/*
 * LuceneIndexReader allows you to read the content of a Lucene index; in particular, you can read a document with all
 * its fields and reconstruct the fields which have the TV stored.
 * @author		Pietro De Rosa
 */
public class LuceneIndexReader {

	private DirectoryReader indexReader;
	private List<String> storedFields;
	private List<String> termVectorFields;





/*
 * Constructor.
 * @param		luceneIndexPath		-	the path of the Lucene index's files
 * @param		storedFields		-	the list containing all the stored fields
 * @param		termVectorFields	-	the list containing all the fields with the term vector
 * @throws		IOException if an error occurs during the reading of the index
 */
	public LuceneIndexReader(String luceneIndexPath, List<String> storedFields, List<String> termVectorFields) throws IOException {
		indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(luceneIndexPath, "")));
		this.storedFields = storedFields;
		this.termVectorFields = termVectorFields;
	}





/*
 * It allows to read a document from the index with a specific id, given as input.
 * @param		docId				-	the id of the document to read
 * @throws		IOException if the document with such id does not exist
 * @return		A map with the field name as key and the corresponding value as object
 */
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

/*
 * It returns the total number of documents stored in the index.
 * @return		the total number of documents stored in the index
 */
	public int getNumDocuments() {
		return indexReader.numDocs();
	}

/*
 * It reconstructs the text of a field, given the term vector as input. Since it does not take into account the
 * positions of the terms but only their frequency, the text may be different from the original one but the term
 * vectors are the same.
 * @param		termVector		-	the hash map representing the term vector <term, frequency>
 * @return		the reconstructed text from the term vector
 */
	public static String getTextFromTermVector(Map<String, Long> termVector) {
		String text = "";

		for(String term : termVector.keySet())
			text += new String(new char[(int)(long)termVector.get(term)]).replace("\0", term + " ");

		return text;
	}
}
