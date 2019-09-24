package student;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import operations.ArticleOperations;

public class ss150222_ArticleOperations implements ArticleOperations {

	@Override
	public int createArticle(int shopID, String artName, int artPrice) {

		Connection con = DB.getInstance().getConnection();
		String insert = "INSERT INTO Article(shopID,name,price,count) VALUES (?,?,?,0)";
		String provera = "SELECT * FROM Shop WHERE shopID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, shopID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No shop with " + shopID + " id");
				return -1;
			}

			PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, shopID);
			ps.setString(2, artName);
			ps.setInt(3, artPrice);

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

		} catch (SQLException ex) {
			Logger.getLogger(ss150222_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
		//	System.out.println("Exeption");
			return -1;
		}

	}

/*	@Override
	public int getCount(int artID) {

		Connection con = DB.getInstance().getConnection();

		String query = "SELECT count FROM Article WHERE artID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, artID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				System.out.println("Success ");
				return rs.getInt(1);
			} else {
				System.out.println("No artID ");
				return -1;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}

	}
*/
}
