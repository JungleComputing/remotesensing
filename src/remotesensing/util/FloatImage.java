package remotesensing.util;

public class FloatImage extends Image {

    private float [] data;

    public FloatImage(int lines, int samples, int bands) {
        super(lines, samples, bands);
        this.data = new float[lines*samples*bands];
    }

    public FloatImage(float [] data, int lines, int samples, int bands) {
        super(lines, samples, bands);
        this.data = data;
    }

    public void getAsRGB(int [] target, int x, int y, int w, int h, Conversion conv) { 
    	// FIXME: NOT implemented!
    }
    
    public void setPixel(int line, int sample, float [] value) { 
        
    	// Assumes band sequential
    	int imagesize = lines*samples;
    	int offset = line * samples + sample;
    	
    	for (int i=0;i<bands;i++) { 
    		data[i*imagesize + offset] = value[i];
    	}
    }    

    public void getPixel(int line, int sample, float [] dest) { 
        
    	// Assumes band sequential
    	int imagesize = lines*samples;
    	int offset = line * samples + sample;
    	
    	for (int i=0;i<bands;i++) { 
    		dest[i] = data[i*imagesize + offset];
    	}
    }    
}
