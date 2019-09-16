package ch.cbdev.mysqlwrapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Query {
    private Database database;
    private String table;
    private String query;
    private boolean needsWhere = true;
    private boolean where = false;
    private boolean needsSet = false;
    private boolean set = false;
    private boolean needsValues = false;
    private boolean values = false;
    private boolean needsOn = false;
    private boolean on = false;

    Query(Database database, String table) {
        this.database = database;
        this.table = table;
        query = "FROM " + "VaorraCore." + table;
    }

    public Query join(String tableName){
        this.needsOn = true;
        this.needsWhere = false;
        query += " JOIN " + tableName + " ON ";

        return this;
    }

    public Query on(String columnLeft, String comparator, String columnRight){
        on = true;

        query += " " + columnLeft + comparator + columnRight;

        CheckForColumnSpecification(columnLeft);
        CheckForColumnSpecification(columnRight);
        return this;
    }

    public Query find(int id){
        return where("id", "=", id);
    }

    public Query where(String column, String comparator, Object value){
        if (where){
            query += " AND";
        }

        query += " WHERE " + column + comparator + convertToSQLString(value);

        where = true;
        return this;
    }

    public Query set(String column, Object value){
        if (set){
            query += ",";
        }

        else{
            query += " SET";
        }

        query += " " + column + "=" + convertToSQLString(value);

        set = true;
        return this;
    }

    public Query values(String key, Object value){

        if (values){
            String left = query.split(" VALUES ")[0];
            String right = query.split(" VALUES ")[1];
            left += ", " + key;
            right += ", " + convertToSQLString(value);
            query = left + " VALUES " + right;
        }

        else{
            query += " (" + key +" VALUES (" + convertToSQLString(value);
        }
        values = true;
        return this;
    }

    public ResultSet get(){
        CheckForCondition();
        query = "SELECT * " + query;
        ResultSet resultSet = database.executeQuery(query);
        try {
            resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public List<Integer> getAll(){
        needsWhere = false;
        ResultSet resultSet = get();
        List<Integer> ids = new ArrayList<Integer>();
        try{
            do{
                ids.add(resultSet.getInt("id"));
            } while (resultSet.next());
        } catch (SQLException e){
            e.printStackTrace();
        }
        return ids;
    }

    public boolean exists(){
        CheckForCondition();
        query = "SELECT * " + query;
        ResultSet resultSet = database.executeQuery(query);
        try {
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int insert(){
        this.needsWhere = false;
        this.needsValues = true;
        CheckForCondition();
        String left = query.split(" VALUES ")[0];
        String right = query.split(" VALUES ")[1];
        left += ")";
        right += ")";
        query = left + " VALUES " + right;
        query = "INSERT INTO " + query.substring(5);
        database.execute(query);
        ResultSet idResult = database.executeQuery("SELECT id FROM " + table + " ORDER BY id DESC LIMIT 1");
        try {
            idResult.next();
            return idResult.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void update(){
        this.needsSet = true;
        CheckForCondition();
        query = "UPDATE " + query.substring(5);
        database.execute(query);
    }

    public void delete(){
        CheckForCondition();
        query = "DELETE " + query;
        database.execute(query);
    }

    private void CheckForCondition(){
        if (needsWhere && !where){
            throw new NoConditionException("where");
        }

        if (needsOn && !on){
            throw new NoConditionException("on");
        }

        if (needsSet && !set){
            throw new NoConditionException("set");
        }

        if (needsValues && !values){
            throw new NoConditionException("values");
        }
    }

    private void CheckForColumnSpecification(String column){
        if (column.split("\\.").length != 2){
            throw new UnspecifiedColumnException(column);
        }
    }

    private String convertToSQLString(Object object){
        if (object == null){
            return "null";
        }
        else if (object instanceof String){
            return "'" + object.toString() + "'";
        }
        else if (object instanceof Date){
            return "'" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((Date)object) + "'";
        }
        else if (object instanceof Boolean){
            return (Boolean)object ? "1" : "0";
        }
        return object.toString();
    }
}
