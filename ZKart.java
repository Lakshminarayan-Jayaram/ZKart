//$Id$
package ZKartEcom;
import java.io.File;
import java.sql.*;
import java.io.IOException;
import java.util.Scanner;


public class ZKart {
	
	
	
	
	
	public static void main(String args[]) {
		Users users = new Users();
		users.readUserData();
		new Stocks().readStocksData();
		try {
			Connection conn = new DBConnection().connect();
			Statement st = conn.createStatement();
			String sql = "DROP TABLE users";
//		    st.executeUpdate(sql);
//		    sql = "DROP TABLE stocks";
//		    st.executeUpdate(sql);
		  //  sql = "DROP TABLE stocks"
			
		}catch (SQLException e) {
			System.out.println(e);
		}
		
		users.giveSignInOrSignup();
		
	}
	
}
