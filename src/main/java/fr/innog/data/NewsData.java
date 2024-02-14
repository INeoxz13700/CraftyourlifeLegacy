package fr.innog.data;

public class NewsData {

	 private String title;
	 private String message;
	 private String date;
	 private String imgUrl;
	 
	 
	 public NewsData(String title, String message, String date, String imgUrl)
	 {
		 this.title = title;
		 this.message = message;
		 this.date = date;
		 this.imgUrl = imgUrl;
	 }


	public String getTitle() {
		return title;
	}


	public String getMessage() {
		return message;
	}


	public String getDate() {
		return date;
	}


	public String getImgUrl() {
		return imgUrl;
	}
	 
	 
	
}
