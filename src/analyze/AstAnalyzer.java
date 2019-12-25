package analyze;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

import astStringGenerator.Config;

public class AstAnalyzer {

	private StringBuilder astString;
	private StringBuilder parents;
	private StringBuilder toks;
	private final boolean isGCN = true; //GCNの場合toksとparentsは不要

	public AstAnalyzer() {
		astString = new StringBuilder();
		parents = new StringBuilder();
		toks = new StringBuilder();
	}

	public void analyze(String target) throws IOException {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(readAll(target).toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		MyVisitor visitor = new MyVisitor();
		unit.accept(visitor);
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
		String str=null;
		if(Config.virtualClassName==null)
			str = Files.lines(Paths.get(path), Charset.forName("UTF-8"))
				.collect(Collectors.joining(System.getProperty("line.separator"))) + "\n";
		else
			str = "class "+Config.virtualClassName+" {\n"+Files.lines(Paths.get(path), Charset.forName("UTF-8"))
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
			if (node.getNodeType() == ASTNode.METHOD_DECLARATION || nest > 0) {
				astString.append("(");
				//			if(!node.toString().contains("\n")) {
				//				System.out.print(node.toString());
				//
				//			}
				astString.append(node.getNodeType() + " ");
				toks.append(node.getNodeType() + " ");
				if (node.getNodeType() == ASTNode.METHOD_DECLARATION) nest++;
			}
			return true;
		}

		@Override
		public void postVisit(ASTNode node) {
			if (nest > 0) {
				astString.append(") ");
			}
			if (node.getNodeType() == ASTNode.METHOD_DECLARATION) {
				nest--;
				if(nest==0)
				astString.append("\n");
			}
		}
	}
}
