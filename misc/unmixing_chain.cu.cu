////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
//											Kernels SPCA													  //
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////

/*Este kernel realiza la multiplicación d_a * d_X = d_y (está hecho como una reducción)
elementos_X es el lines_samples mas una serie de elementos 0s para que sea multiplo de
el numero de hilos por bloques, así podemos hacer la reduccion de forma perfecta. 

GT: This kernel performs multiplication d_a * D_X = d_y (made as a reduction)
lines_samples elementos_X is more a series of 0s elements to be a multiple of
the number of threads per block, so we can make a perfect reduction.
*/

__global__ void reduccion_yi(float *d_X,float *d_yi, float *d_a, int elementos_X)

{
	if (threadIdx.x==0){
		d_yi[blockIdx.x]=0;
	}
	int part=elementos_X/(blockDim.x*2);
	__shared__ float s_a[512];
	for(int it=0; it<part; it++){
		unsigned int tid = threadIdx.x;
		unsigned int i =(elementos_X*blockIdx.x)+ (1024*it) + threadIdx.x;
		s_a[tid]=d_X[i]*d_a[i%elementos_X]+d_X[i+blockDim.x]*d_a[(i%elementos_X)+blockDim.x];
		__syncthreads();
		for (unsigned int s = blockDim.x / 2; s > 0; s>>=1)
		{
			if (tid < s){
				s_a[tid]+=s_a[tid+s];
			}
			__syncthreads();
		}
		d_yi[blockIdx.x]+=s_a[0];
		__syncthreads();
	}
}

/*
Este kernel calcula los elementos del vector d_sum. d_sum es un vector columna, el elemento
i de s_sum se calcula como la suma de los elementos j de la fila i de la matriz d_X siempre
que el elemento j del vector d_yi sea mayor que 0.

This kernel calculates d_sum vector elements. d_sum is a column vector, the element
s_sum i is calculated as the sum of the elements j of row i of the matrix always D_X
the element j d_yi vector is greater than 0.
*/

__global__ void calcular_sum(float *d_X,float *d_yi, float *d_sum, float *d_a, int elementos_X, int num_bands){
	int idx=blockDim.x * blockIdx.x + threadIdx.x;
	__shared__ float s_sum[32];
	s_sum[threadIdx.x]=0;
	for (int i=0; i<num_bands; i++){
		if(d_yi[i]>0){
			s_sum[threadIdx.x]+=d_X[i*elementos_X+idx];
		}
	}
	__syncthreads();
	d_sum[blockIdx.x*blockDim.x+threadIdx.x]=s_sum[threadIdx.x];
}


/*

Este kernel realiza la multiplicación d_a * d_X = d_aux (está hecho como una reducción).
elementos_X es el lines_samples mas una serie de elementos 0s para que sea multiplo de el
numero de hilos por bloques, así podemos hacer la reduccion de forma perfecta. 

This kernel performs multiplication d_a * D_X = d_aux (made as a reduction).
lines_samples elementos_X is more a series of 0s elements to be a multiple of the
number of threads per block, so we can make perfectly reduction
*/

__global__ void reduccion_aux(float *d_X,float *d_aux, float *d_a, int elementos_X){
	int part=elementos_X/(blockDim.x*2);
	__shared__ float s_a[512];
	for(int it=0; it<part; it++){
		unsigned int tid = threadIdx.x;
		unsigned int i =(elementos_X*blockIdx.x)+ (1024*it) + threadIdx.x;
		s_a[tid]=d_X[i]*d_a[i%elementos_X]+d_X[i+blockDim.x]*d_a[(i%elementos_X)+blockDim.x];
		__syncthreads();
		for (unsigned int s = blockDim.x / 2; s > 0; s>>=1)
		{
			if (tid < s){
				s_a[tid]+=s_a[tid+s];
			}
			__syncthreads();
		}
		d_aux[blockIdx.x]+=s_a[0];
		__syncthreads();
	}
}

/*

Este kernel modifica la matriz X restándole a cada elemento el producto de los vectores
d_aux y d_a (el resultado es un número).

This kernel modifies the array to each element X minus the product of vectors
d_aux and d_a (the result is a number).
*/

__global__ void calcular_X(float *d_X,float *d_aux, float *d_a, int elementos_X, int num_bands){
	int idx=blockDim.x * blockIdx.x + threadIdx.x;
	__shared__ float s_a[32];
	s_a[threadIdx.x]=d_a[idx];
	for (int i=0; i<num_bands; i++){
			d_X[i*elementos_X+idx]-=s_a[threadIdx.x]*d_aux[i];
	}
}

////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
// Kernels NFINDR	                                                              //
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////

#define N 19
#define ABS(a)	   (((a) < 0) ? -(a) : (a))

/*
Este kernel calcula el volumen conseguido al agregar cada pixel al conjunto de endmember
Realiza la multiplicación de d_aux * d_HIM2x2, ademas calcula el valor absoluto de cada volumen.

This kernel calculates the volume achieved by adding each pixel to the set of endmembers.
Performs multiplication of d_aux * d_HIM2x2, also calculates the absolute value of each volume.
*/
__global__ void CalcularVolumenes(double *d_aux, double *d_HIM2x2, double *d_Vvolume,double tmp2,int lines_samples)

{
	int idx =  blockDim.x * blockIdx.x+threadIdx.x;
	__shared__ double s_aux[N];
	double a;
	if (idx<lines_samples){
		if(threadIdx.x<N){
			s_aux[threadIdx.x]=d_aux[threadIdx.x];
		}
		syncthreads();
		a=0;
		for(int i=0; i<N; i++){
			a+=s_aux[i]*d_HIM2x2[i*lines_samples+idx];
		}
		a=a*tmp2;
		ABS(a);
		d_Vvolume[idx]=a;	
	}
}

/*
Este kernel obtiene los I volumenes mayores calculados en el kernel anterior siendo I el número
de bloques con que se estructura el lanzamiento del kernel. Además obtiene los índices de los pixel
que otienen dichos volumenes.

This kernel I get the older volumes calculated in the previous kernel I being the number
blocks with which the release of kernel structure. He also gets pixel indices
Otieno said that volumes.
*/

__global__ void ReduccionVolumenes(double *d_Vvolume, double *d_volumenes, int *d_indices){

	__shared__ double s_v[512];
	__shared__ int s_i[512];

	unsigned int tid = threadIdx.x;
	unsigned int i = blockIdx.x * (blockDim.x * 2) + threadIdx.x;

	if((i+blockDim.x)>=122500){
			s_v[tid]=d_Vvolume[i];
			s_i[tid]=i;
	}
	else{
		if(d_Vvolume[i]>d_Vvolume[i + blockDim.x]){
			s_v[tid]=d_Vvolume[i];
			s_i[tid]=i;
		}
		else{
			s_v[tid]=d_Vvolume[i + blockDim.x];
			s_i[tid]=i+ blockDim.x;
		}
	}
	__syncthreads();

	for (unsigned int s = blockDim.x / 2; s > 0; s>>=1){
		if (tid < s){
			if(s_v[tid]<=s_v[tid+s]){
				s_v[tid]=s_v[tid+s];
				s_i[tid]=s_i[tid+s];
			}
		}
		__syncthreads();
	}
	d_volumenes[blockIdx.x]=s_v[0];
	d_indices[blockIdx.x]=s_i[0];
	__syncthreads();
}

////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
// Kernels Unmixing                                                                   //
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////

#define TAMANIO_MATRIZ_C 188

/*
Este kernel realiza la fase final del unmixing, es decir multiplicar cada pixel por la
matriz de cómputo obtenida a partir de la matriz de endmembers, y así obtener las abundancias.

This kernel performs the final stage of unmixing, ie multiply each pixel by
computing matrix obtained from the endmembers matrix, and obtain the abundances.
*/
__global__ void Unmixing(float *d_imagen, float *d_imagen_unmixing,float *d_matriz_computo, int num_lines, int num_samples, int num_bands, int N_END)

{
	int pixel =  blockDim.x * blockIdx.x+threadIdx.x;
	
	__shared__ float matriz_c[TAMANIO_MATRIZ_C];
	float l_pixel[188];
	float a;
	if(pixel<num_lines*num_samples){
		for(int t=0; t<num_bands; t++){
			l_pixel[t]=d_imagen[pixel+(num_lines*num_samples*t)];
		}
		for(int it=0; it<N_END; it++){
			if(threadIdx.x==0){
				for(int i=0; i<num_bands; i++){
					matriz_c[i]=d_matriz_computo[it*num_bands+i];
				}
			}
			syncthreads();
			a=0;
			for(int k=0; k<num_bands; k++){	
				a+=matriz_c[k]*l_pixel[k];
			}
			d_imagen_unmixing[pixel+(num_lines*num_samples*it)]=a;
		}
	}

}
