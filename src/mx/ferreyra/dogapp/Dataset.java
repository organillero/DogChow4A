package mx.ferreyra.dogapp;


public class Dataset {

	private String routeId="";
	private String routeName="";
	private String sourceLatitude="";
	private String sourceLongitude="";
	private String routeLatitude="";
	private String routeLongitude="";
	private String distance="";
	private String timeTaken="";
	private String difficulty="";
	private String userId="";
	private String rating="";


	public String getRouteId() {
		return routeId;
	}
	public void setRouteId(String routeId) {
		this.routeId=routeId;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName=routeName;
	}
	public String getSourceLatitude() {
		return sourceLatitude;
	}
	public void setSourceLatitude(String sourceLatitude) {
		this.sourceLatitude=sourceLatitude;
	}
	public String getSourceLongitude() {
		return sourceLongitude;
	}
	public void setSourceLongitude(String sourceLongitude) {
		this.sourceLongitude=sourceLongitude;
	}
	public String getRouteLatitude() {
		return routeLatitude;
	}
	public void setRouteLatitude(String routeLatitude) {
		this.routeLatitude=routeLatitude;
	}
	public String getRouteLongitude() {
		return routeLongitude;
	}
	public void setRouteLongitude(String routeLongitude) {
		this.routeLongitude=routeLongitude;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance=distance;
	}
	public String getTimeTaken() {
		return timeTaken;
	}
	public void setTimeTaken(String timeTaken) {
		this.timeTaken=timeTaken;
	}
	public String getDifficulty() {
		return difficulty;
	}
	public void setDifficulty(String difficulty) {
		this.difficulty=difficulty;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId=userId;
	}
	public String getRating() {
		return rating;
	}
	public void setRating(String rating) {
		this.rating=rating;
	}


	@Override
	public String toString() {
		super.toString();
		return 	routeId + " " + routeName + " " + sourceLatitude + " " + sourceLongitude + " " + routeLatitude + " " + routeLongitude 
		+ " " + distance + " " + timeTaken + " " + difficulty  + " " + userId + " " + rating;
	}

}
