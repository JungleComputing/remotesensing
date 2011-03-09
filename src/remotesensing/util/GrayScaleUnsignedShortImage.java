package remotesensing.util;

public class GrayScaleUnsignedShortImage extends Image {

    private short [] data;

    public GrayScaleUnsignedShortImage(int lines, int samples) {
        super(lines, samples, 1);
        this.data = new short[lines*samples];
    }

    public GrayScaleUnsignedShortImage(short [] data, int lines, int samples) {
        super(lines, samples, 1);
        this.data = data;
    }

    public void getAsRGB(int [] target, int x, int y, int w, int h, Conversion conv) { 
        
        GrayScaleUnsignedShortToRGBConversion c = (GrayScaleUnsignedShortToRGBConversion) conv;
        
        int dstIndex = 0;
        
        for (int j=0;j<h;j++) { 
        
            int index = (y+j) * samples + x;
            
            for (int i=0;i<w;i++) { 
                target[dstIndex++] = c.convert(data[index++]); 
            }
        }
    }
    
    public void setPixel(int line, int sample, short value) { 
        data[line * samples + sample] = value;
    }    
}
