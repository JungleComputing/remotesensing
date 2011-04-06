package remotesensing.ppi;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.List;

import remotesensing.ppi.PPI.Score;
import remotesensing.util.Image;
import remotesensing.util.Image.ByteOrder;
import remotesensing.util.Image.DataType;
import remotesensing.util.Image.Interleave;
import remotesensing.util.Pixel;
import remotesensing.util.PngEncoder;
import remotesensing.util.Utils;

public class Main {

	private static Image generateResultImage(int lines, int samples, 
			List<Score> maxScore, List<Score> minScore, int skewers) { 
	
		ByteBuffer tmp = ByteBuffer.allocate(lines * samples * 4);
	
		int maxMax = maxScore.get(0).count;
		int maxMin = minScore.get(0).count;
		
		for (Score s : maxScore) { 
			byte value = (byte) (((s.count * 255) / maxMax) & 0xFF); 			
			tmp.put(s.pixel*4 + 1, value);
		
			System.out.println(" " + s.count + " " + s.pixel);
		}
	
		for (Score s : minScore) { 
			byte value = (byte) (((s.count * 255) / maxMin) & 0xFF); 			
			tmp.put(s.pixel*4 + 2, value);
			
			// System.out.println("MIN: " + s.pixel + " " + s.count);
		}

		return new Image(tmp, lines, samples, 4, DataType.U8, 
				Interleave.BIP, ByteOrder.LSF, null, null);
	}
		
	public static void main(String [] args) throws Exception { 

		String header = args[0];
		String data = args[1];
		int skewers = Integer.parseInt(args[2]);

		long start = System.currentTimeMillis();

		Image image = Utils.readENVI(header, data);

		// HACK to check test image....
		
		System.out.println("0x0 " + image.getPixel(0, 0));
		System.out.println("0x" + (image.samples-1) + image.getPixel(0, image.samples-1));
		System.out.println((image.lines-1) + "x0 " + image.getPixel(image.lines-1, 0));
		System.out.println((image.lines-1) + "x" + (image.samples-1) + " " + image.getPixel(image.lines-1, image.samples-1));
		System.out.println((image.lines/2) + "x" + (image.samples/2) + " " + image.getPixel(image.lines/2, image.samples/2));
		
		PPI ppi = new PPI(image, skewers);		

		long mid = System.currentTimeMillis();

		System.out.println("Reading took " + (mid-start)/1000.0 + " sec.");

		ppi.run();

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
