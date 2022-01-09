//$Id$
package ZKartEcom;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	
	public void readStocksData () {
		try {
			Connection conn = new DBConnection().connect();
			Statement stmt = conn.createStatement();
			String sql = "create table stocks(Category VARCHAR(100), Brand VARCHAR(20), Model VARCHAR(100) PRIMARY KEY, Price INT(8), Stocks INT(3), Discount INT(2))";
		//	stmt.executeUpdate(sql);
			conn.close();
			Scanner input = new Scanner(new File("/home/local/ZOHOCORP/lakshmi-5870/Downloads/Personal/zkart/z-kart_db.txt"));
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
			Connection conn = new DBConnection().connect();
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
			Random rn = new Random();
            
              int randomNumber = rn.nextInt(30);
              System.out.println("Random No : " + randomNumber); 
             
		Connection conn = new DBConnection().connect();
		PreparedStatement ps = conn.prepareStatement("UPDATE stocks set Discount=?  WHERE Stocks>=20");
		
		ps.setInt(1, 10);
		ps.executeUpdate();
		
		
		}catch (Exception e) {
			System.out.println(e);
		}
		
	}
		
}


