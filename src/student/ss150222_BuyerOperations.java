package student;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import operations.BuyerOperations;

public class ss150222_BuyerOperations implements BuyerOperations {

	@Override
	public int createBuyer(String name, int cityID) {
		
		Connection con = DB.getInstance().getConnection();
		
		String insert = "INSERT INTO Buyer(name,account,cityID) VALUES(?,0,?)";
		String provera = "SELECT * FROM City WHERE cityID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, cityID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No city with " + cityID + " id");
				return -1;
			}
			
			PreparedStatement ps = con.prepareStatement(insert,Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);
			ps.setInt(2, cityID);
			
			int affected = ps.executeUpdate();
			
			if (affected == 0) {
			//	System.out.println("No affected rows");
				return -1;
			}
			
			ResultSet genKeys = ps.getGeneratedKeys();
			
			if (genKeys.next()) {
			//	System.out.println("Success Id = ");
				return genKeys.getInt(1);
			} else {
			//	System.out.println("No generated");
				return -1;
			}
			
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
		
	}

	@Override
	public int createOrder(int buyerID) {
		
		Connection con = DB.getInstance().getConnection();
		
		String insert = "INSERT INTO Orders(state,location,buyerID) VALUES('created',-1,?)";
		String provera = "SELECT * FROM Buyer WHERE BuyerID = ?";
		try {
			
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, buyerID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No buyer with " + buyerID + " id");
				return -1;
			}

			PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, buyerID);
			

			int affected = ps.executeUpdate();

			if (affected == 0) {
			//	System.out.println("No affected rows ");
				return -1;
			}
			ResultSet genKeys = ps.getGeneratedKeys();
			if (genKeys.next()) {
			//	System.out.println("Success ");
				return genKeys.getInt(1);
			} else {
			//	System.out.println("No generated ");
				return -1;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public BigDecimal getCredit(int buyerID) {
	
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT account FROM Buyer WHERE buyerID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, buyerID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return new BigDecimal (rs.getFloat(1));
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
	public List<Integer> getOrders(int buyerID) {
		
		Connection con = DB.getInstance().getConnection();
		
		List<Integer> lista = new ArrayList<Integer>() ;
		
		String get = "SELECT orderID FROM Orders WHERE buyerID = ?";
		String provera = "SELECT * FROM BUyer WHERE buyerID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, buyerID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No buyer with " + buyerID + " id");
				return lista;
			}
			
			PreparedStatement ps = con.prepareStatement(get);
			ps.setInt(1, buyerID);
			
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				lista.add(rs.getInt(1));
			}
			
			
			return lista;
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			return lista;
		}
		
		
		
	}

	@Override
	public BigDecimal increaseCredit(int buyerID, BigDecimal credit) {
		
		Connection con = DB.getInstance().getConnection();

		String update = "UPDATE Buyer SET account = account + ? WHERE buyerID = ?";
		String query = "SELECT account FROM Buyer WHERE buyerID = ?";
		try {
			PreparedStatement ps = con.prepareStatement(update);
			ps.setBigDecimal(1, credit);
			ps.setInt(2, buyerID);

			int affected = ps.executeUpdate();

			if (affected == 0) {
			//	System.out.println("Error no buyerID ");
				return null;
			}
			
			PreparedStatement ps2 = con.prepareStatement(query);
			ps2.setInt(1, buyerID);
			
			ResultSet rs = ps2.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return new BigDecimal (rs.getFloat(1));
			} else {
			//	System.out.println("Error empty rs ");
				return null;
			}
			

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public int setCity(int buyerID, int cityID) {
		
		Connection con = DB.getInstance().getConnection();

		String update = "UPDATE Buyer SET cityID = ? WHERE buyerID = ?";
		String provera = "SELECT * FROM City WHERE cityID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, cityID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No city with " + cityID + " id");
				return -1;
			}
			
			
			PreparedStatement ps = con.prepareStatement(update);
			ps.setInt(1,cityID);
			ps.setInt(2, buyerID);

			int affected = ps.executeUpdate();

			if (affected == 0) {
			//	System.out.println("Error no buyerID ");
				return -1;
			}else {
				return 1;
			}
			

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getCity(int buyerID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT cityID FROM Buyer WHERE buyerID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, buyerID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return rs.getInt(1);
			} else {
			//	System.out.println("No buyerID ");
				return -1;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	
	

}
