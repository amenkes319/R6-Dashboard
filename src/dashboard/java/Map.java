package dashboard.java;

public enum Map
{
	CHALET("Chalet"), CLUBHOUSE("Clubhouse"), COASTLINE("Coastline"), CONSULATE("Consulate"), KAFE("Kafe"), OREGON("Oregon"), THEMEPARK("Themepark"), VILLA("Villa");
	
	private String name;
	
	private Map(String name)
	{
		this.name = name;
	}
	
	public String toString()
	{
		return this.name;
	}
}
