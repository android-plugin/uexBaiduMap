package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import com.ace.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.ace.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.ace.universalimageloader.core.ImageLoader;
import com.ace.universalimageloader.core.ImageLoaderConfiguration;
import com.ace.universalimageloader.core.assist.FailReason;
import com.ace.universalimageloader.core.assist.QueueProcessingType;
import com.ace.universalimageloader.core.listener.ImageLoadingListener;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.model.LatLngBounds.Builder;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.plugin.uexbaidumap.utils.MLog;

import java.util.HashMap;
import java.util.Map;

public class EBaiduMapOverlayMgr implements OnMarkerClickListener {

    private HashMap<String, EBaiduMapOverlay> mEbaiduMapOverlays;
    protected EBaiduMapBaseFragment baseFragment;
    private Context mContext;
    protected BaiduMap mBaiduMap;
    protected MapView mMapView;
    private Map<String, Bitmap> marks;

    public EBaiduMapOverlayMgr(EBaiduMapBaseFragment context, BaiduMap baiduMap, MapView mapView) {
        baseFragment = context;
        mContext = context.getActivity();
        mBaiduMap = baiduMap;
        mMapView = mapView;
        mEbaiduMapOverlays = new HashMap<String, EBaiduMapOverlay>();
        initImageLoader(mContext);
        mBaiduMap.setOnMarkerClickListener(this);
        marks = new HashMap<String, Bitmap>();
    }

    private void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.MAX_PRIORITY).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)) //可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)  // 内存缓存的最大值
                .memoryCacheSizePercentage(13) // default
                .build();
        ImageLoader.getInstance().init(config);
    }

    public void removeMarkerOverlay(String markerId) {
        try {
            EBaiduMapMarkerOverlay mapMarkerOverlay = (EBaiduMapMarkerOverlay) mEbaiduMapOverlays.get(markerId);
            if (mapMarkerOverlay == null) {
                return;
            }
            mEbaiduMapOverlays.remove(markerId);
            mapMarkerOverlay.clearOverlay();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String addMarkerOverlay(String markerInfo) {

        try {
            final EBaiduMapMarkerOverlayOptions markerOverlayOptions = EBaiduMapUtils.getMarkerOverlayOpitonsWithJSON(markerInfo);
            if (markerOverlayOptions == null) {
                return null;
            }
            final String iconPath = markerOverlayOptions.getIconPath();
            if (iconPath != null) {
                if (marks.containsKey(iconPath)) {
                    addMarkOp(markerOverlayOptions, marks.get(iconPath));
                } else {
                    AsyncTask<String, Void, Bitmap> task = new AsyncTask<String, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(String... params) {
                            return EBaiduMapUtils.getImage(mContext, params[0]);
                        }

                        @Override
                        protected void onPostExecute(Bitmap bitmap) {
                            super.onPostExecute(bitmap);
                            if (bitmap == null) {
                                bitmap = EBaiduMapUtils.getDefaultMarkerBitMap(mContext);
                            }
                            marks.put(iconPath, bitmap);
                            addMarkOp(markerOverlayOptions, bitmap);
                        }
                    };
                    task.execute(iconPath);
                }
            } else {
                Bitmap bitmap = EBaiduMapUtils.getDefaultMarkerBitMap(mContext);
                addMarkOp(markerOverlayOptions, bitmap);
            }
            return markerOverlayOptions.getIdStr();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setMarkerOverlay(String markerId, String markerInfo) {
        try {
            final EBaiduMapMarkerOverlay mapMarkerOverlay = (EBaiduMapMarkerOverlay) mEbaiduMapOverlays.get(markerId);
            if (mapMarkerOverlay == null) {
                return;
            }
            JSONObject makerJsonObject = new JSONObject(markerInfo);
            String markerJsonInfo = makerJsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_MARKERINFO);
            final EBaiduMapMarkerOverlayOptions markerOverlayOptions = EBaiduMapUtils.getMarkerOverlayOpitonsWithJSON(markerJsonInfo);

            String iconPath = markerOverlayOptions.getIconPath();

            if (iconPath != null) {
                ImageLoader.getInstance().loadImage(iconPath, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        if (bitmap == null) {
                            bitmap = EBaiduMapUtils.getDefaultMarkerBitMap(mContext);
                        }
                        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                        mapMarkerOverlay.getMarker().setIcon(markerDescriptor);
                        setMarkOp(mapMarkerOverlay, markerOverlayOptions);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            } else {
                if (mapMarkerOverlay.getMarker().getIcon() == null) {
                    Bitmap bitmap = EBaiduMapUtils.getDefaultMarkerBitMap(mContext);
                    BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                    mapMarkerOverlay.getMarker().setIcon(markerDescriptor);
                }
                setMarkOp(mapMarkerOverlay, markerOverlayOptions);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MLog.getIns().e(e);
        }

    }

    public void showBubble(String markerId) {

        try {

            EBaiduMapMarkerOverlay mapMarkerOverlay = (EBaiduMapMarkerOverlay) mEbaiduMapOverlays.get(markerId);

            if (mapMarkerOverlay == null) {

                return;
            }

            mapMarkerOverlay.setBubbleShow(true);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void hideBubble() {

        try {

            mBaiduMap.hideInfoWindow();

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void clearMapOverLayMgr() {

        for (String s : mEbaiduMapOverlays.keySet()) {

            EBaiduMapOverlay eBaiduMapOverlay = mEbaiduMapOverlays.get(s);
            eBaiduMapOverlay.clearOverlay();
        }
        if (marks != null) {
            marks.clear();
        }
    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub

        EBaiduMapBaseFragment activity;

        activity = baseFragment;

        if (activity != null) {
            EUExBaiduMap uexBaiduMap = activity.getUexBaseObj();
            Bundle b = arg0.getExtraInfo();
            if (b != null) {
                String markerId = (String) b.get(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ID);
                String js = EUExBaiduMap.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_ON_MAKER_CLICK_LISTNER + "){" + EBaiduMapUtils.MAP_FUN_ON_MAKER_CLICK_LISTNER + "('" + markerId + "');}";
                uexBaiduMap.onCallback(js);
                String json = EUExBaiduMap.SCRIPT_HEADER + "if(" + EBaiduMapUtils.MAP_FUN_ON_MARKER_CLICK_LISTENER + "){" + EBaiduMapUtils.MAP_FUN_ON_MARKER_CLICK_LISTENER + "('" + markerId + "');}";
                uexBaiduMap.onCallback(json);
            }
            return true;
        }

        return false;
    }

    public String addDotOverlay(String dotInfo) {
        try {
            EBaiduMapDotOptions info = EBaiduMapUtils.parseDotInfoJson(dotInfo);
            if (info == null || mEbaiduMapOverlays.containsKey(info.getIdStr())) {
                return null;
            }
            int fillColor = BUtility.parseColor(info.getFillColor());
            int radius = (int) Float.parseFloat(info.getRadius());
            DotOptions dotOptions = new DotOptions();
            dotOptions.center(info.getLatLng());
            dotOptions.color(fillColor);
            dotOptions.radius(radius);
            if (info.getVisibleStr() != null) {
                dotOptions.visible(Boolean.parseBoolean(info.getVisibleStr()));
            }
            if (info.getzIndexStr() != null) {
                dotOptions.zIndex((int) Float.parseFloat(info.getzIndexStr()));
            }
            Dot dot = (Dot) mBaiduMap.addOverlay(dotOptions);
            if (info.getExtraStr() != null) {
                Bundle b = new Bundle();
                b.putString(info.getIdStr(), info.getExtraStr());
                dot.setExtraInfo(b);
            }
            EBaiduMapDotOverlay eBaiduMapDotOverlay = new EBaiduMapDotOverlay(info.getIdStr(), baseFragment, mBaiduMap);
            eBaiduMapDotOverlay.setDot(dot);
            mEbaiduMapOverlays.put(info.getIdStr(), eBaiduMapDotOverlay);
            return info.getIdStr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void removeOverlay(String overlayInfo) {
        String[] names = overlayInfo.split(",");
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            EBaiduMapOverlay overlay = mEbaiduMapOverlays.get(name);
            if (overlay != null) {
                overlay.clearOverlay();
                mEbaiduMapOverlays.remove(name);
            }
        }
    }

    public String addPolylineOverlay(String polylineInfo) {
        try {
            EBaiduMapPolylineOptions info = EBaiduMapUtils.parseLineInfoJson(polylineInfo);
            if (info == null || mEbaiduMapOverlays.containsKey(info.getIdStr())) {
                return null;
            }
            int fillColor = BUtility.parseColor(info.getFillColor());
            int lineWidth = (int) Float.parseFloat(info.getLineWidth());
            PolylineOptions polylineOptions = new PolylineOptions().points(info.getList()).color(fillColor).width(lineWidth);
            if (info.getVisibleStr() != null) {
                polylineOptions.visible(Boolean.parseBoolean(info.getVisibleStr()));
            }
            if (info.getzIndexStr() != null) {
                polylineOptions.zIndex((int) Float.parseFloat(info.getzIndexStr()));
            }
            Polyline polyline = (Polyline) mBaiduMap.addOverlay(polylineOptions);
            if (info.getExtraStr() != null) {
                Bundle b = new Bundle();
                b.putString(info.getIdStr(), info.getExtraStr());
                polyline.setExtraInfo(b);
            }
            EBaiduMapPolylineOverlay eBaiduMapPolylineOverlay = new EBaiduMapPolylineOverlay(info.getIdStr(), baseFragment, mBaiduMap);
            eBaiduMapPolylineOverlay.setPolyline(polyline);
            mEbaiduMapOverlays.put(info.getIdStr(), eBaiduMapPolylineOverlay);
            return info.getIdStr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String addArcOverlay(String arcInfo) {
        try {
            EBaiduMapArcOptions info = EBaiduMapUtils.parseArcInfoJson(arcInfo);
            if (info == null || mEbaiduMapOverlays.containsKey(info.getIdStr())) {
                return null;
            }
            int strokeColor = BUtility.parseColor(info.getStrokeColor());
            int lineWidth = (int) Float.parseFloat(info.getLineWidth());
            ArcOptions arcOptions = new ArcOptions().points(info.getStart(), info.getCenter(), info.getEnd()).color(strokeColor).width(lineWidth);
            if (info.getVisibleStr() != null) {
                arcOptions.visible(Boolean.parseBoolean(info.getVisibleStr()));
            }
            if (info.getzIndexStr() != null) {
                arcOptions.zIndex((int) Float.parseFloat(info.getzIndexStr()));
            }
            Arc arc = (Arc) mBaiduMap.addOverlay(arcOptions);
            if (info.getExtraStr() != null) {
                Bundle b = new Bundle();
                b.putString(info.getIdStr(), info.getExtraStr());
                arc.setExtraInfo(b);
            }
            EBaiduMapArcOverlay eBaiduMapArcOverlay = new EBaiduMapArcOverlay(info.getIdStr(), baseFragment, mBaiduMap);
            eBaiduMapArcOverlay.setArc(arc);
            mEbaiduMapOverlays.put(info.getIdStr(), eBaiduMapArcOverlay);
            return info.getIdStr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String addCircleOverlay(String circleInfo) {
        try {
            EBaiduMapCircleOptions info = EBaiduMapUtils.parseCircleInfoJson(circleInfo);
            if (info == null || mEbaiduMapOverlays.containsKey(info.getIdStr())) {
                return null;
            }
            int radius = (int) Float.parseFloat(info.getRadius());
            int strokeColor = BUtility.parseColor(info.getStrokeColor());
            int lineWidth = (int) Float.parseFloat(info.getLineWidth());
            int fillColor = BUtility.parseColor(info.getFillColor());
            Stroke stroke = new Stroke(lineWidth, strokeColor);
            CircleOptions circleOptions = new CircleOptions().center(info.getCenterPoint()).fillColor(fillColor).radius(radius).stroke(stroke);
            if (info.getVisibleStr() != null) {
                circleOptions.visible(Boolean.parseBoolean(info.getVisibleStr()));
            }
            if (info.getzIndexStr() != null) {
                circleOptions.zIndex((int) Float.parseFloat(info.getzIndexStr()));
            }
            Circle circle = (Circle) mBaiduMap.addOverlay(circleOptions);
            if (info.getExtraStr() != null) {
                Bundle b = new Bundle();
                b.putString(info.getIdStr(), info.getExtraStr());
                circle.setExtraInfo(b);
            }
            EBaiduMapCircleOverlay eBaiduMapCircleOverlay = new EBaiduMapCircleOverlay(info.getIdStr(), baseFragment, mBaiduMap);
            eBaiduMapCircleOverlay.setCircle(circle);
            mEbaiduMapOverlays.put(info.getIdStr(), eBaiduMapCircleOverlay);
            return info.getIdStr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String addPolygonOverlay(String polygonInfo) {
        try {
            EBaiduMapPolygonOptions info = EBaiduMapUtils.parasePolygonInfoJson(polygonInfo);
            if (info == null || mEbaiduMapOverlays.containsKey(info.getIdStr())) {
                return null;
            }
            int fillColor = BUtility.parseColor(info.getFillColor());
            int strokeColor = BUtility.parseColor(info.getStrokeColor());
            int lineWidth = (int) Float.parseFloat(info.getLineWidth());
            Stroke stroke = new Stroke(lineWidth, strokeColor);
            PolygonOptions polygonOptions = new PolygonOptions().points(info.getList()).fillColor(fillColor).stroke(stroke);
            if (info.getVisibleStr() != null) {
                polygonOptions.visible(Boolean.parseBoolean(info.getVisibleStr()));
            }
            if (info.getzIndexStr() != null) {
                polygonOptions.zIndex((int) Float.parseFloat(info.getzIndexStr()));
            }
            Polygon polygon = (Polygon) mBaiduMap.addOverlay(polygonOptions);
            if (info.getExtraStr() != null) {
                Bundle b = new Bundle();
                b.putString(info.getIdStr(), info.getExtraStr());
                polygon.setExtraInfo(b);
            }
            EBaiduMapPolygonOverlay eBaiduMapPolygonOverlay = new EBaiduMapPolygonOverlay(info.getIdStr(), baseFragment, mBaiduMap);
            eBaiduMapPolygonOverlay.setPolygon(polygon);
            mEbaiduMapOverlays.put(info.getIdStr(), eBaiduMapPolygonOverlay);
            return info.getIdStr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String addGroundOverlay(String groundInfo) {
        try {
            final EBaiduMapGroundOptions info = EBaiduMapUtils.parseGroundInfoJson(groundInfo);
            if (info == null) {
                return null;
            }
            ImageLoader.getInstance().loadImage(info.getImageUrl(), new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap == null || mEbaiduMapOverlays.containsKey(info.getIdStr())) {
                        return;
                    }
                    GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions();
                    if (info.getList().size() == 1) {
                        LatLng lng = info.getList().get(0);
                        groundOverlayOptions.position(lng);
                        if (info.getGroundWidth() == null) {
                            return;
                        }
                        if (info.getGroundHeight() != null) {
                            groundOverlayOptions.dimensions((int) Float.parseFloat(info.getGroundWidth()), (int) Float.parseFloat(info.getGroundHeight()));
                        } else {
                            groundOverlayOptions.dimensions((int) Float.parseFloat(info.getGroundWidth()));
                        }
                    }
                    if (info.getList().size() == 2) {
                        Builder builder = new LatLngBounds.Builder();
                        builder.include(info.getList().get(0));
                        builder.include(info.getList().get(1));
                        LatLngBounds bounds = builder.build();
                        groundOverlayOptions.positionFromBounds(bounds);
                    }
                    groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));
                    bitmap.recycle();
                    groundOverlayOptions.transparency(Float.parseFloat(info.getTransparency()));
                    if (info.getVisibleStr() != null) {
                        groundOverlayOptions.visible(Boolean.parseBoolean(info.getVisibleStr()));
                    }
                    if (info.getzIndexStr() != null) {
                        groundOverlayOptions.zIndex((int) Float.parseFloat(info.getzIndexStr()));
                    }
                    GroundOverlay groundOverlay = (GroundOverlay) mBaiduMap.addOverlay(groundOverlayOptions);
                    if (info.getExtraStr() != null) {
                        Bundle b = new Bundle();
                        b.putString(info.getIdStr(), info.getExtraStr());
                        groundOverlay.setExtraInfo(b);
                    }
                    EBaiduMapGroundOverlay eBaiduMapGroundOverlay = new EBaiduMapGroundOverlay(info.getIdStr(), baseFragment, mBaiduMap);
                    eBaiduMapGroundOverlay.setGroundOverlay(groundOverlay);
                    mEbaiduMapOverlays.put(info.getIdStr(), eBaiduMapGroundOverlay);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
            return info.getIdStr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String addTextOverlay(String textInfo) {
        try {
            EBaiduMapTextOptions info = EBaiduMapUtils.paraseTextInfo(textInfo);
            if (mEbaiduMapOverlays.containsKey(info.getIdStr())) {
                return null;
            }
            TextOptions textOptions = new TextOptions();
            textOptions.position(info.getLatLng());
            textOptions.fontSize((int) Float.parseFloat(info.getFontSize()));
            textOptions.text(info.getText());
            if (info.getBgColor() != null) {
                textOptions.bgColor(BUtility.parseColor(info.getBgColor()));
            }
            if (info.getFontColor() != null) {
                textOptions.fontColor(BUtility.parseColor(info.getFontColor()));
            }
            if (info.getRotate() != null) {
                textOptions.rotate(Float.parseFloat(info.getRotate()));
            }
            if (info.getVisibleStr() != null) {
                textOptions.visible(Boolean.parseBoolean(info.getVisibleStr()));
            }
            if (info.getzIndexStr() != null) {
                textOptions.zIndex((int) Float.parseFloat(info.getzIndexStr()));
            }
            Text text = (Text) mBaiduMap.addOverlay(textOptions);
            if (info.getExtraStr() != null) {
                Bundle b = new Bundle();
                b.putString(info.getIdStr(), info.getExtraStr());
                text.setExtraInfo(b);
            }
            EBaiduMapTextOverlay eBaiduMapTextOverlay = new EBaiduMapTextOverlay(info.getIdStr(), baseFragment, mBaiduMap);
            eBaiduMapTextOverlay.setText(text);
            mEbaiduMapOverlays.put(info.getIdStr(), eBaiduMapTextOverlay);
            return info.getIdStr();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addMarkOp(final EBaiduMapMarkerOverlayOptions markerOverlayOptions, Bitmap bitmap) {
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

        String markerId = markerOverlayOptions.getIdStr();
        if (mEbaiduMapOverlays.containsKey(markerId)) {
            return;
        }
        String lngStr = markerOverlayOptions.getLngStr();
        String latStr = markerOverlayOptions.getLatStr();

        LatLng llMarker = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lngStr));

        OverlayOptions ooM = new MarkerOptions().position(llMarker).icon(markerDescriptor);

        Marker marker = (Marker) mBaiduMap.addOverlay(ooM);

        EBaiduMapMarkerOverlay markerOverlay = new EBaiduMapMarkerOverlay(markerId, baseFragment, mBaiduMap);

        markerOverlay.setMarker(marker);
        if (markerOverlayOptions.getBubbleTitle() != null) {
            boolean isUse = markerOverlayOptions.isiUseYOffset();
            int yOffset = markerOverlayOptions.getyOffset();
            String title = markerOverlayOptions.getBubbleTitle();
            String subTitle = markerOverlayOptions.getBubbleSubTitle();
            String bgImgPath = markerOverlayOptions.getBubbleBgImgPath();
            markerOverlay.setBubbleViewData(title, subTitle, bgImgPath, yOffset, isUse);
        }
        Bundle b = new Bundle();

        b.putString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ID, markerId);
        marker.setExtraInfo(b);

        mEbaiduMapOverlays.put(markerId, markerOverlay);
    }

    private void setMarkOp(final EBaiduMapMarkerOverlay mapMarkerOverlay, final EBaiduMapMarkerOverlayOptions markerOverlayOptions) {
        String lngStr = markerOverlayOptions.getLngStr();
        String latStr = markerOverlayOptions.getLatStr();
        if (lngStr != null && latStr != null) {

            LatLng llMarker = new LatLng(Double.parseDouble(latStr), Double.parseDouble(lngStr));
            mapMarkerOverlay.getMarker().setPosition(llMarker);
        }
        if (markerOverlayOptions.getBubbleTitle() != null) {
            boolean isUse = markerOverlayOptions.isiUseYOffset();
            int yOffset = markerOverlayOptions.getyOffset();
            String title = markerOverlayOptions.getBubbleTitle();
            String subTitle = markerOverlayOptions.getBubbleSubTitle();
            String bgImgPath = markerOverlayOptions.getBubbleBgImgPath();
            mapMarkerOverlay.setBubbleViewData(title, subTitle, bgImgPath, yOffset, isUse);
        }
    }

}
