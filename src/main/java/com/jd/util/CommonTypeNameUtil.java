package main.java.com.jd.util;

import jd.core.util.TypeNameUtil;

public class CommonTypeNameUtil {

	private  CommonTypeNameUtil() {
	}

	public static  String InternalPathToQualifiedTypeName(String internalPath)
	{
		String internalTypeName = internalPath.substring(0, internalPath.length()-6);
		return TypeNameUtil.InternalTypeNameToQualifiedTypeName(internalTypeName);
	}
	
}