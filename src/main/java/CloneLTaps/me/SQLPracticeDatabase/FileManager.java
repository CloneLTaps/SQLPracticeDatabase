package CloneLTaps.me.SQLPracticeDatabase;

import java.util.prefs.Preferences;
import org.yaml.snakeyaml.Yaml;
import java.util.Map;
import java.io.*;

public class FileManager {
    private final File settingsFile, configFile, problemFile, readMeFile;
    private final boolean mySQL, useExample, pickRandomly, discardAnswers, showDataQueried;
    private final String path, exampleName, databaseName, fileType;
    private final Map<String, ?> configData;

    /**
     * Preferences are a persistent data storage technique designed to store basic information. This data can be viewed
     * in the registry editor under Computer\HKEY_CURRENT_USER\Software\JavaSoft\Prefs
     *
     * I use this to save the path of the resource files so that I can always refer back to these files without asking
     * the user to select where they saved the files each time or auto generating the files in a generic location.
     */
    public FileManager() throws FileNotFoundException {
        final Preferences systemPreferences = Preferences.userNodeForPackage(Main.class);
        final String path = systemPreferences.get("Path", "");
        this.path = path;

        problemFile = new File(path + "/problems.json");
        configFile = new File(path + "/config.yml");
        settingsFile = new File(path + "/settings.yml");
        readMeFile = new File(path + "/readMe.yml");

        final Yaml yaml = new Yaml();
        final Map<String, ?> configData = yaml.load(new FileInputStream(configFile));
        mySQL = (boolean) (configData.get("mysql") != null ? configData.get("mysql") : false);
        useExample = (boolean) (configData.get("use-example") != null ? configData.get("use-example") : true);
        pickRandomly = (boolean) (configData.get("pick-randomly") != null ? configData.get("pick-randomly") : true);
        showDataQueried = (boolean) (configData.get("show-data-queried") != null ? configData.get("show-data-queried") : true);
        discardAnswers = (boolean) (configData.get("discard-correct-answers") != null ? configData.get("discard-correct-answers") : true);
        exampleName = String.valueOf(configData.get("example-name") != null ? configData.get("example-name") : "tutorial");
        databaseName = String.valueOf(configData.get("database") != null ? configData.get("database") : "tutorial");
        fileType = String.valueOf(configData.get("file-type") != null ? configData.get("file-type") : ".db");
        this.configData = configData;
    }

    public boolean doFilesExist() {
        return configFile.exists() && problemFile.exists() && settingsFile.exists() && readMeFile.exists();
    }

    public File getConfigFile() {
        return configFile;
    }

    public File getSettingsFile() {
        return settingsFile;
    }

    public File getProblemFile() {
        return problemFile;
    }

    public File getReadMeFile() {
        return readMeFile;
    }

    public boolean isMySQL() {
        return mySQL;
    }

    public String getPath() {
        return path;
    }

    public boolean useExample() {
        return useExample;
    }

    public boolean pickRandomly() {
        return pickRandomly;
    }

    public String getExampleName() {
        return exampleName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getFileType() {
        return fileType;
    }

    public Map<String, ?> getConfigData() {
        return configData;
    }

    public boolean discardAnswers() {
        return discardAnswers;
    }

    public boolean showDataQueried() {
        return showDataQueried;
    }
}
