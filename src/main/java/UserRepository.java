import java.sql.*;

public class UserRepository {

    private static final String URL =
        "jdbc:sqlite:src/main/resources/database/BudgetData.db";

    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            System.out.println("Σφάλμα ελέγχου username");
            return false;
        }
    }

    public boolean saveUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Το username υπάρχει ήδη");
            return false;
        }
    }

    public boolean checkLogin(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return false;

            return password.equals(rs.getString("password"));

        } catch (Exception e) {
            System.out.println("Σφάλμα login");
            return false;
        }
    }
}

