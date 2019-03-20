package entity;

import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Item {
	private String itemId;
	private String name; 
	private double rating; 
	private String address;
	private Set<String> categories; 
	private String imageUrl;
	private String url;
	private double distance;
	
	

	/**
	 * This is a builder pattern in Java.
	 */
	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.url = builder.url;
		this.distance = builder.distance;
	}

	
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Set<String> getCategories() {
		return categories;
	}
	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	} 
	
	
	public JSONObject toJSONObject() {
		JSONObject object = new JSONObject();
	
			try {
				
				object.put("item_id", itemId);
				object.put("name", name);
				object.put("rating", rating);
				object.put("address", address);
				object.put("categories", new JSONArray(categories));
				object.put("image_url", imageUrl);
				object.put("url", url);
				object.put("distance", distance);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return object; 
	}
	

	
	public static class ItemBuilder{
		private String itemId;
		private String name; 
		private double rating; 
		private String address;
		private Set<String> categories; 
		private String imageUrl;
		private String url;
		private double distance;
		
		
		
		
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}





		public void setName(String name) {
			this.name = name;
		}





		public void setRating(double rating) {
			this.rating = rating;
		}





		public void setAddress(String address) {
			this.address = address;
		}





		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}





		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}





		public void setUrl(String url) {
			this.url = url;
		}





		public void setDistance(double distance) {
			this.distance = distance;
		}





		public Item build() {
			return new Item(this);
		}
		
	}

}
