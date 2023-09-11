import java.util.Map;

public class SqlSessionFactory {
    private TransactionManager transactionManager;
    private Map<String, ReMappedStatement> mappedStatements;

    public SqlSessionFactory(TransactionManager transactionManager, Map<String, ReMappedStatement> mappedStatements) {
        this.transactionManager = transactionManager;
        this.mappedStatements = mappedStatements;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Map<String, ReMappedStatement> getMappedStatements() {
        return mappedStatements;
    }

    public void setMappedStatements(Map<String, ReMappedStatement> mappedStatements) {
        this.mappedStatements = mappedStatements;
    }

    public SqlSession openSession() {
        transactionManager.openConnection();
        return new SqlSession(transactionManager, mappedStatements);
    }

}
