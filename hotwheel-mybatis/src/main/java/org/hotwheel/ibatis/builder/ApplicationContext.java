package org.hotwheel.ibatis.builder;

import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.datasource.DataSourceFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.parsing.PropertyParser;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.hotwheel.core.io.DefaultResourceLoader;
import org.hotwheel.core.io.Resource;
import org.hotwheel.ibatis.session.SqlSessionFactory;
import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.assembly.XmlParser;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by wangfeng on 2016/11/2.
 * @since 1.0
 */
public class ApplicationContext {
    private final static String CONTEXT_ROOT = "/configuration";
    private final static String CONTEXT_SETTINGS = "//settings";
    private final static String CONTEXT_PROPERTIES = "//properties";
    private final static String CONTEXT_TYPE_ALIASES = "typeAliases";
    private final static String CONTEXT_PLUGINS = "plugins";
    private final static String CONTEXT_OBJECT_FACTORY = "objectFactory";
    private final static String CONTEXT_OBJECT_WRAPPER_FACTORY = "objectWrapperFactory";
    private final static String CONTEXT_REFLECTOR_FACTORY = "reflectorFactory";
    private final static String CONTEXT_DATASOURCES = "//dataSource";

    private final static String DATASOURCE_ID          = "name";
    private final static String DATASOURCE_TYPE        = "class";
    private final static String DATASOURCE_TRANSACTION = "transaction";


    private final static String CONTEXT_DATABASE_ID_PROVIDER = "databaseIdProvider";
    private final static String CONTEXT_TYPE_HANDLER = "typeHandlers";
    private final static String CONTEXT_MAPPER = "//mappers";

    private SqlSessionFactory sqlSessionFactory;
    private DefaultResourceLoader resourceLoader;
    private String[] configLocations;
    private boolean parsed;
    private XmlParser parser;
    private String environment;
    private ReflectorFactory localReflectorFactory = new DefaultReflectorFactory();

    public ApplicationContext(String... configLocations) throws IOException {
        this.configLocations = configLocations;
        resourceLoader = new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(configLocations[0]);
        parser = new XmlParser(resource.getInputStream());
    }

    public void parse() {
        SqlContextFactoty contextFactoty = new SqlContextFactoty();
        try {
            Node root = parser.queryOne(CONTEXT_ROOT);
            Node node = parser.queryOne(root, CONTEXT_SETTINGS);
            Properties settings = settingsAsPropertiess(node);
            node = parser.queryOne(root, CONTEXT_PROPERTIES);
            Properties defaults = propertiesElement(node);
            //parser.setVariables(defaults);
            Configuration defaultConfig = contextFactoty.defaultConfiguration(settings);
            defaultConfig.setVariables(defaults);
            loadCustomVfs(defaultConfig, settings);

            //settingsElement(settings);
            // read it after objectFactory and objectWrapperFactory issue #631
            NodeList nodes = parser.query(root, CONTEXT_DATASOURCES);
            sqlSessionFactory = environmentsElement(defaultConfig, nodes);
            node = parser.queryOne(root, CONTEXT_MAPPER);
            mapperElement(sqlSessionFactory, node);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SqlSession getSesseion(Class<?> clazz) {
        Configuration config = sqlSessionFactory.getMapper(clazz);
        return new SqlSessionFactoryBuilder().build(config).openSession(true);
    }

    public <T> T getMapper(Class<T> clazz) {
        SqlSession sqlSession = getSesseion(clazz);
        return sqlSession.getMapper(clazz);
    }

    private String getAttribute(Node node, String key) {
        return getAttribute(node.getAttributes(), key);
    }

    private String getAttribute(NamedNodeMap namedNodeMap, String key) {
        String sRet = null;
        Node tmpNode = namedNodeMap.getNamedItem(key);
        if (tmpNode != null) {
            sRet = tmpNode.getNodeValue();
            if(sRet != null) {
                sRet = sRet.trim();
            }
        }
        return sRet;
    }

    private Properties settingsAsPropertiess(Node context) {
        if (context == null) {
            return new Properties();
        }
        Properties props = getChildrenAsProperties(context, null);
        // Check that all settings are known to the configuration class
        MetaClass metaConfig = MetaClass.forClass(Configuration.class, localReflectorFactory);
        for (Object key : props.keySet()) {
            if (!metaConfig.hasSetter(String.valueOf(key))) {
                throw new BuilderException("The setting " + key + " is not known.  Make sure you spelled it correctly (case sensitive).");
            }
        }
        return props;
    }

    private Properties propertiesElement(Node context) throws Exception {
        Properties defaults = null;
        if (context != null) {
            defaults = getChildrenAsProperties(context, null);
            String filename = getAttribute(context, "resource");
            Resource resource = resourceLoader.getResource(filename);
            try {
                defaults = new Properties();
                defaults.load(resource.getInputStream());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return defaults;
    }

    private List<Node> getChildren(Node context) {
        List<Node> children = new ArrayList<Node>();
        NodeList nodeList = context.getChildNodes();
        if (nodeList != null) {
            for (int i = 0, n = nodeList.getLength(); i < n; i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    children.add(node);
                }
            }
        }
        return children;
    }

    private void loadCustomVfs(Configuration configuration, Properties props) throws ClassNotFoundException {
        String value = props.getProperty("vfsImpl");
        if (value != null) {
            String[] clazzes = value.split(",");
            for (String clazz : clazzes) {
                if (!clazz.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Class<? extends VFS> vfsImpl = (Class<? extends VFS>) Resources.classForName(clazz);
                    configuration.setVfsImpl(vfsImpl);
                }
            }
        }
    }

    private SqlSessionFactory environmentsElement(Configuration config, NodeList nodes) throws Exception {
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactory();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node context = nodes.item(i);
            Properties properties = config.getVariables();
            SqlContextFactoty contextFactoty = new SqlContextFactoty(properties);
            String id = getAttribute(context, DATASOURCE_ID);
            //String transactionManager = getAttribute(context, DATASOURCE_TRANSACTION);
            TransactionFactory txFactory = transactionManagerElement(contextFactoty, context);
            DataSourceFactory dsFactory = dataSourceElement(contextFactoty, context);
            DataSource dataSource = dsFactory.getDataSource();
            Environment.Builder environmentBuilder = new Environment.Builder(id)
                    .transactionFactory(txFactory)
                    .dataSource(dataSource);
            Configuration configuration = contextFactoty.getConfiguration();
            configuration.setEnvironment(environmentBuilder.build());
            sqlSessionFactory.addDataSource(id, configuration);
        }
        return sqlSessionFactory;
    }

    private Properties parseAttributes(Node n, Properties variables) {
        Properties attributes = new Properties();
        NamedNodeMap attributeNodes = n.getAttributes();
        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node attribute = attributeNodes.item(i);
                String value = PropertyParser.parse(attribute.getNodeValue(), variables);
                attributes.put(attribute.getNodeName(), value);
            }
        }
        return attributes;
    }

    private Properties getChildrenAsProperties(Node context, Properties variables) {
        Properties properties = new Properties();
        for (Node child : getChildren(context)) {
            String name = getAttribute(child, "name");
            String value = getAttribute(child, "value");
            if(variables != null) {
                value = PropertyParser.parse(value, variables);
            }
            if (name != null && value != null) {
                properties.setProperty(name, value);
            }
        }
        return properties;
    }



    private DataSourceFactory dataSourceElement(SqlContextFactoty contextFactoty, Node context) throws Exception {
        if (context != null) {
            String type = getAttribute(context, DATASOURCE_TYPE);
            Properties props = getChildrenAsProperties(context, contextFactoty.getConfiguration().getVariables());
            DataSourceFactory factory = contextFactoty.dataSourceElement(type);
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaration requires a DataSourceFactory.");
    }

    private void mapperElement(SqlSessionFactory sqlSessionFactory, Node parent) throws Exception {
        if (parent != null) {
            for (Node child : getChildren(parent)) {
                String id = getAttribute(child, "ref");
                Configuration configuration = sqlSessionFactory.getDataSource(id);
                if ("package".equals(child.getNodeName())) {
                    String mapperPackage = getAttribute(child, "name");
                    configuration.addMappers(mapperPackage);
                } else {
                    String resource = getAttribute(child, "resource");
                    String url = getAttribute(child, "url");
                    String mapperClass = getAttribute(child, "class");
                    if (resource != null && url == null && mapperClass == null) {
                        ErrorContext.instance().resource(resource);
                        InputStream inputStream = Resources.getResourceAsStream(resource);
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource, configuration.getSqlFragments());
                        mapperParser.parse();
                        Class<?> boundType = mapperParser.getBoundType();
                        sqlSessionFactory.addMapper(boundType, configuration);
                    } else if (resource == null && url != null && mapperClass == null) {
                        ErrorContext.instance().resource(url);
                        InputStream inputStream = Resources.getUrlAsStream(url);
                        XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, url, configuration.getSqlFragments());
                        mapperParser.parse();
                        Class<?> boundType = mapperParser.getBoundType();
                        sqlSessionFactory.addMapper(boundType, configuration);
                    } else if (resource == null && url == null && mapperClass != null) {
                        Class<?> mapperInterface = Resources.classForName(mapperClass);
                        configuration.addMapper(mapperInterface);
                        sqlSessionFactory.addMapper(mapperInterface, configuration);
                    } else {
                        throw new BuilderException("A mapper element may only specify a url, resource or class, but not more than one.");
                    }
                }
            }
        }
    }

    private TransactionFactory transactionManagerElement(SqlContextFactoty contextFactoty, Node context) throws Exception {
        if (context != null) {
            String type = getAttribute(context, DATASOURCE_TRANSACTION);
            Properties props = new Properties();
            if (Api.isEmpty(type)) {
                type = "JDBC";
            }
            TransactionFactory factory = (TransactionFactory) contextFactoty.transactionManagerElement(type);
            factory.setProperties(props);
            return factory;
        }
        throw new BuilderException("Environment declaration requires a TransactionFactory.");
    }
}
