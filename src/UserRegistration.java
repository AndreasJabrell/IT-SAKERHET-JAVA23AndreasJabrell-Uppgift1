import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class UserRegistration {

    private static final String BASE_DIRECTORY = Paths.get("").toAbsolutePath() + "/src/Files";

    // Metod för att hasha lösenord med SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveToDB(String name, String password, String email, String phone, String age) {
        String hashedPassword = hashPassword(password);

        String sql = "INSERT INTO userdata (name, age, email, password, phone) VALUES (?,?,?,?,?)";

        try {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, age);
            ps.setString(3, email);
            ps.setString(4, hashedPassword);
            ps.setString(5, phone);
            ps.executeUpdate();

            System.out.println(sql);
            System.out.println("We did it!!!!!!");

        } catch (SQLException e) {
            System.out.println("We didn't make it!!!" + e.getMessage());
        }
    }

    public static void displayUserFromDB() {
        String sql = "SELECT id, name FROM userdata";

        try{
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery(sql);

            while (rs.next()) {
                System.out.println("Name:  " + rs.getString("name"));
                System.out.println("ID:  " + rs.getString("id"));
                System.out.println("--------------------------------------");
            }
        }catch (SQLException e) {
            System.out.println("We didn't make it!!!" + e.getMessage());
        }


    }

    public static void displayLoggedInUserFromDB(String email) {
        String sql = "SELECT * FROM userdata WHERE email = ?";

        try {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            // Sätt värdet på parametern (email) till SQL-frågan
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                System.out.println("ID:  " + rs.getString("id"));
                System.out.println("Name:  " + rs.getString("name"));
                System.out.println("Email:  " + rs.getString("email"));
                System.out.println("Age:  " + rs.getString("age"));
                System.out.println("Phone number:  " + rs.getString("phone"));
                System.out.println("--------------------------------------");
            }

            rs.close();
            ps.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("We didn't make it!!! " + e.getMessage());
        }


    }

    public static void deleteFromDB(String id) {
        String sql = "DELETE FROM userdata WHERE id=?";

        try {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, id);  // Ställ in id-parametern
            ps.executeUpdate();   // Kör frågan utan att skicka SQL-strängen igen
            System.out.println("User with ID " + id + " was deleted successfully.");
        } catch (SQLException e) {
            System.out.println("We didn't make it!!! " + e.getMessage());
        }
    }

    public static void deleteLoggedInUserFromDB(String email) {
        String sql = "DELETE FROM userdata WHERE email=?";

        try {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);  // Ställ in id-parametern
            ps.executeUpdate();   // Kör frågan utan att skicka SQL-strängen igen
            System.out.println("User with email " + email + " was deleted successfully.");
        } catch (SQLException e) {
            System.out.println("We didn't make it!!! " + e.getMessage());
        }
    }


    public static boolean loginUser(String email, String password) {
        String hashedPassword = hashPassword(password);  // Hasha det inmatade lösenordet
        String sql = "SELECT * FROM userdata WHERE email = ?";

        try {
            Connection connection = Database.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);  // Mata in e-post i frågan
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPasswordHash = rs.getString("password");  // Hämta den hashade lösenordet från databasen
                if (hashedPassword.equals(storedPasswordHash)) {
                    System.out.println("User logged in successfully.");
                    /*System.out.println("ID:  " + rs.getString("id"));
                    System.out.println("Name:  " + rs.getString("name"));
                    System.out.println("Email:  " + rs.getString("email"));
                    System.out.println("Age:  " + rs.getString("age"));
                    System.out.println("Phone number:  " + rs.getString("phone"));*/
                } else{
                    System.out.println("WRONG PASSWORD, TRY AGAIN");
                    return false;
                }

                return storedPasswordHash.equals(hashedPassword);  // Jämför hasharna
            } else {
                System.out.println("No user found with this email.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Login failed: " + e.getMessage());
            return false;
        }
    }
}
