package remotesensing.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

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

	public static Image readENVI(String header, String data) throws IOException {
		return readENVI(new File(header), new File(data));
	}

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
        
        int pos = 0;
        int index = 0;
        
        while (index < pixels) { 

        	// Data is read big-endian
            int a = bis.read();
            int b = bis.read();

            if (byteorder == 0) { 
            	// Little endian 
            	// Big endian

            	// Shift pixels from -32K .. +32K to 0 .. 64K 
            	int value = (((b << 8) | (a & 0xff))) + Short.MAX_VALUE;          
            	target[pos++] = (short) value;
            } else { 
            	// Big endian

            	// Shift pixels from -32K .. +32K to 0 .. 64K 
            	int value = (((a << 8) | (b & 0xff))) + Short.MAX_VALUE;          
            	target[pos++] = (short) value;
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

	public static Image readENVI(File header, File data) throws IOException {

		FileInfo tmp = readHeader(header);
		return readENVIData(data, tmp);
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
}
