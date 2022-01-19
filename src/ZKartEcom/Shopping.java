package ZKartEcom;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import DBBackup.DataBackup.OrdersData;

public class Shopping {
	private List<Integer> quantity = new ArrayList<Integer>();
	private String model;
	private String category;
	private HashMap<String, String> users;
	private List<HashMap> cart = new ArrayList<HashMap>();
	private List<HashMap> finalCart = new ArrayList<HashMap>();
	private Connection conn = new DBConnection().connect();

	public void shopping(HashMap<String, String> hmusers) {
		users = hmusers;
		
		choosePreference();
	}

	private void choosePreference() {
		System.out.println("Choose option 1 for Mobile shopping");
		System.out.println("Choose option 2 for Laptop shopping");
		System.out.println("Choose option 3 for Tablet shopping");
		Scanner sc = new Scanner (System.in);
		int option = sc.nextInt();
		if(option == 1) {
			mobileShopping();
		}else if(option == 2) {
			laptopShopping();
		}else if(option == 3) {
			tabletShopping();
		}
	}

	private void tabletShopping() {
		// TODO Auto-generated method stub
		ResultSet rs = getDesiredProductResultSet("tablet");
		//ResultSet backupRs = rs;
		List<HashMap> mobileBrandList = new ArrayList<HashMap>();
		
		int index =0;
		try {
			while (rs.next()) {
				HashMap <String, String> mobileBrands = new HashMap<String, String>();
				ResultSetMetaData rsmd = rs.getMetaData();
				System.out.print("model \t\t brand \t\t price\n");
				System.out.println(rs.getString("Model") +"\t\t"+ rs.getString("Brand") +"\t\t"+ rs.getString("Price"));
				for(int i = 1, l = rsmd.getColumnCount(); i < l; i++) {
					mobileBrands.put(rsmd.getColumnLabel(i), rs.getString(i));
					
				}
				mobileBrandList.add(mobileBrands);
				index++;
			}
			chooseBrandAndModel(mobileBrandList);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	private void laptopShopping() {
		// TODO Auto-generated method stub
		ResultSet rs = getDesiredProductResultSet("laptop");
		//ResultSet backupRs = rs;
		List<HashMap> mobileBrandList = new ArrayList<HashMap>();
		
		int index =0;
		try {
			while (rs.next()) {
				HashMap <String, String> mobileBrands = new HashMap<String, String>();
				ResultSetMetaData rsmd = rs.getMetaData();
				System.out.print("model \t\t brand \t\t price\n");
				System.out.println(rs.getString("Model") +"\t\t"+ rs.getString("Brand") +"\t\t"+ rs.getString("Price"));
				for(int i = 1, l = rsmd.getColumnCount(); i < l; i++) {
					mobileBrands.put(rsmd.getColumnLabel(i), rs.getString(i));
					
				}
				mobileBrandList.add(mobileBrands);
				index++;
			}
			chooseBrandAndModel(mobileBrandList);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	private void mobileShopping() {
		// TODO Auto-generated method stub
		
		ResultSet rs = getDesiredProductResultSet("mobile");
		//ResultSet backupRs = rs;
		List<HashMap> mobileBrandList = new ArrayList<HashMap>();
		
		int index =0;
		try {
			while (rs.next()) {
				HashMap <String, String> mobileBrands = new HashMap<String, String>();
				ResultSetMetaData rsmd = rs.getMetaData();
				System.out.print("model \t\t brand \t\t price\n");
				System.out.println(rs.getString("Model") +"\t\t"+ rs.getString("Brand") +"\t\t"+ rs.getString("Price"));
				for(int i = 1, l = rsmd.getColumnCount(); i < l; i++) {
					mobileBrands.put(rsmd.getColumnLabel(i), rs.getString(i));
					
				}
				mobileBrandList.add(mobileBrands);
				index++;
			}
			chooseBrandAndModel(mobileBrandList);
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	private void chooseBrandAndModel(List<HashMap> brandList) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.println("Choose Brand and Model");
		System.out.println("Enter Brand Name");
		String brndName = sc.next();
		System.out.println("Enter Model Name");
		String modelName = sc.next();
		try {
		PreparedStatement ps = conn.prepareStatement("Select * from stocks where Brand=? and Model =?");
		
			ps.setString(1, brndName);
			ps.setString(2, modelName);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				AddToCart(brndName, modelName, brandList);
			}else {
				System.out.println("Incorrect product selected.Either Brand name or Model name mismatch");
				chooseBrandAndModel(brandList);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}

	private void AddToCart(String brndName, String modelName, List<HashMap> brandList) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
//		System.out.println("Enter No.of Qunatity to be placed");
//		int quants = sc.nextInt();
//		quantity.add(quants);
		System.out.println("Press 1 to continue shopping or 2 to checkout");
		
		int option =sc.nextInt();
		if(option ==1) {
			choosePreference();
		}else {
			
			HashMap<String, String> hm = null;
			for (int i =0, l = brandList.size(); i< l; i++) {
				String tempBrnd = brandList.get(i).get("Brand").toString().toLowerCase();
				String tempMdl = brandList.get(i).get("Model").toString().toLowerCase();
				if(tempBrnd.equals(brndName.toLowerCase()) && tempMdl.equals(modelName.toLowerCase())) {
					hm = brandList.get(i);	 
				}
			}
			cart.add(hm);
			checkOut();
		}
	}

	private void checkOut() {
		// TODO Auto-generated method stub
		boolean [] avail = new boolean[cart.size()];
		for (int i =0, l = cart.size(); i < l; i++) {
			avail[i] = checkAvailbility(cart.get(i));
			if(avail[i]) {
				finalCart.add(cart.get(i));
				System.out.print("Model \t\t Brand \t\t Price\n");
				System.out.println(cart.get(i).get("Model") +"\t\t"+ cart.get(i).get("Brand") +"\t\t"+ cart.get(i).get("Price"));
			}
		}
		if(avail.length == 0) {
			System.out.println("All Items are out of stocks!!!");
			
		} 
		placeOrder();
		
		
	}

	private void placeOrder() {
		if(finalCart.size() > 0) {
			
			
			
			printInvoice();
		}
		
		
	}

	private void populatePurchaseHistory() {
		// TODO Auto-generated method stub
		
	}

	private void printInvoice() {
		
		
		String discountCode = new Users().checkUserDiscount(users.get("Email"));
		
		
		Scanner sc = new Scanner (System.in);
		
		double discPer=0;
		if(discountCode != null) {
			
			System.out.println("Discount code For you:" + discountCode);
			System.out.println("Retype the Discount Code");
			String userEnteredDiscount = sc.next();
			if(discountCode.equals(userEnteredDiscount)) {
				discPer = getDiscountPercentage();
				System.out.println("Discount Availed " +discPer+" Percent");   
			}
		}
		updateOrderDetails(users.get("Email"), discPer);
		//populatePurchaseHistory(invoiceNumber, users.get("Email"));
		
	}

	private void updateOrderDetails(String mail, double discPer) {
		String invoiceNumber = generateInvoiceNumber();
		double amount = 0;
		
		try {
			for (int i =0 , l = finalCart.size(); i< l; i++) {
				amount = Double.parseDouble(finalCart.get(i).get("Price").toString()); 
				
				System.out.println(discPer);
				if(discPer > 0) {
					amount =amount - (amount * (discPer/100));
				}
					
				PreparedStatement ps = conn.prepareStatement("INSERT INTO orders VALUES(?, ?, ?, ?, ?, ?, ?)");
				ps.setString(1,mail);
				ps.setString(2, invoiceNumber);
				ps.setString(3, finalCart.get(i).get("Model").toString());
				ps.setString(4, finalCart.get(i).get("Category").toString());
				ps.setString(5, amount+"");
				ps.setString(6, java.time.LocalDate.now().toString());
				ps.setString(7, discPer+"");
				ps.executeUpdate();
				
				System.out.print("Model \t\t Brand \t\t Price\n");
				System.out.println(finalCart.get(i).get("Model") +"\t\t"+ finalCart.get(i).get("Brand") +"\t\t"+ amount);
				
				
				System.out.println("Order Placed SucccesFully.Thanks for shopping");
				new Stocks().updateStocks(finalCart.get(i));
				UpdateDiscountDetails();
				new Users().giveUserOption();
				
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void UpdateDiscountDetails() {
		// TODO Auto-generated method stub
		int times=0;
		try {
		PreparedStatement ps = conn.prepareStatement("Select * from discount where Email=?");
		ps.setString(1,users.get("Email"));
		ResultSet rs = ps.executeQuery();
		if(rs.next()) {
			times = rs.getInt("DiscountAvailedTimes");
			if(times < 3) {
				times++;
			}
		}
		ps = conn.prepareStatement("Update discount Set DiscountAvailedTimes=? where Email=?");
		ps.setInt(1,times);
		ps.setString(2,users.get("Email"));
		ps.executeUpdate();
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}

	private int getDiscountPercentage() {
		Random rn = new Random();
        
        int randomNumber = rn.nextInt(30);
        while(randomNumber<=20 || randomNumber >=30 ) {
        	randomNumber = rn.nextInt(30);
        }
       
		return randomNumber;
	}

	private String generateInvoiceNumber() {
		 Random rnd = new Random();
		    int number = rnd.nextInt(999999);

		    // this will convert any number sequence into 6 character.
		    return String.format("%06d", number);
		
	}

	private boolean checkAvailbility(HashMap<String, String> hm) {
		// TODO Auto-generated method stub
		boolean flag = true;
		if(Integer.valueOf(hm.get("Stocks")) <= 0) {
			System.out.println("Sorry ,Selected product not available");
			System.out.print("Model \t\t Brand \t\t Stocks\n");
			System.out.println(hm.get("Model") +"\t\t"+ hm.get("Brand") +"\t\t"+ hm.get("Stocks"));
			flag = false;
		}
		return flag ;
	}

	private ResultSet getDesiredProductResultSet(String categoryName) {
		try {
			PreparedStatement ps = conn.prepareStatement("Select * from stocks where Category=?");
			ps.setString(1, categoryName);
			return ps.executeQuery();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return null;
	}

	public void getOrderHistory(HashMap<String, String> users2) {
		// TODO Auto-generated method stub
		try {
			users = users2;
			PreparedStatement ps = conn.prepareStatement("Select * from orders Where Email=?");
			ps.setString(1, users.get("Email"));
			ResultSet rs  = ps.executeQuery();
			//ResultSet backuprs = rs;
			while(rs.next()) {
				System.out.println("InvoiceNumber "+ rs.getString("InvoiceNumber") );
				System.out.println("Date "+ rs.getString("PurchaseDate"));
				System.out.println("Category\tModel\tPrice");
				System.out.println(rs.getString("Category")+"\t"+rs.getString("Model")+"\t"+rs.getString("Amount"));

				
			}
			  try{
		       CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> writeUsersOrdersData());
		        while (!completableFuture.isDone()) {
		        	new Users().giveUserOption();
		           
			      } 
		        String result = completableFuture.get();
		         System.out.println(result);
		        }
		 
		        catch(Exception e){
		            e.printStackTrace();
		        }
		    
	
		}catch (SQLException e) {
			e.printStackTrace();
		}
	
		
	}

	private String writeUsersOrdersData() {
		// TODO Auto-generated method stub
		String result = "";
		try {
			PreparedStatement ps = conn.prepareStatement("Select * from orders Where Email=?");
			ps.setString(1, users.get("Email"));
			ResultSet rs  = ps.executeQuery();
			
			File theDir = new File( users.get("Name"));
			if (!theDir.exists()){
			    theDir.mkdirs();
			}
			OrdersData.Builder ub = OrdersData.newBuilder();
			try {
				String filename = users.get("Name")+"/orders.dat";
				FileOutputStream fout =new FileOutputStream(filename);
				while(rs.next()) {
					try {
						ub.setEmail(rs.getString("Email"));
						ub.setAmount((int)Double.parseDouble(rs.getString("Amount")));
						ub.setCategory(rs.getString("Category"));
						ub.setDiscountavailed(rs.getString("DiscountAvailed"));
						ub.setInvoicenumber(rs.getString("InvoiceNumber"));
						ub.setModel(rs.getString("Model"));
						ub.setPurchasedate(rs.getString("PurchaseDate"));
						
			 			
						ub.build().writeTo(fout);
						result = "Purchase History Persisted";
					}catch (Exception e) {
						//UsersData.getDefaultInstance().writeTo(cos);
						e.printStackTrace();
					}
				}
				
			} catch (FileNotFoundException | SQLException e) {
				
				e.printStackTrace();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
