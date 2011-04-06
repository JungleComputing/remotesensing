package remotesensing.util;

public class LookupTable {

	private long [] lut;
	
	public final long minInputValue;
	public final long maxInputValue;
	public final long inputRange;

	public final long minOutputValue;
	public final long maxOutputValue;

	public LookupTable(long [] lut, long minInputValue, long maxInputValue, long minOutputValue, long maxOutputValue) {
		super();
		this.lut = lut;
		this.minInputValue = minInputValue;
		this.maxInputValue = maxInputValue;
	
		this.inputRange = maxInputValue - minInputValue;
		
		this.minOutputValue = minOutputValue;
		this.maxOutputValue = maxOutputValue;
	}

	public long lookup(long input) {
		
		if (input < minInputValue) { 
			
			System.out.println("LUT returns min");
			
			return minOutputValue;
		}
		
		if (input > maxInputValue) { 
			
			System.out.println("LUT returns max");
			
			return maxOutputValue;
		}
		
		int index = (int) (((double)(input - minInputValue) / inputRange) * (lut.length-1));
		
		return lut[index];
	}
	
}
