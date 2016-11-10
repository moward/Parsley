package parselyserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

@WebServlet(name="ParserServlet", urlPatterns={"/parse"})

public class ParserServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private LexicalizedParser getParser(String modelName) {
		String defaultModelName = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";
	    if (modelName == null) {
	      modelName = defaultModelName;
	    }
	    return LexicalizedParser.loadModel(modelName);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LexicalizedParser lp = getParser(req.getParameter("model"));
		
	    TokenizerFactory<CoreLabel> tokenizerFactory =
	        PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
	    Tokenizer<CoreLabel> tok =
	        tokenizerFactory.getTokenizer(req.getReader());
	    List<CoreLabel> rawText = tok.tokenize();
	    Tree parse = lp.apply(rawText);
	    
	    TreebankLanguagePack tlp = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
	    GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	    GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
	    List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
	    
	    // respond to request
	    resp.setContentType("text/json");
	    PrintWriter out = resp.getWriter();
	    //parse.pennPrint(out);
	    //out.write(tdl.toString());
	    JSONObject treeJSON = TreeToJSON.ConvertToJSON(parse);
	    treeJSON.writeJSONString(out);
	    out.close();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
	    out.write("Please POST in order to use parser.");
	    out.close();
	}
}
