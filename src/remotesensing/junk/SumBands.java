package remotesensing.junk;

public class SumBands {

    public double apply(short[] data, int index, int len) {
    
        double sum = 0.0;
        
        for (int i=0;i<len;i++) { 
            sum += data[index+i];  
        }
        
        return sum;
    }
}
