package remotesensing.util;

public abstract class Pixel {

    protected final int bands;
    
    protected Pixel(int bands) { 
        this.bands = bands;
    }
}
