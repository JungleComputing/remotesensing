package remotesensing.pixels;

import remotesensing.util.Pixel;

public abstract class RealPixel extends Pixel {

	protected RealPixel(int bands) {
		super(bands);
	}
	
	public boolean isReal() { 
		return true;
	}
	    
	public abstract double get(int band);	
	public abstract void put(double value, int band);	
	
    public double getAsReal(int band) { 
    	return get(band);
    }
}
