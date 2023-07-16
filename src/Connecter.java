import java.sql.*;

public class Connecter {
    static Connection connection = null;
    public static Statement statement = null;
    public Connecter() {
        OpenConnection();
    }
    public static void OpenConnection() {
        String url = "jdbc:mysql://localhost:3306/education_tracker";
        String user = "random";
        String password = "Azerty13!";

        try {
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void CloseConnection() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}