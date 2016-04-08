package cs5530;

import java.sql.*;
import java.util.ArrayList;

public class User {

	String username;
	String password;
	String name;
	String address;
	String phone;
	boolean isAdmin = false;
	boolean userSet = false;

	public User()
	{}

	public void verifyLogin(String username, String password, Statement stmt)
	{
		String sql = "select *, count(*) as count from Users where login = '" + username + "' and password = '" + password + "'";
		String count = "";
		String accountType = "";
		String localUsername = "";
		String localPassword = "";
		String localName = "";
		String localAddress = "";
		String localPhone = "";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				count = rs.getString("count");
				accountType = rs.getString("user_type");
				localUsername = rs.getString("login");
				localPassword = rs.getString("password");
				localName = rs.getString("name");
				localAddress = rs.getString("address");
				localPhone = rs.getString("phone");
			}

			rs.close();
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
		}
		finally
		{
			try{
				if (rs!=null && !rs.isClosed())
					rs.close();
			}
			catch(Exception e)
			{
				System.out.println("cannot close resultset");
			}
		}

		int rowCount = Integer.parseInt(count);

		if (rowCount == 1)
		{
			this.username = localUsername;
			this.password = localPassword;
			this.name = localName;
			this.address = localAddress;
			this.phone = localPhone;

			int account = Integer.parseInt(accountType);

			if (account == 1)
			{
				this.isAdmin = true;
			}
			else
			{
				this.isAdmin = false;
			}

			this.userSet = true;
		}
	}

	public int checkUsernameUnique(String username, Statement stmt)
	{
		String sql="select count(*) as count from Users where login = '" + username + "'";
		String output="";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				output = rs.getString("count");
			}

			rs.close();
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
		}
		finally
		{
			try{
				if (rs!=null && !rs.isClosed())
					rs.close();
			}
			catch(Exception e)
			{
				System.out.println("cannot close resultset");
			}
		}
		return Integer.parseInt(output);
	}

	public int createUser(String username, String password, String name, String address, String phone, Statement stmt)
	{
		String sql = "insert into Users values ('" + username + "', '" + name + "', 0, '" + password + "', '" + address + "', '" + phone + "')";
		int output = -1;
		try{
			output = stmt.executeUpdate(sql);
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
			System.out.println(e.getMessage());
		}

		if (output > 0)
		{
			System.out.println("User Creation Successful");
			this.username = username;
			this.password = password;
			this.address = address;
			this.phone = phone;
			this.userSet = true;
			return 1;
		}
		else
		{
			System.out.println("User Creation Failed");
			return 0;
		} 	
	}

	public int favoritePOI(String poiName, String username, Statement stmt)
	{
		String sql="select pid from POI where name = '" + poiName + "'";
		String pid="";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				pid = rs.getString("pid");
			}

			rs.close();
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
		}
		finally
		{
			try{
				if (rs!=null && !rs.isClosed())
					rs.close();
			}
			catch(Exception e)
			{
				System.out.println("cannot close resultset");
			}
		}

		sql = "insert into Favorites values ('" + pid + "', '" + username + "', CURDATE())";
		int output = -1;
		try{
			output = stmt.executeUpdate(sql);
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
			System.out.println(e.getMessage());
		}

		if (output > 0)
		{
			System.out.println("POI Favorited Successfully");
			return 1;
		}
		else
		{
			System.out.println("POI Favorited Failed");
			return 0;
		} 			
	}

	public int trustUser(String username, String trustedUsername, int trustLevel, Statement stmt)
	{
		String sql="select count(*) as count from Trust where login1 = '" + username + "' and login2 = '" + trustedUsername + "'";
		String output="";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				output = rs.getString("count");
			}

			rs.close();
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
		}
		finally
		{
			try{
				if (rs!=null && !rs.isClosed())
					rs.close();
			}
			catch(Exception e)
			{
				System.out.println("cannot close resultset");
			}
		}

		// Insert new record into DB Trust table
		if (output.equals("0"))
		{
			sql = "insert into Trust values ('" + username + "', '" + trustedUsername + "', '" + trustLevel + "')";
			int result = -1;
			try{
				result = stmt.executeUpdate(sql);
			}
			catch(Exception e)
			{
				System.out.println("cannot execute the query");
				System.out.println(e.getMessage());
			}

			if (result > 0)
			{
				System.out.println("User Trusted Successfully");
				return 1;
			}
			else
			{
				System.out.println("User Trusted Failed");
				return 0;
			} 			

		}
		else
		{
			sql = "update Trust set is_trusted = '" + trustLevel + "' where login1 = '" + username + "' and login2 = '" + trustedUsername + "'";
			int result = -1;
			try{
				result = stmt.executeUpdate(sql);
			}
			catch(Exception e)
			{
				System.out.println("cannot execute the query");
				System.out.println(e.getMessage());
			}

			if (result > 0)
			{
				System.out.println("User Trusted Successfully");
				return 1;
			}
			else
			{
				System.out.println("User Trusted Failed");
				return 0;
			} 			

		}
	}

	public static int determineDegreesSeparation(String username1, String username2, Statement stmt)
	{
		ArrayList<String> oneDegreeSeparated = new ArrayList<String>();
		String sql="select temp.login from Favorites, (select * from Favorites) as temp where Favorites.pid = temp.pid and Favorites.login != temp.login and Favorites.login = '" + username1 + "'";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				oneDegreeSeparated.add(rs.getString("login"));
			}

			rs.close();
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
		}	
		finally
		{
			try{
				if (rs!=null && !rs.isClosed())
					rs.close();
			}
			catch(Exception e)
			{
				System.out.println("cannot close resultset");
			}
		}

		if (oneDegreeSeparated.contains(username2))
		{
			// The users are one degree separated, return 1;
			return 1;
		}
		else
		{
			ArrayList<String> twoDegreeSeparated = new ArrayList<String>();
			for (int i = 0; i < oneDegreeSeparated.size(); i++)
			{
				sql="select * from Favorites, (select * from Favorites) as temp where Favorites.pid = temp.pid and Favorites.login != temp.login and temp.login != '" + username1 + "' and Favorites.login = '" + oneDegreeSeparated.get(i) + "'";
				rs=null;
				try{
					rs=stmt.executeQuery(sql);
					while (rs.next())
					{
						twoDegreeSeparated.add(rs.getString("temp.login"));
					}

					rs.close();
				}
				catch(Exception e)
				{
					System.out.println("cannot execute the query");
				}	
				finally
				{
					try{
						if (rs!=null && !rs.isClosed())
							rs.close();
					}
					catch(Exception e)
					{
						System.out.println("cannot close resultset");
					}
				}
			}

			if (twoDegreeSeparated.contains(username2))
			{
				// Uses are 2 degrees separated
				return 2;
			}
			else
			{
				// Neither 1 or 2 degrees separated
				return 0;
			}
		}
	}

	public static ArrayList<String> getMostTrustedUsers(String statNo, Statement stmt)
	{
		ArrayList<String> trustedUsers = new ArrayList<String>();
		String sql="select login2, sum(is_trusted) as trust_level from Trust group by login2 order by trust_level desc limit " + statNo + "";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				trustedUsers.add(rs.getString("login2"));
			}

			rs.close();
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
		}	
		finally
		{
			try{
				if (rs!=null && !rs.isClosed())
					rs.close();
			}
			catch(Exception e)
			{
				System.out.println("cannot close resultset");
			}
		}

		return trustedUsers;
	}

	public static ArrayList<String> getMostUsefulUsers(String statNo, Statement stmt)
	{
		ArrayList<String> usefulUsers = new ArrayList<String>();
		String sql="select Feedback.login, avg(rating) as avg_rating from Feedback, Rates where Feedback.fid = Rates.fid group by Feedback.login order by avg_rating desc limit " + statNo + "";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				usefulUsers.add(rs.getString("login"));
			}

			rs.close();
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
		}	
		finally
		{
			try{
				if (rs!=null && !rs.isClosed())
					rs.close();
			}
			catch(Exception e)
			{
				System.out.println("cannot close resultset");
			}
		}

		return usefulUsers;
	}

}
