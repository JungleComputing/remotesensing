package remotesensing.junk;

public class DoubleImage extends Image {

    private final double [] data;
    
    public DoubleImage(int lines, int samples, int bands) {
        super(lines, samples, bands);
        data = new double[lines * samples * bands];
    }
    
    @Override
    public DoublePixel getPixel(int l, int s) {
        final int index = (samples * l + s) * bands; 
        return new DoublePixel(data, index, bands);
    }

    public void setPixel(int l, int s, DoublePixel p) {
        final int index = (samples * l + s) * bands;
        System.arraycopy(p.getData(), 0, data, index, bands);
    }

    @Override
    public void setPixel(int l, int s, Pixel p) {
        if (p instanceof DoublePixel) { 
            setPixel(l, s, (DoublePixel) p);            
        } else { 
            throw new IllegalArgumentException("Wrong pixel type!");
        }
    }

    public double [] getData() {
        return data;
    }

}
