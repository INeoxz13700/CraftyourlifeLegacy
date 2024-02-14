package fr.innog.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class PhoneSettingsData {
	
	private File file;
	
	public boolean notDisturb;
	
	public String selectedMicroPhone;
	
	public String selectedSourceAudio;
	
	public boolean developperModeActive;
	
	public PhoneSettingsData()
	{

		file = new File("PhoneData");
		
		if(!file.exists()) file.mkdir();

		file = new File("PhoneData/phoneSettings.txt");
		
		if(!file.exists())
		{
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		
		try {
			this.loadData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadData() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		
		String line;
		while((line = br.readLine()) != null)
		{
			if(line.startsWith("notDisturb"))
			{
				this.notDisturb = Boolean.parseBoolean(line.split(" : ")[1]);
			}
			else if(line.startsWith("SelectedMicrophone"))
			{
				this.selectedMicroPhone = line.split(" : ")[1];
			}
			else if(line.startsWith("SelectedSourceAudio"))
			{
				this.selectedSourceAudio = line.split(" : ")[1];
			}
			
		}
		br.close();
	}
	
	public void saveData() throws IOException
	{
		FileWriter fw = new FileWriter(file);
		fw.write("notDisturb : " + this.notDisturb + System.getProperty("line.separator"));
		fw.write("SelectedMicrophone : " + this.selectedMicroPhone + System.getProperty("line.separator"));
		fw.write("SelectedSourceAudio : " + this.selectedSourceAudio);
		fw.close();
	}
	
	public void setDevelopperMode(boolean state)
	{
		developperModeActive = state;
	}
	
	
}