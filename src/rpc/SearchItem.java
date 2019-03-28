package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// The session id is bound with httpServletRequest 
		// session id help finding the session object defined at server side and help indicating the status of login/logout 
		HttpSession session = request.getSession(false); 
		if(session == null) {
			// if user does not have the authority to do something 
			response.setStatus(403);
			return; 
		}
		
		// Is this user_id field already defined in httpsession ? 
		String userId = session.getAttribute("user_id").toString(); 
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String term = request.getParameter("term");
        
		DBConnection connection = DBConnectionFactory.getConnection();
  	    
        try {        	   
  	           List<Item> items = connection.searchItems(lat, lon, term);
  	           // get all the item ids this user like 
  	           Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId); 
  	           
  	           
  	         JSONArray array = new JSONArray();
  		     for (Item item : items) {
  		    	 // for each item searched, add an extra key value pair into item JSONObject
  		    	 // indicating if the user like this item object or not 
  		    	 JSONObject obj = item.toJSONObject(); 
  		    	 obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
  		    	 array.put(obj);
  		      }
  		     RpcHelper.writeJsonArray(response, array);
  		
  	              } catch (Exception e) {
  		   e.printStackTrace();
  	              } finally {
  		 connection.close();
  	              }

	}



	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
