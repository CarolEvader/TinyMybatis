import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionManager {
    void commit();

    void rollback();

    void close();

    void openConnection();

    Connection getConnection();
}
