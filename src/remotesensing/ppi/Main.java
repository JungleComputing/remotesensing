package remotesensing.ppi;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import remotesensing.ppi.PPI.Score;
import remotesensing.util.Image;
import remotesensing.util.Image.ByteOrder;
import remotesensing.util.Image.DataType;
import remotesensing.util.Image.Interleave;
import remotesensing.util.LookupTable;
import remotesensing.util.PngEncoder;
import remotesensing.util.Utils;

public class Main {

	private static Image generateResultImage(int lines, int samples, 
			List<Score> maxScore, List<Score> minScore, int skewers) throws Exception { 
	
		ByteBuffer tmp = ByteBuffer.allocate(lines * samples * 4);
	
	//	int maxMax = maxScore.get(0).count;
	//	int maxMin = minScore.get(0).count;
	
		LookupTable lut = Utils.generateHistogram(maxScore, 10000).generateStretchedLUT(256);
		
		for (Score s : maxScore) { 
	
			byte value = (byte) (lut.lookup(s.count) & 0xFF);
			
			//byte value = (byte) (((s.count * 255) / maxMax) & 0xFF); 			
			tmp.put(s.pixel*4 + 1, value);
		
			System.out.println(" " + s.count + " " + s.pixel);
		}
	
		lut = Utils.generateHistogram(minScore, 10000).generateStretchedLUT(256);
		
		for (Score s : minScore) {
			
			byte value = (byte) (lut.lookup(s.count) & 0xFF);
			
			//byte value = (byte) (((s.count * 255) / maxMin) & 0xFF); 			
			tmp.put(s.pixel*4 + 2, value);
			
			// System.out.println("MIN: " + s.pixel + " " + s.count);
		}

		return new Image(tmp, lines, samples, 4, DataType.U8, 
				Interleave.BIP, ByteOrder.LSF, null, null);
	}
		
	public static void main(String [] args) throws Exception { 

		String header = args[0];
		String data = args[1];
		String type = args[2];
		
		int skewers = Integer.parseInt(args[3]);

		long start = System.currentTimeMillis();
		
		Image image = Utils.readENVI(header, data);
		
		PPI ppi = null;
		
		if (type.equalsIgnoreCase("LINEAR")) { 
			ppi = new LinearPPI(image, skewers);		
		} else if (type.equalsIgnoreCase("TILED")) { 
			ppi = new TiledPPI(image, skewers);		
		} else if (type.equalsIgnoreCase("TILED2")) { 
			ppi = new TiledPPI2(image, skewers);		
		} else { 

			System.err.println("Unknown PPI type " + type);
			System.exit(1);
		}

		long mid = System.currentTimeMillis();

		System.out.println("Reading took " + (mid-start)/1000.0 + " sec.");

		ppi.process();

		long end = System.currentTimeMillis();

		System.out.println("PPI took " + (end-start)/1000.0 + " sec.");

		List<Score> maxScore = ppi.getMaxScores();
		List<Score> minScore = ppi.getMinScores();
		
		Image result = generateResultImage(image.lines, image.samples, maxScore, minScore, skewers);
				
		//Conversion c = new NumberRangeToRGBConversion(ppi.getResultMINCount(), 
		//		                                      ppi.getResultMAXCount()+1);
		
		//result = c.convert(result);
		
		PngEncoder enc = new PngEncoder(result);
		byte [] out = enc.pngEncode(false);
		
		FileOutputStream fout = new FileOutputStream("ppi.png");
		fout.write(out);
		fout.close();
	}
}
