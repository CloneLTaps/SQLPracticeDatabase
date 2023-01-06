package CloneLTaps.me.SQLPracticeDatabase;

import CloneLTaps.me.SQLPracticeDatabase.database.MySQLDatabase;
import CloneLTaps.me.SQLPracticeDatabase.database.SQLiteDatabase;
import CloneLTaps.me.SQLPracticeDatabase.gui.StartUpFileSelector;
import java.util.prefs.Preferences;
import java.io.*;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        final Preferences systemPreferences = Preferences.userNodeForPackage(Main.class);
        final String path = systemPreferences.get("Path", "");

        final File problemFile = new File(path + "/problems.json");
        final File configFile = new File(path + "/config.yml");
        final File settingsFile = new File(path + "/settings.yml");
        final File readMeFile = new File(path + "/readMe.yml");

        if(!problemFile.exists() || !configFile.exists() || !settingsFile.exists() || !readMeFile.exists()) {
            new StartUpFileSelector(problemFile, configFile, settingsFile, readMeFile);
            return;
        }

        final FileManager fileManager = new FileManager();

        if(fileManager.isMySQL()) new MySQLDatabase(fileManager);
        else new SQLiteDatabase(fileManager);
    }

}
