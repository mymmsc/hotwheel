/**
 * @(#)XmlParser.java 6.3.9 09/10/02
 * <p>
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.assembly;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * XML解析器
 * <p>
 * 使用string()方法把其它类型的结果转化成字符串： 1. 如果参数是节点集合，则返回第一个节点的值。 2.
 * 如果参数是结果树分段，则返回结果树中所有的文本（不包括元素），连在一起。 3. 如果参数是数值，则返回欧洲形式的数字字符串。 4.
 * 如果参数是布尔，则true返回”true”,false返回”false”
 * <p>
 * xpath表达式还包括10个字符串操作函数： start-with(a,b) 如果a以b开头，则返回true. Contains(a,b)
 * 如果a包含b，则返回true. Substring(a,offset,length) 返回从a的第offset处开始的、长始为length的字符串
 * Substring-before(a,b) 返回从a 的第一个字符开始，到b第一次出现之间的字符串。 Substring-after(a,b)
 * 返回从b第一次出现的位置，到a的最后一个字符之间的字符串。 String-length(a) 返回a字符串的长度。 Normalize-space(a)
 * 返回去除首尾空格，把a中的连续空格替换成一个空格后的字符串。 Translate(a,b,c) 返回把a中的b换成c后的字符串
 * Concat(a,b,…..) 返回把a、b、……首尾相连后的字符串。 Format-string(a,b,c)
 * 返回a根据b来格式化后的字符串，（c参数的意义没有搞明白）
 * </p>
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 * @since hotwheel 3.0.1
 */
public class XmlParser {
    private static Logger logger = LoggerFactory.getLogger(XmlParser.class);
    @SuppressWarnings("unused")
    private static String xmlFactory = "javax.xml.parsers.DocumentBuilderFactory";
    private static String xmlFactoryImpl = "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl";
    @SuppressWarnings("unused")
    private static String xpathFactory = "javax.xml.xpath.XPathFactory";
    private static String xpathFactoryImpl = "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl";
    private DocumentBuilderFactory m_builderFactory = null;
    private DocumentBuilder m_documentBuilder = null;
    /**
     * XPath解析器
     */
    private XPath m_xpath = null;
    /**
     * 资源输入流
     */
    private InputSource m_is = null;
    /**
     * 系统资源标识串, 可以是本地文件路径, 或者是URL
     */
    private String m_systemId = null;
    /**
     * XML文档
     */
    private Document m_document = null;
    /**
     * 是否检测资源更新
     */
    private boolean m_checkMotified = false;
    /**
     * 记录最近一次系统更新时的时间戳, 仅对文件有效
     */
    private long m_lastmotified = 0;

    /**
     * XmlParser构造函数
     * <p>
     * Create a new input source with a system identifier.
     * <p>
     * <p>
     * Applications may use setPublicId to include a public identifier as well,
     * or setEncoding to specify the character encoding, if known.
     * </p>
     * <p>
     * <p>
     * If the system identifier is a URL, it must be fully resolved (it may not
     * be a relative URL).
     * </p>
     *
     * @param systemId      The system identifier (URI).
     * @param checkMotified 是否检查XML文件更新
     */
    public XmlParser(String systemId, boolean checkMotified) {
        m_systemId = systemId;
        parse(new InputSource(systemId), checkMotified);
    }

    /**
     * XmlParser构造函数
     *
     * @param stream
     */
    public XmlParser(InputStream stream) {
        parse(new InputSource(stream), false);
    }

    /**
     * XmlParser构造函数
     *
     * @param s xml字符串
     */
    public XmlParser(String s) {
        parse(new InputSource(new StringReader(s)), false);
    }

    /**
     * XmlParser构造函数
     *
     * @param is
     */
    public XmlParser(InputSource is) {
        parse(is, false);
    }

    /**
     * doc2XmlFile 将Document对象保存为一个xml文件到本地
     *
     * @param filename 保存的文件名
     * @param document 需要保存的document对象
     * @return true:保存成功 flase:失败
     */
    public static boolean doc2XmlFile(Document document, String filename) {
        boolean flag = true;
        try {
            /** 将document中的内容写入文件中 */
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            /** 编码 */
            // transformer.setOutputProperty(OutputKeys.ENCODING, "GB2312");
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);
        } catch (Exception ex) {
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    /**
     * 关闭XML解析器
     */
    public void close() {
        //
    }

    /**
     * 初始化XML解析器
     *
     * @throws ParserConfigurationException
     * @throws XPathFactoryConfigurationException
     */
    private void init() throws ParserConfigurationException,
            XPathFactoryConfigurationException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        if (m_builderFactory == null) {
            m_builderFactory = DocumentBuilderFactory.newInstance(
                    xmlFactoryImpl, classLoader);
            // m_builderFactory = DocumentBuilderFactory.newInstance();
        }
        if (m_documentBuilder == null) {
            m_documentBuilder = m_builderFactory.newDocumentBuilder();
        }
        if (m_xpath == null) {
            XPathFactory xpf = XPathFactory.newInstance(
                    XPathFactory.DEFAULT_OBJECT_MODEL_URI, xpathFactoryImpl,
                    classLoader);
            if (xpf != null) {
                m_xpath = xpf.newXPath();
            } else {
                m_xpath = XPathFactory.newInstance().newXPath();
            }
        }
    }

    /**
     * 解析 系统id
     *
     * @param is
     * @param checkMotified
     */
    private void parse(InputSource is, boolean checkMotified) {
        m_is = is;
        m_checkMotified = checkMotified;
        try {
            init();
            m_xpath.reset();
            // m_is.setEncoding(Encoding.Default);
            m_document = m_documentBuilder.parse(m_is);
        } catch (SAXException e) {
            logger.error("", e);
        } catch (IOException e) {
            logger.error("", e);
        } catch (ParserConfigurationException e) {
            logger.error("", e);
        } catch (XPathFactoryConfigurationException e) {
            logger.error("", e);
        }
        if (checkMotified) {
            m_lastmotified = Api.getLastModified(m_systemId);
        }
    }

    /**
     * 检测XML是否被更新
     *
     * @return
     */
    private void checkMotified() {
        if (m_checkMotified && m_systemId != null) {
            long tm = Api.getLastModified(m_systemId);
            if (tm > m_lastmotified) {
                parse(new InputSource(m_systemId), true);
            }
        }
    }

    public Element newNode(Node node, String name) {
        return node.getOwnerDocument().createElement(name);
    }

    /**
     * 检索XML文档, 输出一个NodeList
     *
     * @param expression
     * @return NodeList
     * @throws XPathExpressionException
     */
    public NodeList query(String expression)
            throws XPathExpressionException {
        return query(null, expression);
    }

    /**
     * 检索XML文档, 输出一个NodeList
     *
     * @param expression
     * @return NodeList
     * @throws XPathExpressionException
     */
    public Node queryOne(String expression)  throws XPathExpressionException {
        Node node = null;
        NodeList list = query(null, expression);
        if (list != null && list.getLength() > 0) {
            node = list.item(0);
        }
        return node;
    }

    /**
     * 检索XML文档, 输出一个NodeList
     *
     * @param expression
     * @return NodeList
     * @throws XPathExpressionException
     */
    public Node queryOne(Node parent, String expression)  throws XPathExpressionException {
        Node node = null;
        NodeList list = query(parent, expression);
        if (list != null && list.getLength() > 0) {
            node = list.item(0);
        }
        return node;
    }

    /**
     * 检索XML文档, 输出一个NodeList
     *
     * @param node       当前节点
     * @param expression
     * @return NodeList
     * @throws XPathExpressionException
     */
    public NodeList query(Node node, String expression)
            throws XPathExpressionException {
        NodeList lRet = null;
        // 检测是否被更新
        checkMotified();
        NodeList list = null;
        if (node == null) {
            list = m_document.getChildNodes();
        } else {
            list = node.getOwnerDocument().getChildNodes();
        }
        XPathExpression exp = m_xpath.compile(expression);
        Object obj = exp.evaluate(list, XPathConstants.NODESET);
        if (obj != null) {
            lRet = (NodeList) obj;
        }
        return lRet;
    }

    /**
     * 获取一个节点属性值
     *
     * @param node
     * @param name
     * @return String, 如果没有找到对应的属性则返回null
     */
    public String valueOf(Node node, String name) {
        String value = null;
        if (node != null && node.hasAttributes() && name.length() > 0) {
            // 获得一个节点属性值
            NamedNodeMap map = node.getAttributes();
            if (map != null && map.getLength() > 0) {
                Node tmpNode = map.getNamedItem(name);
                if (tmpNode != null) {
                    value = tmpNode.getNodeValue();
                    if (value != null) {
                        value = value.trim();
                    }
                }
            }
        }
        return value;
    }

    /**
     * 获取一个节点属性值
     *
     * @param node
     * @return String, 如果没有找到对应的属性则返回null
     */
    /*
    public Properties getAttributes(Node node) {
        Properties properties = null;
        if (node != null && node.hasAttributes()) {
            // 获得一个节点属性值
            NamedNodeMap map = node.getAttributes();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = Api.toString(entry.getValue());
                String str = String.format("&%s=%s", key, value);
                sb.append(str);
            }
            if (map != null && map.getLength() > 0) {
                Node tmpNode = map.getNamedItem(name);
                if (tmpNode != null) {
                    value = tmpNode.getNodeValue();
                    if (value != null) {
                        value = value.trim();
                    }
                }
            }
        }
        return properties;
    }
*/
    /**
     * 类反射得到一个XPATH查询结果的对象 , 此方法适合以节点为主的一组关联属性的读取
     *
     * @param expression
     * @param clazz
     * @return
     * @throws XPathExpressionException
     */
    public <T> T valueOf(String expression, Class<T> clazz)
            throws XPathExpressionException {
        // 初始状态为null
        T obj = null;
        // 取得clazz类的成员变量列表
        Field[] fields = clazz.getDeclaredFields();
        Field field = null;
        // 查询XML节点
        NodeList list = query(expression);
        if (list != null) {
            Node node = null;
            String nodeName = null;
            String nodeValue = null;
            for (int i = 0; i < list.getLength(); i++) {
                // 读取一个节点
                node = list.item(i);
                nodeName = node.getNodeName().trim();
                // nodeValue = node.getNodeValue().trim();
                nodeValue = node.getTextContent().trim();
                // 遍历所有类成员变量, 为赋值作准备
                for (int j = 0; j < fields.length; j++) {
                    field = fields[j];
                    // 忽略字段名大小写
                    if (Api.fieldMatch(field, nodeName)) {
                        // 得到类成员变量数据类型
                        Class<?> cClass = field.getType();
                        Object objValue = Api.valueOf(cClass, nodeValue);
                        if (obj == null) {
                            try {
                                obj = clazz.newInstance();
                            } catch (InstantiationException e) {
                                logger.error("", e);
                            } catch (IllegalAccessException e) {
                                logger.error("", e);
                            }
                        }
                        if (obj != null) {
                            Api.setValue(obj, field, objValue);
                        }
                        break;
                    }
                }
            }
        }
        return obj;
    }

    /**
     * 类反射得到一个XPATH查询结果的对象 , 此方法适合以节点为主的一组关联属性的读取
     *
     * @param expression
     * @param clazz
     * @return
     * @throws XPathExpressionException
     */
    public <T> List<T> listOf(String expression, Class<T> clazz) throws XPathExpressionException {
        List<T> objList = new ArrayList<T>();
        // 初始状态为null
        T obj = null;
        // 取得clazz类的成员变量列表
        Field[] fields = clazz.getDeclaredFields();
        Field field = null;
        // 查询XML节点
        NodeList list = query(expression);
        for (int k = 0; list != null && k < list.getLength(); k++) {
            Node node = null;
            String nodeName = null;
            String nodeValue = null;
            obj = null;
            NodeList list2 = list.item(k).getChildNodes();
            for (int i = 0; i < list2.getLength(); i++) {
                // 读取一个节点
                node = list2.item(i);
                nodeName = node.getNodeName().trim();
                // nodeValue = node.getNodeValue().trim();
                nodeValue = node.getTextContent().trim();
                // 遍历所有类成员变量, 为赋值作准备
                for (int j = 0; j < fields.length; j++) {
                    field = fields[j];
                    // 忽略字段名大小写
                    if (Api.fieldMatch(field, nodeName)) {
                        // 得到类成员变量数据类型
                        Class<?> cClass = field.getType();
                        Object objValue = Api.valueOf(cClass, nodeValue);
                        if (obj == null) {
                            try {
                                obj = clazz.newInstance();
                            } catch (InstantiationException e) {
                                logger.error("", e);
                            } catch (IllegalAccessException e) {
                                logger.error("", e);
                            }
                        }
                        if (obj != null) {
                            Api.setValue(obj, field, objValue);
                        }
                        break;
                    }
                }
            }
            objList.add(obj);
        }
        return objList;
    }

    /**
     * 修改XML文档节点值
     *
     * @param node
     * @param nodeValue
     * @return
     */
    public boolean updateValue(Node node, String nodeValue) {
        boolean bRet = false;
        if (node != null && nodeValue != null) {
            try {
                node.setNodeValue(nodeValue);
                bRet = true;
            } catch (DOMException e) {
                logger.error("", e);
            }
        }
        return bRet;
    }

    /**
     * 输出XML
     *
     * @param filename
     * @param charset
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public void output(String filename, String charset)
            throws TransformerException, ParserConfigurationException,
            SAXException, IOException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, charset);
        DOMSource xmlSource = new DOMSource(m_document);
        StreamResult outputTarget = new StreamResult(new File(filename));
        transformer.transform(xmlSource, outputTarget);
    }
}
