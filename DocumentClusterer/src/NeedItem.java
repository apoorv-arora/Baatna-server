import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NeedItem {

	public static String filepath = "C:/DocumentClusterer/src/Resources/example";
	
	public static Item buildItem(String fileurl)
	{
		try {
			BufferedReader in = new BufferedReader(new FileReader(fileurl));
			String line = null;
			String title = "";
			String desc = "";
			if((line = in.readLine())!=null)
			{
				title = line;
			}
			while((line = in.readLine()) != null){
				desc += line;
			}
			in.close();
			return new Item(title, desc,fileurl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	 
	public List<Item> getItems() throws IOException{
		List<Item> items = new ArrayList<Item>();
		File folder = new File(filepath);
		for (final File fileEntry : folder.listFiles()) 
		{
			if (fileEntry.isFile()){
			items.add(buildItem(fileEntry.getAbsolutePath()));
		    }
		}
		return items;
	 }
	
	public static void main(String args[]) throws IOException
	{
		NeedItem n = new NeedItem();
		List<Item> items = n.getItems();
		for(Document item : items)
		{
			System.out.println(item.getTitle());
		}
	}
	
}
