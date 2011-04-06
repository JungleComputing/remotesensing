package remotesensing.pixels;

import java.util.Arrays;

import remotesensing.util.Pixel;

public class S16Pixel extends IntegerPixel {

	private final short [] data;
	
	public S16Pixel(int bands) {
		super(bands);
		this.data = new short[bands];
	}
	
	public S16Pixel(short [] data) {
		super(data.length);
		this.data = data;
	}

	@Override
	public Object getData() {
		return data;
	}

	@Override
	public double dot(Pixel other) {

		S16Pixel tmp = (S16Pixel) other;
		
		double dot = 0.0;
		
		for (int b=0;b<bands;b++) { 
			dot += data[b] * tmp.data[b];												
		}
		
		return dot;
	}
	
	@Override
	public String toString() { 
		StringBuilder tmp = new StringBuilder("S16[");
		
		for (int i=0;i<bands;i++) { 
			tmp.append(data[i]);
			
			if (i < bands-1) { 
				tmp.append(",");
			}
		}
		
		tmp.append("]");
		
		return tmp.toString();
	}

	@Override
	public long get(int band) {
		return data[band];
	}

	@Override
	public void put(long value, int band) {
		data[band] = (short) value;
	}
}
