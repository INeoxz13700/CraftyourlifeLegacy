package fr.innog.utils;

import java.io.File;

import fr.innog.data.UploadResult;

public class FileForm {
	
	private File file;
	
	private String[] allowedExtension;
	
	private long maxSize;	
	
	public UploadResult result;
	

	public FileForm(String directory)
	{
		file = new File(directory);
	}
	
	public FileForm allowedExtension(String[] extension)
	{
		allowedExtension = extension;
		return this;
	}
	
	public FileForm fileMaxSize(long size)
	{
		maxSize = size;
		return this;
	}
	
	public UploadResult canUploaded()
	{
		if(file.length() >= maxSize)
	    {
			result = new UploadResult(false,"Taille du fichier trop grande");
			return result;
	    }
	
		String filename = file.getName();
		
		for(String extension : allowedExtension)
		{
			if(filename.endsWith("." + extension))
			{
				result = new UploadResult(true,"");
				return result;
			}
		}
		
		result = new UploadResult(false,"Mauvaise extension du fichier");
		return result;
	}
	
	public File getFile()
	{
		return file;
	}
	

	

}