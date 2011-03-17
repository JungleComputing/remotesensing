package remotesensing.util;

public class RGBUnsignedShortImage extends Image {

    private short [][] data;

    public RGBUnsignedShortImage(int lines, int samples) {
        super(lines, samples, 3);
        this.data = new short[3][lines*samples];
    }

    public RGBUnsignedShortImage(short [][] data, int lines, int samples) {
        super(lines, samples, 3);
        this.data = data;
    }

    public void getAsRGB(int [] target, int x, int y, int w, int h, Conversion conv) { 

    	RGBUnsignedShortToRGBConversion c = (RGBUnsignedShortToRGBConversion) conv;

    	int dstIndex = 0;
        
        for (int j=0;j<h;j++) { 
        
            int index = (y+j) * samples + x;
            
            for (int i=0;i<w;i++) { 
            	target[dstIndex++] = c.convert(data[0][index], data[1][index], data[2][index]);
            	index++;
            }
        }
    }
    
    public void setPixel(int line, int sample, int band, short value) { 
        data[band][line * samples + sample] = value;
    }    
}
