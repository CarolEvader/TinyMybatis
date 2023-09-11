import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SqlSessionFactoryBuilder {
    public SqlSessionFactoryBuilder() {
    }

    public SqlSessionFactory build(InputStream inputStream) throws DocumentException {
        //获取数据源
        //获取事务管理器
        //封装到Factory
        SAXReader saxReader = new SAXReader();
        Document read = saxReader.read(inputStream);
        Element environmentsElt = (Element) read.selectSingleNode("/configuration/environments");
        String defaultEnv = environmentsElt.attributeValue("default");
        Element environmentElt = (Element) read.selectSingleNode("/configuration/environments/environment[@id='" + defaultEnv + "']");
        Element transactionManagerElt = environmentElt.element("transactionManager");
        Element dataSourceElt = environmentElt.element("dataSource");
        DataSource dataSource = getDataSource(dataSourceElt);
        TransactionManager transactionManager = getTransactionManager(transactionManagerElt, dataSource);
        Element mappersElt = environmentsElt.element("mappers");
        Map<String, ReMappedStatement> reMappedStatements = getReMappedStatements(mappersElt);
        return new SqlSessionFactory(transactionManager, reMappedStatements);
    }

    public DataSource getDataSource(Element dataSourceElt) {
        Map<String, String> parameters = new HashMap<>();
        dataSourceElt.elements("property").forEach(property -> {
            parameters.put(property.attributeValue("name"), property.attributeValue("value"));
        });
        String dataSourceType = dataSourceElt.attributeValue("type").toUpperCase();
        DataSource dataSource = null;
        if(dataSourceType.equals("POOLED")) {

        } else if(dataSourceType.equals("UNPOOLED")) {//easyToWrite
            dataSource = new MyDataSource(parameters.get("driver"), parameters.get("username"), parameters.get("password"), parameters.get("url"));
        } else if(dataSourceType.equals("JNDI")) {

        }
        return dataSource;
    }

    public TransactionManager getTransactionManager(Element transactionManagerElt, DataSource dataSource) {
        String transactionManagerType = transactionManagerElt.attributeValue("type").toUpperCase();
        TransactionManager transactionManager = null;
        if(transactionManagerType.equals("JDBC")) {
            transactionManager = new RemybatisJDBCTransaction(dataSource, false);
        }
        return transactionManager;
    }

    public Map<String, ReMappedStatement> getReMappedStatements(Element reMappedStatements) {
        Map<String, ReMappedStatement> map = new HashMap<>();
        reMappedStatements.elements().forEach(reMappedStatement -> {
            try {
                String resource = reMappedStatement.attributeValue("resource");
                SAXReader saxReader = new SAXReader();
                Document read = saxReader.read(Resources.getResourceAsStream(resource));
                Element mapperElt = (Element) read.selectSingleNode("/mapper");
                String namespace = mapperElt.attributeValue("namespace");
                mapperElt.elements().forEach(sqlElt -> {
                    String sqlId = sqlElt.attributeValue("id");
                    String sql = sqlElt.getTextTrim();
                    String resultType = sqlElt.attributeValue("resultType");
                    String parameterType = sqlElt.attributeValue("parameterType");
                    String sqlType = sqlElt.getName().toLowerCase();
                    ReMappedStatement reMappedStatement1 = new ReMappedStatement(sqlId, sql, resultType, parameterType, sqlType);
                    map.put(namespace + "." + sqlId, reMappedStatement1);
                });
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }
        });
        return map;
    }

}
