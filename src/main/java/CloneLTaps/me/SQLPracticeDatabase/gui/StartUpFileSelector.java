package CloneLTaps.me.SQLPracticeDatabase.gui;

import CloneLTaps.me.SQLPracticeDatabase.FileManager;
import CloneLTaps.me.SQLPracticeDatabase.Main;
import CloneLTaps.me.SQLPracticeDatabase.database.MySQLDatabase;
import CloneLTaps.me.SQLPracticeDatabase.database.SQLiteDatabase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.*;
import java.util.prefs.Preferences;

public class StartUpFileSelector extends JFrame implements ActionListener {
    private final File problemFile, configFile, settingsFile, readMeFile;
    private final JButton button;

    /**
     * ActionEvents seem to occur on a separate thread from the main thread which means our main class
     * will finish executing its code while we are still waiting for a users action. Because of this
     * we can just let the main thread finish running and have the ActionEvent's thread handle the rest
     * of connecting to the database after a valid path is chosen. However, if a path was chosen earlier
     * without any errors this class wont be called and everything will execute from the main thread.
     */
    public StartUpFileSelector(File problemFile, File configFile, File settingsFile, File readMeFile) {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

        button = new JButton("Select the directory you would like to save your files in.");
        button.addActionListener(this);
        button.setPreferredSize(new Dimension(350, 50));

        this.add(button);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(true);

        this.problemFile = problemFile;
        this.configFile = configFile;
        this.settingsFile = settingsFile;
        this.readMeFile = readMeFile;
    }

    /**
     * After the person selects their path the file selector will close and the rest of the
     * code inside of main will finish running.
     */
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() != button) return;

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(""));
        fileChooser.setAcceptAllFileFilterUsed(false); // Disables "All files" option
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        final int response = fileChooser.showSaveDialog(null);

        if(response != JFileChooser.APPROVE_OPTION) return;

        final String chosenPath = fileChooser.getSelectedFile().getAbsolutePath();
        final Preferences systemPreferences = Preferences.userNodeForPackage(Main.class);
        systemPreferences.put("Path", chosenPath);

        final Thread t = new Thread(() -> { // Using another thread in order to not block the event thread
            saveFilesToPath(chosenPath);
            deleteGUI();

            try { // This will then start our databases which will then load our main gui
                final FileManager fileManager = new FileManager();
                if(fileManager.isMySQL()) new MySQLDatabase(fileManager);
                else new SQLiteDatabase(fileManager);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void deleteGUI() {
        this.dispose();
    }

    private void saveFilesToPath(String path) {
        final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        final InputStream problemsIs = classloader.getResourceAsStream("problems.json");
        final InputStream configIs = classloader.getResourceAsStream("config.yml");
        final InputStream settingsIs = classloader.getResourceAsStream("settings.yml");
        final InputStream readmeIs = classloader.getResourceAsStream("readme.yml");

        try {
            if(problemsIs != null && !problemFile.exists()) {
                Files.copy(problemsIs, Paths.get(path + "/problems.json"), StandardCopyOption.REPLACE_EXISTING);
                problemsIs.close();
                checkFilesStatus(path + "/problems.json");
            }

            if(configIs != null && !configFile.exists()) {
                Files.copy(configIs, Paths.get(path + "/config.yml"), StandardCopyOption.REPLACE_EXISTING);
                configIs.close();
                checkFilesStatus(path + "/config.yml");
            }

            if(settingsIs != null && !settingsFile.exists()) {
                Files.copy(settingsIs, Paths.get(path + "/settings.yml"), StandardCopyOption.REPLACE_EXISTING);
                settingsIs.close();
                checkFilesStatus(path + "/settings.yml");
            }

            if(readmeIs != null && !readMeFile.exists()) {
                Files.copy(readmeIs, Paths.get(path + "/readme.yml"), StandardCopyOption.REPLACE_EXISTING);
                readmeIs.close();
                checkFilesStatus(path + "/readme.yml");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFilesStatus(String fullPath) {
        while(true) { // This will ensure the file has been created before we continue
            final File file = new File(fullPath);

            if(!file.exists() || file.length() < 20) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else break;
        }
    }


    private void writeFilesToPath(String chosenPath, File problems, File config) {
        try {
            final ObjectMapper om = new ObjectMapper(new YAMLFactory());
            om.writeValue(new File(chosenPath + "/config.yml"), config);

            final Gson gson = new GsonBuilder().setPrettyPrinting().create();
            final Writer writer = new FileWriter(new File(chosenPath + "/problems.json"));
            gson.toJson(problems, writer);
            writer.flush();
            writer.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

}
