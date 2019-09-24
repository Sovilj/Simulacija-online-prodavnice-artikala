package student;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import operations.TransactionOperations;

public class ss150222_TransactionOperations implements TransactionOperations {

	@Override
	public BigDecimal getAmmountThatBuyerPayedForOrder(int orderID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT amount FROM Transactions WHERE orderID = ? and type = 'BO'";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return rs.getBigDecimal(1);
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
	public BigDecimal getAmmountThatShopRecievedForOrder(int shopID,int orderID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT amount FROM Transactions WHERE orderID = ? and shopID = ? and type = 'SO'";

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, orderID);
			ps.setInt(2, shopID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
			//	System.out.println("Success ");
				return rs.getBigDecimal(1);
			} else {
			//	System.out.println("No orderID or shopID ");
				return null;
			}

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getBuyerTransactionsAmmount(int buyerID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT amount FROM Transactions WHERE buyerID = ? and type = 'BO'";
		float sum = 0;

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, buyerID);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				sum += rs.getFloat(1);
			}
			return new BigDecimal(sum).setScale(3);

		} catch (SQLException e) {
			e.printStackTrace();
			return new BigDecimal(-1);
		}
	}

	@Override
	public Calendar getTimeOfExecution(int tranID) {
		Connection con = DB.getInstance().getConnection();

		String query1 = "SELECT exeTime FROM Transactions WHERE tranID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query1);
			ps.setInt(1, tranID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Calendar c = Calendar.getInstance();
				c.setTime(rs.getTimestamp(1));
				return c;
			}

			return null;

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getTransactionAmount(int tranID) {
		Connection con = DB.getInstance().getConnection();

		String query1 = "SELECT amount FROM Transactions WHERE tranID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query1);
			ps.setInt(1, tranID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return new BigDecimal(rs.getFloat(1));
			}

			return null;

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int getTransactionForBuyersOrder(int orderID) {
		Connection con = DB.getInstance().getConnection();

		String query1 = "SELECT tranID FROM Transactions WHERE orderID = ? and type = 'BO'";

		try {
			PreparedStatement ps = con.prepareStatement(query1);
			ps.setInt(1, orderID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

			return -1;

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public int getTransactionForShopAndOrder(int orderID, int shopID) {
		Connection con = DB.getInstance().getConnection();

		String query1 = "SELECT tranID FROM Transactions WHERE orderID = ? "
				+ "and type = 'SO' and shopID = ?";

		try {
			PreparedStatement ps = con.prepareStatement(query1);
			ps.setInt(1, orderID);
			ps.setInt(2, shopID);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);
			}

			return -1;

		} catch (SQLException e) {

			e.printStackTrace();
			return -1;
		}
	}

	@Override
	public List<Integer> getTransationsForBuyer(int buyerID) {
		Connection con = DB.getInstance().getConnection();

		List<Integer> lista = new ArrayList<Integer>();

		String get = "SELECT tranID FROM Transactions WHERE buyerID = ? and type = 'BO'";
		String provera = "SELECT * FROM Buyer WHERE buyerID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, buyerID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No buyer with " + buyerID + " id");
				return null;
			}

			PreparedStatement ps = con.prepareStatement(get);
			ps.setInt(1, buyerID);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(rs.getInt(1));
			}
			if (lista.size() == 0)
				return null;
			else
				return lista;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getShopTransactionsAmmount(int shopID) {
		Connection con = DB.getInstance().getConnection();

		String query = "SELECT amount FROM Transactions WHERE shopID = ? and type = 'SO'";
		float sum = 0;

		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, shopID);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				sum += rs.getFloat(1);
			}
			if (sum != 0)
				return new BigDecimal(sum).setScale(3);
			else 
				return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return new BigDecimal(-1);
		}
	}

	@Override
	public List<Integer> getTransationsForShop(int shopID) {
		Connection con = DB.getInstance().getConnection();

		List<Integer> lista = new ArrayList<Integer>();

		String get = "SELECT tranID FROM Transactions WHERE shopID = ? and type = 'SO'";
		String provera = "SELECT * FROM shop WHERE shopID = ?";
		try {
			PreparedStatement ps_provera = con.prepareStatement(provera);
			ps_provera.setInt(1, shopID);

			ResultSet rs_provera = ps_provera.executeQuery();
			if (!rs_provera.next()) {
			//	System.out.println("No shop with " + shopID + " id");
				return null;
			}

			PreparedStatement ps = con.prepareStatement(get);
			ps.setInt(1, shopID);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				lista.add(rs.getInt(1));
			}

			if (lista.size() == 0)
				return null;
			else
				return lista;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BigDecimal getSystemProfit() {
		Connection con = DB.getInstance().getConnection();

		String query1 = "SELECT profit FROM systemProfit WHERE systemID = 1";

		try {
			PreparedStatement ps = con.prepareStatement(query1);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getBigDecimal(1);
			}

			return new BigDecimal(0);

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}

	}


}
