package remotesensing.ppi;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

	private static class PixelComparator implements Comparator<Score> {

		@Override
		public int compare(Score o1, Score o2) {
			return o1.pixel - o2.pixel;
		} 
	}

	private static class ScoreComparator implements Comparator<Score> {

		@Override
		public int compare(Score o1, Score o2) {
			return o2.count - o1.count;
		} 
	}

	
	private static List<Score> merge(List<Score> maxScore, List<Score> minScore) { 

		// merge the two result lists. Note that a pixels may be in both lists, 
		// but does not have to be!

		// Step 1: append both lists into one
		List<Score> tmp = new ArrayList<Score>();
		tmp.addAll(maxScore);
		tmp.addAll(minScore);
		
		// Step 2: sort this list such that all scores with the same pixel value 
		// are next to each other in the list  
		Collections.sort(tmp, new PixelComparator());
		
		// Next, traverse the items and merge any duplicates that we can find. 
		List<Score> tmp2 = new ArrayList<Score>();
		
		Score current = null;
		
		for (Score s : tmp) { 
			
			if (current != null) { 
				if (current.pixel == s.pixel) { 
					current = new Score(current.pixel, current.count + s.count);
				} else { 
					tmp2.add(current);
					current = s;
				}
			} else { 
				current = s;
			}
		}

		tmp2.add(current);

		// Finally, sort the result by score
		Collections.sort(tmp2, new ScoreComparator());
		
		return tmp2;
	} 
	
	private static void selectEndMembers(List<Score> maxScore, List<Score> minScore) { 
		
		List<Score> tmp = merge(maxScore, minScore);
			
		System.out.println("Resulting list: ");
		
		for (Score s : tmp) { 
			System.out.println("   " + s.pixel + " " + s.count);
		}
		
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
		
		selectEndMembers(maxScore, minScore);
		
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
