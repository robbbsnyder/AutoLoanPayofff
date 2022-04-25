import java.sql.*;

public class DBConnection {

    Connection dbCon;

    DBConnection() {

        // Create a variable for the connection string.
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=Joe;user=sa;password=cvs";

        try {

            dbCon = DriverManager.getConnection(connectionUrl);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Connection getConnection() {
        return dbCon;
    }

}