package remotesensing.junk;

import java.util.Arrays;

public class ShortPixel extends Pixel {

    private final short [] data;
    
    public ShortPixel(int bands) {
        super(bands);
        data = new short[bands];
    }

    public ShortPixel(short [] data) {
        super(data.length);
        this.data = data;  
    }

    public ShortPixel(short [] data, int index, int bands) {
        super(data.length);
        this.data = Arrays.copyOfRange(data, index, index+bands);
    }

    public short [] getData() { 
        return data;
    }
    
    public short getData(int band) { 
        return data[band];
    }

    public void apply(UnaryOp op) {
        op.apply(data);
    }
}
