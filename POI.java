package cs5530;

import java.sql.*;
import java.util.ArrayList;

public class POI {
	public POI ()
	{}

	/**
	 * Helper method for taking a POI name and returning its PID
	 * @param name
	 * @param stmt
	 * @return
	 */
	public static String getPID (String name, Statement stmt)
	{
		String sql="select pid from POI where name = '" + name + "'";
		String output="";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				output = rs.getString("pid");
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

		return output;
	}

	public static int createPOI(String name, String address, String url, String phone, String established, String open_time, String close_time, String price, String category, Statement stmt)
	{
		String sql = "insert into POI (name, address, url, phone, established, open_time, close_time, price, category) values ('" + name + "', '" + address + "', '" + url + "', '" + phone + "', '" + established + "', '" + open_time + "', '" + close_time + "', '" + price + "', '" + category + "')";
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
			System.out.println("POI Creation Successful");
			return 1;
		}
		else
		{
			System.out.println("POI Creation Failed Please Try Again");
			return 0;
		} 	
	}

	public static int updatePOI(String column, String updatedValue, String name, Statement stmt)
	{
		String sql = "update POI set " + column + " = '" + updatedValue + "' where name = '" + name + "'"; 
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
			System.out.println("POI Update Successful");
			return 1;
		}
		else
		{
			System.out.println("POI Update Failed Please Try Again");
			return 0;
		} 	
	}

	public static ArrayList<String> poiSearch(User user, String poiName, String lowerPrice, String upperPrice, String addressCity, String addressState, String keyword, String category, String sortType, Statement stmt)
	{
		String keywords[] = keyword.split(" ");
		boolean firstCondition = true;

		String sql = "";

		if (sortType.equals("price") || sortType.isEmpty())
		{
			// Sorts results by price ascending
			sql += "select * from POI where";
		}
		if (sortType.equals("feedback score"))
		{
			// Sorts results by feedback score descending 
			sql += "select * from POI left outer join (select AVG(score) as score, pid from Feedback group by pid) as temp on POI.pid = temp.pid where";
		}
		if (sortType.equals("trusted user feedback score"))
		{
			sql += "select * from POI left outer join (select AVG(score) as score, pid from Feedback where login in (select login2 from Trust where login1 = '" + user.username +"') group by pid) as temp on POI.pid = temp.pid where";
		}

		if (!poiName.isEmpty())
		{
			if (firstCondition)
			{
				sql += " name = " + poiName;
				firstCondition = false;
			}
			else
				sql += " and name = " + poiName;
		}
		if (!lowerPrice.isEmpty() && !upperPrice.isEmpty())
		{
			if (firstCondition)
			{
				sql += " price between " + lowerPrice + " and " + upperPrice;
				firstCondition = false;
			}
			else
				sql += " and price between " + lowerPrice + " and " + upperPrice;
		}
		if (!addressCity.isEmpty())
		{
			if (firstCondition)
			{
				sql += " address like '%" + addressCity + "%'";
				firstCondition = false;
			}
			else
				sql += " and address like '%" + addressCity + "%'";
		}
		if (!addressState.isEmpty())
		{
			if (firstCondition)
			{
				sql += " address like '%" + addressState + "%'";
				firstCondition = false;
			}
			else
			{
				sql += " and address like '%" + addressState + "%'";
			}
		}
		if (!keywords[0].equals(""))
		{
			if (firstCondition)
			{
				sql += " name like '%" + keywords[0] + "%'";
				for (int i = 1; i < keywords.length; i++)
				{
					sql += " and name like '%" + keywords[i] + "%'";
				}
				firstCondition = false;
			}
			else
			{
				for (int i = 0; i < keywords.length; i++)
				{
					sql += " and name like '%" + keywords[i] + "%'";
				}
			}
		}
		if (!category.isEmpty())
		{
			if (firstCondition)
			{
				sql += " category like '%" + category + "%'";
				firstCondition = false;
			}
			else
				sql += " and category like '%" + category + "%'";
		}
		if (sortType.equals("price"))
		{
			// Sorts results by price ascending
			sql += " order by price asc";
		}
		if (sortType.equals("feedback score"))
		{
			// Sorts results by feedback score descending 
			sql += " order by score desc";
		}
		if (sortType.equals("trusted user feedback score"))
		{
			sql += "order by score desc";
		}

		ArrayList<String> poi = new ArrayList<String>();
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			if (sortType.equals("price") || sortType.isEmpty())
			{
				while (rs.next())
				{
					poi.add(rs.getString("pid")+"   "+rs.getString("name")+"   "+rs.getString("address")+"   "+rs.getString("url")+"   "+rs.getString("phone")+"   "+rs.getString("established")+"   "+rs.getString("open_time")+"   "+rs.getString("close_time")+"   "+rs.getString("price"));
				}
			}
			else
			{
				while (rs.next())
				{
					poi.add(rs.getString("pid")+"   "+rs.getString("name")+"   "+rs.getString("address")+"   "+rs.getString("url")+"   "+rs.getString("phone")+"   "+rs.getString("established")+"   "+rs.getString("open_time")+"   "+rs.getString("close_time")+"   "+rs.getString("price")+"   "+rs.getString("score"));
				}
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

		return poi;
	}

	public static ArrayList<String> getCategories(Statement stmt)
	{
		String sql="select distinct category from POI";
		ArrayList<String> categories = new ArrayList<String>();
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				categories.add(rs.getString("category"));
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

		return categories;
	}

	public static ArrayList<ArrayList<String>> getStatistics(String statNo, Statement stmt)
	{
		ArrayList<String> categories = getCategories(stmt);

		ArrayList<String> mostPopularPOI = new ArrayList<String>();
		ArrayList<String> mostExpensivePOI = new ArrayList<String>();
		ArrayList<String> highestRatedPOI = new ArrayList<String>();

		// Get most popular POI
		for (int i = 0; i < categories.size(); i++)
		{
			String sql="select * from POI, (select pid, count(*) as count from Visit group by pid) as temp where POI.pid = temp.pid and category = '" + categories.get(i) +"' order by count desc limit " + statNo +"";
			ResultSet rs=null;
			try{
				rs=stmt.executeQuery(sql);
				while (rs.next())
				{
					mostPopularPOI.add(rs.getString("pid")+"   "+rs.getString("name")+"   "+rs.getString("address")+"   "+rs.getString("url")+"   "+rs.getString("phone")+"   "+rs.getString("established")+"   "+rs.getString("open_time")+"   "+rs.getString("close_time")+"   "+rs.getString("price")+"   "+rs.getString("category"));
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

		// Get most expensive POI
		for (int i = 0; i < categories.size(); i++)
		{
			String sql="select *, truncate(total_cost/total_heads, 2) as avg_cost from POI, (select login, pid, Visit.vid, visit_date, cost, numberOfHeads, sum(cost) as total_cost, sum(numberOfHeads) as total_heads from Visit, VisitEvent where Visit.vid = VisitEvent.vid group by pid) as temp where POI.pid = temp.pid and category = '" + categories.get(i) + "' order by avg_cost desc limit " + statNo + "";
			ResultSet rs=null;
			try{
				rs=stmt.executeQuery(sql);
				while (rs.next())
				{
					mostExpensivePOI.add(rs.getString("pid")+"   "+rs.getString("name")+"   "+rs.getString("address")+"   "+rs.getString("url")+"   "+rs.getString("phone")+"   "+rs.getString("established")+"   "+rs.getString("open_time")+"   "+rs.getString("close_time")+"   "+rs.getString("price")+"   "+rs.getString("category"));
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

		// Get highest rated POI
		for (int i = 0; i < categories.size(); i++)
		{
			String sql="select * , avg(score) as avg_score from POI, Feedback where POI.pid = Feedback.pid and category = '" + categories.get(i) +"' group by POI.pid limit " + statNo + "";
			ResultSet rs=null;
			try{
				rs=stmt.executeQuery(sql);
				while (rs.next())
				{
					highestRatedPOI.add(rs.getString("pid")+"   "+rs.getString("name")+"   "+rs.getString("address")+"   "+rs.getString("url")+"   "+rs.getString("phone")+"   "+rs.getString("established")+"   "+rs.getString("open_time")+"   "+rs.getString("close_time")+"   "+rs.getString("price")+"   "+rs.getString("category"));
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

		ArrayList<ArrayList<String>> allLists = new ArrayList<ArrayList<String>>();

		allLists.add(mostPopularPOI);
		allLists.add(mostExpensivePOI);
		allLists.add(highestRatedPOI);

		return allLists;
	}

}
