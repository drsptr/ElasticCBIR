package it.unipi.ing.drsptr.elastic.utilities;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.ClassicSimilarity;

import java.io.IOException;
import java.util.*;

/*
 * CosineSimilarity allows you to compute the cosine similarity, starting from the terms vectors.
 * Moreover, it includes other utility functions, such as L2 distance computation, norm computation etc.
 */
public class CosineSimilarity {

    public static Map<String, Float> tfidfTermMap(Terms terms, long docCount) throws IOException{
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

    public static float[] tfidfVector(Map<String, Float> tfidfTermMap, List<String> terms) {
        float[] vector = new float[terms.size()];

        for(int i=0; i<terms.size(); i++)
            vector[i] = tfidfTermMap.get(terms.get(i));

        return vector;
    }

    public static float normL2(float[] vector) {
        float norm2 = 0;
        for (int i = 0; i < vector.length; i++) {
            norm2 += Math.pow(vector[i], 2);
        }
        norm2 = (float) Math.sqrt(norm2);

        return norm2;
    }

    public static float dotProduct(float[] vect1, float[] vect2) {
        if(vect1.length != vect2.length)
            return -1f;

        float dotProd = 0;

        for(int i=0; i<vect1.length; i++)
            dotProd += vect1[i] * vect2[i];

        return dotProd;
    }

    public static float getSimilarity(Terms terms1, Terms terms2, long docCount) throws IOException {
        // Terms to HashMap
        Map<String, Float> tVect1 = tfidfTermMap(terms1, docCount), tVect2 = tfidfTermMap(terms2, docCount);

        // get the common terms from both the term vectors and sort them
        List<String> commonTerms = new ArrayList<>(tVect1.keySet());
        commonTerms.retainAll(tVect2.keySet());
        Collections.sort(commonTerms);

        // get the tfidf vectors
        float[] v1 = tfidfVector(tVect1, commonTerms);
        float[] v2 = tfidfVector(tVect2, commonTerms);

        return dotProduct(v1, v2) / (normL2(v1) * normL2(v2));
    }
}
