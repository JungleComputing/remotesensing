package remotesensing.util;

public abstract class Pixel {

    public final int bands;
    
    protected Pixel(int bands) { 
        this.bands = bands;
    }

    public boolean isInteger() { 
    	return false;
    }
    
    public boolean isReal() { 
    	return false;
    }
    
    public boolean isComplex() { 
    	return false;
    }
    
    public abstract Object getData(); 
    public abstract double dot(Pixel other);
    
    public abstract double getAsReal(int band);
    
}
