//$Id$
package ZKartEcom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

import DBBackup.DataBackup.*;

//import .protobuf.AdminBackup;




public class Users {
	private String email;
	private String  encryptedpwd;
	private String name;
	private String mobile;
	protected int isAdmin = 0;
	private boolean isAdminUser = false;
	Connection conn = new DBConnection().connect();
	HashMap < String, String> users = new HashMap<>();
	public void readUserData () {
		
		try {
			
			Statement stmt = conn.createStatement();
			String sql = "create table users(Email VARCHAR(100) PRIMARY KEY, Encryptedpwd VARCHAR(20), Name VARCHAR(100), Mobile VARCHAR(20), IsAdmin INT(1), Old_EncryptedPwd VARCHAR(200))";
			//stmt.executeUpdate(sql);
			Scanner input = new Scanner(new File("src/ZKartEcom/zusers_db.txt"));
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
					
					//while(!data.hasNextInt()) {
						email = data.next();
						encryptedpwd = data.next();
						name = data.next();
						mobile = data.next();
						isAdmin = data.nextInt();
					//}
					
					data.close();
				}catch (Exception e) {
					System.out.println("Exception occured in Reading Line data Users Class " + e);
				}
				
				//System.out.println(email + "\t"+ encryptedpwd + "\t" +name +"\t" + mobile );
				
				saveData(email, encryptedpwd, name, mobile);
				
			
			}
			input.close();
			
		}catch(IOException e) {
			System.out.println("Exception occured in Reading users data " + e);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//signIn();
	}
	private void saveData (String email, String encryptedpwd, String name, String mobile) {
		try {
			
			PreparedStatement ps = conn.prepareStatement("INSERT INTO users VALUES(?, ?, ?, ?, ?, ? )");
			ps.setString(1, email);
			ps.setString(2, encryptedpwd);
			ps.setString(3, name);
			ps.setString(4, mobile);
			ps.setInt(5, isAdmin);
			ps.setString(6, null);
			
			ps.executeUpdate();
			//conn.close();
			
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	public void giveSignInOrSignup() {
		System.out.println("Enter option 1 to Sign In");
		System.out.println("Enter option 2 to Sign Up");
		Scanner sc = new Scanner(System.in);
		int option = sc.nextInt();
		if(option == 1) {
			signIn();
		}else {
			signUp();
		}
			
	}
	public void getUserHM(String mail) {
		try {
			PreparedStatement ps = conn.prepareStatement("Select * from users where Email =?");
			ps.setString(1, mail);
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int columnCount = rsmd.getColumnCount();
				for (int i =1;  i<=columnCount; i++ ) {
					users.put(rsmd.getColumnName(i), rs.getString(i));
				}
				
			}
		}catch (Exception e) {
			System.out.println("Exception occured in getting users details " + e);		
		}
		System.out.println("USERS "+ users);
	}
	public void signIn() {
		System.out.println("Enter Email :" );
		Scanner sc = new Scanner(System.in);
		String mail = sc.next();
			
		if(checkEmailFormat(mail)) {
			boolean accountAvailable = checkValidUser(mail);
			if(accountAvailable) {
				getPasswordFromUser(mail, sc);
				
			}else {
				System.out.println("Email Id not available. Please Press 1 to sign up");
				int option = sc.nextInt();
				if(option == 1) {
					signUp();
				}
			}
		}else {
			signIn();
		}
		
	}
	public void getPasswordFromUser(String mail, Scanner sc) {
		System.out.println("Enter Password :" );
		String pwd = sc.next();
		boolean pwdVerified = verifyPassword(mail, pwd);
		if(pwdVerified) {
			System.out.println("Login Successful");
			//Shopping needs to be done;
			getUserHM(mail);
			//getDiscountForUser(mail);
			boolean isAdminUser =  users.get("IsAdmin").equals("1");
			System.out.println("ADMIN " + isAdminUser);
			if(isAdminUser && users.get("Encryptedpwd").equals("xyzzy")) {
				System.out.println("Please change your Default password.");
				changePwd();
			}
			giveUserOption(isAdminUser, sc);
			
			
		}else {
			System.out.println("Incorrect Password. Please type the password again.");
			getPasswordFromUser(mail,sc);
		}
	}
	private void getDiscountForUser(String mail) {
		try {
			PreparedStatement ps = conn.prepareStatement("Select * from orders where Email =?");
			ps.setString(1, mail);
			ResultSet rs  =ps.executeQuery();
			int rowCount = 0;
			double totalAmount = 0;
			for (int i =0; rs.next(); i++) {
				System.out.println("RS Size "+rs.getFetchSize());
				rowCount++;
				System.out.println(rs.getString("Amount"));
				totalAmount =Double.parseDouble(rs.getString("Amount"));
			}
			if(rowCount >= 3 || totalAmount >=20000) {
				ps = conn.prepareStatement("Insert into discount VALUES(?, ?, ?)");
				ps.setString(1, mail);
				ps.setString(2, getDiscountCode());
				ps.setInt(3, 0);
				ps.executeUpdate();
				 
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	private String getDiscountCode() {
		// TODO Auto-generated method stub
		
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz";
  
        
        StringBuilder sb = new StringBuilder(6);
  
        for (int i = 0; i < 6; i++) {
  
           
            int index
                = (int)(AlphaNumericString.length()
                        * Math.random());
  
          
            sb.append(AlphaNumericString
                          .charAt(index));
        }
  
        return sb.toString();
		
	}
	protected void giveUserOption() {
		giveUserOption(isAdminUser, new Scanner(System.in));
	}
	private void giveUserOption(boolean isAdminUser, Scanner sc) {
		// TODO Auto-generated method stub
		System.out.println("Choose Option 1 to change Password");
		if(!isAdminUser) {
			System.out.println("Choose Option 2 to Shopping");
			System.out.println("Choose Option 3 to get Order History");
		}else {
			System.out.println("Choose Option 2 to check stock list");
			System.out.println("Choose Option 3 to get Data Backup"); 	
		}
		
		int option = sc.nextInt();
		if(option == 1) {
			changePwd();
		}else if(option == 2) {
			if(!isAdminUser) {
				new Shopping().shopping(users);
			}else {
				new Stocks().checkStockList();
			}
			
		}else if(option == 3 ) {
			if(!isAdminUser) {
				new Shopping().getOrderHistory(users);
			}else {
				getAdminDataBackup();
			}
			
		}
		
	}
	private void getAdminDataBackup() {
		// TODO Auto-generated method stub
		try {
		PreparedStatement ps = conn.prepareStatement("Select * from users");
		ResultSet rs  = ps.executeQuery();
		FileOutputStream fout = null;
		CodedOutputStream cos = null;
		writeUsersData(rs,fout, cos);
		ps = conn.prepareStatement("Select * from orders");
		rs  = ps.executeQuery();
		writeOrdersData(rs,fout, cos);
		ps = conn.prepareStatement("Select * from stocks");
		rs = ps.executeQuery();
		writeStocksData(rs,fout);
		ps = conn.prepareStatement("Select * from discount");
		rs=ps.executeQuery();
		writeDiscountData(rs, fout);
		
		}catch(SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void writeDiscountData(ResultSet rs, FileOutputStream fout) {
		// TODO Auto-generated method stub
		DiscountData.Builder ub =DiscountData.newBuilder();
		try {
			fout =new FileOutputStream("discount.dat");
			while(rs.next()) {
				try {
					ub.setEmail(rs.getString("Email"));  
					
					ub.setDiscountcode(rs.getString("Discountcode"));
					ub.setDiscountavailedtimes(rs.getInt("DiscountAvailedTimes"));
					
					
					
					ub.build().writeTo(fout);
				}catch (Exception e) {
					//UsersData.getDefaultInstance().writeTo(cos);
					e.printStackTrace();
				}
			}
			
		} catch (FileNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	private void writeStocksData(ResultSet rs, FileOutputStream fout) {
		// TODO Auto-generated method stub
		StocksData.Builder ub =StocksData.newBuilder();
		try {
			fout =new FileOutputStream("stocks.dat");
			while(rs.next()) {
				try {
						ub.setCategory(rs.getString("Category"));
						ub.setBrand(rs.getString("Brand"));
						ub.setModel(rs.getString("Model"));
						ub.setPrice(rs.getInt("Price"));
						ub.setStcocks(rs.getInt("Stocks"));
						ub.setDiscount(rs.getInt("Discount"));
						
						ub.build().writeTo(fout);
				}catch (Exception e) {
					//UsersData.getDefaultInstance().writeTo(cos);
					e.printStackTrace();
				}
			}
			
		} catch (FileNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	private void writeOrdersData(ResultSet rs, FileOutputStream fout, CodedOutputStream cos) {
		// TODO Auto-generated method stub
		OrdersData.Builder ub = OrdersData.newBuilder();
		try {
			fout =new FileOutputStream("orders.dat");
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
				}catch (Exception e) {
					//UsersData.getDefaultInstance().writeTo(cos);
					e.printStackTrace();
				}
			}
			
		} catch (FileNotFoundException | SQLException e) {
			
			e.printStackTrace();
		}
		
	}
	private void writeUsersData(ResultSet rs, FileOutputStream fout, CodedOutputStream cos) throws SQLException, IOException {
		// TODO Auto-generated method stub
ResultSetMetaData rsmd = rs.getMetaData();
		
		UsersData.Builder ub = UsersData.newBuilder();
		fout =new FileOutputStream("users.dat");
		//ub.mergeFrom(CodedInputStream.newInstance(fout));
		cos = CodedOutputStream.newInstance(fout);
	
			while(rs.next()) {
				try {
					ub.setEmail(rs.getString("Email"));
					ub.setEpwd(rs.getString("EncryptedPwd"));
					ub.setName(rs.getString("Name"));
					ub.setMobile(rs.getString("Mobile"));
					ub.setIsadmin(rs.getInt("IsAdmin"));
					if(rs.getString("Old_EncryptedPwd") != null) {
						ub.setOldepwd(rs.getString("Old_EncryptedPwd"));
					}
		 			
					ub.build().writeTo(fout);
				}catch (Exception e) {
					//UsersData.getDefaultInstance().writeTo(cos);
					e.printStackTrace();
				}
			}
			readingFromFile();
		
		
	}
	private void readingFromFile() {
		// TODO Auto-generated method stub
		try {
			UsersData ub = UsersData.parseFrom(new FileInputStream("users.dat"));
			System.out.println("DESERIALIZE ::"+ ub.getEmail() +"\t"+ub.getEpwd()+"\t"+ub.getMobile()+"\t"+ub.getName()+"\t"+ub.getIsadmin()+"\t"+ub.getOldepwd());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void changePwd() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter New  Password :" );
		String pwd = sc.next();
		while(!checkPwdFormat(pwd) || !verifyOldPassword(pwd)) {
			System.out.println("Enter New Password :" );
			pwd = sc.next();
		}
		System.out.println("Retype New  Password :" );
		String retypePwd = sc.next();
		if(pwd.equals(retypePwd)) {
			updateNewPwd(pwd);
		}else {
			System.out.println("Both passwords didn't match");	
			changePwd();
		}
		sc.close();
	}
	public boolean verifyOldPassword(String pwd) {
		String oldEPwd = users.get("Old_EncryptedPwd");
		boolean flag = false;
		if(oldEPwd == null) {
			flag = true;
		}else {
			List <String> oldPwdList = new ArrayList<String>(Arrays.asList(oldEPwd.split(",")));
			if(oldEPwd.indexOf(encryptPwd(pwd)) > -1) {
				System.out.println("Your new password cannot be the same as your old password. Kindly type the new password");
				flag = false;
			}else {
				flag = true;
			}
			
		}
		
		return flag;
	}
	public void updateNewPwd(String pwd) {
		try {
			String str = users.get("Old_EncryptedPwd"), ePwd= encryptPwd(pwd);
			if(str == null) {
				str = users.get("Encryptedpwd");
			}else {
				//Persisting max 3 old passwords
				List <String> ind = new ArrayList<String>(Arrays.asList(str.split(",")));
				if(ind.size() < 3) {
					str = users.get("Encryptedpwd") + "," + str;
				}else {
					str = str.substring(0, str.lastIndexOf(","));
					str = users.get("Encryptedpwd") + "," + str;
				}
			}
		
			
			PreparedStatement ps = conn.prepareStatement("UPDATE users set Encryptedpwd=?, Old_EncryptedPwd=?  WHERE Email=?");
			ps.setString(1, ePwd);
			ps.setString(2, str);
			ps.setString(3, users.get("Email"));
			ps.executeUpdate();
			System.out.println("SuccessFully Password Changed. Please Login again.");	
			signIn();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public boolean checkEmailFormat(String mail) {
		final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
		boolean flag = matcher.find();
        if(!flag) {
        	System.out.println("Invalid Email format. Please enter the correct format");
        }
		return flag;
	}
	public boolean checkPwdFormat(String pwd) {
		boolean flag = false;
		//^(?=(?:\D*\d){2})(?=(?:[^a-z]*[a-z]){2})(?=[^A-Z]*[A-Z]).*
		final Pattern VALID_PWD_REGEX = Pattern.compile("^(?=(?:\\D*\\d){2})(?=(?:[^a-z]*[a-z]){2})(?=[^A-Z]*[A-Z]).*");
		Matcher matcher = VALID_PWD_REGEX.matcher(pwd);
		flag = matcher.find() && pwd.length() >= 6 && pwd.length() <= 15;
		 if(!flag) {
	        	System.out.println("Invalid PassWord format. Your password must have at least 2 lower case, 2 upper case and 2 numbers with a minimum length of 6");
	      }
		return flag;
	}
	public void signUp() {
		System.out.println("Enter Email :" );
		Scanner sc = new Scanner(System.in);
		String mail = sc.next();
		while(!checkEmailFormat(mail)) {
			System.out.println("Enter Email :" );
			 mail = sc.next();
		}
		System.out.println("Enter Password :" );
		String pwd = sc.next();
			
		while(!checkPwdFormat(pwd)) {
			System.out.println("Enter Password :" );
			pwd = sc.next();
		}
		pwd = encryptPwd(pwd);
		System.out.println("Enter Name :" );
		String name = sc.next();
		System.out.println("Enter Mobile :" );
		String mobile = sc.next();
		saveData(mail, pwd, name, mobile);
		System.out.println("Sign up Successful");
		
		System.out.println("Please Login to start shopping");
		signIn();
		
		
		
	} 
	public boolean checkValidUser (String email) {
		boolean flag = false;
		PreparedStatement ps;
		try {
			ps = conn.prepareStatement("SELECT Email FROM users where Email=?");

			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				if(email.contentEquals(rs.getString("Email"))) {
					flag = true;
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return flag;
	}
	public String encryptPwd(String pwd) {
		char[]convertedPwd = new char[pwd.length()];
		for (int i =0 , l = pwd.length(); i < l; i++) {
			char c = pwd.charAt(i);
			int ascii = (int) c+1;
			if(ascii > 122) {
				ascii-=26;
			}
			convertedPwd[i] = (char) ascii;	
		}
		return new String(convertedPwd);
	}
	public boolean verifyPassword(String mail, String pwd) {
		boolean returnStmt = false;
		String ePwd="";
		try {
			PreparedStatement ps= conn.prepareStatement("SELECT Encryptedpwd FROM users where Email=?");
			ps.setString(1, mail);
			ResultSet rs  = ps.executeQuery();
		
			if(rs.next()) {
				ePwd = rs.getString("Encryptedpwd");
			}
			
		}catch (SQLException e) {
			System.out.println("Exception occured while fetching password from DB"+ e);
		}
		
		if(encryptPwd(pwd).equals(ePwd)) {
				returnStmt = true;
		}
				
		return returnStmt;
		
	}
	public String checkUserDiscount() {
		// TODO Auto-generated method stub
		PreparedStatement ps;
		String discount = null;
		try {
			ps = conn.prepareStatement("Select * from discount where Email=?");
			ps.setString(1, users.get("Email"));
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				if(rs.getInt("DiscountAvailedTimes") < 3) {
					discount = rs.getString("Discountcode");
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return discount;
		
	}

}
