import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class InitialiseClusters {

	public static String foldername = "C:/Copy of DocumentClusterer/src/Resources";
	public static String readFiles(final String fileName) throws FileNotFoundException 
	{
		 File fileEntry = new File(fileName);
		 String doc = "";  
		 String line = "";
		 try {
	        BufferedReader fr = new BufferedReader(new InputStreamReader(new FileInputStream(fileEntry)));	        
	        while(true)
	        {
				try {
					line = fr.readLine();
		            if(line==null)
		                break;
		            line = line.replaceAll("[^a-zA-Z0-9\\s]", "");
		            doc+=line;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            }
	         return doc;
	        }
	     catch (FileNotFoundException e) {
	        e.printStackTrace();}
		return null;	
}
	public static Document getLabelledDocument( String label){
		String fileName = foldername+"/"+label+".txt";
		String title = label;
		String desc;
		try {
			desc = readFiles(fileName);
			Document doc = new Document(title, desc);
			return doc;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static DocumentCluster buildCluster(String clusterName){
			Document doc = getLabelledDocument(clusterName);
			DocumentStemCountBuilder countStemFreq = new DocumentStemCountBuilder(ClusterAlg.stemStopWords());
			DocumentCluster cluster = new DocumentCluster(clusterName, doc, countStemFreq.getFrequencies(doc,100));
			return cluster;
		}	
	}
