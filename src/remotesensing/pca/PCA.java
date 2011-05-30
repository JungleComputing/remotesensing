package remotesensing.pca;

import java.util.Arrays;

import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;

import remotesensing.pixels.F64Pixel;
import remotesensing.util.Image;
import remotesensing.util.Image.ByteOrder;
import remotesensing.util.Image.DataType;
import remotesensing.util.Image.Interleave;
import remotesensing.util.Pixel;
//import Jama.EigenvalueDecomposition;
//import Jama.Matrix;

public class PCA {

	//final double[][] X; // initial data : lines = events and columns = variables
	
	final Image image;
	final int p;        // required number of components  
		
	//double[] meanX, stdevX;

	//double[][] Z; // X centered reduced

	//double[][] cov; // Z covariance matrix

	//double[][] U; // projection matrix

	//double[] info; // information matrix
	
	Image result;
	
	public PCA(Image img, int p) {
		this.image = img; 
		this.p = p;
	}
	
	/*
	public PCA(double[][] X, int p) {
	 	
		if (X[0].length <= p) { 
			throw new RuntimeException("Too many components requested!");
		}
		
		this.X = X;
		this.p = p;	
	}	

	private static double [][] convertImage(Image img) { 

		if (img.type == DataType.C64 || img.type == DataType.C64) { 
			throw new RuntimeException("Complex type images not supported!");
		}
		
		int pixels = img.lines * img.samples;
		
		double [][] tmp = new double[pixels][img.bands];
		
		Pixel p = img.type.createPixel(img.bands);
		
		if (p.isInteger()) {
		
			IntegerPixel ip = (IntegerPixel) p;
			
			for (int i=0;i<pixels;i++) { 
				img.getPixel(ip, i);

				for (int b=0;b<img.bands;b++) { 
					tmp[i][b] = ip.get(i);
				}
			} 
		} else { 

			RealPixel ip = (RealPixel) p;
			
			for (int i=0;i<pixels;i++) { 
				img.getPixel(ip, i);

				for (int b=0;b<img.bands;b++) { 
					tmp[i][b] = ip.get(i);
				}
			} 
		}
		
		return tmp;
	}
*/
	
	
	public void process() {
		
		// Step 1: Balance the data such that each variable is centered around its mean. 
		
		long t0 = System.currentTimeMillis();

		double [] stddev = new double[image.bands];
		double [] mean   = new double[image.bands];
		
		stddev_mean(image, stddev, mean);

		long t1 =  System.currentTimeMillis();

		System.out.println("stddev + mean took " + (t1-t0) + " ms.");

		double [][] Z = center_reduce(image, stddev, mean);

		long t3 = System.currentTimeMillis();

		System.out.println("center reduce took " + (t3-t1) + " ms.");

		// Step 2: Determine the covariance matrix which describes the relation 
		// between the different variables. 
		
		double [][] cov = covariance5(Z);

		long t4 = System.currentTimeMillis();

		System.out.println("covariance took " + (t4-t3) + " ms.");

		// Step 3: Decompose the covariance matrix so we get the eigenvectors and 
		// eigenvalues. 
		//
		// The eigenvectors are the principal components, that is, a set of 
		// orthogonal vectors that can be used de describe the variables. 
		// 
		// The eigenvalues show the importance of each of the principal components. 
		// The higher the value, the more importance/influence the component has in 
		// the value of the variables. 
		
		//EigenvalueDecomposition e = new EigenvalueDecomposition(new Matrix(cov));
        //	long t5 = System.currentTimeMillis();
        // System.out.println("eigenvalue decomp took " + (t1-t0) + " ms.");
		// double [][] U = transpose(e.getV().getArray());
        // long t6 = System.currentTimeMillis();
        // System.out.println("transpose took " + (t5-t4) + " ms.");
		// double [] info = e.getRealEigenvalues();
		
		System.out.println("cov " + cov.length + " x " + cov[0].length);
		
		//for (int i=0;i<cov.length;i++) { 			
			//System.out.println(Arrays.toString(cov[i]));			
		//}
		
		EigenvalueDecomposition e = new EigenvalueDecomposition(new DenseDoubleMatrix2D(cov));
		
		long t5 = System.currentTimeMillis();

		System.out.println("eigenvalue decomp took " + (t1-t0) + " ms.");

		double [][] U = transpose(e.getV().toArray());

		long t6 = System.currentTimeMillis();

		System.out.println("transpose took " + (t5-t4) + " ms.");

		double [] info = e.getRealEigenvalues().toArray();

		// Step 4: We can now select the 'p' most influencial principal components 
		// out of the information vector and use the corresponding eigenvectors to 
		// create a feature vector.  
		
		double [][] featureT = feature(info, U, p);
		
		long t7 = System.currentTimeMillis();

		System.out.println("feature vector creation took " + (t7-t6) + " ms.");

		// Step 5: We use the feature vector to reduce the dimensionality of the 
		// (centered) dataset. By multiplying the feature vector with the dataset, 		
		// a new, translated, dataset is generated that expresses each variable 
		// relative to each of the selected principal components
	
		// Note: we skip the transpose and simple rig the multiply. Same result, much faster! 
		//featureT = transpose(featureT);
		
		//long t8 = System.currentTimeMillis();
		
		//System.out.println("transpose took " + (t8-t7) + " ms.");
		
		result = new Image(image.lines, image.samples, p, DataType.F64, Interleave.BIP, ByteOrder.LSF, null, "");
		
		times(Z, featureT, result);
		
		System.out.println("Result image contains " + result.bands + " bands " + p);
		
		/*
		for (int i=0;i<result[0].length;i++) { 
			
			double tmp = 0.0;
			
			for (int j=0;j<result.length;j++) { 
				tmp += result[j][i];
			}
			
			System.out.println("result[" + i + "][*] = " + tmp);
		}
		*/
		
		/*
		Z = transpose(Z);
		
		long t8 = System.currentTimeMillis();
		
		System.out.println("transpose took " + (t8-t7) + " ms.");
				
		double [][] result = times(featureT, Z);
		
		for (int i=0;i<result.length;i++) { 
			
			double tmp = 0.0;
			
			for (int j=0;j<result[i].length;j++) { 
				tmp += result[i][j];
			}
			
			System.out.println("result[" + i + "][*] = " + tmp);
		}
		*/
		
		long t9 = System.currentTimeMillis();
		
		System.out.println("dimensionality reduction took " + (t9-t7) + " ms.");
		
		System.out.println("Complete PCA took " + (t9-t0) + " ms.");
		
		// System.out.println("result[" + result.length + "][" + result[0].length + "]");
		
		// info = e.get1DRealD(); // covariance matrix is symetric, so only real eigenvalues...
	}

	public Image getResult() { 
		return result;
	}
	
	private static double [][] feature(double [] info, double [][] eigen, int p) { 

		// FIXME: unsure if eigen is copied correctly!
		
		//int [] indices = new int[p];
		//double [] values = new double[p];

		double [][] featureVectorT = new double[p][eigen.length];
		
		for (int j=0;j<p;j++) {  

			double max = 0.0;
			int index = -1;

			for (int i=0;i<info.length;i++) { 
				if (info[i] > max) { 
					max = info[i];
					index = i;
				}
			}

			System.out.println("FV[" + j + "] = (" + index + ", " + max + ")");
			
			//indices[j] = index;
			//values[j] = max;

			info[index] = 0.0;
			
			System.arraycopy(eigen[index], 0, featureVectorT[j], 0, eigen[index].length);
		}

		return featureVectorT;
	}	                		

	private double times(double [] v1, double [] v2) { 
		
		double value = 0.0;
		
		for (int i=0;i<v1.length;i++) { 
			value += v1[i]*v2[i]; 
		}
		
		return value;
	}
	
	private void times(double [][] image, double [][] feature, Image out) {
		
		System.out.println("Mult image[" + image.length + "][" + image[0].length + "] and feature[" + feature.length + "][" + feature[0].length + "]");
		
		int pixels = image.length;
		
		F64Pixel p = new F64Pixel(out.bands); 
		
		for (int i=0;i<pixels;i++) {
			for (int b=0;b<out.bands;b++) { 
				p.put(times(image[i], feature[b]), b);
			}
			
			out.putPixel(p, i);
		}

	}
	
	
	// start of inlining 

	// Copied from DoubleArray -- Jason
	private double[][] transpose(double[][] M) {
		
		double [][] tM = new double[M[0].length][M.length];
		
		for (int i = 0; i < tM.length; i++)
			for (int j = 0; j < tM[0].length; j++)
				tM[i][j] = M[j][i];
		
		return tM;
	}

	private void stddev_mean(Image image, double [] stddev, double [] mean) {
		
		final int pixels = image.lines * image.samples;		
		final int degrees = (pixels - 1);
	
		final Pixel p = image.type.createPixel(image.bands);

		for (int i=0; i<pixels;i++) {		
			image.getPixel(p, i);
			
			for (int b=0; b<image.bands; b++) {
				mean[b] += p.getAsReal(b);
			}	
		}
			
		//System.out.println("sum " + Arrays.toString(mean));
		
		for (int b=0; b<image.bands; b++) {
			mean[b] = mean[b] / pixels;
		}

		//System.out.println("sum/pixels " + Arrays.toString(mean));
		
		for (int i=0; i<pixels;i++) {		
			image.getPixel(p, i);
			
			for (int b=0; b<image.bands; b++) {
				double tmp = p.getAsReal(b) - mean[b];
				stddev[b] += tmp * tmp;
			}	
		}
		
		//System.out.println("var " + Arrays.toString(stddev));
		
		for (int b=0; b<image.bands; b++) {
			stddev[b] = Math.sqrt(stddev[b] / degrees);
		}
		
		//System.out.println("stddev " + Arrays.toString(stddev));
	}
	
	private double[][] center_reduce(Image image, double [] stddev, double [] mean) {
		
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
	
	
	// Copied from StatisticSample -- Jason
	
	public String printFlops(long flops) { 
	
		if (flops > 1000000000) { 
			return (flops / 1000000000.0) + " Gflop"; 
		} else if (flops > 1000000) {
			return (flops / 1000000.0) + " Mflop";
		} else if (flops > 1000) {
				return (flops / 1000.0) + " Kflop";
		} else { 
			return flops + " flop";
		}
	}
		
	// Very inefficient computation and memory wise ? 
	private double[][] covariance(double[][] v) {
		
		final int m = v.length;
		final int n = v[0].length;
		final int degrees = (m - 1);
		final double[][] X = new double[n][n];
		
		System.out.println("Using covariance implemenation 1 - requires " + printFlops((n*n*(3L+6L*m))));
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				
				double c = 0;
				double s1 = 0;
				double s2 = 0;
		
				for (int k = 0; k < m; k++) {
					s1 += v[k][i];
					s2 += v[k][j];
				}
				
				s1 = s1 / m;
				s2 = s2 / m;
				
				for (int k = 0; k < m; k++) {
					c += (v[k][i] - s1) * (v[k][j] - s2);
				}
				
				X[i][j] = c / degrees;
			}
		}

		for (int i=0;i<n;i++) {
			double s = 0;
			
			for (int j=0;j<n;j++) {
				s += X[i][j]; 
			}	
			
			System.out.println("RS " + i + " = " + s);
		}
		
		return X;
	}

	// Alternative implementation
	private double[][] covariance2(double[][] v) {
		
		final int m = v.length;
		final int n = v[0].length;
		final int degrees = (m - 1);
		final double[][] X = new double[n][n];

		System.out.println("Using covariance implemenation 2 - requires " + printFlops((n*(1L+m)) + (n*n*(1L+4L*m))));
		
		final double [] sum = new double[n];

		long t0 = System.currentTimeMillis();
		
		for (int i = 0; i < n; i++) {
			
			double tmp = 0.0;
			
			for (int k = 0; k < m; k++) {
				tmp += v[k][i];
			}
			
			sum[i] = tmp / m;
		}
		
		long t1 = System.currentTimeMillis();
		
		System.out.println("  Covariance loop 1 took " + (t1-t0) + " (" + printFlops((n*(1L+m))) + ") ");
		
		
		for (int i = 0; i < n; i++) {
			
			double s1 = sum[i];
			
			for (int j = 0; j < n; j++) {
				
				double c = 0;				
				double s2 = sum[j];
				
				for (int k = 0; k < m; k++) {
					c += (v[k][i] - s1) * (v[k][j] - s2);
				}
				
				X[i][j] = c / degrees;
			}
		}
		
		long t2 = System.currentTimeMillis();
		
		System.out.println("  Covariance loop 2 took " + (t2-t1) + " (" + printFlops( (n*n*(1L+4L*m)) ) + ") ");
		
		for (int i=0;i<n;i++) {
			double s = 0;
			
			for (int j=0;j<n;j++) {
				s += X[i][j]; 
			}	
			
			System.out.println("RS " + i + " = " + s);
		}
		
		return X;
	}

	// Alternative implementation 3
	private double[][] covariance3(double[][] v) {
		
		final int m = v.length;
		final int n = v[0].length;
		
		final int degrees = (m - 1);

		System.out.println("Using covariance implemenation 3 - requires " + printFlops((n*(1L+m)) + (n*m) + (n*n*(1L+2L*m))));
		
		long t0 = System.currentTimeMillis();
		
		// First calculate the sum of each column
		final double [] sum = new double[n];

		for (int i = 0; i < n; i++) {
			
			double tmp = 0.0;
			
			for (int k = 0; k < m; k++) {
				tmp += v[k][i];
			}
			
			sum[i] = tmp / m;
		}

		long t1 = System.currentTimeMillis();
		
		System.out.println("  Covariance loop 1 took " + (t1-t0) + " (" + printFlops((n*(1L+m))) + ") ");
		
		// Next calculate the diff between each value and its column's sum.		
		final double [][] diff = new double[m][n];
		
		for (int i = 0; i < n; i++) {
			
			double s1 = sum[i];
			
			for (int k = 0; k < m; k++) {
				diff[k][i] = (v[k][i] - s1);
			}
		}
	
		long t2 = System.currentTimeMillis();
		
		System.out.println("  Covariance loop 2 took " + (t2-t1) + " (" + printFlops(n*m) + ") ");
		
		// Finally, calculate the result matrix 
		final double[][] X = new double[n][n];
		
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				
				double c = 0;				
				
				for (int k = 0; k < m; k++) {
					c += diff[k][i] * diff[k][j];
				}
				
				X[i][j] = c / degrees;
			}
		}
			
		long t3 = System.currentTimeMillis();
	
		System.out.println("  Covariance loop 3 took " + (t3-t2) + " (" + printFlops( (n*n*(1L+2L*m)) ) + ") ");
		
		for (int i=0;i<n;i++) {
			double s = 0;
			
			for (int j=0;j<n;j++) {
				s += X[i][j]; 
			}	
			
			System.out.println("RS " + i + " = " + s);
		}
		
		return X;
	}
	
    // Alternative implementation
	private double[][] covariance4(double[][] v) {
		
		final int m = v.length;
		final int n = v[0].length;
		final int degrees = (m - 1);
		final double[][] X = new double[n][n];

		System.out.println("Using covariance implemenation 4 - requires " + printFlops((n*(1L+m)) + (n*n*(1L+4L*m))/2));
		
		final double [] sum = new double[n];

		long t0 = System.currentTimeMillis();
		
		for (int i = 0; i < n; i++) {
			
			double tmp = 0.0;
			
			for (int k = 0; k < m; k++) {
				tmp += v[k][i];
			}
			
			sum[i] = tmp / m;
		}
		
		long t1 = System.currentTimeMillis();
		
		System.out.println("  Covariance loop 1 took " + (t1-t0) + " (" + printFlops((n*(1L+m))) + ") ");
				
		for (int i = 0; i < n; i++) {
			
			double s1 = sum[i];
			
			for (int j = i; j < n; j++) {
				
				double c = 0;				
				double s2 = sum[j];
				
				for (int k = 0; k < m; k++) {
					c += (v[k][i] - s1) * (v[k][j] - s2);
				}
				
				X[i][j] = X[j][i] = c / degrees;
			}
		}
		
		long t2 = System.currentTimeMillis();
		
		System.out.println("  Covariance loop 2 took " + (t2-t1) + " (" + printFlops( (n*n*(1L+4L*m)) ) + ") ");
		
		for (int i=0;i<n;i++) {
			double s = 0;
			
			for (int j=0;j<n;j++) {
				s += X[i][j]; 
			}	
			
			System.out.println("RS " + i + " = " + s);
		}
		
		return X;
	}

    // Alternative implementation
	private double[][] covariance5(double[][] v) {
		
		final int m = v.length;
		final int n = v[0].length;
		final int degrees = (m - 1);
		
		final double[][] X = new double[n][n];

		System.out.println("Using covariance implemenation 5 - requires " + printFlops((n*(1L+m)) + (m*n*(1L+(3L*n/2))) + (n*n)));
		
		final double [] sum = new double[n];

		long t0 = System.currentTimeMillis();
		
		for (int i = 0; i < n; i++) {
			
			double tmp = 0.0;
			
			for (int k = 0; k < m; k++) {
				tmp += v[k][i];
			}
			
			sum[i] = tmp / m;
			
			/*
			System.out.println("sum[" + i + "] = " + sum[i]);
			
			if (Double.isNaN(sum[i])) { 
				
				for (int k = 0; k < m; k++) {
					System.out.print(" " + v[k][i]);
				}	
				
				System.out.println();
			}
			*/
		}
		
		long t1 = System.currentTimeMillis();
		
		System.out.println("  Covariance loop 1 took " + (t1-t0) + " (" + printFlops((n*(1L+m))) + ") ");
		
		for (int k=0;k<m;k++) { 
		
			for (int i = 0; i < n; i++) {
			
				double s1 = sum[i];
				double c1 = (v[k][i] - s1);
			
				for (int j = i; j < n; j++) {
				
					double s2 = sum[j];
					double c2 = (v[k][j] - s2); 				
				
					X[i][j] += c1*c2;	
				}			
			}
		}
		
		long t2 = System.currentTimeMillis();
				
		System.out.println("  Covariance loop 2 took " + (t2-t1) + " (" + printFlops( (m*n*(1L+(3L*n/2))) ) + ") ");
		
		for (int i = 0; i < n; i++) {
			for (int j = i; j < n; j++) {
				X[i][j] /= degrees;
				X[j][i] = X[i][j]; 
			}
		}
		
		long t3 = System.currentTimeMillis();
		
		System.out.println("  Covariance loop 3 took " + (t3-t2) + " (" + printFlops( n*n ) + ") ");

		return X;
	}
	
}