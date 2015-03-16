package com.lab.mainpacket;

import java.util.List;
import java.util.Random;

import com.lab.mainpacket.NMFDataModel.DataModel;
import com.lab.mainpacket.NMFDataModel.Matrix;

public class NMF {

	public NMF(){
		
	}
	public Matrix<Float[][], Float[][]> trainingData(List<DataModel<Integer, Integer, Float>>trainingSet, int k, int m, int n, int maxLoop, float alpha, float beta){
		System.out.println("training with k="+ k + " loop= "+ maxLoop + " numUsers:" + m + " numItems: "+ n);
		Float[][] P = new Float[m][k];
		Float[][] Q = new Float[n][k];
		Random ran = new Random();
		float error;
		for(int i = 0; i < m; i++){
			for( int j = 0; j < k; j++){
				P[i][j] = ran.nextFloat();
			}
		}
		for(int i = 0; i< n; i++)
			for(int j = 0; j< k; j++){
				Q[i][j] = ran.nextFloat();
			}
		
		double cost = 0, oldCost = 0;
		for (int i = 0; i < maxLoop; i++){
			for(DataModel<Integer, Integer, Float> data : trainingSet){
//				System.out.println("userId: "+ data.userId + " itemId: "+ data.itemId);
				error = data.preference - dotMatrix(P[data.userId], Q[data.itemId]);
				System.out.println("error = " + error);
				//update P
				for(int j= 0; j < k; j++){
					P[data.userId][j] += alpha * (error * Q[data.itemId][j] - beta * P[data.userId][j]);
				}
				error = data.preference - dotMatrix(P[data.userId], Q[data.itemId]);
				System.out.println("error = " + error);
				//update Q
				for(int j= 0; j < k; j++){
					Q[data.itemId][j] += alpha * (error * P[data.userId][j] - beta * Q[data.itemId][j]);
				}
				
				cost += error * error;
			}
			cost = cost/ trainingSet.size();
			if(Math.abs((oldCost - cost)/cost) < 0.0001){
				System.out.println("Stop at maxLoop: "+ i);
				break;
			}
			oldCost = cost;
		}
		
		return new Matrix<Float[][], Float[][]>(P, Q);
	}
	
	/**
	 * @brief: implemention nmf on site: http://www.quuxlabs.com/
	 * @param trainingSet
	 * @param k
	 * @param m
	 * @param n
	 * @param maxLoop
	 * @param alpha
	 * @param beta
	 * @return
	 */
	public Matrix<Float[][], Float[][]> matrixFactorization(List<DataModel<Integer, Integer, Float>>trainingSet, int k, int m, int n, int maxLoop, float alpha, float beta){
		System.out.println("training with k="+ k + " loop= "+ maxLoop + " numUsers:" + m + " numItems: "+ n);
		Float[][] P = new Float[m][k];
		Float[][] Q = new Float[n][k];
		Random ran = new Random();
		float error;
		for(int i = 0; i < m; i++){
			for( int j = 0; j < k; j++){
				P[i][j] = ran.nextFloat()*5;
			}
		}
		for(int i = 0; i< n; i++)
			for(int j = 0; j< k; j++){
				Q[i][j] = ran.nextFloat()*5;
			}
		
		float e = 0;
		for (int i = 0; i < maxLoop; i++){
			for(DataModel<Integer, Integer, Float> data : trainingSet){
				error = data.preference - dotMatrix(P[data.userId], Q[data.itemId]);
				
				for(int j= 0; j < k; j++){
					//update P
					P[data.userId][j] += alpha * (2*error * Q[data.itemId][j] - beta * P[data.userId][j]);
					//update Q
					Q[data.itemId][j] += alpha * (2*error * P[data.userId][j] - beta * Q[data.itemId][j]);
				}
			}
			e = 0;
			for(DataModel<Integer, Integer, Float> data : trainingSet){
				e += Math.pow(data.preference - dotMatrix(P[data.userId], Q[data.itemId]), 2);
				for(int j = 0; j < k; j ++){
					e += (beta/2) * (Math.pow(P[data.userId][j], 2) + Math.pow(Q[data.itemId][j], 2));
				}
			}
			System.out.println("e = " + e);
			if(e < 0.001 || i == maxLoop -1){
				System.out.println("Stop at maxLoop: "+ i);
				break;
			}
		}
		
		return new Matrix<Float[][], Float[][]>(P, Q);
	}
	
	public static float dotMatrix(Float[] mtx1, Float[] mtx2){
		if(mtx1.length != mtx2.length){
			System.out.print("Error matrix length!");
			return 0;
		}
		float result = 0;
		for(int i = 0; i < mtx1.length; i++){
			result += mtx1[i] * mtx2[i];
//			System.out.println(mtx1[i] + " "+ mtx2[i] + " "+ result);
		}
		return result;
	}
	
	
	public static void main(String[] args) {

		DBManager db = new DBManager();
//		NMFDataModel data = db.readSQLData();
		NMFDataModel data = db.readCSVFile("data/test.csv");
		NMF nmf = new NMF();
		for(int k = 2; k <= 2; k++){
			Matrix<Float[][], Float[][]> trainingRes = nmf.matrixFactorization(data.allDataSet, k, data.numUsers, data.numItems, 500000, 0.002f, 0.02f); 
			//get RMSE
			float error = 0;
			float value = 0;
			for(DataModel<Integer, Integer, Float> d : data.testSet){
				float predicate = dotMatrix(trainingRes.P[d.userId], trainingRes.Q[d.itemId]);
				float actual = d.preference;
				error = predicate - actual;
				value += error * error;
			}
			value = value / data.testSet.size();
			double rmse = Math.sqrt(value);
			System.out.println("RMSE: "+ rmse +" k = "+ k);
			
			//test
//			for(DataModel<Integer, Integer, Float> d : data.allDataSet){
//				float predicate = dotMatrix(trainingRes.P[d.userId], trainingRes.Q[d.itemId]);
//				System.out.println(d.userId + "	" + d.itemId + " " + predicate);
//				
//			}
			for(int i = 0; i < 5; i++){
				for(int j = 0; j < 4; j++){
					float predicate = dotMatrix(trainingRes.P[i], trainingRes.Q[j]);
					System.out.print(predicate + "    ");
				}
				System.out.println();
			}
		}
		
	}

}
