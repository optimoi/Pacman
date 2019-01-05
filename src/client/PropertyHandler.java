package client;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Properties;

public class PropertyHandler {
	private static String path = System.getProperty("user.dir") + "/resources/config/config.properties";
	private static Properties props;
	
	private PropertyHandler() {
		BufferedInputStream stream;
		try {
			stream = new BufferedInputStream(new FileInputStream(path));		
			props = new Properties();
			props.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			System.out.println();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void setUserName(String name) {
		if (props == null) {
			new PropertyHandler();
		}
		props.setProperty("app.user", name);
	}
	
	public static String getProperty(String property) {
		if (props == null) {
			new PropertyHandler();
		}
		
		return props.getProperty(property);		
	}
	
	public static int getPropertyAsInt(String property) {
		if (props == null) {
			new PropertyHandler();
		}
		
		return Integer.parseInt(props.getProperty(property));		
	}
	
	public static int[] getLevelData() {
		if (props == null) {
			new PropertyHandler();
		}
		
		String data = props.getProperty("view.leveldata");
		return Arrays.stream(data.split(" ")).mapToInt(Integer::parseInt).toArray();
	}
}
