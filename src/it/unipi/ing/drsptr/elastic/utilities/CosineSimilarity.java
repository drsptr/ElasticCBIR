package it.unipi.ing.drsptr.elastic.utilities;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.ClassicSimilarity;

import java.io.IOException;
import java.util.*;

/**
 * CosineSimilarity allows you to compute the cosine similarity, starting from the terms vectors.
 * @author		Pietro De Rosa
 */
public class CosineSimilarity {

/**
 * Given a terms vector as a Terms object, it returns a map where, for each term in the terms vectos, there is the
 * tf-idf weight.
 * @param		terms			-	the terms vector to process
 * @param       docCount        -   the total number of documents in the index; it is needed to compute the idf
 * * @return      the following map: <term, tf-idf weight>
 */
    private static Map<String, Float> tfidfTermMap(Terms terms, long docCount) throws IOException{
        ClassicSimilarity similarity = new ClassicSimilarity();
        Map<String, Float> termVector = new HashMap<>();
        TermsEnum iterator = terms.iterator();
        PostingsEnum postings = null;
        float tf, idf;

        while(iterator.next() != null) {
            postings = iterator.postings(postings, PostingsEnum.FREQS);
            tf = similarity.tf(postings.freq());
            idf = similarity.idf(iterator.docFreq(), docCount);
            termVector.put(iterator.term().utf8ToString(), tf*idf);
        }

        return termVector;
    }

/**
 * Given a list of terms and the map <term, tf-idf weight>, it builds the corresponding float vector. Notice that if
 * a term is not contained in the map, its corresponding tf-idf weight is set to 0.
 * @param       tfidfTermMap    -   the map <term, tf-idf weight>
 * @param       listOfTerms     -   the list of terms
 * @return      the float tf-idf vector
 */
    private static float[] tfidfVector(Map<String, Float> tfidfTermMap, List<String> listOfTerms) {
        float[] vector = new float[listOfTerms.size()];
        Set<String> keySet = tfidfTermMap.keySet();
        String currentTerm;

        for(int i=0; i<listOfTerms.size(); i++){
            currentTerm = listOfTerms.get(i);
            vector[i] = (keySet.contains(currentTerm))? tfidfTermMap.get(currentTerm) : 0f;
        }

        return vector;
    }

/**
 * Given a float vector, it computes the L2 norm.
 * @param       vector          -   the vector you want to compute the L2 norm
 * @ return     the L2 norm of the given vector
 */
    private static float normL2(float[] vector) {
        float norm2 = 0;
        for (int i = 0; i < vector.length; i++) {
            norm2 += Math.pow(vector[i], 2);
        }
        norm2 = (float) Math.sqrt(norm2);

        return norm2;
    }

/**
 * Given two vectors, it computes the dot product.
 * @param       vect1           -   the 1st vector
 * @param       vect2           -   the 2nd vector
 * @return      it returns the dot product
 */
    private static float dotProduct(float[] vect1, float[] vect2) {
        float dotProd = 0;

        for(int i=0; i<vect1.length; i++)
            dotProd += vect1[i] * vect2[i];

        return dotProd;
    }

/**
 * Given two terms vector and the total number of documents in the index, it computes the cosine similarity.
 * It uses the tf-idf weight as components of the vectors.
 * @param       terms1          -   the 1st terms vector
 * @param       terms2          -   the 2nd terms vector
 * @param       docCount        -   the total number of documents contained in the index
 * @return      the cosine similarity between the two terms vectors
 */
    public static float getSimilarity(Terms terms1, Terms terms2, long docCount) throws IOException {
        // Terms to HashMap
        Map<String, Float> tVect1 = tfidfTermMap(terms1, docCount), tVect2 = tfidfTermMap(terms2, docCount);

        // get the union set from both the terms vectors and sort them
        List<String> unionTerms = new ArrayList<>(tVect1.keySet());
        unionTerms.addAll(tVect2.keySet());
        Collections.sort(unionTerms);

        // get the tfidf vectors
        float[] v1 = tfidfVector(tVect1, unionTerms);
        float[] v2 = tfidfVector(tVect2, unionTerms);

        return dotProduct(v1, v2) / (normL2(v1) * normL2(v2));
    }
}
