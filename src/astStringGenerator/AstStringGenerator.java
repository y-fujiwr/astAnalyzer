package astStringGenerator;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import analyze.AstAnalyzer;
import analyze.CAnalyzer;

public class AstStringGenerator {
	static String target;

	public static void main(String[] args) throws IOException {
		commandOption(args);
		if (Config.lang == Config.CPP) {
			try {
				CAnalyzer canalyzer = new CAnalyzer();
				canalyzer.searchFile(new File(target).getAbsoluteFile());
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
		if (Config.lang == Config.JAVA) {
			javaFileSearch(new File(target).getAbsoluteFile());
		}
	}

	private static void javaFileSearch(File file) throws IOException {
		if (file.isFile() && file.getName().endsWith(".java")) {
			AstAnalyzer astanalyzer = new AstAnalyzer();
			astanalyzer.analyze(file.getPath());
			return;
		} else if (file.isDirectory()) {
			File[] fileList = file.listFiles();
			for (File f : fileList)
				javaFileSearch(f);
		}
	}

	private static void commandOption(String[] args) {
		Options options = new Options();
		options.addOption(Option.builder("d").longOpt("dir").desc("select directory for clone detection").hasArg()
				.argName("dirname").build());
		options.addOption(Option.builder("l").longOpt("lang")
				.desc("select language from following ( default: java )\r\n  * java\r\n  * c").hasArg().argName("lang")
				.build());
		options.addOption(Option.builder("v").longOpt("virtualclass").desc("select virtual class name").hasArg()
				.argName("virtualclassname").build());
		CommandLine cl = null;
		try {
			CommandLineParser parser = new DefaultParser();
			cl = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error: can't read options.");
			System.exit(1);
		}
		if (cl.hasOption("lang")) {
			if (cl.getOptionValue("lang").equals("java") || cl.getOptionValue("lang").equals("Java"))
				Config.lang = Config.JAVA;
			if (cl.getOptionValue("lang").equals("c") || cl.getOptionValue("lang").equals("C"))
				Config.lang = Config.CPP;
		}

		if (cl.hasOption("dir"))
			target = cl.getOptionValue("dir");
		if (target == null) {
			System.err.println("Usage Error: please select target directory.");
			System.exit(1);
		}

		if (cl.hasOption("virtualclass"))
			Config.virtualClassName = cl.getOptionValue("virtualclass");
	}
}
