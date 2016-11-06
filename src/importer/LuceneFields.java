package importer;

public class LuceneFields {
	
	public static final String ID = "ID";		// Indexed(docs), Stored
	
	public static final String IMG = "DEEP";	// Indexed(docs, freqs), term Vector, Norms
	
	public static final String TAGS = "TXT";	// Indexed(docs), Stored, term Vector, Norms
	
	public static final String URI = "URI";		// Indexed(docs), Stored, Norms
}
