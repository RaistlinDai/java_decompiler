package main.java.com.jd;

import main.java.com.jd.loader.DirectoryLoader;
import main.java.com.jd.preferences.CommonPreferences;
import main.java.com.jd.printer.text.PlainTextPrinter;
import main.java.com.jd.util.ClassFileUtil;

import jd.core.Decompiler;
import jd.core.process.DecompilerImpl;
import java.io.File;
import java.io.PrintStream;


public class FtdDecompiler {

	public FtdDecompiler() {
		
	}
	
	
	/**
	 * @param args	Path to java class
	 */
	public static void main(String[] args) {
		
		if (args.length == 0) {
			System.out.println("usage: ...");
		} else {			
			try {
				String pathToClass = args[0].replace('/', File.separatorChar).replace('\\', File.separatorChar);
				System.out.println(pathToClass);
				String directoryPath = ClassFileUtil.ExtractDirectoryPath(pathToClass);
				
				if (directoryPath == null)
					return;
				
				String internalPath = ClassFileUtil.ExtractInternalPath(directoryPath, pathToClass);
				
				if (internalPath == null)
					return;
				
				CommonPreferences preferences = new CommonPreferences();				
				DirectoryLoader loader = new DirectoryLoader(new File(directoryPath));
				
				//PrintStream ps = new PrintStream("test.html");
				//HtmlPrinter printer = new HtmlPrinter(ps);
				PrintStream ps = new PrintStream("test.txt");
				PlainTextPrinter printer = new PlainTextPrinter(preferences, ps);
			
				Decompiler decompiler = new DecompilerImpl();		
				decompiler.decompile(preferences, loader, printer, internalPath);			
				
				System.out.println("done.");
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
