package remotesensing.util.convert;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import remotesensing.pixels.U32Pixel;
import remotesensing.util.Conversion;
import remotesensing.util.Gradient;
import remotesensing.util.Image;
import remotesensing.util.Image.ByteOrder;
import remotesensing.util.Image.DataType;
import remotesensing.util.Image.Interleave;

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
       
        this.minValue = 0;
        this.maxValue = maxValue;

        int steps = maxValue - minValue;
        
        if (steps < 50) { 
        	
        	System.out.println("Increase steps from " + steps + " to 50");
        	
        	steps = 50;
        }
        
        this.LUT = Gradient.createGradient(Color.blue, Color.red, steps);
        
  //      this.LUT = Gradient.createMultiGradient(
   //     		new Color[]{ Color.black, 
    //    				     Color.yellow, 
     //   				     Color.red }, steps);
    
        System.out.println("Min " + minValue + " max " + maxValue);
    }
   
    public Image convert(Image input) { 
    	
    	if (input.bands != 1 || input.type != DataType.U32) {
    		throw new IllegalArgumentException("Incompatible image type!");
    	}
   
    	int pixels = input.lines * input.samples;
    	
   System.out.println("Converting image of " + pixels + " pixels"); 	
    	
    	// Create an ARGB32Image
    	ByteBuffer data = ByteBuffer.allocate(pixels*4);
    	IntBuffer idata = data.asIntBuffer();
    	
    	int [] tmp = new int[1];
    	
    	U32Pixel pixel = new U32Pixel(tmp);
    	
    	for (int i=0;i<pixels;i++) { 
    		input.getPixel(pixel, i);
    		
    		int value = tmp[0];
    		
    	 	if (value < minValue) { 
        		value = minValue;
        	} else if (value > maxValue) { 
        		value = maxValue;
        	}
        
        	// HACK
        	if (value != 0) { 
        		value = maxValue;
        	}
        	
        	int index = (int) Math.round(((value-minValue) / (double) (maxValue-minValue+1)) * LUT.length);
   
        	if (index > LUT.length-1) { 
        
        		System.out.println("Reset LUT index from " + index + " to " + (LUT.length-1));
        		
        		index = LUT.length-1;
        	}
        	
        	idata.put(i, LUT[index].getRGB());
    	}
    	
    	return new Image(data, input.lines, input.samples, 4, 
    			DataType.U8, Interleave.BIP, ByteOrder.LSF, new int [] { 0, 1, 2}, "");
    }
    
   /* 
    public int convert(int value) {
    	
    	if (value < minValue) { 
    		value = minValue;
    	} else if (value > maxValue) { 
    		value = maxValue;
    	}
    
    	// HACK
    	if (value != 0) { 
    		value = maxValue;
    	}
    	
    	int index = (int) Math.round(((value-minValue) / (double) (maxValue-minValue+1)) * LUT.length);
    
    	Color tmp = LUT[index];
    	
    //	System.out.println("value " + value + " index " + index + " rgb " 
    //			+ tmp.getRed() + " " + tmp.getBlue() + " " + tmp.getGreen());
    	
    	return tmp.getRGB();
    }*/
}
