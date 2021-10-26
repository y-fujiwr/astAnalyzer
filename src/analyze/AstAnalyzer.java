package analyze;

<<<<<<< HEAD
import java.io.BufferedWriter;
import java.io.File;
=======
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
>>>>>>> a2ec926e916872435806afa64c1db1bbc84a4a52
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
<<<<<<< HEAD
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
=======
>>>>>>> a2ec926e916872435806afa64c1db1bbc84a4a52
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import astStringGenerator.AstStringGenerator;

import astStringGenerator.AstStringGenerator;
import astStringGenerator.Config;

public class AstAnalyzer {
<<<<<<< HEAD
	int counter = 0;
	static boolean flag = false;
	static int numTryNest = 0;

	public void searchFile2(File file) throws IOException {
		if (file.isFile() && file.getName().endsWith(".java")) {
			try {
				String target = file.getPath();
				MyVisitor visitor = analyze(target);
				PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(target + "/"
						+ file.getName().substring(0, file.getName().lastIndexOf(".")) + "." + visitor.methodname)));
				pw.print(visitor.methodstr);
				pw.close();
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

	public void searchFile(File file) throws IOException {
		Files.walkFileTree(Paths.get(file.getPath()), new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				//if (counter % 10 == 9) {
				File file = new File(path.toString());
				if (file.getName().endsWith(".java")) {
					try {
						String target = file.toString();
						MyVisitor visitor = analyze(target);
						for (int i = 0; i < visitor.methodname.size(); i++) {
							PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
									target.substring(0, target.lastIndexOf(".")) + "."
											+ visitor.methodname.get(i))));
							pw.print(visitor.methodstr.get(i));
							pw.close();
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				//}counter++;
				return super.visitFile(path, attrs);
			}
		});

	}

	private MyVisitor analyze(String target) throws IOException {
=======

	private StringBuilder astString;
	private StringBuilder parents;
	private StringBuilder toks;
	private static ArrayList<String> dictionary = new ArrayList<String>();;
	private final boolean isGCN = true; //GCNの場合toksとparentsは不要
	private final static int IDENTIFIER = 42;

	public AstAnalyzer() {
		astString = new StringBuilder();
		parents = new StringBuilder();
		toks = new StringBuilder();
	}

	public void analyze(String target) throws IOException {
		if (!Config.mode.equals("train"))
			loadDictionary();
>>>>>>> a2ec926e916872435806afa64c1db1bbc84a4a52
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(readAll(target).toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		MyVisitor visitor = new MyVisitor();
		unit.accept(visitor);
<<<<<<< HEAD
		return visitor;
	}

	private static String readAll(String path) throws IOException {
		//System.out.println(path);
		String str = null;
		str = Files.lines(Paths.get(path), Charset.forName("UTF-8"))
				.collect(Collectors.joining(System.getProperty("line.separator"))) + "\n";
		return str;
	}

	public class MyVisitor extends ASTVisitor {
		ArrayList<String> methodstr = new ArrayList<String>();
		ArrayList<String> methodname = new ArrayList<String>();
		Boolean canWrite = true;

		@Override
		public boolean visit(MethodDeclaration node) {

			return true;
		}

		public boolean visit(FieldDeclaration node) {
			if (node.toString().indexOf("Scanner") != -1&& AstStringGenerator.doRemoveExceptionSyntax) {
				node.delete();
			} else if (node.toString().indexOf("Input") != -1 && AstStringGenerator.doRemoveExceptionSyntax) {
				node.delete();
=======
		toks.setLength(toks.length() - 1);
		astString.setLength(astString.length() - 1);
		extractParents(astString.toString());
		PrintWriter astWriter = new PrintWriter(new BufferedWriter(new FileWriter(target + "-ast.txt", false)));
		astWriter.println(astString.toString());
		astWriter.close();
		if (!isGCN) {
			PrintWriter toksWriter = new PrintWriter(new BufferedWriter(new FileWriter(target + ".toks", false)));
			PrintWriter parentsWriter = new PrintWriter(new BufferedWriter(new FileWriter(target + ".parents", false)));
			toksWriter.println(toks.toString());
			parentsWriter.println(parents.toString());
			toksWriter.close();
			parentsWriter.close();
		}
		if (Config.mode.equals("train"))
			saveDictionary();
	}

	private void extractParents(String ast) {
		String[] nodes = ast.split(" ");
		ArrayList<Integer> parentArray = new ArrayList<Integer>();
		int pointer = 0;
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].contains("(")) {
				parents.append(pointer + " ");
				parentArray.add(pointer);
				pointer = parentArray.size();
			} else {
				pointer = parentArray.get(pointer - 1);
			}
		}
		parents.setLength(parents.length() - 1);
	}

	private static String readAll(String path) throws IOException {
		String str = null;
		if (Config.virtualClassName == null)
			str = Files.lines(Paths.get(path), Charset.forName("UTF-8"))
					.collect(Collectors.joining(System.getProperty("line.separator"))) + "\n";
		else
			str = "class " + Config.virtualClassName + " {\n" + Files.lines(Paths.get(path), Charset.forName("UTF-8"))
					.collect(Collectors.joining(System.getProperty("line.separator"))) + "\n}\n";
		return str;
	}

	public String getAstString() {
		return astString.toString();
	}

	public String getParents() {
		return parents.toString();
	}

	public String getToks() {
		return toks.toString();
	}

	public class MyVisitor extends ASTVisitor {
		int nest = 0;

		@Override
		public boolean preVisit2(ASTNode node) {
			if (node.getNodeType() == ASTNode.BLOCK || nest > 0) {
				astString.append("(");
				//System.out.println(node.toString());
				//System.out.println(node.getNodeType());
				if (node.getNodeType() == IDENTIFIER) {
					String str = node.toString();
					if (node.toString().length() <= 2) {//2字以下の変数はひとまとめにする
						str = "x";
					}
					int id = dictionary.indexOf(str);
					if (id != -1) {//登録済みの語彙を復元
						astString.append(String.valueOf(86 + id) + " ");
						//System.out.print(86+id);
					} else {
						if (Config.mode.equals("train")) {//訓練データ作成時は新たに語彙を登録
							dictionary.add(str);
							astString.append(String.valueOf(85 + dictionary.size()) + " ");
						} else {//テストデータ作成時は新語彙は登録せずにそのまま
							astString.append(IDENTIFIER + " ");
						}
						//System.out.println(String.valueOf(85+dictionary.size()));
					}
				} else {
					astString.append(node.getNodeType() + " ");
				}
				toks.append(node.getNodeType() + " ");
				if (node.getNodeType() == ASTNode.BLOCK)
					nest++;
>>>>>>> a2ec926e916872435806afa64c1db1bbc84a4a52
			}
			return true;
		}

<<<<<<< HEAD
		public boolean visit(VariableDeclarationStatement node) {
			if (node.toString().indexOf("Scanner") != -1 && AstStringGenerator.doRemoveExceptionSyntax) {
				node.delete();
			} else if (node.toString().indexOf("Input") != -1 && AstStringGenerator.doRemoveExceptionSyntax) {
				node.delete();
			}
			return true;
		}

		public boolean visit(VariableDeclarationExpression node) {
			if (node.toString().indexOf("Scanner") != -1 && AstStringGenerator.doRemoveExceptionSyntax) {
				node.delete();
			} else if (node.toString().indexOf("Input") != -1 && AstStringGenerator.doRemoveExceptionSyntax) {
				node.delete();
			}
			return true;
		}

		public boolean visit(CatchClause node) {
			if (AstStringGenerator.doRemoveExceptionSyntax)
				node.delete();
			return true;
		}

		public boolean visit(TryStatement node) {
			flag = false;
			numTryNest++;
			return true;
		}

		public boolean visit(Block node) {
			if (node.getParent().getNodeType() == ASTNode.TRY_STATEMENT) {
				if (flag && AstStringGenerator.doRemoveExceptionSyntax) {
					node.delete();
				} else {
					flag = true;
				}
			}
			return true;
		}

		public void endVisit(TryStatement node) {
			if (--numTryNest == 0)
				flag = false;
		}

		public void preVisit(ASTNode node) {
			//System.out.println(node.getNodeType());
			//System.out.println(node.toString());
			//if(node.getParent()!=null)
			//System.out.println(node.getParent().toString());
		}

		public void endVisit(MethodDeclaration node) {
			if (canWrite) {
				methodstr.add(node.toString());
				methodname.add(node.getName().toString());

				//canWrite = false;
			}
=======
		@Override
		public void postVisit(ASTNode node) {
			if (nest > 0) {
				astString.append(") ");
			}
			if (node.getNodeType() == ASTNode.BLOCK) {
				nest--;
				if (nest == 0)
					astString.append("\n");
			}
		}
	}

	private void saveDictionary() throws IOException {
		PrintWriter dicWriter = new PrintWriter(new BufferedWriter(new FileWriter(AstStringGenerator.target + "dictionary.txt", false)));
		for (String word : dictionary) {
			dicWriter.println(word);
		}
		dicWriter.close();
	}

	private void loadDictionary() throws IOException {
		BufferedReader dicReader = new BufferedReader(new FileReader(AstStringGenerator.target + "dictionary.txt"));
		String word;
		while ((word = dicReader.readLine()) != null) {
			dictionary.add(word);
>>>>>>> a2ec926e916872435806afa64c1db1bbc84a4a52
		}
		dicReader.close();
	}
}
