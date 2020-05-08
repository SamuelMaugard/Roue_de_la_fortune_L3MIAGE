package core.mot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ListePhrase {

	private ArrayList<String> phrases;
	
	public ListePhrase () {
		phrases = new ArrayList<String>();
		lireFichier();
		assert(phrases.size()==20);
	}

	public void lireFichier() {
		try
	    {
	      File file = new File("src/core/mot/phrases.txt");    
	      FileReader fr = new FileReader(file);         
	      BufferedReader br = new BufferedReader(fr);    
	      String line;
	      while((line = br.readLine()) != null)
	      {
	        phrases.add(line);    
	      }
	      fr.close();     
	    }
	    catch(IOException e)
	    {
	      e.printStackTrace();
	    }
	}
	
	public String getPhrase() {
		Random r = new Random();
		String rep = phrases.get(r.nextInt(phrases.size()));
		phrases.remove(rep);
		return rep;
	}
	
}