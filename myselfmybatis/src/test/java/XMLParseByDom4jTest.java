import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Text;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLParseByDom4jTest {
    @Test
    public void XMLParseByDom4j() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document read = saxReader.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("mybatis-config.xml"));
        Element environmentsElt = (Element) read.selectSingleNode("/configuration/environments");
        String def = environmentsElt.attributeValue("default");
        System.out.println(def);
        Element environmentEml = (Element) read.selectSingleNode("/configuration/environments/environment[@id='" + def + "']");
        Element transactionManagerElt = environmentEml.element("transactionManager");
        String transactionManagerType = transactionManagerElt.attributeValue("type");
        System.out.println(transactionManagerType);
        Element dataSourceElt = environmentEml.element("dataSource");
        String dataSourceType = dataSourceElt.attributeValue("type");
        System.out.println(dataSourceType);
        Map<String, String> propertiesMap = new HashMap<>();
        dataSourceElt.elements("property").forEach(property -> propertiesMap.put(property.attributeValue("name"), property.attributeValue("value")));
        System.out.println(propertiesMap);
        Element mappersElt = environmentsElt.element("mappers");
        List<String> mappersList = new ArrayList<>();
        mappersElt.elements("mapper").forEach(mapper -> mappersList.add(mapper.attributeValue("resource")));
        System.out.println(mappersList);
    }

    @Test
    public void XMLParseByDom4jTest2() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document read = saxReader.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("sqlMapper.xml"));
        Element mapperElt = (Element) read.selectSingleNode("/mapper");
        String namespace = mapperElt.attributeValue("namespace");
        System.out.println(namespace);
        mapperElt.elements().forEach(statement -> {
            String name = statement.getName();
            if(name.equals("select") || name.equals("insert")) {
                String id = statement.attributeValue("id");
                System.out.println(id);
                String value = statement.attributeValue("resultType");
                System.out.print(value == null ? "" : value + "\n");
                String statementTextTrim = statement.getTextTrim();
                System.out.println(statementTextTrim);
            }
        });
    }

    @Test
    public void t1() throws DocumentException {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        sqlSession.close();
    }

}