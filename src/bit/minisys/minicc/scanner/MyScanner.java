package bit.minisys.minicc.scanner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.xml.transform.TransformerException;

public class MyScanner implements IMiniCCScanner{
	private int beginIndex , endIndex ;
	private int lineNum;
	private BufferedReader reader;
	private String s;
	private XmlImple xml;
	private ArrayList<Token> tokenList = new ArrayList<Token>();
	private ArrayList<String> keywordList = new ArrayList<String>();
	private ArrayList<Character> singleOperatorList = new ArrayList<Character>();
	private ArrayList<String> complexOperatorList = new ArrayList<String>();
	private ArrayList<Character> sepList = new ArrayList<Character>();

	public void run(String inputFile, String outputFile) throws IOException{
		try{
			//MyPreProcessor myP = new MyPreProcessor();
			//output to the same file
			//myP.run(inputFile, inputFile);
			
			lineNum = 1;
			reader = new BufferedReader(new FileReader(inputFile));
			s = new String();			
			
			int tokenNum = 0;
			beginIndex = endIndex = 0;
			initArrayList();
			s = reader.readLine();
			while(true){
				//avoid overflow
				if(beginIndex >= s.length() || s.charAt(beginIndex) == '\n'){
					// whether next line exists
					if( readNextLine() == false){
						break;
					}
					continue;
				}
				
				//process blanks
				if(s.charAt(beginIndex) == ' '){
					beginIndex ++;
					continue;
				}
				
				endIndex = beginIndex;
				
				//used to store value
				StringBuilder sb = new StringBuilder();
				
				//detect keyword or identifier
				if(Character.isAlphabetic(s.charAt(beginIndex)) || s.charAt(beginIndex) == '_'){
					while(Character.isAlphabetic(s.charAt(endIndex)) || Character.isDigit(s.charAt(endIndex))){
						sb.append(s.charAt(endIndex));
						endIndex ++;
					}

					if(keywordList.contains(sb.toString())){//detected a keyword
						//System.out.println("DEBUG" + sb.toString());
						tokenList.add(new Token(++tokenNum, sb.toString(), TokenType.KEYWORD, lineNum, true));
						System.out.println(sb);
					}else{//detected identifier
						tokenList.add(new Token(++tokenNum, sb.toString(), TokenType.IDENTIFIER, lineNum, true));
						System.out.println(sb);
					}
					beginIndex = endIndex;
					continue;
				}

				/**
				 * detection covers numbers with '+', '-', numbers with 'e',
				 *                  floating number,  ...
				 */
				//TODO detect prefix 0x(hex), 0(oct)
				//TODO detect suffix: u,U l,L ll,LL f,l,F,L ...
				double number = 0;
				//TODO: implement detection of 'e' and 'E'
				if(Character.isDigit(s.charAt(beginIndex))
						//there might be several blanks between the symbol and the digits
						|| s.charAt(beginIndex) == '+' && Character.isDigit(getNextNonblankChar(s)) 
						|| s.charAt(beginIndex) == '-' && Character.isDigit(getNextNonblankChar(s))){
					boolean isInteger = true;
					boolean isPositive = true;
					Stack<Integer> stack = new Stack<Integer>();
					
					if(s.charAt(beginIndex) == '-'){
						isPositive = false;
						endIndex += 2;
					}
					
					if(s.charAt(beginIndex) == '+')
						endIndex += 2;
					while(Character.isDigit(s.charAt(endIndex))){
						stack.push(Character.getNumericValue(s.charAt(endIndex)));
						
						endIndex ++;
					}
					int i = 0;
					while(!stack.isEmpty()){
						number += stack.pop() * Math.pow(10, i++);
					}
					
					if(s.charAt(endIndex) == 'e' || s.charAt(endIndex) == 'E'){
						double exp = 0;
						
						endIndex ++;
						while(Character.isDigit(s.charAt(endIndex))){
							stack.push(Character.getNumericValue(s.charAt(endIndex)));
							endIndex ++;
						}
						
						i = 0;
						while(!stack.isEmpty()){
							exp += stack.pop()* Math.pow(10, i++);
						}
						System.out.println("exp"+ exp);
						number *= Math.pow(10, exp);
						
					}
					//decimal part
					int num = 0;
					if(s.charAt(endIndex) == '.'){
						isInteger = false;
						endIndex ++;
						while(Character.isDigit(s.charAt(endIndex))){
							number = number + Character.getNumericValue(s.charAt(endIndex))*Math.pow(10, --num);
							endIndex ++;
						}
					}
					
					if(!isPositive)
						number = -number;
					
					if(isInteger){
						tokenList.add(new Token(++tokenNum, Double.toString(number), TokenType.CONST_INTEGER, lineNum, true));
					}else{
						tokenList.add(new Token(++tokenNum, Double.toString(number), TokenType.CONST_FLOAT, lineNum, true));	
					}
					
					System.out.println(number);
					beginIndex = endIndex;
					continue;
				}
				
				//detect constant string
				//TODO detect escape char
				if(s.charAt(beginIndex) == '\"'){
					endIndex ++;
					while(s.charAt(endIndex) != '\"'){
						sb.append(s.charAt(endIndex));
						endIndex ++;
					}
					System.out.println(sb.toString());
					tokenList.add(new Token(++tokenNum, sb.toString(), TokenType.CONST_STRING, lineNum, true));
					endIndex ++;
					beginIndex = endIndex;
					continue;
				}
				
				//detect operators
				if(singleOperatorList.contains(s.charAt(beginIndex))){
					//System.out.println(s.charAt(beginIndex));
					sb.append(s.charAt(beginIndex));
					endIndex++;
					if(endIndex >= s.length()){
						readNextLine();
					}
					String _sb = sb.toString() + s.charAt(endIndex);
					if(complexOperatorList.contains(_sb)){
						tokenList.add(new Token(++tokenNum, _sb, TokenType.OPERATOR, lineNum, true));
						endIndex ++;
						System.out.println(_sb);
					}else{
						tokenList.add(new Token(++tokenNum, sb.toString(), TokenType.OPERATOR, lineNum, true));
						System.out.println(sb);
					}
					
					beginIndex = endIndex;
					continue;
				}
				
				//detect separators
				if(sepList.contains(s.charAt(beginIndex))){
					System.out.println("SEP: " + s.charAt(beginIndex));
					sb.append(s.charAt(beginIndex));
					tokenList.add(new Token(++tokenNum, sb.toString(), TokenType.SEPARATOR, lineNum , true));
					beginIndex = ++endIndex;
					continue;
				}
				
			}
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			generateXMLFile(outputFile);
		} catch (TransformerException e) {
			System.out.println("Transformer Exception!");
		}
	}
	
	private boolean readNextLine() throws IOException{
		lineNum ++;
		s = reader.readLine();
		beginIndex = endIndex = 0;
		return s != null;
	}
	private void initArrayList(){
		//add keywords to list
		for(int i = 0; i < keywords.length; i ++){
			keywordList.add(keywords[i]);
		}
		
		for(int i = 0; i < operators.length; i ++){
			if(operators[i].length() == 1){
				singleOperatorList.add(operators[i].charAt(0));
			}else{
				complexOperatorList.add(operators[i]);
			}
		}
		
		for(int i = 0; i < separators.length; i ++){
			sepList.add(separators[i]);
		}
		
		//TODO add others
	}
	
	/**
	 * get next character that is not a blank
	 * @param str input string
	 * @return next non blank character
	 */
	public char getNextNonblankChar(String str){
		//avoid overflow
		//System.out.println("inside func" + str.length());
		if(endIndex+1 >= str.length()){
			return ' ';
		}
		int temp = endIndex+1;
		while(str.charAt(temp) == ' '){
			temp ++;
		}
		return str.charAt(temp);
	}
	
	public void generateXMLFile(String fileName) throws FileNotFoundException, TransformerException{
		xml = new XmlImple();
		xml.createXml(fileName);
		for(Token token:tokenList){
			xml.addNode(token);
		}
		xml.writeFile();
		
	}
	/**
	 * all the type of tokens including keywords, constant value, identifier, separator, operator
	 * @author Jayzh7
	 */
	public enum TokenType{
		KEYWORD, IDENTIFIER, CONST_STRING, CONST_CHAR, CONST_INTEGER, CONST_FLOAT, OPERATOR, SEPARATOR
	}
	
	public static String[] keywords = {
		"auto", "short", "int", "long", "float", "double", "char", "struct", "union", "enum", "typedef", "const",
		"unsigned", "signed", "extern", "register", "static", "volatile", "void", "if", "else", "switch", "case",
		"for", "do", "while", "goto", "continue", "break", "default", "sizeof", "return", "_Alignas", "_Alignof",
		"_Atomic", "_Bool", "_Complex", "_Generic", "_Imaginery", "_Noreturn", "_Static_assert", "_Thread_local"
	};
	
	public static char[] singleOperators = {
		'(', ')', '{', '}', '[', ']', '.', '?', ':', '+', '-', '*', '/', '=', '>', '<', '&', '~', '|', '!',
		'%'
	};
	
	public static String[] complexOperators = {
		"++", "->", "--", "<<", ">>", "<=", ">=", "==", "+=", "-=", "*=", "/=", "%=", "<<=", ">>=", "&=", "^=", "|=",
		"<:", ":>", "<%", "<%", "%:", "%:%:", "!=", "&&", "||",
	};
	
	public static String[] operators = {
			"++", "->", "--", "<<", ">>", "<=", ">=", "==", "+=", "-=", "*=", "/=", "%=", "<<=", ">>=", "&=", "^=", "|=",
			"<:", ":>", "<%", "<%", "%:", "%:%:", "!=", "&&", "||","(", ")",  "[", "]", ".", "?", ":", "+", "-", "*", "/", "=", ">", "<", "&", "~", "|", "!",
			"%"
	};
	public static char[] separators = {
		',', ';','{', '}',
	};
}
