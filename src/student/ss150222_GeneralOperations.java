package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import operations.GeneralOperations;

public class ss150222_GeneralOperations implements GeneralOperations {

	@Override
	public Calendar getCurrentTime() {

		Connection con = DB.getInstance().getConnection();

		String query = "SELECT time FROM Time WHERE timeID = 1";
		
		Calendar c = new GregorianCalendar();
		try {
			PreparedStatement ps = con.prepareStatement(query);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				Date d = new Date(rs.getTimestamp(1).getTime());
				c.setTime(d);
				return c;
			} else {
			//	System.out.println("No buyerID ");
				return null;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}

	}

	@Override
	public void setInitialTime(Calendar time) {
		Connection con = DB.getInstance().getConnection();

		String update = "UPDATE Time SET initialTime = ? WHERE timeID = 1";
		String update2 = "UPDATE Time SET Time = ? WHERE timeID = 1";
		String sel = "SELECT * FROM time";
		String in = "INSERT INTO Time VALUES(1,?,?)";
		
		try {
			PreparedStatement ps = con.prepareStatement(sel);
			
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				PreparedStatement ps3 = con.prepareStatement(in);
				ps3.setTimestamp(1, new Timestamp(time.getTimeInMillis()));
				ps3.setTimestamp(2, new Timestamp(time.getTimeInMillis()));
			
				ps3.executeUpdate();
				
			}else {
				
				PreparedStatement ps1 = con.prepareStatement(update);
				ps1.setTimestamp(1, new Timestamp(time.getTimeInMillis()));
		
				ps1.executeUpdate();
			
				PreparedStatement ps2 = con.prepareStatement(update2);
				ps2.setTimestamp(1, new Timestamp(time.getTimeInMillis()));
		
				ps2.executeUpdate();
			}

		} catch (SQLException e) {

			e.printStackTrace();

		}
	}

	@Override
	public Calendar time(int days) {
		
		Connection con = DB.getInstance().getConnection();

		String update = "UPDATE Time SET time = ? WHERE timeID = 1";
		String get ="SELECT time FROM Time WHERE timeID = 1";
		
		Timestamp staro;
		Calendar c = Calendar.getInstance();
		
		try {
			PreparedStatement ps = con.prepareStatement(get);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				staro = rs.getTimestamp(1);
			//	System.out.println("stari time: "+staro);
			} else {
			//	System.out.println("Error ");
				return null;
			}
			c.setTime(staro);
			c.add(Calendar.DATE, days);
			Timestamp novo = new Timestamp(c.getTimeInMillis());
			c.setTime(novo);
			
			PreparedStatement ps2 = con.prepareStatement(update);
			ps2.setTimestamp(1, novo);

			int affected = ps2.executeUpdate();
			if (affected == 0 )
				return null;
			else
				return c;

		} catch (SQLException e) {

			e.printStackTrace();
			return null;

		}
	}

	@Override
	public void eraseAll() {
		Connection con = DB.getInstance().getConnection();

	
		String delete1 = "delete  from Connected ";
		String delete2 = "delete  from Item ";
		String delete3 = "delete  from Transactions ";
		String delete4 = "delete  from Article ";
		String delete5 = "delete  from Shop ";
		String delete6 = "delete  from Orders";
		String delete7 = "delete  from Buyer ";
		String delete8 = "delete  from City ";
		String delete9 = "delete  from Time ";
		String delete10 = "delete  from SystemProfit ";
		

		try {
			PreparedStatement ps1 = con.prepareStatement(delete1);
			ps1.executeUpdate();
			
			PreparedStatement ps2 = con.prepareStatement(delete2);
			ps2.executeUpdate();
			
			PreparedStatement ps3 = con.prepareStatement(delete3);
			ps3.executeUpdate();
			
			PreparedStatement ps4 = con.prepareStatement(delete4);
			ps4.executeUpdate();
			
			PreparedStatement ps5 = con.prepareStatement(delete5);
			ps5.executeUpdate();
			
			PreparedStatement ps6 = con.prepareStatement(delete6);
			ps6.executeUpdate();
			
			PreparedStatement ps7 = con.prepareStatement(delete7);
			ps7.executeUpdate();
			
			PreparedStatement ps8 = con.prepareStatement(delete8);
			ps8.executeUpdate();
			
			PreparedStatement ps9 = con.prepareStatement(delete9);
			ps9.executeUpdate();
			
			PreparedStatement ps10 = con.prepareStatement(delete10);
			ps10.executeUpdate();
		} catch (SQLException e) {

			e.printStackTrace();
			

		}
		
	}

}
