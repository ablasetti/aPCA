package aPCA;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathArrays.OrderDirection;




public class aPCA {

	int N;
	RealMatrix pcaMatrix;

	public aPCA(Integer components) {
		N = components;
	}


	public RealMatrix fit(RealMatrix M) {
		RealMatrix covarianceMatrix =  new Covariance(M).getCovarianceMatrix();
		System.out.println("COV Matrix dim: "+covarianceMatrix.getRowDimension()+ " x " + covarianceMatrix.getColumnDimension());

		EigenDecomposition eigenVV = new EigenDecomposition(covarianceMatrix);
		double[] E = eigenVV.getRealEigenvalues();

		System.out.println("Autovalori e autovettori:");

		pcaMatrix = MatrixUtils.createRealMatrix(M.getColumnDimension(),N);


		LinkedHashMap eingmap = sortEigens(E);
		Set<Integer> keys = eingmap.keySet();
		int i = 0;
		for(int key: keys) {
			if(i<N) {
				RealVector rv = eigenVV.getEigenvector(key); 
				pcaMatrix.setColumnVector(key, rv);
			}
			i++;
		}
			
		
		
//		for(int j=0;j<N;j++) {
//			RealVector rv = eigenVV.getEigenvector(j); 
//			System.out.println(E[j] + " - Autovettore; "+rv);
//			pcaMatrix.setColumnVector(j, rv);
//		}

		return pcaMatrix;
	}

	
	
	
	
	

	public LinkedHashMap<Integer, Double> sortEigens(double[] E) {
		System.out.println(Arrays.toString(E));
		LinkedHashMap<Integer, Double> reverseSortedMap = new LinkedHashMap<>(); 
		for(int i=0;i<E.length;i++) {
			reverseSortedMap.put(i,E[i]);
			System.out.println(i+" "+E[i]);
		}

		reverseSortedMap.entrySet()
		.stream()
		.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) 
		.forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

		System.out.println("Reverse Sorted Map   : " + reverseSortedMap);
		return(reverseSortedMap);
	}
	
	
	
	
	

	public RealMatrix tranform(RealMatrix M) {
		System.out.println("Data Matrix dim: "+M.getRowDimension()+ " x " + M.getColumnDimension());
		System.out.println("PCA Matrix dim: "+pcaMatrix.getRowDimension()+ " x " + pcaMatrix.getColumnDimension());

		RealMatrix proj = M.multiply(pcaMatrix);
		System.out.println("Transformed Data dim: "+proj.getRowDimension()+ " x " + proj.getColumnDimension());

		return(proj);
	}



	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RealMatrix H = new Array2DRowRealMatrix(new double[][] { 
			{0.0, 1.0, 0.0, 7.0, 0.0 }, 
			{2.0, 0.0, 3.0, 4.0, 5.0}, 
			{4.0, 0.0, 0.0, 6.0, 7.0 } },	    		
				false);

		aPCA pca = new aPCA(2);
		RealMatrix pcaMatrix = pca.fit(H);
		System.out.println(pcaMatrix);
		RealMatrix projectedMat = pca.tranform(H);
		System.out.println(projectedMat);

		//		 SPARK Projected vector of principal component:
		//		[1.6485728230883807,-4.013282700516296]
		//		[-4.645104331781533,-1.1167972663619026]
		//		[-6.428880535676489,-5.337951427775355]

		//		THIS CODE 
		//		{1.6485728231,4.0132827005},
		//		{-4.6451043318,1.1167972664},
		//		{-6.4288805357,5.3379514278}}
	}
	

}
