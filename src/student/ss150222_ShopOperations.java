package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import operations.ShopOperations;

public class ss150222_ShopOperations implements ShopOperations {

	@Override
	public int createShop(String name, String cityName) {

		Connection con = DB.getInstance().getConnection();

		String insert = "INSERT INTO Shop(name,cityName,discount,account,cityID) " + "	VALUES(?,?,0,0,?)";
		String getCity = "SELECT cityID FROM city WHERE name = ?";
		int cityID;
		try {
			PreparedStatement ps = con.prepareStatement(getCity);
			ps.setString(1, cityName);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				cityID = rs.getInt(1);
			} else {
			//	System.out.println("No city with this name ");
				return -1;
			}

			PreparedStatement ps2 = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			ps2.setString(1, name);
			ps2.setString(2, cityName);
			ps2.setInt(3, cityID);

			int affected = ps2.executeUpdate();

			if (affected == 0) {
			//	System.out.println("No affected rows");
				return -1;
			}

			ResultSet genKeys = ps2.getGeneratedKeys();

			if (genKeys.next()) {
			//	System.out.println("Success ");
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
	public List<Integer> getArticles(int shopID) {
		Connection con = DB.getInstance().getConnection();

		List<Integer> lista = new ArrayList<Integer>();

		String get = "SELECT artID FROM Article WHERE shopID = ?";
		String provera = "SELECT * FROM Shop WHERE shopID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, shopID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No shop with " + shopID + " id");
				return lista;
			}

			PreparedStatement ps = con.prepareStatement(get);
			ps.setInt(1, shopID);

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
	public int getDiscount(int shopID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT discount FROM Shop WHERE shopID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, shopID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return rs.getInt(1);
			} else {
			//	System.out.println("No shopID ");
				return -1;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int increaseArticleCount(int articleID, int increment) {
		
		
		Connection con = DB.getInstance().getConnection();

		String query = "UPDATE Article SET count = count + ? WHERE artID = ?";
		String provera2 = "SELECT * FROM Article WHERE artID = ?";
		String count = "SELECT count FROM Article WHERE artID = ?";
		
		try {
			
			PreparedStatement ps_provera2 = con.prepareStatement(provera2);
			ps_provera2.setInt(1, articleID);

			ResultSet rs_provera2 = ps_provera2.executeQuery();
			if (!rs_provera2.next()) {
			//	System.out.println("No article with " + articleID + " id");
				return -1;
			}
			
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, increment);
			ps.setInt(2, articleID);

			int affected = ps.executeUpdate();

			if (affected == 0) {
			//	System.out.println("No shopID or artID");
				return -1;
			}
			
			PreparedStatement ps2 = con.prepareStatement(count);
			ps2.setInt(1, articleID);

			ResultSet rs2 = ps2.executeQuery();
			if (rs2.next()) {
				return rs2.getInt(1);
			}else
				return -1;
			
			

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
		
	}

	@Override
	public int setCity(int shopID, String cityName) {
		
		Connection con = DB.getInstance().getConnection();

		String update = "UPDATE shop SET cityID = ? , cityName = ? WHERE shopID = ?";
		String getCity = "SELECT cityID FROM city WHERE name = ?";
		int cityID;
		String provera = "SELECT * FROM City WHERE name = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setString(1, cityName);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No city with " + cityName + " name");
				return -1;
			}

			PreparedStatement ps = con.prepareStatement(getCity);
			ps.setString(1, cityName);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				cityID = rs.getInt(1);
			} else {
			//	System.out.println("No city with this name ");
				return -1;
			}

			PreparedStatement ps2 = con.prepareStatement(update);
			ps2.setInt(1, cityID);
			ps2.setString(2, cityName);
			ps2.setInt(3, shopID);

			int affected = ps2.executeUpdate();

			if (affected == 0) {
			//	System.out.println("Error no shop ");
				return -1;
			} else {
				return 1;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int setDiscount(int shopID, int discount) {
		Connection con = DB.getInstance().getConnection();

		String update = "UPDATE Shop SET discount = ? WHERE shopID = ?";
		String provera = "SELECT * FROM Shop WHERE shopID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, shopID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No shop with " + shopID + " id");
				return -1;
			}
			
			
			PreparedStatement ps = con.prepareStatement(update);
			ps.setInt(1,discount);
			ps.setInt(2, shopID);

			int affected = ps.executeUpdate();

			if (affected == 0) {
			//	System.out.println("Error no shop ");
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
	public int getCity(int shopID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT cityID FROM Shop WHERE shopID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, shopID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return rs.getInt(1);
			} else {
			//	System.out.println("No shopID ");
				return -1;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getArticleCount(int shopID, int articleID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT count FROM Article WHERE shopID = ? and artID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, shopID);
			ps.setInt(2, articleID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return rs.getInt(1);
			} else {
			//	System.out.println("No shopID or artID");
				return -1;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

}
