package parsleyserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.trees.Tree;

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
		// Some setup
		LexicalizedParser lp = getParser(req.getParameter("model"));
	    
		System.out.println(req.getHeader("content-type"));
		
		DocumentPreprocessor pre = new DocumentPreprocessor(req.getReader());
		
		ArrayList<Tree> sentenceTrees = new ArrayList<Tree>();
		
		for (List<HasWord> sentence: pre) {
		    Tree parse = lp.apply(sentence);
		    
		    sentenceTrees.add(parse);
		}
	    
	    // respond to request
	    resp.setContentType("text/json");
	    PrintWriter out = resp.getWriter();

	    JSONArray treeJSON = TreeToJSON.TreesToJSON(sentenceTrees);
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
