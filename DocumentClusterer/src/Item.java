public class Item extends Document{	
	private String url;
	public Item(String title, String description, String url){
		super(title, description);
		this.url = url;
	}
	
	public String getUrl(){
		return url;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	@Override
	public String toString(){
		return super.toString()+"url: "+url;
	}
	
	@Override
	public boolean equals(Object o){
		 if ( this == o ) return true;
		 if ( !(o instanceof Item) ) return false;
		 Item item = (Item)o;
		 return super.equals(item) &&
		 item.getUrl().equals(url);
	}
	
	@Override
	public int hashCode(){
		return super.hashCode() + url.hashCode();
	}
}

