package main.java.com.jd;

import main.java.com.jd.loader.DirectoryLoader;
import main.java.com.jd.loader.JarLoader;
import main.java.com.jd.preferences.CommonPreferences;
import main.java.com.jd.printer.text.PlainTextPrinter;
import main.java.com.jd.util.ClassFileUtil;
import main.java.com.jd.util.MessageConstant;
import jd.core.Decompiler;
import jd.core.process.DecompilerImpl;
import java.io.File;
import java.io.PrintStream;
import java.util.Map;

public class FtdDecompiler {

	public FtdDecompiler() {

	}

	/**
	 * @param args
	 *            Path to java class
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("usage: ...");
		} else {
			FtdDecompiler.classDecompiler(args[0], args[1]);
		} 
	}

	/**
	 * decompile the class and output to the target directory
	 * 
	 * @param classpath
	 * @param outputpath
	 * @return message if catching exceptions or validating fails
	 */
	public static String classDecompiler(String classPath, String outputPath) {

		try {
			String pathToClass = classPath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			outputPath = outputPath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			if (!outputPath.endsWith(String.valueOf(File.separatorChar)))
				outputPath = outputPath + File.separatorChar;

			String directoryPath = ClassFileUtil.ExtractDirectoryPathFromClassPath(pathToClass);

			if (directoryPath == null)
				return MessageConstant.INVALID_FILE_DIRECTORY;

			String internalPath = ClassFileUtil.ExtractInternalPath(directoryPath, pathToClass);

			if (internalPath == null)
				return MessageConstant.CANNOT_EXTRACT_PACKAGE_CLASS;

			// trim the interal class path
			internalPath = internalPath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			// output file
			ClassFileUtil.VerifyAndCreateDirectory(outputPath);
			outputPath = outputPath + internalPath.substring(internalPath.lastIndexOf(String.valueOf(File.separatorChar))).replace(".class", "._java");
			
			CommonPreferences preferences = new CommonPreferences();
			DirectoryLoader loader = new DirectoryLoader(new File(directoryPath));

			PrintStream ps = new PrintStream(outputPath);
			PlainTextPrinter printer = new PlainTextPrinter(preferences, ps);

			Decompiler decompiler = new DecompilerImpl();
			decompiler.decompile(preferences, loader, printer, internalPath);

			System.out.println("decompile done.");

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
	/**
	 * decompile the jar and output to the target directory
	 * 
	 * @param jarPath
	 * @param outputPath
	 * @return
	 */
	public static String jarDecompiler(String jarPath, String outputPath) {

		try {
			String pathToJar = jarPath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			outputPath = outputPath.replace('/', File.separatorChar).replace('\\', File.separatorChar);
			if (!outputPath.endsWith(String.valueOf(File.separatorChar)))
				outputPath = outputPath + File.separatorChar;

			Map<String, String> pathToSrc = ClassFileUtil.ExtractDirectoryPathFromJarPath(pathToJar);
			for (Map.Entry<String, String> entry :pathToSrc.entrySet()) {
				String internalPath = entry.getKey();
				// trim the java path
				String javaPath = outputPath + internalPath.replaceAll("\\.class$", "._java").replace('/', File.separatorChar).replace('\\', File.separatorChar);
				// output directory
				String subPath = javaPath.substring(0, javaPath.lastIndexOf(String.valueOf(File.separatorChar)));
				ClassFileUtil.VerifyAndCreateDirectory(subPath);
				
				CommonPreferences preferences = new CommonPreferences();
				JarLoader loader = new JarLoader(new File(jarPath));

				PrintStream ps = new PrintStream(javaPath);
				PlainTextPrinter printer = new PlainTextPrinter(preferences, ps);

				Decompiler decompiler = new DecompilerImpl();
				decompiler.decompile(preferences, loader, printer, internalPath);
			}
			
			System.out.println("decompile done.");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
