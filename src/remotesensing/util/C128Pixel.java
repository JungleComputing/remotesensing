package remotesensing.util;

public class C128Pixel extends Pixel {

	private final double [] data;
	
	public C128Pixel(int bands) {
		super(bands);
		this.data = new double[2*bands];
	}
	
	public C128Pixel(double [] data) {
		super(data.length/2);
		this.data = data;
	}
	
	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {
		throw new RuntimeException("C128Pixel.dot NOT implemented!");
	}
}
