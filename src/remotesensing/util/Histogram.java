package remotesensing.util;

public interface Histogram {
	LookupTable generateStretchedLUT(int maxOutputValue);
}
