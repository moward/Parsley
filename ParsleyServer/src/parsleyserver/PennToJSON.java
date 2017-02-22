package parsleyserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.NPTmpRetainingTreeNormalizer;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;

/**
 * Servlet implementation class PennToJSON
 */
@WebServlet("/penntojson")
public class PennToJSON extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		BufferedReader reqReader = request.getReader();
		PennTreeReader treeReader = new PennTreeReader(reqReader, new LabeledScoredTreeFactory(),
                new NPTmpRetainingTreeNormalizer());
		
		Tree t;
		
		ArrayList<Tree> trees = new ArrayList<>(); 
		
		while ((t = treeReader.readTree()) != null) {
			trees.add(t);
		}
		
		// respond to request
	    response.setContentType("text/json");
	    PrintWriter out = response.getWriter();

	    JSONArray treeJSON = TreeToJSON.TreesToJSON(trees);
	    treeJSON.writeJSONString(out);
	    out.close();
	    
	    treeReader.close();
	}

}
