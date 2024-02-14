package fr.innog.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileEditor{
	
	private File editFile;
	
	private String data;

	public FileEditor(File file, boolean createFile) throws Exception
	{
		if(!file.exists())
		{
			if(!createFile) throw new Exception("Fichier introuvable");
			
			file.createNewFile();
		}
		this.data = "";
		this.editFile = file;
		this.loadFileData();
	}
	
	public boolean fileExist()
	{
		return editFile.exists();
	}
	
	private void loadFileData()
	{
		StringBuilder sb = null;
		try
		{
			FileReader reader = new FileReader(this.editFile);
			BufferedReader buffer = new BufferedReader(reader);
			String line = buffer.readLine();
			sb = new StringBuilder();
			        
			while(line != null){
			   sb.append(line).append("\n");
			   line = buffer.readLine();
			}
			
			buffer.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		data = sb.toString();
	}
	
	public void writeDataToFile()
	{
		try {
			data = data.replaceAll("\n", System.getProperty("line.separator"));
			FileWriter writer = new FileWriter(this.editFile,false);
			writer.write(data);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getString(String key)
	{
		for(String data : data.split("\n"))
		{
			String[] line = data.split("=");
			if(line[0].equalsIgnoreCase(key))
			{
				return line[1];
			}
		}
		return null;
	}
	
	public int getInt(String key)
	{
		try
		{
			return Integer.parseInt(getString(key));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public float getFloat(String key)
	{
		try
		{
			return Float.parseFloat(getString(key));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return 0;
	}
	
	public boolean getBoolean(String key)
	{
		String value = getString(key);
		if(value.equalsIgnoreCase("0"))
		{
			return false;
		}
		else if(value.equalsIgnoreCase("1"))
		{
			return true;
		}
		else if(value.equalsIgnoreCase("true"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void reset()
	{
		data = "";
	}
	
	public void write(String key, Object value)
	{
		data += key + "=" + value + "\n";
	}
	

	
}