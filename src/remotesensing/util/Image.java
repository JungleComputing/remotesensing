package remotesensing.util;

public abstract class Image {

    protected final int lines;
    protected final int samples;
    protected final int bands;
    
    protected Image(int lines, int samples, int bands) { 
        this.lines = lines;
        this.samples = samples; 
        this.bands = bands;
    }
    
    public int getSamples() {
        return samples;
    }

    public int getLines() {
        return lines;
    }

    public int getBands() {
        return bands;
    }
    
    public abstract void getAsRGB(int [] target, int x, int y, int w, int h, Conversion c); 
}
