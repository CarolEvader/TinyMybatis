import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class RemybatisJDBCTransaction implements TransactionManager {

    private Connection conn;

    private DataSource dataSource;

    private boolean autoConnection;

    public RemybatisJDBCTransaction(DataSource dataSource, boolean autoConnection) {
        this.dataSource = dataSource;
        this.autoConnection = autoConnection;
    }

    @Override
    public void commit() {
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rollback() {
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void openConnection() {
        try {
            this.conn = dataSource.getConnection();
            this.conn.setAutoCommit(this.autoConnection);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection getConnection() {
        return conn;
    }
}
