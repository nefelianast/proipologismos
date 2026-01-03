import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import ui.DatabaseConnection;
//working on it :)
public class DataStorer {
    
    public void Store() {
        try {
            Connection conn = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        
        

    }

}