//$Id$
package ZKartEcom;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Stocks {
	private String category;
	private String brand;
	private String model;
	private int price;
	private int stocks;
	private int discount;
	private long productId;
	Connection conn = new DBConnection().connect();
	public void readStocksData () {
		try {
			Connection conn = new DBConnection().connect();
			Statement stmt = conn.createStatement();
			String sql = "create table stocks(Category VARCHAR(100), Brand VARCHAR(20), Model VARCHAR(100), Price INT(8), Stocks INT(3), Discount INT(2), PRIMARY KEY(Model, Category) )";
			//stmt.executeUpdate(sql);
			conn.close();
			Scanner input = new Scanner(new File("src/ZKartEcom/z-kart_db.txt"));
			//Skipping column name from file.
			if(input.hasNextLine()) {
				input.nextLine();
			}
			while (input.hasNextLine()) {
				String line = input.nextLine();
				//System.out.println("Line " + line);
				if(line.length() <=0) {
					continue;
				}
				try {
					Scanner data = new Scanner(line);
					
						category = data.next();
						brand = data.next();
						model = data.next();
						price = data.nextInt();
						stocks = data.nextInt();
						
						
						
					
					
					data.close();
				}catch (Exception e) {
					System.out.println("Exception occured in Reading Line data  stocks class" + e);
				}
				
				//System.out.println(email + "\t"+ encryptedpwd + "\t" +name +"\t" + mobile );
				
				saveData();
				
			//	sql = "DROP TABLE users";
		      //  stmt.executeUpdate(sql);
			}
			input.close();
		}catch(IOException e) {
			System.out.println("Exception occured in Reading stocks data " + e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void saveData () {
		try {
			//Connection conn = new DBConnection().connect();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO stocks VALUES(?, ?, ?, ?, ?, ?)");
			ps.setString(1, category);
			ps.setString(2, brand);
			ps.setString(3, model);
			ps.setInt(4, price);
			ps.setInt(5, stocks);
			ps.setInt(6, 0);
			
			ps.executeUpdate();
			conn.close();
			
		}catch (Exception e) {
			System.out.println(e);
		}
		addDiscount();
	}
	public void addDiscount() {
		try {
		
		
		PreparedStatement ps = conn.prepareStatement("UPDATE stocks set Discount=?  WHERE Stocks>=20");
		
		ps.setInt(1, 10);
		ps.executeUpdate();
		
		
		}catch (Exception e) {
			System.out.println(e);
		}
		
	}
	public void checkStockList() {
		// TODO Auto-generated method stub
		try {
			PreparedStatement ps = conn.prepareStatement("Select * from stocks where stocks <= 10");
			ResultSet rs  = ps.executeQuery();
			while(rs.next()) {
				HashMap<String, String> smap = new HashMap<>();
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int i =1;  i<=columnCount; i++ ) {
					smap.put(rsmd.getColumnName(i), rs.getString(i));
					
				}
				System.out.println("Printing items less than 10");
				System.out.println(smap);
				refill(smap);
			}
			System.out.println("All Stocks are Updated");
			new Users().giveUserOption();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void refill(HashMap<String, String> smap) {
		// TODO Auto-generated method stub
		
		try {
			PreparedStatement ps = conn.prepareStatement("Update stocks set Stocks =? where Model=? and Category=? ");
			int count = Integer.valueOf(smap.get("Stocks")) + 10;
			ps.setInt(1, count);
			ps.setString(2, smap.get("Model"));
			ps.setString(3, smap.get("Category"));
			ps.executeUpdate(); 
			
			System.out.println("Stocks Updated to "+ count);
			if(count > 20) {
				addDiscount();
			}
		} catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void updateStocks(HashMap<String, String> finalCart) {
		// TODO Auto-generated method stub
		try {
			PreparedStatement ps = conn.prepareStatement("Update stocks set Stocks =? where Model=? and Category=? ");
			int count = Integer.valueOf(finalCart.get("Stocks")) -1;
			ps.setInt(1, count);
			ps.setString(2, finalCart.get("Model"));
			ps.setString(3, finalCart.get("Category"));
			ps.executeUpdate(); 
		}
		catch (NumberFormatException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}


