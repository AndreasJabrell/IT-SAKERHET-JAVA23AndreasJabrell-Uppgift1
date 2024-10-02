import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {
        String jdbcUrl = "jdbc:mysql://localhost:3306/userdata";
        String username = "root";
        String password = "Andreas1";
        int choice = 0;
        int loggedInChoice = 0;
        boolean loggedIn = false;
        boolean running = true;
        Scanner scanner = new Scanner(System.in);


        while(running) {
            String welcome = "Welcome, make a choice between 1-Create, 2-Log in, 3-Quit";
            System.out.println(welcome);
            choice = scanner.nextInt();
            switch (choice) {

                case 1:
                    System.out.print("Enter Name (with spaces if needed): ");
                    String name = scanner.next();  // Läser hela raden inklusive mellanslag

                    System.out.print("Enter E-mail: ");
                    String email = scanner.next();

                    System.out.print("Enter age: ");
                    String age = scanner.next();

                    System.out.print("Enter PhoneNumber: ");
                    String phone = scanner.next();

                    System.out.print("Enter Password: ");
                    String userPassword = scanner.next();

                    UserRegistration.saveToDB(name, userPassword, email, phone, age);
                    break;

                case 2:
                    System.out.print("Enter E-mail: ");
                    String userEmail = scanner.next();
                    System.out.print("Enter password: ");
                    String userPasswordLogIn = scanner.next();
                    if (UserRegistration.loginUser(userEmail, userPasswordLogIn)) {
                        loggedIn = true;
                        while (loggedIn) {
                            System.out.println("Press 1 to show user data, 2 to delete account, 3 to quit");

                            loggedInChoice = scanner.nextInt();
                            switch (loggedInChoice) {
                                case 1:
                                    System.out.println(userEmail);
                                    UserRegistration.displayLoggedInUserFromDB(userEmail);
                                    break;
                                case 2:
                                    UserRegistration.deleteLoggedInUserFromDB(userEmail);
                                    userEmail = "";
                                    userPasswordLogIn = "";
                                    loggedIn = false;

                                    break;
                                case 3:
                                    System.out.println("You're now logged out");
                                    userEmail = "";
                                    userPasswordLogIn = "";
                                    loggedIn = false;
                                    break;
                                default:
                                    System.out.println("Invalid choice. Please select between 1-3.");
                            }

                        }
                    }

                    break;

                case 3:
                    System.exit(0);


                    //kommande två alternativen här är för eventuell admin
                case 4:
                    System.out.println("Enter ID of person to be deleted:  ");
                    String deleteID = scanner.next();
                    UserRegistration.deleteFromDB(deleteID);
                    break;
                case 5:
                    UserRegistration.displayUserFromDB(); //Maybe add search by email here
                    break;
                default:
                    System.out.println("Invalid choice. Please select between 1-4.");
                    break;

            }
        }

        try {
            // Ladda JDBC-drivrutinen (behövs inte längre med moderna JDK-versioner, men bra att inkludera för äldre versioner)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Skapa en anslutning till databasen
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Anslutningen till MySQL var framgångsrik!");

            // Stäng anslutningen när du är klar
            connection.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Kunde inte ladda MySQL JDBC-drivrutinen");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Kunde inte ansluta till databasen");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
