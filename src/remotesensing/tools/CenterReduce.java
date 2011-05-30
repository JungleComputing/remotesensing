package remotesensing.tools;

import remotesensing.util.Image;
import remotesensing.util.Utils;

public class CenterReduce {

	public static void main(String [] args) throws Exception { 
		
		String headerIn = args[0];
		String dataIn = args[1];
		
		String headerOut = args[2];
		String dataOut = args[3];
		
		Image image = Utils.readENVI(headerIn, dataIn);		
		image = Utils.centerReduceToF32Image(image);
		Utils.writeENVI(headerOut, dataOut, image);
	}
}
