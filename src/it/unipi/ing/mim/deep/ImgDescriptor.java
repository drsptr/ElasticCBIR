package it.unipi.ing.mim.deep;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ImgDescriptor implements Serializable, Comparable<ImgDescriptor> {

	private static final long serialVersionUID = 1L;
	
	private float[] normalizedVector; // image feature
	
	private String id; // unique id of the image (usually file name)
	
	private double dist; // used for sorting purposes
	
	public ImgDescriptor(float[] features, String id) {
		float norm2 = evaluateNorm2(features);
		this.normalizedVector = getNormalizedVector(features, norm2);
		this.id = id;
	}
	
	//////
	public ImgDescriptor(String id)
	{
		this.id = id;
	}
	
	public float[] getFeatures() {
		return normalizedVector;
	}
	
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	// compare with other friends using distances
	@Override
	public int compareTo(ImgDescriptor arg0) {
		return new Double(dist).compareTo(arg0.dist);
	}
	
	//evaluate Euclidian distance
	public double distance(ImgDescriptor desc) {
		float[] queryVector = desc.getFeatures();
		
		dist = 0;
		for (int i = 0; i < queryVector.length; i++) {
			dist += Math.pow(normalizedVector[i] - queryVector[i], 2);
		}
		dist = Math.sqrt(dist);
		
		return dist;
	}
	
	//Normalize the vector values 
	private float[] getNormalizedVector(float[] vector, float norm) {
		if (norm != 0) {
			for (int i = 0; i < vector.length; i++) {
				vector[i] = vector[i]/norm;
			}
		}
		return vector;
	}
	
	//Norm 2
	private float evaluateNorm2(float[] vector) {
		float norm2 = 0;
		for (int i = 0; i < vector.length; i++) {
			norm2 += Math.pow(vector[i], 2);
		}
		norm2 = (float) Math.sqrt(norm2);
		
		return norm2;
	}
	
	  public byte[] toBytes() throws IOException {
	      ByteArrayOutputStream baos = new ByteArrayOutputStream();
	      ObjectOutputStream os = new ObjectOutputStream(baos);
	      os.writeObject(this);
	      return baos.toByteArray();
	  }
	  
		public static ImgDescriptor fromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
	        ByteArrayInputStream fis = new ByteArrayInputStream(bytes);
	        ObjectInputStream ois = new ObjectInputStream(fis);
	        ImgDescriptor ids = null;
	        try {
	        	ids = (ImgDescriptor) ois.readObject();
	        } finally {
	        	ois.close();
	        	fis.close();
	        }
	        return ids;
		}
    
}
