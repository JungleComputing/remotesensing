package remotesensing.util;

public class S64Pixel extends Pixel {

	private final long [] data;
	
	public S64Pixel(int bands) {
		super(bands);
		this.data = new long[bands];
	}
	
	public S64Pixel(long [] data) {
		super(data.length);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		S64Pixel tmp = (S64Pixel) other;
		
		double dot = 0.0;
		
		for (int b=0;b<bands;b++) { 
			dot += data[b] * tmp.data[b];												
		}
		
		return dot;
	}
}