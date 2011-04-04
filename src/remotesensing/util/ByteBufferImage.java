package remotesensing.util;

import java.nio.ByteBuffer;

public class ByteBufferImage extends Image {

	private ByteBuffer data;

	public ByteBufferImage(ByteBuffer data, 
			int lines, int samples, int bands, 
			DataType type, Interleave interleave, ByteOrder order,
			int[] defaultbands, String description) {

		super(lines, samples, bands, type, interleave, order, defaultbands, description);

		this.data = data;

		if (order == ByteOrder.LSF) { 
			data.order(java.nio.ByteOrder.LITTLE_ENDIAN);
		} else { 
			data.order(java.nio.ByteOrder.BIG_ENDIAN);
		}        
	}

	@Override
	public Object getData() { 
		// returns data as a single array of correct primitive type
		int len = samples*lines*bands;
		Object target = type.createArray(len);
		type.get(data, 0, len, target, 0, len);
		return target;
	}

	@Override
	protected Object getBandBSQ(int band) { 

		int len = samples*lines;    	
		int offset = band * len; 

		Object target = type.createArray(len);
		type.get(data, offset, len, target, 0, len);
		return target;    	
	}

	@Override
	protected Object getBandBIP(int band) {

		int len = samples*lines;    	
		int offset = band;

		Object target = type.createArray(len);

		for (int i=0;i<len;i++) { 
			type.get(data, offset, target, i);
			offset += bands;
		}

		return target;    	
	}
	
	@Override
	protected Pixel getPixelBIP(Pixel p, int index) {     
		int offset = index * bands;
		type.get(data, offset, bands, p.getData(), 0, bands);
		return p;    	
	}

	@Override
	protected Pixel getPixelBSQ(Pixel p, int index) { 

		int size = samples * bands;

		for (int i=0;i<bands;i++) { 
			type.get(data, index + i*size, p.getData(), i);
		}

		return p;    	
	}


}
