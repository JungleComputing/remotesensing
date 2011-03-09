package remotesensing.util;

public class GrayScaleUnsignedShortToRGBConversion extends Conversion {

    private final int minValue;  
    private final int maxValue;
    private final double scale;

    private int [] lut;
    
    public GrayScaleUnsignedShortToRGBConversion(int[] histogram) {
   
        int min = -1;
        int max = -1;
        int used = 0;
      
        long minSum = -1;
        
        long [] cdf = new long[65636];
        
        long sum = 0;
        
        for (int i=0;i<histogram.length;i++) { 
            
            if (histogram[i] != 0) { 
                used++;
                
                if (min == -1) { 
                    min = i;
                }
                
                max = i;
                
                sum += histogram[i];
        
                if (minSum == -1) { 
                    minSum = sum;
                }
            }
            
            cdf[i] = sum;
        }
   
   //     System.out.println("SUM " + sum + " minsum " + minSum);
        
    //    System.exit(1);
   //     
        minValue = min;
        maxValue = max; 
        
        lut = new int[65636];
        
        for (int i=0;i<lut.length;i++) { 
        
            if (i < min) { 
                lut[i] = 0;
            } else {  
                int tmp = (int) Math.round( (255.0 * (cdf[i]-minSum)) / (sum-minSum));
            
     //           System.out.println("Value " + tmp + " " + cdf[i]);
            
                lut[i] = (tmp & 0xff) << 24 | (tmp & 0xff) << 16 | (tmp & 0xFF) << 8 | (tmp & 0xFF);
            }
        }
        
        scale = -1;
    }
    
    public GrayScaleUnsignedShortToRGBConversion(int minValue, int maxValue) {
        super();
        this.minValue = minValue;
        this.maxValue = maxValue;

        this.scale = 255.0 / (maxValue - minValue); 
        this.lut = null;
    }


    public int convert(short v) {

        int value = v & 0xffff;
        int tmp = 0;
        
        if (lut != null) { 
            return lut[value];
        } else { 
            tmp = (int) ((value - minValue) * scale);
            return (0xff) << 24 | (tmp & 0xff) << 16 | (tmp & 0xFF) << 8 | (tmp & 0xFF);
        }
    }

}
