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
    private static Map<String, Double> tfidfTermMap(Terms terms, long docCount) throws IOException {
        ClassicSimilarity similarity = new ClassicSimilarity();
        Map<String, Double> termVector = new HashMap<>();
        TermsEnum iterator = terms.iterator();
        PostingsEnum postings = null;
        double tf, idf;

        while(iterator.next() != null) {
            postings = iterator.postings(postings, PostingsEnum.FREQS);
            tf = similarity.tf(postings.freq());
            idf = similarity.idf(iterator.docFreq(), docCount);
            termVector.put(iterator.term().utf8ToString(), tf*idf);
        }

        return termVector;
    }

/**
 * Given a terms vector as a Terms object, it returns a map where, for each term in the terms vectos, there is the
 * term frequency.
 * @param		terms			-	the terms vector to process
 * @return      the following map: <term, frequency>
 */
    private static Map<String, Double> freqTermMap(Terms terms) throws IOException {
        Map<String, Double> termVector = new HashMap<>();
        TermsEnum iterator = terms.iterator();
        PostingsEnum postings = null;
        double tf, idf;

        while(iterator.next() != null) {
            postings = iterator.postings(postings, PostingsEnum.FREQS);
            termVector.put(iterator.term().utf8ToString(), (double)postings.freq());
        }

        return termVector;
    }

/**
 * Given a list of terms and the map <term, value>, it builds the corresponding double vector. Notice that if
 * a term is not contained in the map, its corresponding value is set to 0.
 * @param       termMap    -   the map <term, tf-idf weight>
 * @param       listOfTerms     -   the list of terms
 * @return      the double tf-idf vector
 */
    private static double[] toDoubleVector(Map<String, Double> termMap, List<String> listOfTerms) {
        double[] vector = new double[listOfTerms.size()];
        Set<String> keySet = termMap.keySet();
        String currentTerm;

        for(int i=0; i<listOfTerms.size(); i++){
            currentTerm = listOfTerms.get(i);
            vector[i] = (keySet.contains(currentTerm))? termMap.get(currentTerm) : 0f;
        }

        return vector;
    }

/**
 * Given a double vector, it computes the L2 norm.
 * @param       vector          -   the vector you want to compute the L2 norm
 * @ return     the L2 norm of the given vector
 */
    private static double normL2(double[] vector) {
        double norm2 = 0;
        for (int i = 0; i < vector.length; i++) {
            norm2 += Math.pow(vector[i], 2);
        }
        norm2 = (double) Math.sqrt(norm2);

        return norm2;
    }

/**
 * Given two vectors, it computes the dot product.
 * @param       vect1           -   the 1st vector
 * @param       vect2           -   the 2nd vector
 * @return      it returns the dot product
 */
    private static double dotProduct(double[] vect1, double[] vect2) {
        double dotProd = 0;

        for(int i=0; i<vect1.length; i++)
            dotProd += vect1[i] * vect2[i];

        return dotProd;
    }

/**
 * Given two terms vector as Map<String, Double>, it computes the cosine similarity.
 * @param       tVect1          -   the 1st terms vector
 * @param       tVect2          -   the 2nd terms vectorss
 * @return      the cosine similarity between the two terms vectors
 */
    private static double computeSimilarity(Map<String, Double> tVect1, Map<String, Double> tVect2) throws IOException {
        // get the union set from both the terms vectors and sort them
        List<String> unionTerms = new ArrayList<>(tVect1.keySet());
        unionTerms.addAll(tVect2.keySet());
        Collections.sort(unionTerms);

        // get the double vectors
        double[] v1 = toDoubleVector(tVect1, unionTerms);
        double[] v2 = toDoubleVector(tVect2, unionTerms);

        return dotProduct(v1, v2) / (normL2(v1) * normL2(v2));
    }

/**
 * Given two terms vector and the total number of documents in the index, it computes the cosine similarity.
 * It uses the tf-idf weight as the components of the vectors.
 * @param       terms1          -   the 1st terms vector
 * @param       terms2          -   the 2nd terms vector
 * @param       docCount        -   the total number of documents contained in the index
 * @return      the cosine similarity between the two terms vectors
 */
    public static double getTfidfSimilarity(Terms terms1, Terms terms2, long docCount) throws IOException {
        Map<String, Double> tVect1 = tfidfTermMap(terms1, docCount), tVect2 = tfidfTermMap(terms2, docCount);

        return computeSimilarity(tVect1, tVect2);
    }

/**
 * Given two terms vector and the total number of documents in the index, it computes the cosine similarity.
 * It uses the terms' frequencies as the components of the vectors.
 * @param       terms1          -   the 1st terms vector
 * @param       terms2          -   the 2nd terms vector
 * @return      the cosine similarity between the two terms vectors
 */
    public static double getSimilarity(Terms terms1, Terms terms2) throws IOException {
        Map<String, Double> tVect1 = freqTermMap(terms1), tVect2 = freqTermMap(terms2);

        return computeSimilarity(tVect1, tVect2);
    }
}
