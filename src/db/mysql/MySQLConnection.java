package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection{

	private Connection conn;
	   
	   public MySQLConnection() {
	  	 try {
	  		 Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
	  		 conn = DriverManager.getConnection(MySQLDBUtil.URL);
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 }
	   }

	@Override
	public void close() {
		 if (conn != null) {
	  		 try {
	  			 conn.close();
	  		 } catch (Exception e) {
	  			 e.printStackTrace();
	  		 }
	  	 }

	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		//insert n rows into history table if this user like n times 
	    if (conn == null) {
	  		 System.err.println("DB connection failed");
	  		 return;
	  	       }
		
		try {
			String sql = "INSERT IGNORE INTO history VALUES (?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			
			for (String itemId: itemIds) {
				ps.setString(2, itemId);
				ps.execute();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		 if (conn == null) {
	  		 System.err.println("DB connection failed");
	  		 return;
	  	       }
	  	
	  	      try {
	  		 String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
	  		 PreparedStatement ps = conn.prepareStatement(sql);
	  		 ps.setString(1, userId);
	  		 for (String itemId : itemIds) {
	  			 ps.setString(2, itemId);
	  			 ps.execute();
	  		 }
	  		
	  	       } catch (Exception e) {
	  		 e.printStackTrace();
	  	       }

		
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<String> favoriteItems = new HashSet<>();
		
		try {
			String sql = "SELECT item_id FROM history WHERE user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItems.add(itemId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return favoriteItems;

	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		
		try {
			String sql = "SELECT * FROM items WHERE item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : itemIds) {
				stmt.setString(1, itemId);
				
				ResultSet rs = stmt.executeQuery();
				
				ItemBuilder builder = new ItemBuilder();
				
				while (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(itemId));
					builder.setDistance(rs.getDouble("distance"));
					builder.setRating(rs.getDouble("rating"));
					
					favoriteItems.add(builder.build());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return favoriteItems;

	}

	@Override
	public Set<String> getCategories(String itemId) {
	
			if (conn == null) {
				return null;
			}
			Set<String> categories = new HashSet<>();
			try {
				String sql = "SELECT category from categories WHERE item_id = ? ";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1, itemId);
				ResultSet rs = statement.executeQuery();
				while (rs.next()) {
					String category = rs.getString("category");
					categories.add(category);
				}
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			return categories;

		}

	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		   TicketMasterAPI ticketMasterAPI = new TicketMasterAPI();
 	        List<Item> items = ticketMasterAPI.search(lat, lon, term);
 	
 	        for(Item item : items) {
 		 saveItem(item);
 	        }
 	
 	        return items;

	}

	@Override
	public void saveItem(Item item) {
		//multiple rows at the same time 
		//if no ignore, insert nothing 
		//if ignoe error, insert no error rows 
 		try {
 			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, item.getItemId());
	  		 ps.setString(2, item.getName());
	  		 ps.setDouble(3, item.getRating());
	  		 ps.setString(4, item.getAddress());
	  		 ps.setString(5, item.getImageUrl());
	  		 ps.setString(6, item.getUrl());
	  		 ps.setDouble(7, item.getDistance());
	  		 ps.execute();

	  		 sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
	  		 ps = conn.prepareStatement(sql);
	  		 ps.setString(1, item.getItemId());
	  		 for(String category : item.getCategories()) {
	  			 ps.setString(2, category);
	  			 ps.execute();
	  		 }


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	@Override
	public String getFullname(String userId) {
		if (conn == null) {
			return "";
		}
		
		String name = ""; 
		try {
			String sql = "SELECT first_name, last_name FROM users WHERE user_id =  ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery(); 
			while (rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			return false;
		}
		try {
			String sql = "SELECT user_id, password FROM users WHERE user_id = ? AND password = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			stmt.setString(2, password);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed"); 
		}
		
		//insert the user information to users table 
		try {
			//define sql string 
			String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)"; 
			//define preparedStatement type statement based on the sql string  
			PreparedStatement ps = conn.prepareStatement(sql);
			//set each parameter of the statement 
			ps.setString(1, userId); 
			ps.setString(2, password); 
			ps.setString(3, firstname);
			ps.setString(4, lastname);
			
			// if executeUpdate() returns 1, then the statement is executed successfully 
			return ps.executeUpdate() == 1; 
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		// if execute statement successfully, the program will not reach here 
		return false; 
		
		
		
		
		
		
	}
	
	
}
