import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ClusterAlg {
	private final static int MIN_WORDS_MATCH = 5;
	static ArrayList<DocumentCluster> labelledClusters = new ArrayList<DocumentCluster>();
	
	public static void clustering() throws Exception{		
		DocumentStemCountBuilder stemBuilder = new DocumentStemCountBuilder(stemStopWords());
		
		//initialize clusters to category lists
		labelledClusters.add(InitialiseClusters.buildCluster("sport"));
		labelledClusters.add(InitialiseClusters.buildCluster("france"));
		labelledClusters.add(InitialiseClusters.buildCluster("america"));
		labelledClusters.add(InitialiseClusters.buildCluster("india"));
				
		//initialize unlabelled data clusters
		ArrayList<DocumentCluster> clusters = new ArrayList<DocumentCluster>();
		NeedItem needs = new NeedItem();
		List<Item> items = needs.getItems();
		HashSet<String> files = new HashSet<String>();
		for(Item item : items)
		{	
			if((item.getContent().split(" ").length > 0 || item.getTitle().split(" ").length > 0) && !files.contains(item.getUrl()))
			{
				Map<WordStem,Double> counts = stemBuilder.getFrequencies(item);
				DocumentCluster cluster = new DocumentCluster(item.getUrl(),item,counts);
				clusters.add(cluster);
				files.add(item.getUrl());	
			}
		}
		
		//clustering			
		labelledClusters = cluster(clusters);
		
		/*			
		Collections.sort(docs, new Comparator<DocumentCluster>(){
			@Override
			public int compare(DocumentCluster o1, DocumentCluster o2) {
				return o2.getDocuments().size() - o1.getDocuments().size();
			}	
		});*/	
		//write information to text file
		writeClusters();
	}
	
	public static ArrayList<DocumentCluster> cluster(ArrayList<DocumentCluster> clusters){
		return cluster(clusters, MIN_WORDS_MATCH, 5);
	}
	
	private static ArrayList<DocumentCluster> cluster(ArrayList<DocumentCluster> clusters, int minMatch, int numIterations)
	{
		if(numIterations < 0){return clusters;}
		System.out.println("Iteration="+numIterations);
		HashSet<Integer> isClustered = new HashSet<Integer>();
		for(int i = 0; i < clusters.size(); i++)
		{
			if(!isClustered.contains(i))
			{
				System.out.println(clusters.get(i).getName()+ "not clustered yet");
				DocumentCluster c1 = clusters.get(i);
				for(int j = 0; j < labelledClusters.size(); j++)
				{
					DocumentCluster c2 = labelledClusters.get(j);
					System.out.println("Testing with "+ c2.getName());
					Set<WordStem> topWords2 = new HashSet<WordStem>(c2.getTopWords());
					Set<WordStem> topWords1 = new HashSet<WordStem>(c1.getTopWords());
					//topWords1.retainAll(topWords2);
					int y=0;
					for(WordStem w : topWords1)
					{		
					//if(topWords1.size() > minMatch)
						System.out.println(w.getWord());
						for(WordStem m: topWords2)
						{
							if(w.getWord()== m.getWord())
							System.out.println(m.getWord());
						}
						if(topWords2.contains(w))
						{y++;
						System.out.println(y);}
					}
					if(y > minMatch)
					{
						c2.merge(c1);
						isClustered.add(i);
						System.out.println("Match!");
					}	
				}
			}
		}
		System.out.println(isClustered.size());
		ArrayList<DocumentCluster> clusterList = new ArrayList<DocumentCluster>();
		for(int i1 = 0; i1 < clusters.size(); i1++)
		{
			if(!isClustered.contains(i1))
				clusterList.add(clusters.get(i1));
		}
		if(isClustered.size() == 0){
			return labelledClusters;
		}
		return cluster(clusterList, minMatch, numIterations -1);
	}
	
	public static void writeClusters() throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter("C:/DocumentClusterer/src/Resources/out.txt"));
		for(int i = 0; i < labelledClusters.size(); i++){
			out.write("cluster "+(i+1));
			List<Document> items = labelledClusters.get(i).getDocuments();
			int entryNum = 1;
			out.newLine();
			for(Document item : items){
				out.write(entryNum+": "+item.getTitle());
				out.newLine();
				entryNum++;
			}
		}
		out.close();
	}
	
	public static Set<String> stemStopWords()
	{
		//make a set of stop words
		List<String> stopList;
		try {
			stopList = new ArrayList<String>(readFileToCollection("C:/DocumentClusterer/src/Resources/stopList.txt"));
			Set<String> stopWords = new HashSet<String>();
			Stemmer stemmer = new Stemmer();
			for(String word : stopList){
				word = word.trim();
				stopWords.add(stemmer.stem(word));
			}	
			return stopWords;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		}
	
	public static Collection<String> readFileToCollection(String filename) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(filename));
		String line = null;
		List<String> lines = new ArrayList<String>();
		while((line = in.readLine()) != null){
			lines.add(line);
		}
		return lines;
	}
}
