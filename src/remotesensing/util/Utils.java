package remotesensing.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.BitSet;
import java.util.List;
import java.util.StringTokenizer;

import remotesensing.pixels.F32Pixel;
import remotesensing.pixels.IntegerPixel;
import remotesensing.pixels.RealPixel;
import remotesensing.ppi.PPI.Score;
import remotesensing.util.Image.ByteOrder;
import remotesensing.util.Image.DataType;
import remotesensing.util.Image.Interleave;

public class Utils {
/*
	private static class FileInfo { 
		int lines;
		int samples;
		int bands;
		int datatype;
		int byteorder;
		String interleave;
	}

	private static FileInfo readHeader(File header) throws IOException { 

		System.out.println("Reading header file " + header);

		FileInfo info = new FileInfo();

		BufferedReader r = new BufferedReader(new FileReader(header));

		String l = r.readLine();

		while (l != null) { 

			if (l.startsWith("samples")) { 
				info.samples = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
			} else if (l.startsWith("lines")) {
				info.lines = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
			} else if (l.startsWith("bands")) {
				info.bands = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
			} else if (l.startsWith("interleave")) { 
				info.interleave = l.substring(l.indexOf("=") + 1).trim();
			} else if (l.startsWith("data type")) { 
				info.datatype = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
			} else if (l.startsWith("byte order")) { 
				info.byteorder = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
			}

			l = r.readLine();
		}

		r.close();

		if (info.samples <= 0 || info.lines <= 0 || info.bands <= 0) { 
			throw new IOException("Incorrect header file");
		}

		System.out.println("Lines " + info.lines);
		System.out.println("Sample " + info.samples);
		System.out.println("Bands " + info.bands);
		System.out.println("Interleave " + info.interleave);
		System.out.println("Data type " + info.datatype);
		System.out.println("Byte order" + info.byteorder); 

		return info;
	}
*/
	public static Image readENVI(String header, String data) throws Exception {
		return readENVI(new File(header), new File(data));
	}
	
/*
	private static Image readENVIDataBSQ(File data, FileInfo info) throws IOException { 
		throw new IOException("NOT implemented!");
	}

	private static void readBandBip(File data, int band, short [] target, int pixels, int bands, int byteorder) throws IOException {

    	System.out.println("VERY INEFFICIENT! Converting band " + band + " from BIP to BSQ");

        BufferedInputStream bis = 
            new BufferedInputStream(new FileInputStream(data));

        int skip = band*2;
        
        while (skip > 0) { 
        	skip -= bis.skip(skip);
        }
        
        int pos = band*pixels;
        int index = 0;
        
        while (index < pixels) { 

        	// Data is read big-endian
            int a = bis.read();
            int b = bis.read();

            if (byteorder == 0) { 
            	// Little endian 
            	target[pos++] = (short) ((b << 8) | (a & 0xff));
            } else { 
            	// Big endian
            	target[pos++] = (short) ((a << 8) | (b & 0xff));
            }
            
            skip = (bands-1)*2;
            
            while (skip > 0) { 
            	skip -= bis.skip(skip);
            }
            
            index++;
        }

        bis.close();        
    }
	
	private static Image readENVIDataBIP(File data, FileInfo info) throws IOException {

		// FIXME: HACK!
		if (info.datatype != 2) { 
			throw new IOException("Unsupported datatype!");
		}		

		final int lines = info.lines;
		final int samples = info.samples;
		final int bands = info.bands;

		final long bandsize = lines*samples*2;
		final long totalsize = bandsize * bands;

		System.out.println("Reading data file of " + totalsize + " bytes");
		System.out.println("Each band contains " + bandsize + " bytes");

		short [] tmp = new short[(int)totalsize];

		UnsignedShortImage image = new UnsignedShortImage(tmp, lines, samples, bands);

		for (int i=0;i<bands;i++) { 
			readBandBip(data, i, tmp, lines*samples, bands, info.byteorder);
		}

		return image; 
	}

	private static Image readENVIDataBIL(File data, FileInfo info) throws IOException {
		throw new IOException("NOT implemented!");
	}

	private static Image readENVIData(File data, FileInfo info) throws IOException {

		if (info.interleave.equalsIgnoreCase("bsq")) { 
			return readENVIDataBSQ(data, info);
		} else if (info.interleave.equalsIgnoreCase("bip")) {
			return readENVIDataBIP(data, info);
		} else if (info.interleave.equalsIgnoreCase("bil")) {
			return readENVIDataBIL(data, info);
		} else { 
			throw new IOException("Unsupported interleave format: " + info.interleave);
		}
	}
*/
	public static Image readENVI(File header, File data) throws Exception {

		System.out.println("Reading header file " + header);

		int samples = -1;
		int lines = -1;
		int bands = -1;
		
		// Default values....
		Image.ByteOrder order = ByteOrder.MSF; 
		Image.Interleave interleave = Interleave.BSQ;
		Image.DataType type = DataType.S8;
		
		BufferedReader r = new BufferedReader(new FileReader(header));

		String l = r.readLine();

		while (l != null) { 

			if (l.startsWith("samples")) { 
				samples = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
			} else if (l.startsWith("lines")) {
				lines = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
			} else if (l.startsWith("bands")) {
				bands = Integer.parseInt(l.substring(l.indexOf("=") + 1).trim());
			} else if (l.startsWith("interleave")) { 
				interleave = Interleave.convert(l.substring(l.indexOf("=") + 1).trim());
			} else if (l.startsWith("data type")) { 
				type = DataType.convert(Integer.parseInt(l.substring(l.indexOf("=") + 1).trim()));
			} else if (l.startsWith("byte order")) { 
				order = ByteOrder.convert(Integer.parseInt(l.substring(l.indexOf("=") + 1).trim()));
			}

			l = r.readLine();
		}

		r.close();

		if (samples <= 0 || lines <= 0 || bands <= 0) { 
			throw new IOException("Incorrect header file");
		}

		System.out.println("Lines " + lines);
		System.out.println("Sample " + samples);
		System.out.println("Bands " + bands);
		System.out.println("Interleave " + interleave);
		System.out.println("Data type " + type);
		System.out.println("Byte order " + order); 

		FileInputStream fin = new FileInputStream(data);

		System.out.println("Size " + data.length()); 

		MappedByteBuffer buffer = fin.getChannel().map(MapMode.READ_ONLY, 0, data.length());
		
		return new Image(buffer, lines, samples, bands, type, interleave, order, new int[] { 0, 0, 0 }, "empty");
	}

	public static int countBands(boolean [] bands) throws IOException {
		
		int count = 0;
		
		for (int i=0;i<bands.length;i++) {
			if (bands[i]) { 
				count++;
			}
		}
		
		return count;
	}

	private static void writeENVIDataBSQ(FileOutputStream out, Image img, BitSet bands) throws Exception {
	
		FileChannel fout = out.getChannel();
		
		if (bands == null || bands.cardinality() == img.bands) { 
			// write all!
			fout.write(img.getDataReadOnly());
		} else { 
			
			final int bandsize = img.lines * img.lines * img.type.bytes; 
			
			for (int i=0;i<img.bands;i++) { 
				if (bands.get(i)) { 
					fout.write(img.getDataReadOnly(i*bandsize, bandsize));
				}
			}
		}
	}

	private static void writeENVIDataBIP(FileOutputStream out, Image img, BitSet bands) throws Exception {

		if (bands == null || bands.cardinality() == img.bands) { 

			// write all!
			FileChannel fout = out.getChannel();
			fout.write(img.getDataReadOnly());

		} else { 
			
			BufferedOutputStream bout = new BufferedOutputStream(out);
		
			// Write on a line-by-line basis		
			int datasize = img.type.bytes;		
			int size = img.samples * img.bands * datasize;
		
			byte [] line = new byte[size];

			for (int i=0;i<img.lines;i++) {
				img.getRawData(i*size, line, 0, size);	
		
				int off = 0;
				
				// SLOW!
				for (int p=0;p<img.samples;p++) { 
					for (int b=0;b<img.bands;b++) {
						
						if (bands.get(b)) { 
							bout.write(line, off, datasize);
						}						
						off+= datasize;
					}					
				}
			} 
			
			bout.flush();
		}
	}
	
	private static void writeENVIDataBIL(FileOutputStream out, Image img, BitSet bands) throws Exception {
		throw new Exception("writeENVIDataBIL NOT implemented!");
	}
	
	private static void writeENVIData(File data, Image img, BitSet bands) throws Exception {
		
		FileOutputStream out = new FileOutputStream(data);
		
		switch (img.interleave) { 
		case BSQ:
			writeENVIDataBSQ(out, img, bands);
			break;
		case BIP:
			writeENVIDataBIP(out, img, bands);
			break;
		case BIL:
			writeENVIDataBIL(out, img, bands);
			break;
		default: 
			out.close();
			throw new IOException("Cannot write ENVI data: unknown interleave");
		}
		
		out.close();
	}
	
	private static void writeENVIHeader(File header, Image img, BitSet bands) throws IOException {

		BufferedWriter out = new BufferedWriter(new FileWriter(header));
		
		out.write("samples = " + img.samples + "\n");
		out.write("lines = " + img.lines + "\n");
		out.write("bands = " + bands.cardinality() + "\n");
		out.write("interleave = " + img.interleave.name() + "\n");
		out.write("byte order = " + img.order.number + "\n");
		out.write("data type = " + img.type.number + "\n");
	
		out.close();
	}
	
	public static void writeENVI(String header, String data, Image img) throws Exception {
		
		BitSet set = new BitSet(img.bands);
		set.set(0, img.bands, true);
		
		writeENVI(new File(header), new File(data), img, set);
	}
	
	public static void writeENVI(String header, String data, Image img, BitSet bands) throws Exception {
		writeENVI(new File(header), new File(data), img, bands);
	}
	
	public static void writeENVI(File header, File data, Image img, BitSet bands) throws Exception {
		writeENVIHeader(header, img, bands);
		writeENVIData(data, img, bands);
	}
	
	public static byte [] generateLUT(int[] histogram) {

		int min = -1;
		int used = 0;

		long minSum = -1;

		long [] cdf = new long[65636];

		long sum = 0;

		for (int i=0;i<histogram.length;i++) { 

			if (histogram[i] != 0) { 
				used++;

				if (min == -1) { 
					min = i;
				}

				sum += histogram[i];

				if (minSum == -1) { 
					minSum = sum;
				}
			}

			cdf[i] = sum;
		}

		byte [] lut = new byte[65636];

		for (int i=0;i<lut.length;i++) { 

			if (i < min) { 
				lut[i] = 0;
			} else {  
				int tmp = (int) Math.round( (255.0 * (cdf[i]-minSum)) / (sum-minSum));
				lut[i] = (byte)(tmp & 0xff);
			}
		}

		return lut;
	}

	public static int getMax(int[] histogram) {
		for (int i=histogram.length-1;i>=0;i--) { 
			if (histogram[i] != 0) {
				return i;
			}
		}

		return 0;
	}

	public static int getMin(int[] histogram) {

		for (int i=0;i<histogram.length;i++) { 
			if (histogram[i] != 0) {
				return i;
			}
		}

		return histogram.length-1;
	}
	
	public static Histogram generateHistogram(Image img, int buckets) throws Exception { 
		return generateHistogram(img, buckets, false);
	}

	private static Histogram generateIntegerHistogram(Image img, int bins, boolean variableRange) throws Exception { 
				
		long maxValue; 
		long minValue; 

		long pixels = img.lines * img.samples;

		IntegerPixel p = (IntegerPixel) img.type.createPixel(1);

		if (variableRange) { 

			// NOTE: inverted so max/min works.
			maxValue = img.type.minIntegerValue;
			minValue = img.type.maxIntegerValue;
			
			for (int i=0;i<pixels;i++) { 
				
				img.getPixel(p, i);		
			
				long v = p.get(0);			
				
				if (v > maxValue) { 
					maxValue = v;
				}
				
				if (v < minValue) { 
					minValue = v;
				}
			} 
			
		} else { 
			maxValue = img.type.maxIntegerValue;
			minValue = img.type.minIntegerValue;
		}
		
		long range = maxValue - minValue;

		if (bins > range) {
			bins = (int) range;
		}		
		
		long [] hist = new long[bins]; 
		
		int smallestUsedBin = bins;
		int largestUsedBin = 0;
		
		long smallestBinValue = pixels;
		long largestBinValue = 0;
		
		for (int i=0;i<pixels;i++) { 
			
			img.getPixel(p, i);		
			
			long v = p.get(0);			
			
			double tmp = ((double)(v - minValue)) / range;			
			int bin = (int) (tmp * (bins-1));
			
			hist[bin]++;
			
			if (bin < smallestUsedBin) { 
				smallestUsedBin = bin;
			}
			
			if (bin > largestUsedBin) { 
				largestUsedBin = bin;
			}
			
			if (hist[bin] < smallestBinValue) { 
				smallestBinValue = bin;
			}
			
			if (hist[bin] > largestBinValue) { 
				largestBinValue = bin;
			}
		}
		/*
		for (int i=0;i<hist.length;i++) { 
			if (hist[i] != 0) { 
				System.out.println(i + " " + hist[i]);
			}
		}*/
		
		
		return new IntegerHistogram(hist, minValue, maxValue, smallestUsedBin, largestUsedBin, smallestBinValue, largestBinValue);		
	}
	
	private static Histogram generateRealHistogram(Image img, int bins) throws Exception { 

		double maxValue; 
		double minValue; 

		long pixels = img.lines * img.samples;

		RealPixel p = (RealPixel) img.type.createPixel(1);

		// NOTE: inverted so max/min works.
		maxValue = img.type.minRealValue;
		minValue = img.type.maxRealValue;
			
		for (int i=0;i<pixels;i++) { 
				
			img.getPixel(p, i);		

			double v = p.get(0);			

			if (v > maxValue) { 
				maxValue = v;
			}

			if (v < minValue) { 
				minValue = v;
			}
		} 
		
		double range = maxValue - minValue;
		
		long [] hist = new long[bins]; 
		
		int smallestUsedBin = bins;
		int largestUsedBin = 0;
		
		long smallestBinValue = pixels;
		long largestBinValue = 0;
		
		for (int i=0;i<pixels;i++) { 
			
			img.getPixel(p, i);		
			
			double v = p.get(0);			
			
			double tmp = (v - minValue) / range;			
			int bin = (int) (tmp * (bins-1));
			
			hist[bin]++;
			
			if (bin < smallestUsedBin) { 
				smallestUsedBin = bin;
			}
			
			if (bin > largestUsedBin) { 
				largestUsedBin = bin;
			}
			
			if (hist[bin] < smallestBinValue) { 
				smallestBinValue = bin;
			}
			
			if (hist[bin] > largestBinValue) { 
				largestBinValue = bin;
			}
		}
		
		return new RealHistogram(hist, minValue, maxValue, smallestUsedBin, largestUsedBin, smallestBinValue, largestBinValue);		
	}
	
	
	public static Histogram generateHistogram(Image img, int bins, boolean variableRange) throws Exception { 
	
		if (img.type.complex || img.type == DataType.U64 || img.type == DataType.S64) { 
			throw new Exception("Unsupported image type!");
		}

		if (img.type.real) { 
			return generateRealHistogram(img, bins);
		} else { 
			return generateIntegerHistogram(img, bins, variableRange);
		}
	}
	
	public static Histogram generateHistogram(List<Score> scores, int bins) throws Exception { 

		// Determine range
		int maxValue = Integer.MIN_VALUE; 
		int minValue = Integer.MAX_VALUE;
		
		long sum = 0;
		
		for (Score s : scores) { 
			if (s.count > maxValue) { 
				maxValue = s.count;
			}
			
			if (s.count < minValue) { 
				minValue = s.count;
			}
			
			sum += s.count;
		}
				
		long range = maxValue - minValue;

		if (bins > range) {
			bins = (int) range;
		}		
		
		long [] hist = new long[bins]; 
		
		int smallestUsedBin = bins;
		int largestUsedBin = 0;
		
		long smallestBinValue = sum;
		long largestBinValue = 0;
		
		for (Score s : scores) { 
			
			double tmp = ((double)(s.count - minValue)) / range;			
			int bin = (int) (tmp * (bins-1));
			
			hist[bin]++;
			
			if (bin < smallestUsedBin) { 
				smallestUsedBin = bin;
			}
			
			if (bin > largestUsedBin) { 
				largestUsedBin = bin;
			}
			
			if (hist[bin] < smallestBinValue) { 
				smallestBinValue = bin;
			}
			
			if (hist[bin] > largestBinValue) { 
				largestBinValue = bin;
			}
		}
		
		return new IntegerHistogram(hist, minValue, maxValue, smallestUsedBin, largestUsedBin, smallestBinValue, largestBinValue);		
	}
	
	public static void bandwiseStandardDeviationAndMean(Image image, double [] stddev, double [] mean) {
		
		final int pixels = image.lines * image.samples;		
		//final int degrees = (pixels - 1);
	
		final Pixel p = image.type.createPixel(image.bands);

		for (int i=0; i<pixels;i++) {		
			image.getPixel(p, i);
			
			for (int b=0; b<image.bands; b++) {
				mean[b] += p.getAsReal(b);
			}	
		}
		
		for (int b=0; b<image.bands; b++) {
			mean[b] = mean[b] / pixels;
		}
		
		for (int i=0; i<pixels;i++) {		
			image.getPixel(p, i);
			
			for (int b=0; b<image.bands; b++) {
				double tmp = p.getAsReal(b) - mean[b];
				stddev[b] += tmp * tmp;
			}	
		}
		
		for (int b=0; b<image.bands; b++) {
			//stddev[b] = Math.sqrt(stddev[b] / degrees);
			stddev[b] = Math.sqrt(stddev[b] / pixels);
		}		
	}
	
	public static double[][] centerReduceToDouble(Image image, double [] stddev, double [] mean) {

		final int pixels = image.lines * image.samples;
		
		double [][] y = new double[pixels][image.bands];
		
		Pixel p = null;
		
		for (int i=0;i<pixels;i++) {
			
			p = image.getPixel(p, i);
							
			for (int j=0;j<image.bands;j++) {
				y[i][j] = (p.getAsReal(j) - mean[j]) / stddev[j];
			}
		}
		return y;
	} 
		
	
	public static double[][] centerReduceToDouble(Image image) {
		
		double [] stddev = new double[image.bands];
		double [] mean = new double[image.bands];
		
		bandwiseStandardDeviationAndMean(image, stddev, mean);

		return centerReduceToDouble(image, stddev, mean);
	}
	
	public static float[][] centerReduceToFloat(Image image, double [] stddev, double [] mean) {
		
		final int pixels = image.lines * image.samples;
		
		float [][] y = new float[pixels][image.bands];
		
		Pixel p = null;
		
		for (int i=0;i<pixels;i++) {
			
			p = image.getPixel(p, i);
							
			for (int j=0;j<image.bands;j++) {
				y[i][j] = (float) ((p.getAsReal(j) - mean[j]) / stddev[j]);
			}
		}
		return y;
	}

	public static float[][] centerReduceToFloat(Image image) {

		double [] stddev = new double[image.bands];
		double [] mean = new double[image.bands];
		
		bandwiseStandardDeviationAndMean(image, stddev, mean);

		return centerReduceToFloat(image, stddev, mean);
	}
		
	public static Image centerReduceToF32Image(Image image) {

		double [] stddev = new double[image.bands];
		double [] mean = new double[image.bands];
		
		bandwiseStandardDeviationAndMean(image, stddev, mean);

		return centerReduceToF32Image(image, stddev, mean);
	}
			
	public static Image centerReduceToF32Image(Image image, double [] stddev, double [] mean) {
		
		final int pixels = image.lines * image.samples;
		
		final Image result = new Image(image.lines, image.samples, image.bands, 
				                       DataType.F32, Interleave.BIP, ByteOrder.LSF, 
				                       image.getDefaultBands(), image.getDescription());

		final float [] tmp = new float[image.bands];
		final F32Pixel out = new F32Pixel(tmp);
		
		Pixel p = null;
		
		for (int i=0;i<pixels;i++) {
			
			p = image.getPixel(p, i);
							
			for (int j=0;j<image.bands;j++) {
				tmp[j] = (float) ((p.getAsReal(j) - mean[j]) / stddev[j]);
			}
			
			result.putPixel(out, i);
		}
		
		return result;
	}
	
	public static float [][] readSpectralCalibration(String file, int bands) throws IOException {
		return readSpectralCalibration(new File(file), bands);
	}
	
	public static float [][] readSpectralCalibration(File file, int bands) throws IOException { 
		
		BufferedReader r = new BufferedReader(new FileReader(file));
		
		float [][] result = new float[bands][4];
		
		for (int i=0;i<bands;i++) { 
			String line = r.readLine();
		
			StringTokenizer tok = new StringTokenizer(line);
			
			float bandPosition = Float.parseFloat(tok.nextToken());
			float fwhm = Float.parseFloat(tok.nextToken());
			float bandUncertain = Float.parseFloat(tok.nextToken());
			float fwhmUncertain = Float.parseFloat(tok.nextToken());
			
			int band = Integer.parseInt(tok.nextToken());
			
			result[band-1][0] = bandPosition;
			result[band-1][1] = fwhm;
			result[band-1][2] = bandUncertain;
			result[band-1][3] = fwhmUncertain;
		}
		
		r.close();
		
		return result;
	}
	
}
