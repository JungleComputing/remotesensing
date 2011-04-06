package remotesensing.pixels;

import remotesensing.util.Pixel;

public class U64Pixel extends IntegerPixel {

	private final long [] data;

	public U64Pixel(int bands) {
		super(bands);
		this.data = new long[bands];
	}

	public U64Pixel(long [] data) {
		super(data.length);
		this.data = data;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {
		throw new RuntimeException("U64Pixel.dot NOT implemented!");
	}

	@Override
	public long get(int band) {
		
		// NOTE: this returns a signed (!) long, as there is no 
		//       way to represent unsigned longs in Java
		return data[band]; 
	}
	
	@Override
	public void put(long value, int band) {

		// NOTE: this stores a signed (!) long, as there is no 
		//       way to represent unsigned longs in Java
		data[band] = value;
	}
}
