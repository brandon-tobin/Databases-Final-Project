package cs5530;

import java.sql.*;
import java.util.ArrayList;

public class Feedback {
	public Feedback()
	{}

	public static int checkFeedbackExists(String username, String pid, Statement stmt)
	{
		String sql="select fid from Feedback where login = '" + username + "' and pid = '" + pid + "'";
		String fid="";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				fid = rs.getString("fid");
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

		if (fid.isEmpty())
		{
			return 1;
		}
		else 
		{
			return 0;
		}
	}

	public static int addFeedback(String username, String pid, String score, String text, Statement stmt)
	{
		String sql = "insert into Feedback (login, pid, score , text, feedback_date) values ('" + username + "', '" + pid + "', '" + score + "', '" + text + "', CURDATE())";
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
			System.out.println("Feedback Added Successful");
			return 1;
		}
		else
		{
			System.out.println("Feedback Failed Please Try Again");
			return 0;
		}	
	}

	public static String getFeedback(String poiName, Statement stmt)
	{
		String pid = POI.getPID(poiName, stmt);

		String sql="select * from Feedback where pid = " + pid + "";
		String output="";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				output+=rs.getString("fid")+"   "+rs.getString("login")+"   "+rs.getString("score")+"   "+rs.getString("text")+"   "+rs.getString("feedback_date")+"\n"; 
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

	public static int checkFeedbackRatingExists(String username, String fid, Statement stmt)
	{
		String sql="select count(*) as count from Rates where login = '" + username + "' and fid = '" + fid + "'";
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

		if (output.equals(0))
		{
			return 1;
		}
		else 
		{
			return 0;
		}

	}

	public static String rateFeedback(String username, String fid, String rating, Statement stmt)
	{
		String sql="select login from Feedback where fid = " + fid + "";
		String output="";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				output = rs.getString("login");
			}

			rs.close();
		}
		catch(Exception e)
		{
			System.out.println("cannot execute the query");
			System.out.println(e.getMessage());
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

		if (output.equals(username))
		{
			System.out.println("You cannot rate your own feedback!");
			return "0";
		}
		else
		{
			sql = "insert into Rates values ('" + username + "', '" + fid + "', '" + rating + "')";
			int sqlReturn = -1;
			try{
				sqlReturn = stmt.executeUpdate(sql);
			}
			catch(Exception e)
			{
				System.out.println("cannot execute the query");
				System.out.println(e.getMessage());
			}

			if (sqlReturn > 0)
			{
				System.out.println("Feedback Added Successful");
				return "1";
			}
			else
			{
				System.out.println("Feedback Failed Please Try Again");
				return "0";
			}	
		}
	}

	public static ArrayList<String> getNMostUsefulFeedbacks(String poiName, String n, Statement stmt)
	{
		ArrayList<String> feedBacks = new ArrayList<String>();
		String sql="select *, avg(rating) from Feedback, (select login, fid, rating from Rates where fid IN (select fid from Feedback where pid = (select pid from POI where name = '" + poiName + "'))) as temp where Feedback.fid = temp.fid group by Feedback.fid order by avg(rating) DESC limit " + n + "";
		ResultSet rs=null;
		try{
			rs=stmt.executeQuery(sql);
			while (rs.next())
			{
				feedBacks.add(rs.getString("fid") +"   "+rs.getString("login") +"   "+rs.getString("pid") +"   "+rs.getString("score") +"   "+rs.getString("text") +"   "+rs.getString("feedback_date"));
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

		return feedBacks;
	}
}
