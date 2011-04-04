package remotesensing.util;

import java.awt.Color;

public class NumberRangeToRGBConversion extends Conversion {

	private final int minValue;  
    private final int maxValue;
   
    private final Color [] LUT;  
    
    public NumberRangeToRGBConversion(int minValue, int maxValue, Color [] LUT) {
        super();
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.LUT = LUT; 
   
        System.out.println("Min " + minValue + " max " + maxValue);
    }
    
    public NumberRangeToRGBConversion(int minValue, int maxValue) {
        super();
        this.minValue = minValue;
        this.maxValue = maxValue;

        int steps = maxValue - minValue;
        
        if (steps < 50) { 
        	steps = 50;
        }
        
        this.LUT = Gradient.createMultiGradient(
        		new Color[]{ Color.black, 
        				     Color.yellow, 
        				     Color.red }, steps);
    
        System.out.println("Min " + minValue + " max " + maxValue);
    }
    
    
    public int convert(int value) {
    
    	if (value < minValue) { 
    		value = minValue;
    	} else if (value > maxValue) { 
    		value = maxValue;
    	}
    	
    	int index = (int) Math.round(((value-minValue) / (double) (maxValue-minValue+1)) * LUT.length);
    
    	Color tmp = LUT[index];
    	
    //	System.out.println("value " + value + " index " + index + " rgb " 
    //			+ tmp.getRed() + " " + tmp.getBlue() + " " + tmp.getGreen());
    	
    	return tmp.getRGB();
    }
}
