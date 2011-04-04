package remotesensing.util;

import java.io.IOException;
import java.util.Arrays;

public class RemoveBands {

	private static void handleBands(String arg, boolean [] bands) throws Exception { 
		
		int index = arg.indexOf("-");
		
		int start = 0;
		int end = 0;
		
		if (index > 0) { 			
			// It's a range
			start = Integer.parseInt(arg.substring(0, index));
			end = Integer.parseInt(arg.substring(index+1));
		} else { 
			// It's a single value
			start = end = Integer.parseInt(arg);
		}

		if (start < 1 || end > bands.length || end < start) { 
			throw new Exception("Illegal band value! " + start + "-" + end);
		}

		for (int i=start;i<=end;i++) { 
			bands[i-1] = false;
		}
	}
	
	public static void main(String [] args) throws Exception {
		
		String inheader = args[0];
		String indata = args[1];
		
		String outheader = args[2];
		String outdata = args[3];
		
		Image img = Utils.readENVI(inheader, indata);
		
		boolean [] bands = new boolean[img.bands];
		
		Arrays.fill(bands, true);
		
		for (int i=4;i<args.length;i++) { 
			handleBands(args[i], bands);
		}
	
		Utils.writeENVI(outheader, outdata, img, bands);
	}
	
	
}
