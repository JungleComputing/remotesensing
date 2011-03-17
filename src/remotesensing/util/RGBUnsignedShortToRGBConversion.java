package remotesensing.util;

public class RGBUnsignedShortToRGBConversion extends Conversion {

	private final boolean useLUT;

	private byte [] rlut;
	private byte [] glut;
	private byte [] blut;
	
	private int [] minValue;  
	// private int [] maxValue;
	private double [] scale;
	
	public RGBUnsignedShortToRGBConversion(byte [] rlut, byte [] glut, byte [] blut) { 
		this.rlut = rlut;
		this.glut = glut;
		this.blut = blut;
		useLUT = true;
	}

	public RGBUnsignedShortToRGBConversion(int [] minValue, int [] maxValue) { 
		
		this.minValue = minValue;
		// this.maxValue = maxValue;

		this.scale = new double[3];
		
		for (int i=0;i<scale.length;i++) { 
			this.scale[i] = 255.0 / (maxValue[i] - minValue[i]); 
	    }

		useLUT = false;
	} 
	
	public int convert(short r, short g, short b) {

		int rv = r & 0xffff;
		int gv = g & 0xffff;
		int bv = b & 0xffff;

		if (useLUT) { 
			return (0xff) << 24 | (rlut[rv] & 0xff) << 16 | (glut[gv] & 0xff) << 8 | (blut[bv] & 0xff);  
		} else { 
			int tmp1 = (int) ((rv - minValue[0]) * scale[0]);
			int tmp2 = (int) ((gv - minValue[1]) * scale[1]);
			int tmp3 = (int) ((bv - minValue[2]) * scale[2]);
			
			return (0xff) << 24 | (tmp1 & 0xff) << 16 | (tmp2 & 0xFF) << 8 | (tmp3 & 0xFF);
		}
	}
}
