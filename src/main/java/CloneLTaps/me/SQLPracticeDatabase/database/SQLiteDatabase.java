package CloneLTaps.me.SQLPracticeDatabase.database;

import CloneLTaps.me.SQLPracticeDatabase.FileManager;
import CloneLTaps.me.SQLPracticeDatabase.gui.ScreenGui;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.sql.*;
import java.util.List;

public class SQLiteDatabase extends Database {
    private Connection connection;

    public SQLiteDatabase(final FileManager fileManager) throws FileNotFoundException {
        super(fileManager);

        File dbFile;
        boolean createdFile = false;
        if(fileManager.useExample()) {
            System.out.println("Using Example SQLite Database");
            dbFile = new File(fileManager.getPath(), fileManager.getExampleName() + ".db");

            if(!dbFile.exists()) { // We need to create our tutorial file
                try {
                    dbFile.createNewFile();
                    dbFile = new File(fileManager.getPath(), fileManager.getExampleName() + ".db");
                    createdFile = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            dbFile = new File(fileManager.getPath(), fileManager.getDatabaseName() + fileManager.getFileType());
            System.out.println("Using Custom SQLite Database");

            if(!dbFile.exists()) { // This means the persons custom SQLite file does not exist
                System.out.println(fileManager.getDatabaseName() + fileManager.getFileType() + " does not exist at " + fileManager.getPath());
                System.exit(1);
                return;
            }
        }

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(createdFile) populateExampleDb(fileManager);

        // This opens the question qui
        new ScreenGui(this, fileManager);
    }

    @SuppressWarnings("unchecked")
    private void populateExampleDb(final FileManager f) {
        try {
            if(connection != null && !connection.isClosed()) {
                final Statement s = connection.createStatement();

                final String createTableOne = String.valueOf(f.getConfigData().get("create-table-one")).replace("[", "").replace("]", "");
                s.executeUpdate("CREATE TABLE IF NOT EXISTS " + f.getConfigData().get("table-one") + " (" + createTableOne + ")");

                final String createTableTwo = String.valueOf(f.getConfigData().get("create-table-two")).replace("[", "").replace("]", "");
                s.executeUpdate("CREATE TABLE IF NOT EXISTS " + f.getConfigData().get("table-two") + " (" + createTableTwo + ")");

                for(String t1 : (List<String>) f.getConfigData().get("insert-table-one")) {
                    s.executeUpdate(t1);
                }

                for(String t2 : (List<String>) f.getConfigData().get("insert-table-two")) {
                    s.executeUpdate(t2);
                }
                s.close();
            }
        } catch (SQLException | NullPointerException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public Connection getConnection() {
        return connection;
    }
}
