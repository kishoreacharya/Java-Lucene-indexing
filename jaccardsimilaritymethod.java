package lab_one;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.DefaultSimilarity;

public class jaccardsimilaritymethod extends DefaultSimilarity {

    int nt;


    public float idf(long docFreq, long numDocs) {
        return 1;
    }

    
    public int getNumberTerms() {
        return nt;
    } 

    public float tf(float freq) {
        return 1.f;
    }    

    public float coord(int overlap, int maxOverlap) {
        System.out.println(this.getNumberTerms());
        float union = (this.getNumberTerms() + (maxOverlap - overlap));
        float coord = (union == 0 ? 0 : (overlap / union));
        return coord;
    }
    
    public float queryNorm(float sumOfSquaredWeights) {
        return 1.0f;
    }
    
    
    public void setNumberTerms(int numberOfDocumentTerms) {
        System.out.println(this.getNumberTerms());
        nt = numberOfDocumentTerms;
    }

}
