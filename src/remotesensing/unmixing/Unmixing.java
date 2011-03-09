package remotesensing.unmixing;

public class Unmixing {

	// Simple spectral unmixing experiment. 
	
	private final short [] src;

	private final int lines;
	private final int samples;
	private final int bands;
	private final int linesSamples;

	private final int planes;
	private final int downmix;
	
	public Unmixing(short [] src, int lines, int samples, int bands, int planes, int downmix) { 

		this.src = src;
		this.lines = lines;
		this.samples = samples;
		this.bands = bands;
		this.planes = planes;
		this.downmix = downmix;
		
		linesSamples = lines * samples;
	}
	
	public void run() { 
	
		short [] tmp = src;
		int lines = this.lines;
		int samples = this.samples;
		int samplelines = 
		
		
		if (downmix > 1) { 
		
			
			
			tmp = downmix()
		}
		
		
		
		
	}
	
}
