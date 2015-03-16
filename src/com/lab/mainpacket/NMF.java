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
				//update P
				for(int j= 0; j < k; j++){
					P[data.userId][j] += alpha * (error * Q[data.itemId][j]) - beta * P[data.userId][j];
				}
				error = data.preference - dotMatrix(P[data.userId], Q[data.itemId]);
				//update Q
				for(int j= 0; j < k; j++){
					Q[data.itemId][j] += alpha * (error * P[data.userId][j]) - beta * Q[data.itemId][j];
				}
				
				cost += error * error;
			}
			cost = cost/ trainingSet.size();
			if(Math.abs((oldCost - cost)/cost) < 0.00001){
				System.out.println("Stop at maxLoop: "+ i);
				break;
			}
			oldCost = cost;
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
		}
		return result;
	}
	
	
	public static void main(String[] args) {

		DBManager db = new DBManager();
//		NMFDataModel data = db.readSQLData();
		NMFDataModel data = db.readCSVFile("data/test.csv");
		NMF nmf = new NMF();
		for(int k = 20; k <= 20; k++){
			Matrix<Float[][], Float[][]> trainingRes = nmf.trainingData(data.allDataSet, k, data.numUsers, data.numItems, 5000, 0.002f, 0.02f); 
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
			for(DataModel<Integer, Integer, Float> d : data.allDataSet){
				float predicate = dotMatrix(trainingRes.P[d.userId], trainingRes.Q[d.itemId]);
//				float actual = d.preference;
				System.out.println(d.userId + "	" + d.itemId + " " + predicate);
				
			}
		}
		
	}

}
