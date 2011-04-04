package remotesensing.util;

public class U32Pixel extends Pixel {

	private final int [] data;
	
	public U32Pixel(int bands) {
		super(bands);
		this.data = new int[bands];
	}
	
	public U32Pixel(int [] data) {
		super(data.length);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		U32Pixel tmp = (U32Pixel) other;
		
		double dot = 0.0;
		
		for (int b=0;b<bands;b++) { 
			dot += (data[b] & 0xFFFFFFFFL) * (tmp.data[b] & 0xFFFFFFFFL);												
		}
		
		return dot;
	}
}
