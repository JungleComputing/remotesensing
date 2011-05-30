package remotesensing.pixels;

import remotesensing.util.Pixel;

public abstract class IntegerPixel extends Pixel {

	protected IntegerPixel(int bands) {
		super(bands);
	}

	public boolean isInteger() { 
		return true;
	}
	    
	public abstract long get(int band);	
	
	public abstract void put(long value, int band);	

    public double getAsReal(int band) { 
    	return get(band);
    }
}



