package remotesensing.junk;

public abstract class Image {

    protected final int lines; 
    protected final int samples; 
    protected final int bands; 
    
    public Image(int lines, int samples, int bands) { 
        this.lines = lines; 
        this.samples = samples; 
        this.bands = bands; 
    }
    
    public int getLines() {
        return lines;
    }    
    
    public int getSamples() {
        return samples;
    }
    
    public int getBands() {
        return bands;
    }
    
    public abstract Pixel getPixel(int l, int s);
    public abstract void setPixel(int l, int s, Pixel p);

     /*   
        int index = (l * samples + s) * bands;
        
        return new Pixel(bands, type, data, index);
        
        
        
        
    }*/
}
