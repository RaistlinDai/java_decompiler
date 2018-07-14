package main.java.com.jd;

import main.java.com.jd.loader.DirectoryLoader;
import main.java.com.jd.preferences.CommonPreferences;
import main.java.com.jd.printer.text.PlainTextPrinter;
import main.java.com.jd.util.ClassFileUtil;
import main.java.com.jd.util.MessageConstant;
import jd.core.Decompiler;
import jd.core.process.DecompilerImpl;
import java.io.File;
import java.io.PrintStream;

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

			System.out.println(args[0]);
			System.out.println(args[1]);

			FtdDecompiler.decompiler(args[0], args[1]);
		}
	}

	/**
	 * decompile the class and output to the target directory
	 * 
	 * @param classpath
	 * @param outputpath
	 * @return message if catching exceptions or validating fails
	 */
	public static String decompiler(String classpath, String outputpath) {

		try {
			String pathToClass = classpath.replace('/', File.separatorChar).replace('\\', File.separatorChar);

			String directoryPath = ClassFileUtil.ExtractDirectoryPath(pathToClass);

			if (directoryPath == null)
				return MessageConstant.INVALID_FILE_DIRECTORY;

			String internalPath = ClassFileUtil.ExtractInternalPath(directoryPath, pathToClass);

			if (internalPath == null)
				return MessageConstant.CANNOT_EXTRACT_PACKAGE_CLASS;

			CommonPreferences preferences = new CommonPreferences();
			DirectoryLoader loader = new DirectoryLoader(new File(directoryPath));

			PrintStream ps = new PrintStream(outputpath);
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

}
