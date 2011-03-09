package remotesensing.junk;

public class ShortImage extends Image {

    private final short [] data;
    
    public ShortImage(int lines, int samples, int bands) {
        super(lines, samples, bands);
        data = new short[lines * samples * bands];
    }
    
    @Override
    public ShortPixel getPixel(int l, int s) {
        final int index = (samples * l + s) * bands; 
        return new ShortPixel(data, index, bands);
    }
    
    public ShortPixel getPixel(int l, int s, ShortPixel p) {
        final int index = (samples * l + s) * bands; 
        System.arraycopy(data, index, p.getData(), 0, bands);
        return p;
    }

    public void setPixel(int l, int s, ShortPixel p) {
        final int index = (samples * l + s) * bands; 
        System.arraycopy(p.getData(), 0, data, index, bands);
    }
    
    @Override
    public void setPixel(int l, int s, Pixel p) {
        if (p instanceof ShortPixel) { 
            setPixel(l, s, (ShortPixel) p);
        } else { 
            throw new IllegalArgumentException("Wrong pixel type");
        }
    }

    public short[] getData() {
        return data;
    }
    
}
