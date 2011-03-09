package remotesensing.junk;

import java.util.Arrays;

public class DoublePixel extends Pixel {

    private final double [] data;
    
    public DoublePixel(int bands) {
        super(bands);
        data = new double[bands];
    }

    public DoublePixel(double [] data) {
        super(data.length);
        this.data = data;  
    }

    public DoublePixel(double [] data, int index, int bands) {
        super(data.length);
        this.data = Arrays.copyOfRange(data, index, index+bands);
    }

    public double [] getData() { 
        return data;
    }
    
    public double getData(int band) { 
        return data[band];
    }
    
}
