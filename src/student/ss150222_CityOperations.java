package student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import operations.CityOperations;

public class ss150222_CityOperations implements CityOperations {

	@Override
	public int connectCities(int cityID1, int cityID2, int distance) {

		Connection con = DB.getInstance().getConnection();

		String insert = "INSERT INTO Connected(cityID1,cityID2,distance) VALUES(?,?,?)";
		String provera1 = "SELECT * FROM City WHERE cityID = ?";
		String provera2 = "SELECT * FROM City WHERE cityID = ?";
		String provera3 = "SELECT * FROM Connected WHERE (cityID1 = ? and cityID2 = ?)"
				+ "or (cityID1 = ? and cityID2 = ?)";
		
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera1);
			ps_provera.setInt(1, cityID1);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No city with " + cityID1 + " id");
				return -1;
			}
			ps_provera = con.prepareStatement(provera2);
			ps_provera.setInt(1, cityID2);

			rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No city with " + cityID2 + " id");
				return -1;
			}

			PreparedStatement ps_provera3 = con.prepareStatement(provera3);
			ps_provera3.setInt(1, cityID1);
			ps_provera3.setInt(2, cityID2);
			ps_provera3.setInt(3, cityID2);
			ps_provera3.setInt(4, cityID1);

			ResultSet rs_provera3 = ps_provera3.executeQuery();
			if (rs_provera3.next()) {
			//	System.out.println("line already exists");
				return -1;
			}

			PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, cityID1);
			ps.setInt(2, cityID2);
			ps.setInt(3, distance);

			int affected = ps.executeUpdate();

			if (affected == 0) {
			//	System.out.println("No affected rows");
				return -1;
			}

			ResultSet genKeys = ps.getGeneratedKeys();

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
	public int createCity(String name) {

		Connection con = DB.getInstance().getConnection();

		String insert = "INSERT INTO City(name) VALUES(?)";

		try {

			PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, name);

			int affected = ps.executeUpdate();

			if (affected == 0) {
			//	System.out.println("No affected rows");
				return -1;
			}

			ResultSet genKeys = ps.getGeneratedKeys();

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
	public List<Integer> getConnectedCities(int cityID) {

		Connection con = DB.getInstance().getConnection();

		List<Integer> lista = new ArrayList<Integer>();

		String get1 = "SELECT cityID1 FROM Connected WHERE cityID2 = ? AND NOT cityID1 = ?";
		String get2 = "SELECT cityID2 FROM Connected WHERE cityID1 = ? AND NOT cityID2 = ?";
		String provera = "SELECT * FROM City WHERE cityID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, cityID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No city with " + cityID + " id");
				return lista;
			}

			PreparedStatement ps = con.prepareStatement(get1);
			ps.setInt(1, cityID);
			ps.setInt(2, cityID);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(rs.getInt(1));
			}
			PreparedStatement ps2 = con.prepareStatement(get2);
			ps2.setInt(1, cityID);
			ps2.setInt(2, cityID);

			ResultSet rs2 = ps2.executeQuery();

			while (rs2.next()) {
				lista.add(rs2.getInt(1));
			}

			return lista;

		} catch (SQLException e) {
			e.printStackTrace();
			return lista;
		}

	}

	@Override
	public List<Integer> getShops(int cityID) {

		Connection con = DB.getInstance().getConnection();

		List<Integer> lista = new ArrayList<Integer>();

		String get = "SELECT shopID FROM Shop WHERE cityID = ?";
		String provera = "SELECT * FROM City WHERE cityID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, cityID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No city with " + cityID + " id");
				return null;
			}

			PreparedStatement ps = con.prepareStatement(get);
			ps.setInt(1, cityID);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(rs.getInt(1));
			}

			return lista;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public List<Integer> getCities() {
		Connection con = DB.getInstance().getConnection();

		List<Integer> lista = new ArrayList<Integer>();

		String get = "SELECT cityID FROM City";
		
		try {

			PreparedStatement ps = con.prepareStatement(get);
	
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(rs.getInt(1));
			}
			if (lista.size() == 0 )
				return null;
			else
				return lista;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

}
