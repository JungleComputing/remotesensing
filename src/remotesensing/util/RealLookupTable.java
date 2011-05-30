package remotesensing.util;

public class RealLookupTable extends LookupTable {

	private long [] lut;
	
	public final double minInputValue;
	public final double maxInputValue;
	public final double inputRange;

	public final long minOutputValue;
	public final long maxOutputValue;

	public RealLookupTable(long [] lut, double minInputValue, double maxInputValue, long minOutputValue, long maxOutputValue) {
		super();
		this.lut = lut;
		this.minInputValue = minInputValue;
		this.maxInputValue = maxInputValue;
	
		this.inputRange = maxInputValue - minInputValue;
		
		this.minOutputValue = minOutputValue;
		this.maxOutputValue = maxOutputValue;
	}

	public long lookup(double input) {
		
		if (input < minInputValue) { 
			
			System.out.println("LUT returns min");
			
			return minOutputValue;
		}
		
		if (input > maxInputValue) { 
			
			System.out.println("LUT returns max");
			
			return maxOutputValue;
		}
		
		int index = (int) (((input - minInputValue) / inputRange) * (lut.length-1));
		
		return lut[index];
	}
	
	public long lookup(long input) {
		return lookup((double) input);
	}
}
