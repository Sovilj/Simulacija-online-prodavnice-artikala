package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import operations.OrderOperations;

public class ss150222_OrderOperations implements OrderOperations {

	@Override
	public int addArticle(int orderID, int articleID, int count) {

		Connection con = DB.getInstance().getConnection();

		String insert = "INSERT INTO Item(artID,amount,orderID,location)VALUES(?,?,?,?)";
		String provera1 = "SELECT * FROM Orders WHERE orderID = ?";
		String provera2 = "SELECT * FROM Article WHERE artID = ?";
		String getCity = "SELECT s.cityID FROM Article a,Shop s "
				+ "WHERE a.artID = ? and s.shopID = a.shopID";

		int ItemCity;
		try {
			PreparedStatement ps1 = con.prepareStatement(provera1);
			ps1.setInt(1, orderID);

			ResultSet rs1 = ps1.executeQuery();

			if (!rs1.next()) {
			//	System.out.println("No order with this id ");
				return -1;
			}

			PreparedStatement ps2 = con.prepareStatement(provera2);
			ps2.setInt(1, articleID);

			ResultSet rs2 = ps2.executeQuery();

			if (!rs2.next()) {
			//	System.out.println("No article with this id ");
				return -1;
			}
			PreparedStatement ps3 = con.prepareStatement(getCity);
			ps3.setInt(1, articleID);

			ResultSet rs3 = ps3.executeQuery();
			
			if (rs3.next()) {
				ItemCity = rs3.getInt(1);
			//	System.out.println("item city got");
			}else 
				ItemCity = -1;
			
			PreparedStatement ps = con.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, articleID);
			ps.setInt(2, count);
			ps.setInt(3, orderID);
			ps.setInt(4, ItemCity);
			
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
	public int completeOrder(int orderID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT state FROM Orders WHERE orderID = ?";
		String update = "UPDATE Orders SET state = ? WHERE orderID = ?";
		
		String getCA = "SELECT a.count,i.amount FROM Article a, Item i WHERE i.itemID = ? AND a.artID = i.artID";

		String oldState;
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				oldState = rs.getString(1);
			} else {
			//	System.out.println("No orderID ");
				return -1;
			}
			
			//provera da li moze da ide u stanje sent tj da li postoje svi artikli u radnjama
			
			List<Integer> lista = getItems(orderID);
			
			for (int i = 0; i < lista.size(); i++) {
				int item = lista.get(i);
				PreparedStatement ps2 = con.prepareStatement(getCA);
				ps2.setInt(1, item);

				ResultSet rs2 = ps2.executeQuery();

				if (rs2.next()) {
					if (rs2.getInt(1) < rs2.getInt(2)) {
				//		System.out.println(" Not enough articles in shop ");
						return -1;
					}
				}else {
				//	System.out.println(" Not valid item");
					return -1;
				}
			}

			switch (oldState) {
			case "created":
				PreparedStatement ps2 = con.prepareStatement(update);
				ps2.setString(1, "sent");
				ps2.setInt(2, orderID);

				int affected = ps2.executeUpdate();

				if (affected == 0) {
				//	System.out.println("No affected rows");
					return -1;
				}

				return 1;

			case "sent":
			//	System.out.println("Already sent");
				return -1;

			case "arrived":
			//	System.out.println("Already arrived");
				return -1;

			default:
				return -1;

			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getBuyer(int orderID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT buyerID FROM Orders WHERE orderID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return rs.getInt(1);
			} else {
			//	System.out.println("No orderID ");
				return -1;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public BigDecimal getDiscountSum(int orderID) {
		
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT state FROM Orders WHERE orderID = ?";

		String getPriceNoDisc = "SELECT cast (a.price*i.amount as decimal(10,3))"
				+ "FROM Item i, Article a WHERE i.ItemID = ? and a.artID = i.artID ";
		String oldState;
		float combPriceNoDisc = 0;
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				oldState = rs.getString(1);
			} else {
			//	System.out.println("No orderID ");
				return null;
			}

			if (oldState.equals("created")) {
			//	System.out.println("Not completed order");
				return null;
			}

			List<Integer> lista = getItems(orderID);

			for (int i = 0; i < lista.size(); i++) {
				int item = lista.get(i);
				PreparedStatement ps2 = con.prepareStatement(getPriceNoDisc);
				ps2.setInt(1, item);

				ResultSet rs2 = ps2.executeQuery();

				if (rs2.next())
					combPriceNoDisc += rs2.getFloat(1);
				else
					return null;

			}
			return new BigDecimal( combPriceNoDisc - getFinalPrice(orderID).floatValue()).setScale(3) ;

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getFinalPrice(int orderID) {

		Connection con = DB.getInstance().getConnection();

		String query = "SELECT state FROM Orders WHERE orderID = ?";

		String getPrice = "SELECT cast (cast (a.price*i.amount*(100-s.discount) as decimal(10,3)) "
				+ "/ cast (100 as decimal(10,3)) as decimal (10,3)) "
				+ "FROM Item i, Article a,Shop s WHERE i.ItemID = ? and" + " a.artID = i.artID and s.shopID = a.shopID";
		String oldState;
		long combPrice = 0;
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				oldState = rs.getString(1);
			} else {
			//	System.out.println("No orderID ");
				return null;
			}

			if (oldState.equals("created")) {
			//	System.out.println("Not completed order");
				return null;
			}

			List<Integer> lista = getItems(orderID);

			for (int i = 0; i < lista.size(); i++) {
				int item = lista.get(i);
				PreparedStatement ps2 = con.prepareStatement(getPrice);
				ps2.setInt(1, item);

				ResultSet rs2 = ps2.executeQuery();

				if (rs2.next())
					combPrice += rs2.getFloat(1);
				else
					return null;

			}
			return new BigDecimal(combPrice).setScale(3);

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Integer> getItems(int orderID) {
		Connection con = DB.getInstance().getConnection();

		List<Integer> lista = new ArrayList<Integer>();

		String get = "SELECT itemID FROM Item WHERE orderID = ?";
		String provera = "SELECT * FROM Orders WHERE orderID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, orderID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No order with " + orderID + " id");
				return lista;
			}

			PreparedStatement ps = con.prepareStatement(get);
			ps.setInt(1, orderID);

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
	public int getLocation(int orderID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT location FROM Orders WHERE orderID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return rs.getInt(1);
			} else {
			//	System.out.println("No orderID ");
				return -1;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public Calendar getRecievedTime(int orderID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT receivedTime FROM Orders WHERE orderID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				if (rs.getTimestamp(1) == null)
					return null;
				Calendar c = new GregorianCalendar();
				Date d = new Date(rs.getTimestamp(1).getTime());
				c.setTime(d);
				return c;
			} else {
			//	System.out.println("No orderID ");
				return null;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Calendar getSentTime(int orderID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT sentTime FROM Orders WHERE orderID = ?";
		
		Calendar c = new GregorianCalendar();
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				if (rs.getTimestamp(1) == null)
					return null;
				
				c.setTimeInMillis(rs.getTimestamp(1).getTime());
				return c;
			} else {
			//	System.out.println("No orderID ");
				return null;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getState(int orderID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT state FROM Orders WHERE orderID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return rs.getString(1);
			} else {
			//	System.out.println("No orderID ");
				return null;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int removeArticle(int orderID, int articleID) {
		Connection con = DB.getInstance().getConnection();

		String query = "DELETE FROM Item WHERE orderID = ? and artID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);
			ps.setInt(2, articleID);

			int affected = ps.executeUpdate();

			if (affected == 0) {
			//	System.out.println("No orderID ");
				return -1;
			} else {
				return 1;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

}
