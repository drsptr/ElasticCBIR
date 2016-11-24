package it.unipi.ing.drsptr.elastic.img.tools;

/**
 * DeepFeatureEncoder is a class with static methods that allows to get an encoded text representing the given image,
 * starting from its deep features normalized vector. This transformation allows to search for visual similar images
 * using standard search engines.
 * @author		Pietro De Rosa
 */
public class DeepFeatureEncoder {

/**
 * This static method allows to get the quantized vector from the L2 normalized deep features vector. It simply multiply
 * each component of the vector for a quantization factor.
 * @param		normL2features	-	the L2 normalized deep features vector
 * @param		qFactor			-	the quantization factor
 * @return		the quantized vector
 */
	public static int[] getQuantizedVector(float[] normL2features, int qFactor){
		int[] qVector = new int[normL2features.length];
		for(int i=0; i<qVector.length; i++)
			qVector[i] = (int)Math.floor(normL2features[i] * qFactor);

		return qVector;
	}

/**
 * This static method returns the encoded text representing the image.
 * @param		normL2features	-	the L2 normalized deep features vector
 * @param		qFactor			-	the quantization factor
 * @return		the encoded text for the given image
 */
	public static String encode(float[] normL2features, int qFactor) {
		String encodedStr = new String();

		int[] qVector = DeepFeatureEncoder.getQuantizedVector(normL2features, qFactor);
		for(int i=0; i<qVector.length; i++)
			for(int j=0; j<qVector[i]; j++)
				encodedStr += "f" + (i + 1) + " ";
		
		return encodedStr;
	}
}
