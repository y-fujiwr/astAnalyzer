package analyze;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import grammar.CPP14.CPP14BaseListener;
import grammar.CPP14.CPP14Lexer;
import grammar.CPP14.CPP14Parser;

public class CAnalyzer {
	CPP14BaseListener listener;

	public CAnalyzer() {
	}

	/**
	 * <p>
	 * ディレクトリ探索
	 * </p>
	 *
	 * @param file
	 * @throws IOException
	 */
	public void searchFile(File file) throws IOException {
		if (file.isFile() && (file.getName().endsWith(".c") || file.getName().endsWith(".cpp"))) {
			try {
				extractMethod(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (file.isDirectory()) {
			// System.out.println(file.toString());
			File[] fileList = file.listFiles();
			for (File f : fileList)
				searchFile(f);
		}
	}

	/**
	 * <p>
	 * ソースファイルから関数を抽出する
	 * </p>
	 *
	 * @param file
	 * @throws IOException
	 */
	private void extractMethod(File file) throws IOException {
		String input = preProcessor(file);
		CharStream stream = CharStreams.fromString(input, file.toString());
		CPP14Lexer lexer = new CPP14Lexer(stream);
		lexer.removeErrorListeners();
		lexer.addErrorListener(DescriptiveErrorListener.INSTANCE);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		CPP14Parser parser = new CPP14Parser(tokens);
		ParseTreeWalker walker = new ParseTreeWalker();
		listener = new CPP14BaseListener();
		walker.walk(listener, parser.translationunit());
		
		File outputDir = new File(file.getParent());
		outputDir.mkdirs();
		PrintWriter output = new PrintWriter(
				new BufferedWriter(new FileWriter(new File(outputDir.getPath(), file.getName() + "-ast.txt"))));
		output.print(listener.getAstString());
		output.close();
	}

	// プリプロセッサ
	// マクロの除去
	private String preProcessor(File file) {
		StringBuilder buf = new StringBuilder();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while ((line = reader.readLine()) != null) {
				// #から始まる行の削除
				if (line.startsWith("#")) {
					buf.append("\n");
					// 行の最後が'\'で終わる場合次の行もマクロとして削除
					while (line.matches(".*?\\\\s*")) {
						// System.out.println(file);
						// System.out.println(line);
						line += reader.readLine();
						buf.append("\n");

					}

					// else | elif を見つけたら，マクロによって中括弧の破たんがないか調査
					if (line.matches("#\\s*(else|elif).*")) {
						// if(line.contains("\\")) System.out.println(line);
						int count = 0, loc = 0, ifcnt = 0;
						StringBuilder subbuf = new StringBuilder();
						while ((line = reader.readLine()) != null) {
							// 中括弧の数を調べる
							count += line.replaceAll("\\}", "").length() - line.replaceAll("\\{", "").length();
							if (line.matches("#\\s*if.*")) {
								// ifdefのネスト関係を調べる
								ifcnt++;
							} else if (line.matches("#\\s*endif.*")) {
								if (ifcnt != 0) {
									ifcnt--;
								} else {
									// if (count != 0) {
									// // 中括弧の対応関係が破たんしていた場合，その箇所の削除
									// for (int i = 0; i <= loc; i++) {
									// buf.append("\n");
									// }
									// // System.out.println(subbuf.toString());
									// } else {
									// // 中括弧の対応関係が破たんしていない場合，その箇所は残す
									// buf.append(subbuf.toString());
									// buf.append("\n");
									// }
									for (int i = 0; i <= loc; i++) {
										buf.append("\n");
									}
									break;
								}

							}
							subbuf.append(line);
							subbuf.append("\n");
							loc++;
						}
					}
				} else {
					buf.append(line);
					buf.append("\n");
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return buf.toString();
	}
}
