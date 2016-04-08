package cs5530;

import java.sql.*;
import java.util.ArrayList;

public class Visit {
	public Visit ()
	{}

	/**
	 * Helper method for inserting into the VisitEvent table
	 * @param cost
	 * @param numberOfHeads
	 * @param stmt
	 * @return
	 */
	public static String insertVisitEvent (String cost, String numberOfHeads, Statement stmt)
	{
		String sql = "insert into VisitEvent (cost, numberOfHeads) values ('" + cost + "', '" + numberOfHeads + "')";
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
			System.out.println("VisitEvent Creation Successful");

			sql="select max(vid) as max from VisitEvent";
			String vid="";
			ResultSet rs=null;
			try{
				rs=stmt.executeQuery(sql);
				while (rs.next())
				{
					vid = rs.getString("max");
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

			return vid;
		}
		else
		{
			System.out.println("VisitEvent Creation Failed Please Try Again");
			return "0";
		}
	}

	/**
	 * Adds visit to the visit table
	 * @param cost
	 * @param numberOfHeads
	 * @param stmt
	 * @return
	 */
	public static ArrayList<String> insertVisit (User user, String pid, String vid, Statement stmt)
	{
		String sql = "insert into Visit (login, pid, vid , visit_date) values ('" + user.username + "', '" + pid + "', '" + vid + "', CURDATE())";
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
			System.out.println("VisitEvent Creation Successful");

			return getVisitSuggestions(pid, user, stmt);
		}
		else
		{
			System.out.println("VisitEvent Creation Failed Please Try Again");
			return null;
		}
	}

	public static ArrayList<ArrayList<String>> addVisit (ArrayList<String> visits, User user, Statement stmt)
	{	
		ArrayList<ArrayList<String>> suggestions = new ArrayList<ArrayList<String>>();
		for (int i = 0; i < visits.size(); i+=3)
		{
			suggestions.add(insertVisit(user, POI.getPID(visits.get(i), stmt), insertVisitEvent(visits.get(i+1), visits.get(i+2), stmt), stmt));			
		}

		return suggestions;
	}

	public static ArrayList getVisitSuggestions(String pid, User user, Statement stmt)
	{
		String sql="select * from POI, (select pid, count(*) as count from Visit where login in (select login from Visit where pid = " + pid + " AND login not like '" + user.username + "') and pid not like " + pid + " ) as temp where POI.pid = temp.pid group by POI.pid order by count desc;";
		ArrayList<String> suggestions = new ArrayList<String>();
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				suggestions.add(rs.getString("pid")+"   "+rs.getString("name")+"   "+rs.getString("address")+"   "+rs.getString("url")+"   "+rs.getString("phone")+"   "+rs.getString("established")+"   "+rs.getString("open_time")+"   "+rs.getString("close_time")+"   "+rs.getString("price"));
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
		return suggestions;
	}
}


