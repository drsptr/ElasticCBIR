package it.unipi.ing.drsptr.elastic.utilities;

import java.util.Base64;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.search.SearchHit;


public class JsonDocParser {
	
	private static final int FROM_INDEX = 3;
	private static final int ERROR_LENGTH = 1;
	
	
	
	public static String getStringFieldValue(String jsonDoc, String fieldName) throws JsonDocParserFieldNotFoundException {
		String[] splits = jsonDoc.split(fieldName);
		String result = null;
		
		if(splits.length > ERROR_LENGTH)
			splits[1].substring(FROM_INDEX, splits[1].indexOf("\"", FROM_INDEX));
		else
			throw new JsonDocParserFieldNotFoundException();
		
		return result;
	}
	
	
	public static String getStringFieldValue(GetResponse gResponse, String fieldName) throws JsonDocParserFieldNotFoundException {
		if(!gResponse.getSource().containsKey(fieldName))
			throw  new JsonDocParserFieldNotFoundException();
		
		return gResponse.getSource().get(fieldName).toString();
	}
	
	
	public static String getStringFieldValue(SearchHit srcHit, String fieldName) throws JsonDocParserFieldNotFoundException {
		if(!srcHit.getSource().containsKey(fieldName))
				throw new JsonDocParserFieldNotFoundException();
		
		return srcHit.getSource().get(fieldName).toString();
	}
	
	
	
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
	
	
	public static byte[] getBase64EncodedBinaryField(GetResponse gResponse, String fieldName) throws JsonDocParserFieldNotFoundException {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] result = null;
		
		if(!gResponse.getSource().containsKey(fieldName))
			throw  new JsonDocParserFieldNotFoundException();
		
		result = gResponse.getSource().get(fieldName).toString().getBytes();
		
		return decoder.decode(result);
	}
	
	
	public static byte[] getBase64EncodedBinaryField(SearchHit srcHit, String fieldName) throws JsonDocParserFieldNotFoundException {
		Base64.Decoder decoder = Base64.getDecoder();
		byte[] result = null;
		
		if(!srcHit.getSource().containsKey(fieldName))
			throw  new JsonDocParserFieldNotFoundException();
		
		result = srcHit.getSource().get(fieldName).toString().getBytes();
		
		return decoder.decode(result);
	}
}
