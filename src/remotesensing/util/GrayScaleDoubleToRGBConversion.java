package remotesensing.util;

public class GrayScaleDoubleToRGBConversion extends Conversion {
    
    private final double minValue;  
    private final double maxValue;
    private final double scale;
    
    public GrayScaleDoubleToRGBConversion(double minValue, double maxValue) {
        super();
        this.minValue = minValue;
        this.maxValue = maxValue;
        
        this.scale = 256.0 / (maxValue - minValue); 
   
        System.out.println("Min " + minValue + " max " + maxValue + " diff " + (maxValue - minValue) +  " scale " + scale);
    }
    
    
    public int convert(double v) {
        
        int tmp = (int) ((v - minValue) * scale);
        
      //  if (tmp < 0 || tmp > 255) { 
       //     System.out.println("EEP: " + tmp);
       // }
        
        return (0xff << 24 | (tmp & 0xff) << 16 | (tmp & 0xff) << 8 | (tmp & 0xff));
    }
    
}
