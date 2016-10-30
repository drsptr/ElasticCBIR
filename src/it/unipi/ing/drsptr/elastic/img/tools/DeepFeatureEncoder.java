package it.unipi.ing.drsptr.elastic.img.tools;

public class DeepFeatureEncoder {
	
	public static int[] getQuantizedVector(float[] normL2features, int qFactor){
		int[] qVector = new int[normL2features.length];
		for(int i=0; i<qVector.length; i++)
			qVector[i] = (int)Math.floor(normL2features[i] * qFactor);
		
		return qVector;
	}
	
	
	
	public static String encode(float[] normL2features, int qFactor) {
		String encodedStr = new String();

		int[] qVector = DeepFeatureEncoder.getQuantizedVector(normL2features, qFactor);
		for(int i=0; i<qVector.length; i++)
			for(int j=0; j<qVector[i]; j++)
				encodedStr += "f" + (i + 1) + " ";
		
		return encodedStr;
	}
	
}
