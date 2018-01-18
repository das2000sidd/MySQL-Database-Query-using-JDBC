//r0618335-SIDDHARTHA DAS
// Before running this program, I downloaded and added the mysql connector driver for JDBC to the lib folder of the WEB-INF of my project
import java.sql.*;
import java.util.Scanner;
import java.io.*;
public class OrderDB {
	//JDBC driver name and database url

	static final String JDBCDRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:8889/orderdb";
	// Database credentials
	static final String USER= "root";
	static final String PASS="root";
	public static void main(String[] args) throws SQLException{



		Connection conn=null; //declaration of connection type object for connecting to the MySQL database
		PreparedStatement stmt=null; // preparedStatement object that will be used for processing all subsequent queries
		ResultSet rs=null; // resultSet object where the value of prepareD statement object execution will be passed to
		try{
			//Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");
			//Open a connection
			System.out.println("Connecting to a selected database");
			conn = DriverManager.getConnection(DB_URL,USER,PASS); // setting up the connection to the database
			System.out.println("For this program to run enter a number other than 0");

			System.out.println("Option '1'- Takes in customer id, asks for specific product and then gives the best rated restaurant for it");
			System.out.println("Option '2'- It will give you categories of food that have been ordered at least 5 times");
			System.out.println("Option '3'- It will give you the list of 5 most sold products");

			System.out.println("Please make a choice between numbers 1 to 3 below");
			Scanner s3 = new Scanner(System.in);
			int customerChoice = s3.nextInt();


			while(customerChoice !=0){

			

				System.out.println("Please make a choice between numbers 1 to 3 below");
				try{
					BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
					customerChoice = Integer.parseInt(br1.readLine());
				} catch(NumberFormatException ex){
					System.err.println("Not a valid number");
				} catch(IOException e){
					e.printStackTrace();
				}


				if(customerChoice==1){
					// Execute query
					System.out.println("Creating statement...");
					System.out.println("Please enter your customer number");
					//Get details of customer based on their id

					Scanner s = new Scanner(System.in);
					int customerId = s.nextInt();
					String sql="SELECT * FROM customer WHERE customer_id=?"; //SQL QUERY
					stmt = conn.prepareStatement(sql);
					stmt.setInt(1, customerId);
					rs = stmt.executeQuery();
					while(rs.next()){
						String customer_first_name = rs.getString("first_name");
						String customer_last_name = rs.getString("last_name");
						int zip_code = rs.getInt("zipcode");
						int customer_id = rs.getInt("customer_id");
						System.out.println("Customer id"+" "+"Customer First Name" + "  " + "Customer Last Name" + "  " +"Zip code");
						System.out.println(customer_id+ " " + customer_first_name + "   " +customer_last_name  +"    " +zip_code);
						System.out.println("Successful display of customer details");
					}
					System.out.println("\n");
					System.out.println("\n");
					System.out.println("\n");
					System.out.println("\n");
					//Order all food items first by category and then by name
					String product = "SELECT * FROM product ORDER BY category,name"; //SQL QUERY
					stmt=conn.prepareStatement(product);
					rs = stmt.executeQuery();
					System.out.println(" Product id "+" "+ " Product name " + " Product category ");
					while(rs.next()){
						int product_id = rs.getInt("product_id");
						String product_name = rs.getString("name");
						String category = rs.getString("category");

						System.out.println(product_id+ " " + product_name + "   " +category );
					}
					System.out.println("What product are you interested in");
					//BufferedReader br2 = new BufferedReader(new InputStreamReader(System.setIn(in);));
					//productOfInterest = Integer.parseString(br2.readLine());
					Scanner t = new Scanner(System.in);
					String productOfInterest= t.nextLine(); // User input object is passed to productOfInterest and processed further
					int product_id;
					String idProduct = "SELECT product.product_id FROM product WHERE product.name=?"; //SQL QUERY
					stmt=conn.prepareStatement(idProduct);
					stmt.setString(1,productOfInterest);
					rs=stmt.executeQuery();
					while(rs.next()){
						product_id = rs.getInt(1);
						//System.out.println("The id of " + productOfInterest + " is "+ product_id);
						String updateOrder = "INSERT INTO orders(customer_id,product_id) SELECT customer_id,product_id FROM customer,product WHERE customer.customer_id=? AND product.product_id=?";
						stmt = conn.prepareStatement(updateOrder);
						stmt.setInt(1, customerId);
						stmt.setInt(2, product_id);
						stmt.executeUpdate();

					}
					System.out.println("\n");
					System.out.println("\n");
					System.out.println("\n");
					System.out.println("\n");


					//Best restaurant for specific food item
					String bestRestaurant = 
							"SELECT restaurant.rest_id,restaurant.name,product.product_id,product.name AS `Product Name`,offers.rating " +  
									"FROM restaurant,product,offers " +  
									"WHERE product.product_id=offers.product_id AND restaurant.rest_id=offers.rest_id AND product.name=? " + 
									"ORDER BY offers.rating DESC " + 
									"LIMIT 1 "; // THIS WORKS FINE WHEN entry is copy pasted and not typed in Eclipse console since only column arguement is passed to setString in line 126
					stmt = conn.prepareStatement(bestRestaurant);
					stmt.setString(1,productOfInterest);
					rs=stmt.executeQuery();
					System.out.println("Displaying information about best rated restaurant for your product");
					while(rs.next()){
						//	int restaurantId = rs.getInt("rest_id"); Not needed as per question	
						String restaurantName = rs.getString("name");
						//	int productId = rs.getInt("product_id"); // not needed as per question
						String productName1 = rs.getString("Product Name");
						int restaurantRating = rs.getInt("rating");
						System.out.println("  Product Name    " + " Restaurant Name  " + "  restaurantRating  ");

						System.out.println(productName1+ " "+ restaurantName+ " "+ restaurantRating);
					}
					System.out.println("\n");
					System.out.println("\n");
					System.out.println("\n");
					System.out.println("\n");
				} else if(customerChoice==2){			

					// Food category ordered at least 5 times
					System.out.println("Displaying food category sold count");
					String noOfOrdersByCategory = 
							" SELECT a.category,COUNT(*) AS `Total Ordered` " + 
									" FROM product AS a,orders AS b " +
									" WHERE a.product_id=b.product_id " +
									" GROUP BY a.category " +
									" HAVING COUNT(*) >= 5 " +
									" ORDER BY COUNT(*) DESC,a.category ";
					stmt = conn.prepareStatement(noOfOrdersByCategory);
					rs=stmt.executeQuery();
					System.out.println("  Category of Food  " + "   Total no of orders  " );
					while(rs.next()){
						String productCategory = rs.getString("category");
						int totalOrders = rs.getInt("Total Ordered");

						System.out.println(productCategory+ " "+totalOrders);
					}
					//break;
				} else if(customerChoice==3){	

					// Top 5 most ordered products
					System.out.println("Displaying food item sold count");
					String ordersOfFoodType = " SELECT product.product_id,product.name,COUNT(*) AS `Total Ordered` " + 
							" FROM product,orders " +
							" WHERE product.product_id=orders.product_id " +
							" GROUP BY product.product_id " +
							" ORDER BY COUNT(*) DESC, product.product_id " +  
							" LIMIT 5 ";
					stmt = conn.prepareStatement(ordersOfFoodType);
					rs=stmt.executeQuery();
					System.out.println("  Product ID  "+"   Product Name   "+ "   Total Orders   ");
					while(rs.next()){
						int productID = rs.getInt(1);
						String productName = rs.getString(2);
						int totalOrders = rs.getInt(3);
						System.out.println(productID+ "   "+productName+"   "+totalOrders);
					}

				}	

			}
			System.out.println("End of session");

		} // try block closing
		
		catch(ClassNotFoundException e){
			System.out.println("Error: " + e.getMessage()); // if jdbc driver not in classpath
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		conn.close();
	}// end of main block
}// end of class

