package analyze;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class AstAnalyzer {
	public void analyze(String target) throws IOException {
		ASTParser parser = ASTParser.newParser(AST.JLS4);
		parser.setSource(readAll(target).toCharArray());
		CompilationUnit unit = (CompilationUnit) parser.createAST(new NullProgressMonitor());
		MyVisitor visitor = new MyVisitor();
		unit.accept(visitor);
	}

	private static String readAll(String path) throws IOException {
	    return Files.lines(Paths.get(path), Charset.forName("UTF-8"))
	        .collect(Collectors.joining(System.getProperty("line.separator")))+"\n";
	}

	public class MyVisitor extends ASTVisitor {
		@Override
		public boolean preVisit2(ASTNode node) {
			System.out.print("( ");
			if(!node.toString().contains("\n")) {
				System.out.print(node.toString());

			}
			System.out.print(node.getNodeType()+" ");
			return true;
		}
		@Override
		public void postVisit(ASTNode node) {
			System.out.print(") ");
		}
	}
}
