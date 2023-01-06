package CloneLTaps.me.SQLPracticeDatabase.database;

import CloneLTaps.me.SQLPracticeDatabase.FileManager;
import CloneLTaps.me.SQLPracticeDatabase.objects.QueryObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.Yaml;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.io.*;

public abstract class Database {
    private List<QueryObject> finalQueryObjectList = new LinkedList<>();
    private final List<QueryObject> usedQueryObjectsList = new ArrayList<>();

    protected final String host, connectionString, username, password;
    protected final Integer port;

    public Database(final FileManager fileManager) throws FileNotFoundException {
        final Yaml yaml = new Yaml();
        final InputStream inputStream = new FileInputStream(fileManager.getConfigFile());
        final Map<String, ?> configData = yaml.load(inputStream);

        port = (int) (configData.get("port") != null ? configData.get("port") : 0);
        host = String.valueOf(configData.get("host") != null ? configData.get("host") : "");
        username = String.valueOf(configData.get("username") != null ? configData.get("username") : "");
        password = String.valueOf(configData.get("password") != null ? configData.get("password") : "");

        if(fileManager.isMySQL()) connectionString = "jdbc:mysql://" + host + ":" + port + "/" + fileManager.getDatabaseName();
        else connectionString = "";

        recoverData(fileManager);
    }

    /**
     * This handles turning all json problems into objects. Then I have to go through each and add set
     * the database since its a transient variable due to me constructing the database and starting a
     * connection at runtime. However, it does not technically need to be transient due to it only be
     * deserialized but it makes it obvious that it's not saved in the problems.json file.
     */
    private void recoverData(final FileManager fileManager) {
        final File problemFile = fileManager.getProblemFile();

        if(problemFile.exists() && problemFile.length() > 2) {
            try {
                final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                final Type type = new TypeToken<List<QueryObject>>() {}.getType();
                final List<QueryObject> queryList = gson.fromJson(new FileReader(problemFile), type);

                for(QueryObject query : queryList) {
                    query.setDataBase(this);
                    finalQueryObjectList.add(query);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract Connection getConnection();

    public QueryObject getFinalQueryIndex(int i) {
        return finalQueryObjectList.get(i);
    }

    public int getFinalQuerySize() {
        return finalQueryObjectList.size();
    }

    public void removeFinalQuery(QueryObject queryObject) {
        finalQueryObjectList.remove(queryObject);
    }

    public void setFinalQueryObjectsList() {
        finalQueryObjectList = new LinkedList<>(usedQueryObjectsList);
    }

    public void addUsedQuery(QueryObject queryObject) {
        usedQueryObjectsList.add(queryObject);
    }

}