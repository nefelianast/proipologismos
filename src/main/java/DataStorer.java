import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//working on it :)
public class DataStorer {
    
    public void Store() {
        String DBurl = "jdbc:sqlite:src/main/resources/database/BudgetData.db";
       

        try {
            Connection conn = DriverManager.getConnection(DBurl);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        
        

    }

}