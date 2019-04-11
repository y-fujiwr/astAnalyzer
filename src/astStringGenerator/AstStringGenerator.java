package astStringGenerator;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import analyze.AstAnalyzer;

public class AstStringGenerator {
	static String target;
	public static void main(String[] args) throws IOException {
		commandOption(args);
//		try {
//			CAnalyzer canalyzer = new CAnalyzer();
//			canalyzer.searchFile(new File(target).getAbsoluteFile());
//		} catch (IOException e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
		AstAnalyzer astanalyzer = new AstAnalyzer();
		astanalyzer.analyze(target);

	}
	private static void commandOption(String[] args) {
		Options options = new Options();
		options.addOption(Option.builder("d").longOpt("dir").desc("select directory for clone detection").hasArg()
				.argName("dirname").build());
		CommandLine cl = null;
		try {
			CommandLineParser parser = new DefaultParser();
			cl = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Error: can't read options.");
			System.exit(1);
		}
		if (cl.hasOption("dir"))
			target = cl.getOptionValue("dir");
		if (target == null) {
			System.err.println("Usage Error: please select target directory.");
			System.exit(1);
		}
	}

}
