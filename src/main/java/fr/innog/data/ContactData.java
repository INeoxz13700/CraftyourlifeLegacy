package fr.innog.data;

import java.util.ArrayList;
import java.util.List;

public class ContactData {
	
	private List<ContactData> contacts = new ArrayList<>();

	public String name;
	
	public String number;
	
	public ContactData(String name, String number) 
	{ 
		this.name = name;
		this.number = number;
	}
	
	public ContactData() 
	{ 
		
	}
	

}