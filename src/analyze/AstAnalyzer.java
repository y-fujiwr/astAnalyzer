package analyze;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
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

public class AstAnalyzer {
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
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(readAll(target).toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		MyVisitor visitor = new MyVisitor();
		unit.accept(visitor);
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
			}
			return true;
		}

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
		}
	}
}
