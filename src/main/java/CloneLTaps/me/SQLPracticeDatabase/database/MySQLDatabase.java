package CloneLTaps.me.SQLPracticeDatabase.database;

import CloneLTaps.me.SQLPracticeDatabase.FileManager;
import CloneLTaps.me.SQLPracticeDatabase.gui.ScreenGui;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabase extends Database {
    private Connection connection;

    public MySQLDatabase(final FileManager fileManager) throws FileNotFoundException {
        super(fileManager);
        System.out.println("Using MySQL Database");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(this.connectionString, this.username, this.password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // This opens the question qui
        new ScreenGui(this, fileManager);
    }

    @Override
    public Connection getConnection() {
        return connection;
    }
}
