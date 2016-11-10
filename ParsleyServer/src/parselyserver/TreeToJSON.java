package parselyserver;

import org.json.simple.*;

import edu.stanford.nlp.trees.Tree;

public class TreeToJSON {

	@SuppressWarnings("unchecked")
	public static JSONObject ConvertToJSON(Tree t) {
		JSONObject obj = new JSONObject();
		
		// label and value
		obj.put("label", t.label().toString());
		
		if (t.isPreTerminal() && t.firstChild().isLeaf()) {
			obj.put("word", t.firstChild().value());
		} else if (t.isPhrasal()) {
			// phrasal, descent recursively to children
			Tree[] children = t.children();
			JSONArray childrenArray = new JSONArray();
			
			for (Tree child: children) {
				childrenArray.add(ConvertToJSON(child));
			}
			
			obj.put("children", childrenArray);
		} else {
			String value = t.value();
			if (value != null) {
				obj.put("value", value);
			}
		}
		
		return obj;
	}
}
