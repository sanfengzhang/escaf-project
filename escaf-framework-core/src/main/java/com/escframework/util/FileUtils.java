package com.escframework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class FileUtils
{

	public static final String XML_FILE_TTPE = "xml";

	public static final String PROP_FILE_TYPE = "properties";

	public static List<File> loadFile(String rootPath, String suffix, List<File> list)
	{

		File file = new File(rootPath);
		if (file.isDirectory())
		{
			File files[] = file.listFiles();
			for (File f : files)
			{
				String path = f.getPath();
				loadFile(path, suffix, list);
			}
		} else
		{
			String fileName = file.getName();
			int len = fileName.length();
			int start = fileName.lastIndexOf(".");
			String fileType = fileName.substring(start + 1, len);
			if (suffix != null && suffix.equals(fileType))
			{
				list.add(file);
			}

		}

		return list;

	}

	public static List<Resource> loadResources(String rootPath)
	{
		return loadResources(rootPath, PROP_FILE_TYPE);
	}

	public static List<Resource> loadResources(String rootPath, String fileType)
	{
		List<File> list = loadFile(rootPath, fileType, new ArrayList<File>());
		List<Resource> result = new ArrayList<Resource>();
		for (File file : list)
		{
			FileSystemResource fileSystemResource = new FileSystemResource(file);
			result.add(fileSystemResource);

		}

		return result;
	}

	public static List<File> list(File f, List<File> result) throws FileNotFoundException, IOException
	{
		File files[] = f.listFiles();
		for (File file : files)
		{
		
			if (file.isDirectory())
			{
				list(file, result);
				continue;
			}
			result.add(file);
			System.out.println(IOUtils.toString(new FileInputStream(file)));
		}

		return result;

	}

}
