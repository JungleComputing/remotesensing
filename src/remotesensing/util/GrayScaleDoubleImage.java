package remotesensing.util;

public class GrayScaleDoubleImage extends Image {

    private float [] data;

    public GrayScaleDoubleImage(int lines, int samples) {
        super(lines, samples, 1);
        this.data = new float[lines*samples];
    }
    
    public GrayScaleDoubleImage(float [] data, int lines, int samples) {
        super(lines, samples, 1);
        this.data = data;
    }

    public void getAsRGB(int [] target, int x, int y, int w, int h, Conversion conv) { 
        
        GrayScaleDoubleToRGBConversion c = (GrayScaleDoubleToRGBConversion) conv;
        
        int dstIndex = 0;
        
        for (int j=0;j<h;j++) { 
        
            int index = (y+j) * samples + x;
            
            for (int i=0;i<w;i++) { 
                target[dstIndex++] = c.convert(data[index++]); 
            }
        }
    }
    
    public void setPixel(int line, int sample, float value) { 
        data[line * samples + sample] = value;
    }
    
}
