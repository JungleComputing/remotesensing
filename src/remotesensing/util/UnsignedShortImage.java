package remotesensing.util;

public class UnsignedShortImage extends Image {

    private short [] data;

    public UnsignedShortImage(int lines, int samples, int bands) {
        super(lines, samples, bands);
        this.data = new short[lines*samples*bands];
    }

    public UnsignedShortImage(short [] data, int lines, int samples, int bands) {
        super(lines, samples, bands);
        this.data = data;
    }

    public void getAsRGB(int [] target, int x, int y, int w, int h, Conversion conv) { 
    	// FIXME: NOT implemented!
    }
    
    public void setPixel(int line, int sample, short [] value) { 
        
    	// Assumes band sequential
    	int imagesize = lines*samples;
    	int offset = line * samples + sample;
    	
    	for (int i=0;i<bands;i++) { 
    		data[i*imagesize + offset] = value[i];
    	}
    }    

    public void getPixel(int line, int sample, short [] dest) { 
        
    	// Assumes band sequential
    	int imagesize = lines*samples;
    	int offset = line * samples + sample;
    	
    	for (int i=0;i<bands;i++) { 
    		dest[i] = data[i*imagesize + offset];
    	}
    }   
    
    public short [] getData() { 
    	return data;
    }
}
