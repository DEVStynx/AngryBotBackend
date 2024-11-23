package de.stynxyxy.angrybot.db;

import java.sql.*;
import java.util.UUID;

public class AngryDataBase {


    private Connection DATABASECONNECTION;
    public AngryDataBase(String url, String user, String password) {
        try {
            this.DATABASECONNECTION = createConnection(url,user, password);
            Statement statement = this.DATABASECONNECTION.createStatement();
            ResultSet set =  statement.executeQuery("SELECT * FROM USERDATA");


            int elements = 0;
            while(set.next()) {
                elements++;
            }

            System.out.printf("Found %s UserEntries In Database! \n",elements);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static Connection createConnection(String url, String Username, String Password) {
        System.out.printf("SQL url: %s Username: %s, Password: %s",url,Username,Password);
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, Username, Password);
            return con;
        } catch (SQLException exception) {
            exception.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("MYSQL Driver is not found;");
        }
        return null;

    }
    public Connection getDATABASECONNECTION() {
        return DATABASECONNECTION;
    }
    public void addConnectionToDb(String name, UUID id, String ipAddress, Date lastLoginDate) throws SQLException {

        PreparedStatement statement = this.DATABASECONNECTION.prepareStatement("SELECT * FROM userdata WHERE LastIPAdress= (?)");
        statement.setString(1,ipAddress);
        ResultSet result = statement.executeQuery();
        int size = 0;
        while (result.next()) {
            size++;
        }
        if (size <1) {
            String sql = "INSERT INTO USERDATA (USERNAME, USERID, LASTIPADRESS, LASTLOGINDATE) VALUES (?, ?, ?, ?)";
            try (PreparedStatement preparedStatement = this.DATABASECONNECTION.prepareStatement(sql)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, id.toString());
                preparedStatement.setString(3, ipAddress);
                preparedStatement.setDate(4, new java.sql.Date(lastLoginDate.getTime()));

                preparedStatement.executeUpdate();
            }
        } else {
            System.out.println("There already was a Database Entry dedicated to this Ipadress!");
        }


    }

}
