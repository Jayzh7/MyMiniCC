package bit.minisys.minicc.scanner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlImple {

	private Document doc;
	private Element root;
	private String fileName;
	private TransformerFactory factory;
	/**
	 * add tokens to root as nodes
	 * @param token tokens to be converted to nodes to be appended
	 */
	public void addNode(Token token){
		root.appendChild(tokenToNode(token));
	}
	
	/**
	 * convert token to node
	 * @param token
	 * @return converted node
	 */
	private Element tokenToNode(Token token){
		
		Element node = doc.createElement("token");
		Element number, value, type, line, valid;
		number = doc.createElement("number");
		number.appendChild(doc.createTextNode(token.getNumStr()));
		node.appendChild(number);
		value = doc.createElement("value");
		value.appendChild(doc.createTextNode(token.getValStr()));
		System.out.println(" IMPLE:" + token.getValStr());
		node.appendChild(value);
		type = doc.createElement("type");
		type.appendChild(doc.createTextNode(token.getTtStr()));
		node.appendChild(type);
		line = doc.createElement("line");
		line.appendChild(doc.createTextNode(token.getLinStr()));
		node.appendChild(line);
		valid = doc.createElement("valid");
		valid.appendChild(doc.createTextNode(token.getValiStr()));
		node.appendChild(valid);
		
		//node.appendChild(number).appendChild(value).appendChild(type).appendChild(line).appendChild(valid);
		
		return node;
	}
	
	/**
	 * initialization
	 */
	public void init(){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.doc = builder.newDocument();
		}catch(ParserConfigurationException e){
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * create xml file
	 * @param fileName output filename, if not exist, a new one will be created.
	 */
	public void createXml(String fileName){
		init();
		this.fileName = fileName;
		root = this.doc.createElement("tokens");
		this.doc.appendChild(root);
	}
	
	public void writeFile() throws TransformerException, FileNotFoundException{
		factory = TransformerFactory.newInstance();
		
		Transformer transformer = factory.newTransformer();
		DOMSource source = new DOMSource(doc);
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		PrintWriter pw =  new PrintWriter(new FileOutputStream(fileName));
		StreamResult result = new StreamResult(pw);
		transformer.transform(source, result);
		System.out.println("Generating XML succeed!");
	}
}
