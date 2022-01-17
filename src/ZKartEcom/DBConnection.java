//$Id$
package ZKartEcom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	protected Connection connect() {
		String jdbcURL = "jdbc:mysql://localhost:3306/lakdb";
		String uname = "root";
		String pwd = "root"; 
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(jdbcURL, uname, pwd);
		}catch(SQLException | ClassNotFoundException e) {
			System.out.println(e);
			return null;
		}
	}

}
