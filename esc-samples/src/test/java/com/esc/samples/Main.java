package com.esc.samples;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

public class Main
{

	public static void main(String[] args) throws IOException
	{
	
		ClassPathResource resource = new ClassPathResource("/resource/conf/");
		File f = resource.getFile();
		list(f);
	}

	public static void list(File f) throws FileNotFoundException, IOException
	{
		File files[] = f.listFiles();
		for (File file : files)
		{
			System.out.println(file.getPath());
			if (file.isDirectory())
			{
				list(file);
				continue;
			}
			System.out.println(IOUtils.toString(new FileInputStream(file)));
		}

	}

}
