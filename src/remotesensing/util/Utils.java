package remotesensing.util;

public class Utils {
	
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
