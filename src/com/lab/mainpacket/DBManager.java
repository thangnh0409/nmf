package com.lab.mainpacket;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.lab.mainpacket.NMFDataModel.DataModel;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class DBManager {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3307/amobi";
	static final String USER = "root";
	static final String PASS = "";
	
	Connection conn;
	Statement stmt;
	int nextDev = 0;
	int nextAd = 0;
	int deviceIndex = 0;
	int adIndex = 0;
	
	 public static HashMap<String, Integer> deviceDic = new HashMap<String, Integer>();
	 public static HashMap<String, Integer> adDic = new HashMap<String, Integer>();
	
	public DBManager(){
		conn = null;
		stmt = null;
	}
	
	public NMFDataModel readSQLData(){
		ArrayList<DataModel<Integer, Integer, Float>> data = new ArrayList<DataModel<Integer, Integer, Float>>();
		long startTime = System.currentTimeMillis();
		try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = (Connection) DriverManager.getConnection(DB_URL,USER,PASS);

		      //STEP 4: Execute a query
		      System.out.println("Creating statement...");
		      stmt = (Statement) conn.createStatement();
		      String sql;
		      
		      //Test app-ad
		      sql = "SELECT `widget_code`, `link_id`, SUM(`clicks`) as clicks, SUM(`views`) as views FROM `widget_publisher_code` GROUP BY `widget_code`,`link_id`";
		      ResultSet rs = stmt.executeQuery(sql);

		      while(rs.next()){
		         String app_id = rs.getString("widget_code");
		         String link_id = rs.getString("link_id");
		         int clicks = rs.getInt("clicks");
		         int views = rs.getInt("views");
		         float preference = clicks/(float)views;
		         
		         if(deviceDic.containsKey(app_id)){
		        	 deviceIndex = deviceDic.get(app_id);
		         }else{
		        	 deviceIndex = nextDev++;
		        	 deviceDic.put(app_id, deviceIndex);
		         }
		         if(adDic.containsKey(link_id)){
		        	 adIndex = adDic.get(link_id);
		         }else{
		        	 adIndex = nextAd++;
		        	 adDic.put(link_id, adIndex);
		         }
		         
		         System.out.println("userId: "+ deviceIndex + " itemId: "+ adIndex+ " prefer: "+ preference);
		         data.add(new DataModel<Integer, Integer, Float>(deviceIndex, adIndex, preference));
		      }
		      
		      System.out.println("Data size: "+ data.size());
		      
		      Collections.shuffle(data);
		      int index = 4*data.size()/5;
		      
		      long endTime = System.currentTimeMillis();
		      System.out.println("Time read DB " + (endTime - startTime) + " milliseconds");
		        
		      //STEP 6: Clean-up environment
		      rs.close();
		      stmt.close();
		      conn.close();
		      
		      return new NMFDataModel(data.subList(0, index), data.subList(index, data.size()), nextDev, nextAd);
		      
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		return null;
	}
	
	public NMFDataModel readCSVFile(String filePath){
		BufferedReader br = null;
		String line = "";
		int userId;
		int itemId;
		int userCount = 0;
		int itemCount = 0;
		HashMap<Integer, Integer> userDic = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> itemDic = new HashMap<Integer, Integer>();
		float preference;
		ArrayList<DataModel<Integer, Integer, Float>> data = new ArrayList<DataModel<Integer, Integer, Float>>();
		try {
			br = new BufferedReader(new FileReader(filePath));
			while((line = br.readLine()) != null){
				String[] obj = line.split(",");
				userId = Integer.parseInt(obj[0]);
				if(!userDic.containsKey(userId)){
					userCount++;
					userDic.put(userId, userCount);
				}
				itemId = Integer.parseInt(obj[1]);
				if(!itemDic.containsKey(itemId)){
					itemCount++;
					itemDic.put(itemId, itemCount);
				}
				preference = Float.parseFloat(obj[2]);
				System.out.println("userId: "+ userId + " itemId: "+ itemId+ " prefer: "+ preference);
				data.add(new DataModel<Integer, Integer, Float>(userId, itemId, preference));
			}
			
			Collections.shuffle(data);
			int index = 4*data.size()/5;
			return new NMFDataModel(data.subList(0, index), data.subList(index, data.size()), data, userCount+1, itemCount+1);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
