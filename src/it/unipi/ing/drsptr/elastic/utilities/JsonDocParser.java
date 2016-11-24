package it.unipi.ing.drsptr.elastic.utilities;

import java.util.Base64;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHit;

/**
 * JsonDocParser is a class with static methods that allows to parse a JSON document and getting
 * the value of the fields.
 * @author		Pietro De Rosa
 */
public class JsonDocParser {
	
	private static final int FROM_INDEX = 3;
	private static final int ERROR_LENGTH = 1;
	
	



/**
 * It allows to retrieve the value of a textual field from a JSON document given as input.
 * @param		jsonDoc			-	the JSON document to parse
 * @param		fieldName		-	the name of the field you want to get
 * @throws		JsonDocParserFieldNotFoundException if the field cannot be found
 * @return		the value of the text field you want to retrieve
 */
	public static String getStringFieldValue(String jsonDoc, String fieldName) throws JsonDocParserFieldNotFoundException {
		String[] splits = jsonDoc.split(fieldName);
		String result = null;
		
		if(splits.length > ERROR_LENGTH)
			splits[1].substring(FROM_INDEX, splits[1].indexOf("\"", FROM_INDEX));
		else
			throw new JsonDocParserFieldNotFoundException();
		
		return result;
	}

/**
 * It allows to retrieve the value of a textual field from the _source field after performed a get operation.
 * @param		gResponse		-	the GetResponse object containing the JSON document of the desidered document
 * @param		fieldName		-	the name of the field you want to get
 * @throws		JsonDocParserFieldNotFoundException if the field cannot be found
 * @return		the value of the text field you want to retrieve
 */
	public static String getStringFieldValue(GetResponse gResponse, String fieldName) throws JsonDocParserFieldNotFoundException {
		if(!gResponse.getSource().containsKey(fieldName))
			throw  new JsonDocParserFieldNotFoundException();
		
		return gResponse.getSource().get(fieldName).toString();
	}
	
/**
 * It allows to retrieve the value of a textual field from a matched elements, after a search operation.
 * @param		srcHit			-	the document that matched the search
 * @param		fieldName		-	the name of the field you want to get
 * @throws		JsonDocParserFieldNotFoundException if the field cannot be found
 * @return		the value of the text field you want to retrieve
 */
	public static String getStringFieldValue(SearchHit srcHit, String fieldName) throws JsonDocParserFieldNotFoundException {
		if(!srcHit.getSource().containsKey(fieldName))
				throw new JsonDocParserFieldNotFoundException();
		
		return srcHit.getSource().get(fieldName).toString();
	}
	
	



/**
 * It allows to retrieve the value of a Base64 encoded binary field from a JSON document given as input.
 * @param		jsonDoc			-	the JSON document to parse
 * @param		fieldName		-	the name of the field you want to get
 * @throws		JsonDocParserFieldNotFoundException if the field cannot be found
 * @return		the value of the Base64 encoded binary field you want to retrieve
 */
	public static byte[] getBase64EncodedBinaryField(String jsonDoc, String fieldName) throws JsonDocParserFieldNotFoundException {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] result = null;
		
		String[] splits = jsonDoc.split(fieldName);
		
		if(splits.length > ERROR_LENGTH) {
			result = splits[1]
						.substring(FROM_INDEX, splits[1].indexOf("\"", FROM_INDEX))
						.getBytes();
		
			result = decoder.decode(result);
		}
		else
			throw new JsonDocParserFieldNotFoundException();
		
		return result;
	}
	
/**
 * It allows to retrieve the value of a Base64 encoded binary field from the _source field after performed a get operation.
 * @param		gResponse		-	the GetResponse object containing the JSON document of the desidered document
 * @param		fieldName		-	the name of the field you want to get
 * @throws		JsonDocParserFieldNotFoundException if the field cannot be found
 * @return		the value of the Base64 encoded binary field you want to retrieve
 */
	public static byte[] getBase64EncodedBinaryField(GetResponse gResponse, String fieldName) throws JsonDocParserFieldNotFoundException {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] result = null;
		
		if(!gResponse.getSource().containsKey(fieldName))
			throw  new JsonDocParserFieldNotFoundException();
		
		result = gResponse.getSource().get(fieldName).toString().getBytes();
		
		return decoder.decode(result);
	}
	
/**
 * It allows to retrieve the value of a Base64 encoded binary field from a matched elements, after a search operation.
 * @param		srcHit			-	the document that matched the search
 * @param		fieldName		-	the name of the field you want to get
 * @throws		JsonDocParserFieldNotFoundException if the field cannot be found
 * @return		the value of the Base64 encoded binary field you want to retrieve
 */
	public static byte[] getBase64EncodedBinaryField(SearchHit srcHit, String fieldName) throws JsonDocParserFieldNotFoundException {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] result = null;
		
		if(!srcHit.getSource().containsKey(fieldName))
			throw  new JsonDocParserFieldNotFoundException();
		
		result = srcHit.getSource().get(fieldName).toString().getBytes();
		
		return decoder.decode(result);
	}
}
