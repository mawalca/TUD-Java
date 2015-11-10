package managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import domain.Cake;

public class CakeManager {
	
	private Connection con;
	private String url = "jdbc:hsqldb:hsql://localhost/workdb";
	
	private String createTableCake = "CREATE TABLE Cake(id bigint GENERATED BY DEFAULT AS IDENTITY, " +
										"name varchar(40), price double)";
	private Statement stmt;

	private PreparedStatement getStmt;
	private PreparedStatement getAllStmt;
	private PreparedStatement removeAllStmt;
	private PreparedStatement countStmt;
	
	private PreparedStatement addStmt;
	private PreparedStatement updateStmt;
	private PreparedStatement deleteStmt;

	public CakeManager() {
		try {
			con = DriverManager.getConnection(url);
			stmt = con.createStatement();
			
			ResultSet rs = con.getMetaData().getTables(null, null, null, null);
			boolean tableExists = false;
			
			while(rs.next()){
				if("Cake".equalsIgnoreCase(rs.getString("TABLE_NAME"))) {
					tableExists = true;
					break;					
				}
			}
			if(!tableExists)
				stmt.executeUpdate(createTableCake);
						
			getStmt = con.prepareStatement("SELECT * FROM Cake WHERE id=?");
			getAllStmt = con.prepareStatement("SELECT * FROM Cake");
			removeAllStmt = con.prepareStatement("DELETE FROM Cake");
			countStmt = con.prepareStatement("SELECT count(*) FROM Cake");
			
			addStmt = con.prepareStatement("INSERT INTO Cake (name, price) VALUES (?, ?)");
			updateStmt = con.prepareStatement("UPDATE Cake SET name=?, price=? WHERE id=?");
			deleteStmt = con.prepareStatement("DELETE FROM Cake WHERE id=?");
			
		} catch(SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public Connection getConnection() {
		return con;
	}
	
	public int count() {
		
		int nr = 0;
		
		try {
			ResultSet rs = countStmt.executeQuery();
			if (rs.next())
				nr = rs.getInt(1);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return nr;
	}
	
	public Cake getOne(long id) {
		
		Cake c = new Cake();
		
		try {
			getStmt.setLong(1, id);
			ResultSet rs = getStmt.executeQuery();
			rs.next();
			
			c.setId(rs.getLong("id"));
			c.setName(rs.getString("name"));
			c.setPrice(rs.getDouble("price"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return c;
		
	}
	
	public List<Cake> getAll() {
		
		List<Cake> cakes = new ArrayList<Cake>();
		
		try {
			ResultSet rs = getAllStmt.executeQuery();
			
			while(rs.next()) {
				Cake c = new Cake();
				c.setId(rs.getLong("id"));
				c.setName(rs.getString("name"));
				c.setPrice(rs.getDouble("price"));
				cakes.add(c);
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return cakes;		
	}
	
	public void removeAll() {
		try {
			removeAllStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addCake(Cake cake) {
		
		try {
			addStmt.setString(1, cake.getName());
			addStmt.setDouble(2, cake.getPrice());
			
			addStmt.executeUpdate();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addCakes(List<Cake> cakes) throws SQLException {
		
		try {
			con.setAutoCommit(false);
			
			for(Cake c : cakes) {
				addStmt.setString(1, c.getName());
				addStmt.setDouble(2, c.getPrice());
				
				addStmt.executeUpdate();
			}
			
			con.commit();
		} catch(SQLException e) {
			con.rollback();
		} finally {
			con.setAutoCommit(true);
		}
	}
	
	public void updateCake(Cake cake, String newName, double newPrice) {
		
		try {
			updateStmt.setString(1, newName);
			updateStmt.setDouble(2, newPrice);
			updateStmt.setLong(3, cake.getId());
			
			updateStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteCake(Cake cake) {
		try {
			deleteStmt.setLong(1, cake.getId());
			
			deleteStmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
