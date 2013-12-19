package util; 


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * xml解析类
 */
public class XMLParser {
	public static final String UTF_8 = "utf-8";
	Document doc;
	String encoder = UTF_8;

	public XMLParser() {
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public Document CreateDoc() {
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
		}
		return doc;
	}

	/**
	 * 添加指定属性节点(不添加null值属性)
	 */
	public Node addAttrValues(Node ele, Map<String, String> attr_vals) {
		Set<String> attrs = attr_vals.keySet();
		Iterator<String> it = attrs.iterator();
		while (it.hasNext()) {
			String attr = it.next();
			String value = attr_vals.get(attr);
			if (value != null)
				((Element) ele).setAttribute(attr, value);
		}
		return ele;
	}

	public void appendAttrValues(Node node, String... attr_values) {
		if (attr_values.length % 2 != 0) {
			throw new RuntimeException(
					String.format("attr_values' length = %d", attr_values.length));
		}
		for (int i = 0; i < attr_values.length;) {
			String attr = attr_values[i++];
			String value = attr_values[i++];
			if (value == null) {
				continue;
			}
			((Element) node).setAttribute(attr, value);
		}
	}

	/**
	 * 添加指定属性节点(不添加null值属性)
	 */
	public Node addTag1(String tagName, String attr1_name, String attr1_value) {
		return doc.appendChild(createTag1(tagName, attr1_name, attr1_value));
	}

	public Node addTag(String tag, String... attr_values) {
		if (attr_values.length % 2 != 0)
			throw new RuntimeException(
					String.format("attr_values' length = %d", attr_values.length));
		return doc.appendChild(createTag(tag, attr_values));
	}

	/**
	 * 添加指定属性节点(不添加null值属性)
	 */
	public Node addTag2(String tagName, String attr1_name, String attr1_value, String attr2_name,
			String attr2_value) {
		return doc
				.appendChild(createTag2(tagName, attr1_name, attr1_value, attr2_name, attr2_value));
	}

	public Node add_tag_parent_val(Node nodeParent, String tagName, String tagVal) {
		Element ele = doc.createElement(tagName);
		// ele.setAttribute(attr1_name, attr1_value);
		Text val = doc.createTextNode(tagVal);
		ele.appendChild(val);
		return nodeParent.appendChild(ele);
	}

	public Node add_tag_parent(Node nodeParent, String... attr_values) {
		int length = attr_values.length;
		if (length % 2 != 1) {
			throw new RuntimeException("attr_value's length ：" + length);
		}
		String tagName = attr_values[0];
		Element ele = doc.createElement(tagName);
		for (int i = 1; i < attr_values.length; i += 2) {
			String key = attr_values[i];
			String value = attr_values[i + 1];
			ele.setAttribute(key, value);
		}
		if (nodeParent == null) {
			nodeParent = doc;
		}
		return nodeParent.appendChild(ele);
	}

	/**
	 * 创建指定属性节点(不添加null值属性)
	 */
	public Node createTag1(String tagName, String attr1_name, String attr1_value) {
		Element ele = doc.createElement(tagName);
		if (attr1_value != null)
			ele.setAttribute(attr1_name, attr1_value);
		return ele;
	}

	/**
	 * 不添加null属性
	 * 
	 * @param tag
	 * @param attr_values
	 * @return
	 */
	public Node createTag(String tag, String... attr_values) {
		Element ele = doc.createElement(tag);
		for (int i = 0; i < attr_values.length;) {
			if (attr_values[i + 1] != null) {
				ele.setAttribute(attr_values[i++], attr_values[i++]);
			} else {
				i += 2;
				continue;
			}
		}
		return ele;
	}

	/**
	 * 创建指定属性节点(不添加null值属性)
	 */
	public Node createTag2(String tagName, String attr1_name, String attr1_value,
			String attr2_name, String attr2_value) {
		Element ele = doc.createElement(tagName);
		if (attr1_value != null)
			ele.setAttribute(attr1_name, attr1_value);
		if (attr2_value != null)
			ele.setAttribute(attr2_name, attr2_value);
		return ele;
	}

	/**
	 * 添加指定属性节点(不添加null值属性)
	 */
	public Node createTag3(String tagName, String attr1_name, String attr1_value,
			String attr2_name, String attr2_value, String attr3_name, String attr3_value) {
		Element ele = doc.createElement(tagName);
		if (attr1_value != null)
			ele.setAttribute(attr1_name, attr1_value);
		if (attr2_value != null)
			ele.setAttribute(attr2_name, attr2_value);
		if (attr3_value != null)
			ele.setAttribute(attr3_name, attr3_value);
		return ele;
	}

	/**
	 * 添加指定属性节点(不添加null值属性)
	 */
	public Node createTag4(String tagName, String attr1_name, String attr1_value,
			String attr2_name, String attr2_value, String attr3_name, String attr3_value,
			String attr4_name, String attr4_value) {
		Element ele = doc.createElement(tagName);
		if (attr1_value != null)
			ele.setAttribute(attr1_name, attr1_value);
		if (attr2_value != null)
			ele.setAttribute(attr2_name, attr2_value);
		if (attr3_value != null)
			ele.setAttribute(attr3_name, attr3_value);
		if (attr4_value != null)
			ele.setAttribute(attr4_name, attr4_value);
		return ele;
	}

	public void setValue(Node node, String value) {
		node.appendChild(doc.createTextNode(value));
	}

	public static NodeList parseString(String toParse) {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(toParse.getBytes()));
			if (doc != null) {
				return doc.getChildNodes();
			}
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}
	
	public static Node string2Node(String toParse) {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(new ByteArrayInputStream(toParse.getBytes()));
			if (doc != null) {
				return doc.getDocumentElement();
			}
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
	}

	public static Document parseBuffer(byte[] buffer, int offset, int length) {
		if (length == 0) {
			return null;
		}
		byte end = buffer[offset + length - 1];
		if (end == '>') {
			DocumentBuilder builder = null;
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				// String tem = new String(buffer, offset, length);
				// System.out.println(tem);
				// Document doc = builder.parse(new ByteArrayInputStream(buffer,
				// offset, length +
				// 1));
				InputStream is = new ByteArrayInputStream(buffer, offset, length);
				Document doc = builder.parse(is);
				is.close();
				return doc;
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				return null;
			} catch (SAXException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			int red = redundant(buffer, offset, length);
			if (red == -1) {
				return null;
			} else {
				length -= red;
				return parseBuffer(buffer, offset, length);
			}
		}
	}

	public static NodeList ParseBuffer(byte[] buffer, int offset, int length) {
		byte end = buffer[offset + length - 1];
		if (end == '>') {
			DocumentBuilder builder = null;
			try {
				builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				// String tem = new String(buffer, offset, length);
				// System.out.println(tem);
				// Document doc = builder.parse(new ByteArrayInputStream(buffer,
				// offset, length +
				// 1));
				InputStream is = new ByteArrayInputStream(buffer, offset, length);
				Document doc = builder.parse(is);
				is.close();
				if (doc != null) {
					return doc.getChildNodes();
				} else {
					return null;
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				return null;
			} catch (SAXException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			int red = redundant(buffer, offset, length);
			if (red == -1) {
				return null;
			} else {
				length -= red;
				return ParseBuffer(buffer, offset, length);
			}
		}
	}

	private static int redundant(byte[] response, int offset, int length) {
		int len = response.length;
		if (len < 8) {
			return -1;
		}
		int max = 0;
		while (max <= 16) {
			if (response[offset + length - 1 - max] == '>') {
				return max;
			}
			max++;
		}
		return -1;
	}

	/**
	 * @param node
	 *            要搜索的node.
	 * @param attr_name
	 *            要搜索的属性名
	 * @return
	 */
	public static String GetAttrVal(Node node, String attr_name) {
		NamedNodeMap mnm = node.getAttributes();
		if (mnm != null) {
			int num = mnm.getLength();
			int index = 0;
			while (index < num) {
				Node nd = mnm.item(index);
				if (nd.getNodeName().equals(attr_name))
					return nd.getNodeValue();
				index++;
			}
		}
		return null;
	}

	public static String getAttrVal(Node node, String attr_name, String defValue) {
		NamedNodeMap mnm = node.getAttributes();
		if (mnm != null) {
			int num = mnm.getLength();
			int index = 0;
			while (index < num) {
				Node nd = mnm.item(index);
				if (nd.getNodeName().equals(attr_name))
					return nd.getNodeValue();
				index++;
			}
		}
		return defValue;
	}

	public static void getAttrVal(Node node, Map<String, String> map) {
		NamedNodeMap mnm = node.getAttributes();
		if (mnm != null) {
			int num = mnm.getLength();
			int index = 0;
			while (index < num) {
				Node nd = mnm.item(index);
				map.put(nd.getNodeName(), nd.getNodeValue());
				index++;
			}
		}
	}

	public static String GetAttrVal_Chid(Node node, String attr_name) {
		NodeList nl = node.getChildNodes();
		int num = nl.getLength();
		if (num > 0) {
			int index = 0;
			while (index < num) {
				Node nd = nl.item(index);
				String val = GetAttrVal(nd, attr_name);
				if (val != null) {
					return val;
				}
				index++;
			}
		}
		return null;
	}

	public int GetAttrVals(Node node, String attr, Vector<String> vals) {
		NamedNodeMap mnm = node.getAttributes();
		int total = 0;
		if (mnm != null) {
			int num = mnm.getLength();
			int index = 0;
			while (index < num) {
				Node nd = mnm.item(index);
				if (nd.getNodeName().equals(attr)) {
					vals.add(nd.getNodeValue());
					total++;
				}
				index++;
			}
		}
		return total;
	}

	public static String getNodeVal(Node node) {
		if (node.getNodeType() == 3) {
			return node.getNodeValue();
		} else {
			NodeList nl = node.getChildNodes();
			int num = nl.getLength();
			int index = 0;
			while (index < num) {
				Node nd = nl.item(index);
				if (nd.getNodeType() == 3) {
					return nd.getNodeValue();
				}
				index++;
			}
		}
		return null;
	}

	public static Node FindSubNode_1(Node parent, String name) {
		NodeList nl = parent.getChildNodes();
		int num = nl.getLength();
		int index = 0;
		while (index < num) {
			Node nd = nl.item(index);
			String nm = nd.getNodeName();
			if (nm.equals(name)) {
				return nd;
			}
			index++;
		}
		return null;
	}

	public static int FindSubNodes_1(Node parent, String name, Vector<Node> vals) {
		NodeList nl = parent.getChildNodes();
		int num = nl.getLength();
		int index = 0;
		int total = 0;
		while (index < num) {
			Node nd = nl.item(index);
			String nm = nd.getNodeName();
			if (nm.equals(name)) {
				vals.add(nd);
				total++;
			}
			index++;
		}
		return total;
	}

	public Node FindSubNade_n(Node parent, String name) {
		Node node = FindSubNode_1(parent, name);
		if (node != null) {
			return node;
		} else {
			NodeList nl = parent.getChildNodes();
			int num = nl.getLength();
			int index = 0;
			while (index < num) {
				Node nd = nl.item(index);
				node = FindSubNode_1(nd, name);
				if (node != null) {
					return nd;
				}
				index++;
			}
		}
		return null;
	}

	public String toString() {
		if (doc != null) {
			return nodeToStr(doc, encoder);
		}
		return null;
	}

	public static String node2string(Node node) {

		StringBuilder result = new StringBuilder();
		short type = node.getNodeType();
		if (type == 3)
			result.append(node.getNodeValue());
		else {
			if (type != 9) {
				StringBuffer attrs = new StringBuffer();
				for (int k = 0; k < node.getAttributes().getLength(); ++k) {
					attrs.append(" ").append(node.getAttributes().item(k).getNodeName())
							.append("=\"").append(node.getAttributes().item(k).getNodeValue())
							.append("\"");
				}
				result.append("<").append(node.getNodeName()).append(attrs).append(">");
			}
			NodeList nodes = node.getChildNodes();
			for (int i = 0, j = nodes.getLength(); i < j; i++) {
				Node subNode = nodes.item(i);
				result.append(node2string(subNode));
			}
			if (type != 9) {
				result.append("</").append(node.getNodeName()).append(">");
			}
		}
		return result.toString();

	}

	public static String nodeToStr(Node root, String encoder)// throws
	{
		StringBuilder result = new StringBuilder();
		short type = root.getNodeType();
		if (type == 3)
			result.append(root.getNodeValue());
		else {
			if (type != 9) {
				StringBuffer attrs = new StringBuffer();
				for (int k = 0; k < root.getAttributes().getLength(); ++k) {
					attrs.append(" ").append(root.getAttributes().item(k).getNodeName())
							.append("=\"").append(root.getAttributes().item(k).getNodeValue())
							.append("\"");
				}
				result.append("<").append(root.getNodeName()).append(attrs).append(">");
			} else {
				if (encoder == null) {
					encoder = UTF_8;
				}
				result.append("<?xml version=\"1.0\" encoding=\"" + encoder + "\"?>");
			}

			NodeList nodes = root.getChildNodes();
			for (int i = 0, j = nodes.getLength(); i < j; i++) {
				Node node = nodes.item(i);
				result.append(nodeToStr(node, encoder));
			}

			if (type != 9) {
				result.append("</").append(root.getNodeName()).append(">");
			}
		}
		return result.toString();
	}

	public void setEncoder(String string) {
		encoder = string;
	}

	public Document getDocument() {
		return doc;
	}

	public static void main(String... args) {
		String str1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?><Params Version=\"";
		String str2 = "\"></Params>";
		String str3 = new String(new byte[] { 48, 0 });

		StringBuffer sb = new StringBuffer(str1);
		sb.append(str3);
		sb.append(str2);

		NodeList n = XMLParser.parseString(sb.toString());
		System.out.println(n.getLength());
		sb = new StringBuffer(str1);
		sb.append((char) 0);
		sb.append(str2);
		n = XMLParser.parseString(sb.toString());
		System.out.println(n.getLength());
	}

}
