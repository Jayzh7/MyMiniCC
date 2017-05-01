package bit.minisys.minicc.scanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MyPreProcessor {
	
	/**run the preprocessor
	 * @author Jayzh7
	 * @param inputFile  
	 * @param outputFile
	 */
	public void run(String inputFile, String outputFile){
		
		try {
			//System.out.println(modifyString(readFile(inputFile)));
			writeFile(modifyString(readFile(inputFile)), outputFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//TODO a lot to implement, e.g. delete comments
	/** delete extra spaces
	 * @author Jayzh7
	 * @param inputString string to be modified
	 * @return modified string
	 * @throws IOException
	 */
	public String modifyString(String inputString) throws IOException{
		StringBuilder preprocessed = new StringBuilder();
		StringBuilder modified = new StringBuilder();
		
		int loop = 0;
		boolean firstBlank = true;
		
		/*
		 * replace all the \n and \t with blank
		 */
		for(;loop<inputString.length();loop++){
			char ch = inputString.charAt(loop);
			if(ch == '\n' || ch == '\t'){
				preprocessed.append(' ');
			}else{
				preprocessed.append(ch);
			}
		}
		
		/*
		 * replace all the continuous blanks with one blank
		 */
		for(loop = 0;loop<inputString.length();loop++){
			char ch = preprocessed.charAt(loop);
			if(ch == ' '){
				if(firstBlank){
					modified.append(ch);
					firstBlank = false;
				}
			}else{
				modified.append(ch);
				firstBlank = true;
			}
		}

		return modified.toString();
	}
	
	/**
	 * @author Jayzh7
	 * @param inputString input string
	 * @param outputFile  file path to receive the string
	 */
	public void writeFile(String inputString, String outputFile){
		BufferedWriter bw = null;
		
		try{
			bw = new BufferedWriter(new FileWriter(outputFile));
			
			bw.write(inputString);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(bw != null){
				try{
					bw.close();
				}catch(IOException e){
					bw = null;
				}
			}
		}
	}
	
	/**
	 * @author Jayzh7
	 * @param input file path
	 * @return file content
	 */
	public String readFile(String input){
		BufferedReader br = null;
		String content = null;
		StringBuffer buf = new StringBuffer();
		try{
			br = new BufferedReader(new FileReader(input));
			while((content = br.readLine())!= null){
				buf.append(content);
			}			
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			if(br != null){
				try{
					br.close();
				}catch(IOException e){
					br = null;
				}
			}
		}
		
		return buf.toString();
		
	}
}
