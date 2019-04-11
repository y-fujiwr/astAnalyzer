package grammar.Java;

import java.util.ArrayList;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

public class JavaMyListener extends JavaBaseListener{
	ArrayList<Token> tokenList;
	public JavaMyListener(ArrayList<Token> tokenList) {
		this.tokenList = tokenList;
	}

	@Override
	public void visitTerminal(TerminalNode node) {
		tokenList.add(node.getSymbol());
	}

}
