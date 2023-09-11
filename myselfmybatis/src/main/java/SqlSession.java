import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SqlSession {
    private TransactionManager transactionManager;
    private Map<String, ReMappedStatement> mappedStatements;

    public SqlSession(TransactionManager transactionManager, Map<String, ReMappedStatement> mappedStatements) {
        this.transactionManager = transactionManager;
        this.mappedStatements = mappedStatements;
    }

    public void commit() {
        transactionManager.commit();
    }

    public void rollback() {
        transactionManager.rollback();
    }

    public void close() {
        transactionManager.close();
    }

    public int insert(String sqlId, Object obj) {
        ReMappedStatement mappedStatement = mappedStatements.get(sqlId);
        Connection conn = transactionManager.getConnection();
        String reMybatisSql = mappedStatement.getSql();
        String sql = reMybatisSql.replaceAll("#\\{[a-zA-Z0-9_\\$]*}", "?");
        Map<Integer, String> map = new HashMap<>();
        int index = 1;
        while(reMybatisSql.contains("#")) {
            int begin = reMybatisSql.indexOf("#") + 2;
            int end = reMybatisSql.indexOf("}");
            map.put(index ++ , reMybatisSql.substring(begin, end).trim());
            reMybatisSql = reMybatisSql.substring(end + 1);
        }
        final PreparedStatement ps;
        try {
            ps = conn.prepareStatement(sql);
            map.forEach((k, v) -> {
                try {
                    String getMethodName = "get" + v.toUpperCase().charAt(0) + v.substring(1);
                    Method method = obj.getClass().getDeclaredMethod(getMethodName);
                    ps.setString(k, method.invoke(obj).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            int count = ps.executeUpdate();
            ps.close();
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object selectOne(String sqlId, Object parameterObj) {
        ReMappedStatement mappedStatement = mappedStatements.get(sqlId);
        Connection conn = transactionManager.getConnection();
        String reMybatisSql = mappedStatement.getSql();
        String sql = reMybatisSql.replaceAll("#\\{[a-zA-Z0-9_\\$]*}", "?");
        PreparedStatement ps = null;
        ResultSet rs = null;
        Object obj = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, parameterObj.toString());
            rs = ps.executeQuery();
            if(rs.next()) {
                String resultType = mappedStatement.getResultType();
                Class<?> clazz = Class.forName(resultType);
                Constructor<?> constructor = clazz.getConstructor();
                obj = constructor.newInstance();
                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for(int i = 1; i <= columnCount; i ++ ) {
                    String columnName = rsmd.getColumnName(i);
                    String setMethodName = "set" + columnName.toUpperCase().charAt(0) + columnName.substring(1);
                    Method method = clazz.getDeclaredMethod(setMethodName, clazz.getDeclaredField(columnName).getType());
                    method.invoke(obj, rs.getString(columnName));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return obj;
    }
}
