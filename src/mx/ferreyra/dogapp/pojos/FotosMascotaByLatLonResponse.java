package mx.ferreyra.dogapp.pojos;

import java.util.Date;

public class FotosMascotaByLatLonResponse {

    private Long photoId;
    private Long ownerId;
    private Date date;
    private String photoAsBase64Binary;
    private Double latitude;
    private Double longitude;
    private String comments1;
    private String comments2;
    private Date creationDate;
    private Double distance;

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPhotoAsBase64Binary() {
        return photoAsBase64Binary;
    }

    public void setPhotoAsBase64Binary(String photoAsBase64Binary) {
        this.photoAsBase64Binary = photoAsBase64Binary;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getComments1() {
        return comments1;
    }

    public void setComments1(String comments1) {
        this.comments1 = comments1;
    }

    public String getComments2() {
        return comments2;
    }

    public void setComments2(String comments2) {
        this.comments2 = comments2;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }
}
