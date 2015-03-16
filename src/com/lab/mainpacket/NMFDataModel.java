package com.lab.mainpacket;

import java.util.List;


public class NMFDataModel {
	public final List<DataModel<Integer, Integer, Float>> allDataSet;
	public final List<DataModel<Integer, Integer, Float>> trainingSet;
	public final List<DataModel<Integer, Integer, Float>> testSet;
	public final int numUsers;
	public final int numItems;
	
	public NMFDataModel(List<DataModel<Integer, Integer, Float>> trainingSet, List<DataModel<Integer, Integer, Float>>testSet, int numUsers,int numItems){
		this.trainingSet = trainingSet;
		this.testSet = testSet;
		this.numItems = numItems;
		this.numUsers = numUsers;
		this.allDataSet = null;
	}
	public NMFDataModel(List<DataModel<Integer, Integer, Float>> trainingSet, List<DataModel<Integer, Integer, Float>>testSet, List<DataModel<Integer, Integer, Float>> allData, int numUsers,int numItems){
		this.trainingSet = trainingSet;
		this.testSet = testSet;
		this.numItems = numItems;
		this.numUsers = numUsers;
		this.allDataSet = allData;
	}
	
	public static class DataModel<X, Y, Z>{
		public final X userId;
		public final Y itemId;
		public final Z preference;
		
		public DataModel(X userId, Y itemId, Z preference){
			this.userId = userId;
			this.itemId = itemId;
			this.preference = preference;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof  DataModel) {
				return userId.equals(((DataModel) obj).userId) && itemId.equals(((DataModel) obj).itemId) && preference.equals(((DataModel) obj).preference);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return userId.hashCode() ^ itemId.hashCode() ^ preference.hashCode();
		}
	}
	public static class Matrix<X, Y>{
		public final X P;
		public final Y Q;
		
		public Matrix(X P, Y Q){
			this.P = P;
			this.Q = Q;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof  Matrix) {
			return P.equals(((Matrix) obj).P) && Q.equals(((Matrix) obj).Q);
			}
			return false;
		}

		@Override
		public int hashCode() {
		return P.hashCode() ^ Q.hashCode();
		}
	}
}


