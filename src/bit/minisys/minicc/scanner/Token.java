package bit.minisys.minicc.scanner;

import bit.minisys.minicc.scanner.MyScanner.TokenType;

/**
 * a class to save a single token
 * @author Jay
 */
public class Token {
	/**
	 * details of the token
	 */
	private int number;
	private String value;
	private TokenType tt;
	private int line;
	private boolean valid;
	
	public Token(int num, String val, TokenType t, int l, boolean vali){
		number = num;
		value = val;
		tt = t;
		line = l;
		valid = vali;
	}
	
	public String getNumStr(){
		return Integer.toString(number);
	}
	
	public String getValStr(){
		return value;
	}
	
	public String getTtStr(){
		return tt.toString().toLowerCase();
	}
	
	public String getLinStr(){
		return Integer.toString(line);
	}
	
	public String getValiStr(){
		if(valid == true)
			return new String("true");
		else
			return new String("false");
	}
	
	public void modifyEscapeChar(){
		if(this.value.equals(">")){
			this.value = "&gt;"; 
		}else if(this.value.equals("<")){
			this.value = "&lt;";
		}else if(this.value.equals("&")){
			this.value = "&amp;";
		}
	}
}
