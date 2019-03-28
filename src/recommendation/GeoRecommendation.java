package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

// Recommendation based on geo distance and similar categories 
public class GeoRecommendation {
	
	public List<Item> recommendItems(String userId, double lat, double lon){
		List<Item> recommendedItems = new ArrayList<>(); 
		
		//Step 1, get all favorited itemids 
		DBConnection connection = DBConnectionFactory.getConnection(); 
		Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId); 
		System.out.println("GeoRecommendation/recommendItems: favoriteItemIdssize: " + favoritedItemIds.size());
		
		//Step 2, get all categories, sort by count 
		//{"sports": 5, "music": 3, "art": 2} 
		
		// traverse all the item the user like, and count the number of items of each category 
		// sort the category by count 	
		Map<String, Integer> allCategories = new HashMap<>(); 
		for (String itemId: favoritedItemIds) {
			Set<String> categories = connection.getCategories(itemId); 
			for(String category: categories) {
				// try to get category if the key exist, set it to default value 0 if the key does not exist. 
				// then add the value by 1 
			
				allCategories.put(category, allCategories.getOrDefault(category, 0) + 1); 
				
			}
		}
		
		// Sort the map, by its value 
		// convert this hashmap to a list, and sort the list by comparing the value of each key value entry 
		List<Entry<String, Integer>> categoryList = new ArrayList<>(allCategories.entrySet()); 
		// the second parameter is a comparator function, the return value of the function indicates the priority of the first parameter, in this case it is e1 
		// if the comparator function return negative value, then e1 has the greater priority 
		// And when e2 < e1, Integer.compare(e2,getValue(), e1.getValue()) return -1; (return e2 - e1) 
		// so when e1 > e2, e1 has higher priority
		Collections.sort(categoryList, (Entry<String, Integer> e1, Entry<String, Integer> e2) -> {
			return Integer.compare(e2.getValue(), e1.getValue()); 
		});
		
		//another way to sort is to use priorityqueue 
		
		
		//Step 3, search based on category, filter out favorite items 
		//add user's favorite category first then others 
		Set<String> visitedItemIds = new HashSet<>(); 
		for (Entry<String, Integer>category : categoryList) {
			List<Item> items = connection.searchItems(lat, lon, category.getKey());
			for (Item item: items) {
				if(!favoritedItemIds.contains(item.getItemId()) && !visitedItemIds.contains((item.getItemId()))) {
					recommendedItems.add(item); 
					visitedItemIds.add(item.getItemId()); 
				}
			}
		}
	
		connection.close(); 
		return recommendedItems; 
	}
	}


