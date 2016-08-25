package org.zywx.wbpalmstar.plugin.uexbaidumap.bean;

public class MapStatusChangeBean {
    public static final String TAG_ZOOM = "zoom";
    public static final String TAG_OVERLOOK = "overlook";
    public static final String TAG_ROTATE = "rotate";
    public static final String TAG_OLDZOOM = "oldZoom";
    public static final String TAG_NEWZOOM = "newZoom";
    public static final String TAG_OLDOVERLOOK = "oldOverlook";
    public static final String TAG_NEWOVERLOOK = "newOverlook";
    public static final String TAG_OLDROTATE = "oldRotate";
    public static final String TAG_NEWROTATE = "newRotate";
    public static final String TAG_CENTER = "center";
    public static final String TAG_NORTHEAST = "northeast";
    public static final String TAG_SOUTHWEST = "southwest";
    private float oldZoom;
    private float newZoom;
    private float oldOverlook;
    private float newOverlook;
    private float oldRotate;
    private float newRotate;
    private double oldNortheastLongitude;
    private double oldNortheastLatitude;
    private double oldSouthwestLongitude;
    private double oldSouthwestLatitude;
    private double oldCenterLongitude;
    private double oldCenterLatitude;
    private double newNortheastLongitude;
    private double newNortheastLatitude;
    private double newSouthwestLongitude;
    private double newSouthwestLatitude;
    private double newCenterLongitude;
    private double newCenterLatitude;

    public double getOldNortheastLongitude() {
        return oldNortheastLongitude;
    }

    public void setOldNortheastLongitude(double oldNortheastLongitude) {
        this.oldNortheastLongitude = oldNortheastLongitude;
    }

    public double getOldNortheastLatitude() {
        return oldNortheastLatitude;
    }

    public void setOldNortheastLatitude(double oldNortheastLatitude) {
        this.oldNortheastLatitude = oldNortheastLatitude;
    }

    public double getOldSouthwestLongitude() {
        return oldSouthwestLongitude;
    }

    public void setOldSouthwestLongitude(double oldSouthwestLongitude) {
        this.oldSouthwestLongitude = oldSouthwestLongitude;
    }

    public double getOldSouthwestLatitude() {
        return oldSouthwestLatitude;
    }

    public void setOldSouthwestLatitude(double oldSouthwestLatitude) {
        this.oldSouthwestLatitude = oldSouthwestLatitude;
    }

    public double getOldCenterLongitude() {
        return oldCenterLongitude;
    }

    public void setOldCenterLongitude(double oldCenterLongitude) {
        this.oldCenterLongitude = oldCenterLongitude;
    }

    public double getOldCenterLatitude() {
        return oldCenterLatitude;
    }

    public void setOldCenterLatitude(double oldCenterLatitude) {
        this.oldCenterLatitude = oldCenterLatitude;
    }

    public double getNewNortheastLongitude() {
        return newNortheastLongitude;
    }

    public void setNewNortheastLongitude(double newNortheastLongitude) {
        this.newNortheastLongitude = newNortheastLongitude;
    }

    public double getNewNortheastLatitude() {
        return newNortheastLatitude;
    }

    public void setNewNortheastLatitude(double newNortheastLatitude) {
        this.newNortheastLatitude = newNortheastLatitude;
    }

    public double getNewSouthwestLongitude() {
        return newSouthwestLongitude;
    }

    public void setNewSouthwestLongitude(double newSouthwestLongitude) {
        this.newSouthwestLongitude = newSouthwestLongitude;
    }

    public double getNewSouthwestLatitude() {
        return newSouthwestLatitude;
    }

    public void setNewSouthwestLatitude(double newSouthwestLatitude) {
        this.newSouthwestLatitude = newSouthwestLatitude;
    }

    public double getNewCenterLongitude() {
        return newCenterLongitude;
    }

    public void setNewCenterLongitude(double newCenterLongitude) {
        this.newCenterLongitude = newCenterLongitude;
    }

    public double getNewCenterLatitude() {
        return newCenterLatitude;
    }

    public void setNewCenterLatitude(double newCenterLatitude) {
        this.newCenterLatitude = newCenterLatitude;
    }

    public float getOldZoom() {
        return oldZoom;
    }

    public void setOldZoom(float oldZoom) {
        this.oldZoom = oldZoom;
    }

    public float getNewZoom() {
        return newZoom;
    }

    public void setNewZoom(float newZoom) {
        this.newZoom = newZoom;
    }

    public float getOldOverlook() {
        return oldOverlook;
    }

    public void setOldOverlook(float oldOverlook) {
        this.oldOverlook = oldOverlook;
    }

    public float getNewOverlook() {
        return newOverlook;
    }

    public void setNewOverlook(float newOverlook) {
        this.newOverlook = newOverlook;
    }

    public float getOldRotate() {
        return oldRotate;
    }

    public void setOldRotate(float oldRotate) {
        this.oldRotate = oldRotate;
    }

    public float getNewRotate() {
        return newRotate;
    }

    public void setNewRotate(float newRotate) {
        this.newRotate = newRotate;
    }

    public boolean isZoomChanged() {
        return this.newZoom != this.oldZoom;
    }

    public boolean isOverlookChanged() {
        return this.oldOverlook != this.newOverlook;
    }

    public boolean isRotateChanged() {
        return this.oldRotate != this.newRotate;
    }

    public boolean isCenterChanged() {
        return this.oldCenterLatitude != this.newCenterLatitude ||
                this.oldCenterLongitude != this.newCenterLongitude;
    }

    public boolean isNortheastChanged() {
        return this.oldNortheastLatitude != this.newNortheastLatitude ||
                this.oldNortheastLongitude != this.newNortheastLongitude;
    }

    public boolean isSouthWestChanged() {
        return this.oldSouthwestLatitude != this.newSouthwestLatitude ||
                this.oldNortheastLongitude != this.newNortheastLongitude;
    }
}
