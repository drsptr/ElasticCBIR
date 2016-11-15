package it.unipi.ing.drsptr.elastic.utilities;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;

/*
 * MappingBuilder is a class with only a static method which allows to create the mapping JSON document for a given
 * index and/or type.
 * @author		Pietro De Rosa
 */
public class MappingBuilder {

/*
 * It allows to build the JSON document representing the mapping for an index and/or type.
 * @param		fields			-	the hash map containing, for each field, another hash map which stores
 * 									all the properties (key) and their corresponding values (value); for example,
 * 									<"IMG", <"store", "yes">
 * 									 		<"term_vector", "yes">,
 * 									 "TXT", <"store", "no">
 * 									 		<"term_vector", "yes">>
 * @throws		IOException if some goes wrong during the creation of the JSON document
 * @return		the JSON document representing the mapping
 */
	public static String build(Map<String, Map<String, String>> fields) throws IOException {
		XContentBuilder builder = jsonBuilder();

		builder.startObject();
			builder.field("properties");
				builder.startObject();
		
					for(String field : fields.keySet())
						builder.field(field, fields.get(field));
			
			builder.endObject();
		builder.endObject();
		
		return builder.string();
	}
}
