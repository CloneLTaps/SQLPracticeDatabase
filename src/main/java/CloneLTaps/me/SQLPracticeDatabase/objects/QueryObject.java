package CloneLTaps.me.SQLPracticeDatabase.objects;

import CloneLTaps.me.SQLPracticeDatabase.database.Database;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class QueryObject {
    private transient Database dataBase;
    private final String description, explanation, correctQuery;

    public QueryObject(String description, String explanation, String correctQuery) {
        this.description = description;
        this.explanation = explanation;
        this.correctQuery = correctQuery;
    }

    public void setDataBase(Database dataBase) {
        this.dataBase = dataBase;
    }

    public String getProblemDescription() {
        return description;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getCorrectQuery() {
        return correctQuery;
    }

    private List<?> getAnswer(String query) {
        final List<Sextuple<String, String, String, String, String, String>> tableList = new LinkedList<>();

        if(query.equals("")) return tableList;

        try { // keeping the connection open since we are the only ones that are using it
            final Statement statement = dataBase.getConnection().createStatement();
            final ResultSet resultSet = statement.executeQuery(query);
            final int columnCount = resultSet.getMetaData().getColumnCount();

            while(resultSet.next()) {
                final String first = columnCount > 0 ? resultSet.getString(1) + " " : "";
                final String second = columnCount > 1 ? resultSet.getString(2) + " " : "";
                final String third = columnCount > 2 ? resultSet.getString(3) + " " : "";
                final String fourth = columnCount > 3 ? resultSet.getString(4) + " " : "";
                final String fifth = columnCount > 4 ? resultSet.getString(5) + " " : "";
                final String sixth = columnCount > 5 ? resultSet.getString(6) + " " : "";
                tableList.add(new Sextuple<>(first, second, third, fourth, fifth, sixth));
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tableList;
    }

    public boolean isQueryValid(String query) {
        final List<?> answer = getAnswer(getCorrectQuery());
        final List<?> userQuery = getAnswer(query);

        if(answer.size() != userQuery.size()) return false;

        for(int i=0; i<answer.size(); i++) {
            if(!(answer.get(i).equals(userQuery.get(i)))) return false;
        }
        return true;
    }

    public String getQueriedAnswer(String query) {
        if(query == null || query.equals("")) return "";
        final String answer = String.valueOf(getAnswer(query)).replace("[", "").replace("]", "");
        return answer.length() > 0 ? answer.substring(0, answer.length() - 1) : answer;
    }
}
