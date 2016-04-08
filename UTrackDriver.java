package cs5530;

import java.util.ArrayList;
import java.io.*;

public class UTrackDriver {

	/**
	 * @param args
	 */
	public static void displayLoginMenu()
	{
		System.out.println("        Welcome to the UTrack System     ");
		System.out.println("Please login or create an account to continue.");
		System.out.println("1. Login:");
		System.out.println("2. Registration:");
	}
	public static void displayAdminMenu()
	{
		System.out.println("        Welcome to the UTrack System     ");
		System.out.println("Please refer to assignment specification for detailed descriptions of each command.");
		System.out.println("1. Record a Visit:");
		System.out.println("2. Create New POI -- Admin Only:");
		System.out.println("3. Update POI -- Admin Only:");
		System.out.println("4. Favorite a POI:");
		System.out.println("5. Leave Feedback For POI:");
		System.out.println("6. View Feedback For POI:");
		System.out.println("7. Rate Feedback:");
		System.out.println("8. Declare User as Trusted:");
		System.out.println("9. Declare User as Not Trusted:");
		System.out.println("10. Search For POI:");
		System.out.println("11. Find Most Useful Feedback For POI:");
		System.out.println("12. Determine Degree of Separation:");
		System.out.println("13. Statistics:");
		System.out.println("14. Find Best Users -- Admin Only:");
	}
	public static void displayUserMenu()
	{
		System.out.println("        Welcome to the UTrack System     ");
		System.out.println("Please refer to assignment specification for detailed descriptions of each command.");
		System.out.println("1. Record a Visit:");
		System.out.println("2. Favorite a POI:");
		System.out.println("3. Leave Feedback For POI:");
		System.out.println("4. View Feedback For POI:");
		System.out.println("5. Rate Feedback:");
		System.out.println("6. Declare User as Trusted:");
		System.out.println("7. Declare User as Not Trusted:");
		System.out.println("8. Search For POI:");
		System.out.println("9. Find Most Useful Feedback For POI:");
		System.out.println("10. Determine Degree of Separation:");
		System.out.println("11. Statistics:");
	}

	public static void main(String[] args) {
		Connector con=null;
		User user = new User();

		try
		{
			//remember to replace the password
			con= new Connector();
			System.out.println ("Database connection established");

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

			getUserAccess(in, con, user);

			if (user.isAdmin == true)
			{
				// Get admin functions
				getAdminActions(in, con, user);
			}
			else
			{
				// Get user functions
				getUserActions(in, con, user);

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.err.println ("Either connection error or query execution error!");
		}
		finally
		{
			if (con != null)
			{
				try
				{
					con.closeConnection();
					System.out.println ("Database connection terminated");
				}

				catch (Exception e) { /* ignore close errors */ }
			}	 
		}
	}

	public static void getUserAccess(BufferedReader in, Connector con, User user) throws IOException 
	{
		String choice = "";
		int c = 0;

		while(true)
		{
			displayLoginMenu();
			while ((choice = in.readLine()) == null && choice.length() == 0);
			try{
				c = Integer.parseInt(choice);
			}catch (Exception e)
			{

				continue;
			}
			if (c<1 | c>2)
				continue;
			if (c==1)
			{
				// Verify Login into UTrak
				String localUsername;
				String localPassword;
				System.out.println("User Login");
				System.out.println("Please enter your Username:");
				while ((localUsername = in.readLine()) == null && localUsername.length() == 0);
				System.out.println("Please enter your Password:");
				while ((localPassword = in.readLine()) == null && localPassword.length() == 0);

				user.verifyLogin(localUsername, localPassword, con.stmt);

				if (user.userSet == true)
				{
					System.out.println("Login Successful");
					break;
				}
				else
				{
					System.out.println("Login Failed");
				}						
			}
			if (c==2)
			{
				// Create a new user
				String localUsername;
				String localPassword;
				System.out.println("New User Registration");
				System.out.println("Please enter your desired Username:");
				while ((localUsername = in.readLine()) == null && localUsername.length() == 0);
				System.out.println("Please enter your desired Password:");
				while ((localPassword = in.readLine()) == null && localPassword.length() == 0);

				int count = user.checkUsernameUnique(localUsername, con.stmt);

				if (count == 0)
				{
					String name;
					String address;
					String phone;

					System.out.println("Please enter your name:");
					while ((name = in.readLine()) == null && name.length() == 0);
					System.out.println("Please enter your address:");
					while ((address = in.readLine()) == null && address.length() == 0);
					System.out.println("Please enter your phone number (No parens, dashes, just 10 numbers):");
					while ((phone = in.readLine()) == null && phone.length() == 0);

					if (phone.length() != 10)
					{
						System.out.println("Please enter your phone number (No parens, dashes, just 10 numbers):");
						while ((phone = in.readLine()) == null && phone.length() == 0);
					}

					int creation = user.createUser(localUsername, localPassword, name, address, phone, con.stmt);

					if (creation == 1)
						break;
				}
				else
				{
					System.out.println("Username is already taken. Please re-enter the user creation and choose a different username.");
				}
			}
		}
	}

	public static void getAdminActions(BufferedReader in, Connector con, User user) throws IOException
	{
		String choice = "";
		int c = 0;

		while(true)
		{
			displayAdminMenu();

			while ((choice = in.readLine()) == null && choice.length() == 0);
			try{
				c = Integer.parseInt(choice);
			}catch (Exception e)
			{

				continue;
			}
			if (c<1 | c>15)
				continue;

			switch (c) {

			case 1: 
				// Record Visit
				ArrayList<String> visits = new ArrayList<String>();
				String input = "";

				while (true)
				{
					System.out.println("Please enter the Name of the POI you visited:");
					while ((input = in.readLine()) == null && input.length() == 0);

					visits.add(input);

					System.out.println("Please enter the amount you paid to visit the POI:");
					while ((input = in.readLine()) == null && input.length() == 0);

					visits.add(input);

					System.out.println("Please enter the number of people in your party:");
					while ((input = in.readLine()) == null && input.length() == 0);

					visits.add(input);

					System.out.println("Do you have more visits to record? (Yes/No):");
					while ((input = in.readLine()) == null && input.length() == 0);

					if (input.equals("Yes"))
					{
						continue;
					}
					else
					{
						break;
					}
				}

				System.out.println("Please verify the following records below:");

				for (int i = 0; i < visits.size(); i+=3)
				{
					System.out.println("POI Name: " + visits.get(i) + " Cost: " + visits.get(i+1) + " Number In Party: " + visits.get(i+2));
				}

				System.out.println("Would you like to proceed with adding the records? (Yes/No):");
				while ((input = in.readLine()) == null && input.length() == 0);

				if (input.equals("Yes"))
				{
					Visit visit = new Visit();
					ArrayList<ArrayList<String>> suggestions = visit.addVisit(visits, user, con.stmt);
					ArrayList<String> temp = new ArrayList<String>();		

					System.out.println("Suggested POIs Outputted in form: pid, name, address, url, phone, established, open_time, close_time, price");
					for (int i = 0; i < suggestions.size(); i++)
					{
						temp = suggestions.get(i);
						for (int j = 0; j < temp.size(); j++)
						{
							System.out.println(temp.get(j));
						}
					}
				} 
				else
				{
					System.out.println("Records disregarded");
					continue;
				}

				break;

			case 2:
				// Create New POI
				String name = "";
				String address = "";
				String url = "";
				String phone = "";
				String established = "";
				String open_time = "";
				String close_time = "";
				String price = "";
				String category = "";

				System.out.println("Please enter the POI Name:");
				while ((name = in.readLine()) == null && name.length() == 0);

				System.out.println("Please enter the POI Address:");
				while ((address = in.readLine()) == null && address.length() == 0);

				System.out.println("Please enter the POI URL:");
				while ((url = in.readLine()) == null && url.length() == 0);

				System.out.println("Please enter the POI Phone Number:");
				while ((phone = in.readLine()) == null && phone.length() == 0);

				System.out.println("Please enter the POI Established Date in format YYYY-MM-DD:");
				while ((established = in.readLine()) == null && established.length() == 0);

				System.out.println("Please enter the POI Open Time in format HH:MM :");
				while ((open_time = in.readLine()) == null && open_time.length() == 0);

				System.out.println("Please enter the POI Close Time in format HH:MM :");
				while ((close_time = in.readLine()) == null && close_time.length() == 0);

				System.out.println("Please enter the POI Cost:");
				while ((price = in.readLine()) == null && price.length() == 0);

				System.out.println("Please enter the POI Category:");
				while ((category = in.readLine()) == null && category.length() == 0);

				POI poi = new POI();
				int result = poi.createPOI(name, address, url, phone, established, open_time, close_time, price, category, con.stmt);

				break;

			case 3:
				// Update POI
				String column = "";
				String updatedValue = "";
				String poiName = "";

				System.out.println("Please enter the POI column you wish to change:");
				while ((column = in.readLine()) == null && column.length() == 0);

				System.out.println("Please enter the new value:");
				while ((updatedValue = in.readLine()) == null && updatedValue.length() == 0);

				System.out.println("Please enter the POI Name:");
				while ((poiName = in.readLine()) == null && poiName.length() == 0);

				poi = new POI();
				int result2 = poi.updatePOI(column, updatedValue, poiName, con.stmt);

				if (result2 == 1)
					// creation successful 

					break;

			case 4:
				String username = user.username;
				poiName = "";

				System.out.println("Please enter the name of the POI you wish to favorite:");
				while ((poiName = in.readLine()) == null && poiName.length() == 0);


				result = user.favoritePOI(poiName, username, con.stmt);

				break;

			case 5:
				// Leave feedback for POI
				String score = "";
				String text = "";

				System.out.println("Please enter the POI Name:");
				while ((name = in.readLine()) == null && name.length() == 0);

				String pid = POI.getPID(name, con.stmt);

				int checkFeedback = Feedback.checkFeedbackExists(user.username, pid, con.stmt);

				if (checkFeedback == 1)
				{
					System.out.println("Please enter your score rating for the POI (0 = terrible, 10 = excellent):");
					while ((score = in.readLine()) == null && score.length() == 0);

					System.out.println("Please enter optional short comment:");
					while ((text = in.readLine()) == null && text.length() == 0);

					Feedback feedback = new Feedback();
					feedback.addFeedback(user.username, POI.getPID(name, con.stmt), score, text, con.stmt);	
				}
				else
				{
					System.out.println("You have already left feedback for this POI.");
				}


				break;

			case 6:
				// View feedback for a particular POI
				System.out.println("Please enter the POI Name you wish to view Feedback for:");
				while ((name = in.readLine()) == null && name.length() == 0);

				String feedBack = Feedback.getFeedback(name, con.stmt);

				System.out.println(feedBack);
				break;

			case 7:
				// Rate Feedback
				String fid = "";
				String rating = "";

				System.out.println("Please enter the ID of the Feedback you wish to rate:");
				while ((fid = in.readLine()) == null && fid.length() == 0);

				System.out.println("Please enter the Rating you wish to give to the feedback. 0 = useless, 1 = useful, 2 = very useful:");
				while ((rating = in.readLine()) == null && rating.length() == 0);

				if (!(rating.equals("0") || rating.equals("1") || rating.equals("2")))
				{
					System.out.println("Please enter a valid rating.");
					break;
				}
				else
				{
					if (Feedback.checkFeedbackExists(user.username, fid, con.stmt) == 1)
					{
						Feedback.rateFeedback(user.username, fid, rating, con.stmt);
					}
					else
					{
						System.out.println("You have already rated this feedback!");
					}		
				}

				break;

			case 8:
				// Declare a user as trusted
				String trustedUsername = "";

				System.out.println("Please enter the Username of the user you wish to trust or not-trust:");
				while ((trustedUsername = in.readLine()) == null && trustedUsername.length() == 0);

				int exists = user.checkUsernameUnique(trustedUsername, con.stmt);

				if (exists == 1)
				{
					user.trustUser(user.username, trustedUsername, 1, con.stmt);
				}
				else
				{
					System.out.println("The user does not exist!");
				}

				break;

			case 9:
				// Declare a user as not trusted
				String notTrustedUsername = "";

				System.out.println("Please enter the Username of the user you wish to not trust:");
				while ((notTrustedUsername = in.readLine()) == null && notTrustedUsername.length() == 0);

				exists = user.checkUsernameUnique(notTrustedUsername, con.stmt);

				if (exists == 1)
				{
					user.trustUser(user.username, notTrustedUsername, -1, con.stmt);
				}
				else
				{
					System.out.println("The user does not exist!");
				}
				break;

			case 10:
				// Search for POI
				String upperPrice = "";
				String lowerPrice = "";
				String keyword = "";
				category = "";
				String addressCity = "";
				String addressState = "";
				String sortType = "";
				poiName = "";

				System.out.println("Please enter the following search parameters. If you do not wish to search by the parameter, leave it blank.");

				System.out.println("Please enter the name of the POI:");
				while ((poiName = in.readLine()) == null && poiName.length() == 0);

				System.out.println("Please enter the lowest price you wish to search by:");
				while ((lowerPrice = in.readLine()) == null && lowerPrice.length() == 0);

				System.out.println("Please enter the highest price you wish to search by:");
				while ((upperPrice = in.readLine()) == null && upperPrice.length() == 0);

				System.out.println("Please enter the city:");
				while ((addressCity = in.readLine()) == null && addressCity.length() == 0);

				System.out.println("Please enter the state in abbreviated form:");
				while ((addressState = in.readLine()) == null && addressState.length() == 0);

				System.out.println("Please enter the keywords separated by a space:");
				while ((keyword = in.readLine()) == null && keyword.length() == 0);

				System.out.println("Please enter the category:");
				while ((category = in.readLine()) == null && category.length() == 0);

				System.out.println("Please enter the sort by method: price, feedback score, trusted user feedback score, or leave blank if you do not wish to sort results.");
				while ((sortType = in.readLine()) == null && sortType.length() == 0);

				ArrayList<String> pois = POI.poiSearch(user, poiName, lowerPrice, upperPrice, addressCity, addressState, keyword, category, sortType, con.stmt);

				for (int i = 0; i < pois.size(); i++)
				{
					System.out.println(pois.get(i));
				}
				break;

			case 11: 
				// Top n most 'useful' feedbacks
				String n = "";

				System.out.println("Please enter the name of the POI you would like useful feedback from:");
				while ((poiName = in.readLine()) == null && poiName.length() == 0);

				System.out.println("Please enter the amount of feedbacks you would like:");
				while ((n = in.readLine()) == null && n.length() == 0);

				ArrayList<String> feedBacks = Feedback.getNMostUsefulFeedbacks(poiName, n, con.stmt);

				System.out.println("Feedback is printed with the following fields: fid, login, pid, score, text, date");

				for (int i = 0; i < feedBacks.size(); i++)
				{
					System.out.println(feedBacks.get(i));
				}

				break;

			case 12:
				// Determine Degrees of separation
				String username1 = "";
				String username2 = "";

				System.out.println("Please enter the first username:");
				while ((username1 = in.readLine()) == null && username1.length() == 0);

				System.out.println("Please enter the second username:");
				while ((username2 = in.readLine()) == null && username2.length() == 0);

				int degreesSeparated = User.determineDegreesSeparation(username1, username2, con.stmt);

				if (degreesSeparated == 0)
				{
					System.out.println("The users are neither 1 or 2 degrees separated.");
				}
				else if (degreesSeparated == 1)
				{
					System.out.println("The users are one degree separated.");
				}
				else if (degreesSeparated == 2)
				{
					System.out.println("The users are two degrees separated.");
				}

				break;

			case 13:
				// Statistics
				String statNo = "";

				System.out.println("Please enter the amount of POIs you wish to view for statistics:");
				while ((statNo = in.readLine()) == null && statNo.length() == 0);

				ArrayList<ArrayList<String>> allStats = POI.getStatistics(statNo, con.stmt);
				ArrayList<String> mostPopularPOI = allStats.get(0);
				ArrayList<String> mostExpensivePOI = allStats.get(1);
				ArrayList<String> highestRatedPOI = allStats.get(2);

				System.out.println("The " + statNo + " Most Popular POIs Per Category Are:");
				for (int i = 0; i < mostPopularPOI.size(); i++)
				{
					System.out.println(mostPopularPOI.get(i));
				}

				System.out.println("The " + statNo + " Most Expensive POIs Per Category Are:");
				for (int i = 0; i < mostExpensivePOI.size(); i++)
				{
					System.out.println(mostExpensivePOI.get(i));
				}

				System.out.println("The " + statNo + " Highest Rated POIs Per Category Are:");
				for (int i = 0; i < highestRatedPOI.size(); i++)
				{
					System.out.println(highestRatedPOI.get(i));
				}

				break;

			case 14:
				// Best Users -- User Awards
				System.out.println("Please enter the amount of Users you wish to view for the best uers:");
				while ((statNo = in.readLine()) == null && statNo.length() == 0);

				System.out.println("The most trusted users are: ");
				ArrayList<String> trustedUsers = User.getMostTrustedUsers(statNo, con.stmt);
				for (int i = 0; i < trustedUsers.size(); i++)
				{
					System.out.println(trustedUsers.get(i));
				}

				System.out.println("The most useful users are: ");
				ArrayList<String> usefulUsers = User.getMostUsefulUsers(statNo, con.stmt);
				for (int i = 0; i < usefulUsers.size(); i++)
				{
					System.out.println(usefulUsers.get(i));
				}
				break;
			}
		}
	}

	public static void getUserActions(BufferedReader in, Connector con, User user) throws IOException
	{
		String choice = "";
		int c = 0;

		while(true)
		{
			displayUserMenu();

			while ((choice = in.readLine()) == null && choice.length() == 0);
			try{
				c = Integer.parseInt(choice);
			}catch (Exception e)
			{

				continue;
			}
			if (c<1 | c>11)
				continue;

			switch (c) {

			case 1: 
				// Record Visit
				ArrayList<String> visits = new ArrayList<String>();
				String input = "";

				while (true)
				{
					System.out.println("Please enter the Name of the POI you visited:");
					while ((input = in.readLine()) == null && input.length() == 0);

					visits.add(input);

					System.out.println("Please enter the amount you paid to visit the POI:");
					while ((input = in.readLine()) == null && input.length() == 0);

					visits.add(input);

					System.out.println("Please enter the number of people in your party:");
					while ((input = in.readLine()) == null && input.length() == 0);

					visits.add(input);

					System.out.println("Do you have more visits to record? (Yes/No):");
					while ((input = in.readLine()) == null && input.length() == 0);

					if (input.equals("Yes"))
					{
						continue;
					}
					else
					{
						break;
					}
				}

				System.out.println("Please verify the following records below:");

				for (int i = 0; i < visits.size(); i+=3)
				{
					System.out.println("POI Name: " + visits.get(i) + " Cost: " + visits.get(i+1) + " Number In Party: " + visits.get(i+2));
				}

				System.out.println("Would you like to proceed with adding the records? (Yes/No):");
				while ((input = in.readLine()) == null && input.length() == 0);

				if (input.equals("Yes"))
				{
					Visit visit = new Visit();
					ArrayList<ArrayList<String>> suggestions = visit.addVisit(visits, user, con.stmt);
					ArrayList<String> temp = new ArrayList<String>();		

					System.out.println("Suggested POIs Outputted in form: pid, name, address, url, phone, established, open_time, close_time, price");
					for (int i = 0; i < suggestions.size(); i++)
					{
						temp = suggestions.get(i);
						for (int j = 0; j < temp.size(); j++)
						{
							System.out.println(temp.get(j));
						}
					}
				} 
				else
				{
					System.out.println("Records disregarded");
					continue;
				}

				break;

			case 2:
				String username = user.username;
				String poiName = "";

				System.out.println("Please enter the name of the POI you wish to favorite:");
				while ((poiName = in.readLine()) == null && poiName.length() == 0);

				int result = user.favoritePOI(poiName, username, con.stmt);

				break;

			case 3:
				// Leave feedback for POI
				String score = "";
				String text = "";
				String name = "";

				System.out.println("Please enter the POI Name:");
				while ((name = in.readLine()) == null && name.length() == 0);

				String pid = POI.getPID(name, con.stmt);

				int checkFeedback = Feedback.checkFeedbackExists(user.username, pid, con.stmt);

				if (checkFeedback == 1)
				{
					System.out.println("Please enter your score rating for the POI (0 = terrible, 10 = excellent):");
					while ((score = in.readLine()) == null && score.length() == 0);

					System.out.println("Please enter optional short comment:");
					while ((text = in.readLine()) == null && text.length() == 0);

					Feedback feedback = new Feedback();
					feedback.addFeedback(user.username, POI.getPID(name, con.stmt), score, text, con.stmt);	
				}
				else
				{
					System.out.println("You have already left feedback for this POI.");
				}


				break;

			case 4:
				// View feedback for a particular POI
				System.out.println("Please enter the POI Name you wish to view Feedback for:");
				while ((name = in.readLine()) == null && name.length() == 0);

				String feedBack = Feedback.getFeedback(name, con.stmt);

				System.out.println(feedBack);
				break;

			case 5:
				// Rate Feedback
				String fid = "";
				String rating = "";

				System.out.println("Please enter the ID of the Feedback you wish to rate:");
				while ((fid = in.readLine()) == null && fid.length() == 0);

				System.out.println("Please enter the Rating you wish to give to the feedback. 0 = useless, 1 = useful, 2 = very useful:");
				while ((rating = in.readLine()) == null && rating.length() == 0);

				if (!(rating.equals("0") || rating.equals("1") || rating.equals("2")))
				{
					System.out.println("Please enter a valid rating.");
					break;
				}
				else
				{
					if (Feedback.checkFeedbackExists(user.username, fid, con.stmt) == 1)
					{
						Feedback.rateFeedback(user.username, fid, rating, con.stmt);
					}
					else
					{
						System.out.println("You have already rated this feedback!");
					}		
				}

				break;

			case 6:
				// Declare a user as trusted
				String trustedUsername = "";

				System.out.println("Please enter the Username of the user you wish to trust:");
				while ((trustedUsername = in.readLine()) == null && trustedUsername.length() == 0);

				int exists = user.checkUsernameUnique(trustedUsername, con.stmt);

				if (exists == 1)
				{
					user.trustUser(user.username, trustedUsername, 1, con.stmt);
				}
				else
				{
					System.out.println("The user does not exist!");
				}

				break;

			case 7:
				// Declare a user as not trusted
				String notTrustedUsername = "";

				System.out.println("Please enter the Username of the user you wish to trust:");
				while ((notTrustedUsername = in.readLine()) == null && notTrustedUsername.length() == 0);

				exists = user.checkUsernameUnique(notTrustedUsername, con.stmt);

				if (exists == 1)
				{
					user.trustUser(user.username, notTrustedUsername, -1, con.stmt);
				}
				else
				{
					System.out.println("The user does not exist!");
				}
				break;

			case 8:
				// Search for POI
				String upperPrice = "";
				String lowerPrice = "";
				String keyword = "";
				String category = "";
				String addressCity = "";
				String addressState = "";
				String sortType = "";
				poiName = "";

				System.out.println("Please enter the following search parameters. If you do not wish to search by the parameter, leave it blank.");

				System.out.println("Please enter the name of the POI:");
				while ((poiName = in.readLine()) == null && poiName.length() == 0);

				System.out.println("Please enter the lowest price you wish to search by:");
				while ((lowerPrice = in.readLine()) == null && lowerPrice.length() == 0);

				System.out.println("Please enter the highest price you wish to search by:");
				while ((upperPrice = in.readLine()) == null && upperPrice.length() == 0);

				System.out.println("Please enter the city:");
				while ((addressCity = in.readLine()) == null && addressCity.length() == 0);

				System.out.println("Please enter the state in abbreviated form:");
				while ((addressState = in.readLine()) == null && addressState.length() == 0);

				System.out.println("Please enter the keywords separated by a space:");
				while ((keyword = in.readLine()) == null && keyword.length() == 0);

				System.out.println("Please enter the category:");
				while ((category = in.readLine()) == null && category.length() == 0);

				System.out.println("Please enter the sort by method: price, feedback score, trusted user feedback score, or leave blank if you do not wish to sort results.");
				while ((sortType = in.readLine()) == null && sortType.length() == 0);

				ArrayList<String> pois = POI.poiSearch(user, poiName, lowerPrice, upperPrice, addressCity, addressState, keyword, category, sortType, con.stmt);

				for (int i = 0; i < pois.size(); i++)
				{
					System.out.println(pois.get(i));
				}
				break;

			case 9: 
				// Top n most 'useful' feedbacks
				String n = "";

				System.out.println("Please enter the name of the POI you would like useful feedback from:");
				while ((poiName = in.readLine()) == null && poiName.length() == 0);

				System.out.println("Please enter the amount of feedbacks you would like:");
				while ((n = in.readLine()) == null && n.length() == 0);

				ArrayList<String> feedBacks = Feedback.getNMostUsefulFeedbacks(poiName, n, con.stmt);

				System.out.println("Feedback is printed with the following fields: fid, login, pid, score, text, date");

				for (int i = 0; i < feedBacks.size(); i++)
				{
					System.out.println(feedBacks.get(i));
				}

				break;

			case 10:
				// Determine Degrees of separation
				String username1 = "";
				String username2 = "";

				System.out.println("Please enter the first username:");
				while ((username1 = in.readLine()) == null && username1.length() == 0);

				System.out.println("Please enter the second username:");
				while ((username2 = in.readLine()) == null && username2.length() == 0);

				int degreesSeparated = User.determineDegreesSeparation(username1, username2, con.stmt);

				if (degreesSeparated == 0)
				{
					System.out.println("The users are neither 1 or 2 degrees separated.");
				}
				else if (degreesSeparated == 1)
				{
					System.out.println("The users are one degree separated.");
				}
				else if (degreesSeparated == 2)
				{
					System.out.println("The users are two degrees separated.");
				}

				break;

			case 11:
				// Statistics
				String statNo = "";

				System.out.println("Please enter the amount of POIs you wish to view for statistics:");
				while ((statNo = in.readLine()) == null && statNo.length() == 0);

				ArrayList<ArrayList<String>> allStats = POI.getStatistics(statNo, con.stmt);
				ArrayList<String> mostPopularPOI = allStats.get(0);
				ArrayList<String> mostExpensivePOI = allStats.get(1);
				ArrayList<String> highestRatedPOI = allStats.get(2);

				System.out.println("The " + statNo + " Most Popular POIs Per Category Are:");
				for (int i = 0; i < mostPopularPOI.size(); i++)
				{
					System.out.println(mostPopularPOI.get(i));
				}

				System.out.println("The " + statNo + " Most Expensive POIs Per Category Are:");
				for (int i = 0; i < mostExpensivePOI.size(); i++)
				{
					System.out.println(mostExpensivePOI.get(i));
				}

				System.out.println("The " + statNo + " Highest Rated POIs Per Category Are:");
				for (int i = 0; i < highestRatedPOI.size(); i++)
				{
					System.out.println(highestRatedPOI.get(i));
				}

				break;
			}

		}

		
		
	}
}
