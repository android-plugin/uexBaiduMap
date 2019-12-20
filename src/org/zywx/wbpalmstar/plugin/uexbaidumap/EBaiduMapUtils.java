package org.zywx.wbpalmstar.plugin.uexbaidumap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.webkit.URLUtil;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.PlanNode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BDebug;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.platform.certificates.Http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EBaiduMapUtils {

    // 1
    public static final String MAP_FUN_PARAMS_KEY = "mapFunParamsKey";
    public static final String MAP_ACTIVITY_ID = "mapActivityID";

    // 2
    public static final int MAP_MSG_CODE_OPEN = 0;
    public static final int MAP_MSG_CODE_CLOSE = 1;
    public static final int MAP_MSG_CODE_SETMAPTYPE = 2;
    public static final int MAP_MSG_CODE_SETTRAFFIC = 3;
    public static final int MAP_MSG_CODE_SETCENTER = 4;
    public static final int MAP_MSG_CODE_ZOOMTO = 5;
    public static final int MAP_MSG_CODE_ZOOMIN = 6;
    public static final int MAP_MSG_CODE_ZOOMOUT = 7;
    public static final int MAP_MSG_CODE_ROTATE = 8;
    public static final int MAP_MSG_CODE_OVERLOOK = 9;
    public static final int MAP_MSG_CODE_ZOOMENABLE = 10;
    public static final int MAP_MSG_CODE_ROTATEENABLE = 11;
    public static final int MAP_MSG_CODE_COMPASSENABLE = 12;
    public static final int MAP_MSG_CODE_SCROLLENABLE = 13;
    public static final int MAP_MSG_CODE_OVERLOOKENABLE = 14;
    public static final int MAP_MSG_CODE_ADDMARKERSOVERLAY = 15;
    public static final int MAP_MSG_CODE_SETMARKERSOVERLAY = 16;
    public static final int MAP_MSG_CODE_SHOWBUBBLE = 17;
    public static final int MAP_MSG_CODE_HIDEBUBBLE = 18;
    public static final int MAP_MSG_CODE_REMOVEMAKERSOVER = 19;
    public static final int MAP_MSG_CODE_REMOVEOVERLAY = 20;
    public static final int MAP_MSG_CODE_ADDDOTOVERLAY = 21;
    public static final int MAP_MSG_CODE_ADDPOLYLINEOVERLAY = 22;
    public static final int MAP_MSG_CODE_ADDARCOVERLAY = 23;
    public static final int MAP_MSG_CODE_ADDCIRCLEOVERLAY = 24;
    public static final int MAP_MSG_CODE_ADDPOLYGONOVERLAY = 25;
    public static final int MAP_MSG_CODE_ADDGROUNDOVERLAY = 26;
    public static final int MAP_MSG_CODE_ADDTEXTOVERLAY = 27;
    public static final int MAP_MSG_CODE_POISEARCHINCITY = 28;
    public static final int MAP_MSG_CODE_POINEARBYSEARCH = 29;
    public static final int MAP_MSG_CODE_POIBOUNDSEARCH = 30;
    public static final int MAP_MSG_CODE_BUSLINESEARCH = 31;
    public static final int MAP_MSG_CODE_PREBUSLINENODE = 32;
    public static final int MAP_MSG_CODE_NEXTBUSLINENODE = 33;
    public static final int MAP_MSG_CODE_PREROUTENODE = 35;
    public static final int MAP_MSG_CODE_NEXTROUTENODE = 36;
    public static final int MAP_MSG_CODE_GEOCODE = 37;
    public static final int MAP_MSG_CODE_REVERSEGEOCODE = 38;
    public static final int MAP_MSG_CODE_GETCURRENTLOCATION = 39;
    public static final int MAP_MSG_CODE_STARTLOCATION = 40;
    public static final int MAP_MSG_CODE_STOPTLOCATION = 41;
    public static final int MAP_MSG_CODE_SETMYLOCATIONENABLE = 42;
    public static final int MAP_MSG_CODE_REMOVEBUSLINE = 43;
    public static final int MAP_MSG_CODE_REMOVEROUTEPLAN = 44;
    public static final int MAP_MSG_CODE_SETUSERTRACKINGMODE = 45;
    public static final int MAP_MSG_CODE_HIDEMAP = 46;
    public static final int MAP_MSG_CODE_SHOWMAP = 47;
    public static final int MAP_MSG_CODE_ZOOMCONTROLSENABLED = 48;
    public static final int MAP_MSG_CODE_GETDISTANCE = 49;
    public static final int MAP_MSG_CODE_GETCENTER = 50;
    // 3
    public final static String MAP_EXTRA_LAN = "org.zywx.wbpalmstar.plugin.uexbaidumap.MAP_EXTRA_LAN";
    public final static String MAP_EXTRA_LNG = "org.zywx.wbpalmstar.plugin.uexbaidumap.MAP_EXTRA_LNG";
    public final static String MAP_EXTRA_UEXBASE_OBJ = "org.zywx.wbpalmstar.plugin.uexbaidumap.MAP_EXTRA_UEXBASE_OBJ";

    // 4
    public final static String MAP_PARAMS_JSON_KEY_MARKERINFO = "makerInfo";
    public final static String MAP_PARAMS_JSON_KEY_ID = "id";
    public final static String MAP_PARAMS_JSON_KEY_LNG = "longitude";
    public final static String MAP_PARAMS_JSON_KEY_LAT = "latitude";
    public final static String MAP_PARAMS_JSON_KEY_DISTANCE = "distance";// by_waka_新增字段distance
    public final static String MAP_PARAMS_JSON_KEY_ICON = "icon";
    public final static String MAP_PARAMS_JSON_KEY_BUBBLE = "bubble";
    public final static String MAP_PARAMS_JSON_KEY_TITLE = "title";
    public final static String MAP_PARAMS_JSON_KEY_SUBTITLE = "subTitle";
    public final static String MAP_PARAMS_JSON_KEY_BGIMG = "bgImage";
    public final static String MAP_PARAMS_JSON_KEY_YOFFSET = "yOffset";
    public final static String MAP_PARAMS_JSON_KEY_ADDRESS = "address";
    public static final String MAP_PARAMS_JSON_KEY_START = "start";
    public static final String MAP_PARAMS_JSON_KEY_END = "end";
    public static final String MAP_PARAMS_JSON_KEY_CITY = "city";
    public static final String MAP_PARAMS_JSON_KEY_NAME = "name";
    public static final String MAP_PARAMS_JSON_KEY_TYPE = "type";
    public static final String MAP_TAG = "MapUtillity";
    public static final String MAP_PARAMS_JSON_KEY_FILLCOLOR = "fillColor";
    public static final String MAP_PARAMS_JSON_KEY_LINEWIDTH = "lineWidth";
    public static final String MAP_PARAMS_JSON_KEY_PROPERTY = "property";
    public static final String MAP_PARAMS_JSON_KEY_EXTRAINFO = "extraInfo";
    public static final String MAP_PARAMS_JSON_KEY_VISIBLE = "visible";
    public static final String MAP_PARAMS_JSON_KEY_ZINDEX = "zIndex";
    public static final String MAP_PARAMS_JSON_KEY_STROKECOLOR = "strokeColor";
    public static final String MAP_PARAMS_JSON_KEY_RADIUS = "radius";
    public static final String MAP_PARAMS_JSON_KEY_IMAGEWIDTH = "imageWidth";
    public static final String MAP_PARAMS_JSON_KEY_IMAGEHEIGHT = "imageHeight";
    public static final String MAP_PARAMS_JSON_KEY_POIINFO = "poiInfo";
    public static final String MAP_PARAMS_JSON_KEY_TOTALPOINUM = "totalPoiNum";
    public static final String MAP_PARAMS_JSON_KEY_TOTALPAGENUM = "totalPageNum";
    public static final String MAP_PARAMS_JSON_KEY_CURRENTPAGENUM = "currentPageNum";
    public static final String MAP_PARAMS_JSON_KEY_CURRENTPAGECAPACITY = "currentPageCapacity";
    public static final String MAP_PARAMS_JSON_KEY_UID = "uid";
    public static final String MAP_PARAMS_JSON_KEY_PHONENUM = "phoneNum";
    public static final String MAP_PARAMS_JSON_KEY_POSTCODE = "postCode";
    public static final String MAP_PARAMS_JSON_KEY_POITYPE = "poiType";
    public static final String MAP_PARAMS_JSON_KEY_BUSLINENAME = "busLineName";
    public static final String MAP_PARAMS_JSON_KEY_BUSCOMPANY = "busCompany";
    public static final String MAP_PARAMS_JSON_KEY_BUSSTARTTIME = "startTime";
    public static final String MAP_PARAMS_JSON_KEY_BUSENDTIME = "endTime";
    public static final String MAP_PARAMS_JSON_KEY_BUSSTATION = "busStation";
    public static final String MAP_PARAMS_JSON_KEY_ERRORINFO = "errorInfo";
    public static final String MAP_PARAMS_JSON_KEY_SEARCHKEY = "searchKey";
    public static final String MAP_PARAMS_JSON_KEY_PAGENUM = "pageNum";
    public static final String MAP_PARAMS_JSON_KEY_TIMESTAMP = "timestamp";

    public static final String MAP_PARAMS_JSON_KEY_START_LATITUDE = "startLatitude";
    public static final String MAP_PARAMS_JSON_KEY_START_LONGITUDE = "startLongitude";
    public static final String MAP_PARAMS_JSON_KEY_CENTER_LATITUDE = "centerLatitude";
    public static final String MAP_PARAMS_JSON_KEY_CENTER_LONGITUDE = "centerLongitude";
    public static final String MAP_PARAMS_JSON_KEY_END_LATITUDE = "endLatitude";
    public static final String MAP_PARAMS_JSON_KEY_END_LONGITUDE = "endLongitude";
    public static final String MAP_PARAMS_JSON_KEY_IMAGEURL = "imageUrl";
    public static final String MAP_PARAMS_JSON_KEY_TRANSPARENCY = "transparency";
    public static final String MAP_PARAMS_JSON_KEY_FONTSIZE = "fontSize";
    public static final String MAP_PARAMS_JSON_KEY_TEXT = "text";
    public static final String MAP_PARAMS_JSON_KEY_ROTATE = "rotate";
    public static final String MAP_PARAMS_JSON_KEY_FONTCOLOR = "fontColor";
    public static final String MAP_PARAMS_JSON_KEY_BGCOLOR = "bgColor";
    public static final String MAP_PARAMS_JSON_KEY_NORTHEAST = "northeast";
    public static final String MAP_PARAMS_JSON_KEY_SOUTHWEST = "southwest";

    // 5
    public final static String MAP_FUN_ON_MAKER_CLICK_LISTNER = "uexBaiduMap.onMakerClickListner";
    public final static String MAP_FUN_ON_MAKER_BUBBLE_CLICK_LISTNER = "uexBaiduMap.onMakerBubbleClickListner";
    public final static String MAP_FUN_ON_MARKER_CLICK_LISTENER = "uexBaiduMap.onMarkerClickListener";
    public final static String MAP_FUN_ON_MARKER_BUBBLE_CLICK_LISTENER = "uexBaiduMap.onMarkerBubbleClickListener";
    public final static String MAP_FUN_ON_MAP_CLICK_LISTNER = "uexBaiduMap.onMapClickListener";
    public final static String MAP_FUN_ON_MAP_DOUBLE_CLICK_LISTNER = "uexBaiduMap.onMapDoubleClickListener";
    public final static String MAP_FUN_ON_MAP_LONG_CLICK_LISTNER = "uexBaiduMap.onMapLongClickListener";
    public final static String MAP_FUN_ON_SDK_RECEIVER_ERROR = "uexBaiduMap.onSDKReceiverError";
    public final static String MAP_FUN_ON_RECEIVE_LOCATION = "uexBaiduMap.onReceiveLocation";
    public final static String MAP_FUN_ON_SEARCH_ROUTE_PLAN = "uexBaiduMap.onSearchRoutePlan";
    public final static String MAP_FUN_ON_ZOOM_LEVEL_CHANGE_LISTENER = "uexBaiduMap.onZoomLevelChangeListener";
    public final static String MAP_FUN_ON_MAP_STATUS_CHANGE_LISTENER = "uexBaiduMap.onMapStatusChangeListener";

    public final static String MAP_FUN_CB_GEOCODE_RESULT = "uexBaiduMap.cbGeoCodeResult";
    public final static String MAP_FUN_CB_REVERSE_GEOCODE_RESULT = "uexBaiduMap.cbReverseGeoCodeResult";
    public final static String MAP_FUN_CB_CURRENT_LOCATION = "uexBaiduMap.cbCurrentLocation";
    public final static String MAP_FUN_CB_POISEARCH_RESULT = "uexBaiduMap.cbPoiSearchResult";
    public final static String MAP_FUN_CB_BUSLINE_SEARCH_RESULT = "uexBaiduMap.cbBusLineSearchResult";
    public final static String MAP_FUN_CB_OPEN = "uexBaiduMap.cbOpen";
    public final static String MAP_FUN_CB_GETCENTER = "uexBaiduMap.cbGetCenter";

    public final static String MAP_FUN_CB_GET_DISTANCE = "uexBaiduMap.cbGetDistance";// 计算两点之间的距离by_waka

    public static Bitmap getDefaultMarkerBitMap(Context ctx) {

        Bitmap bitmap;

        int defaultMarkerDrawableId = EUExUtil.getResDrawableID("plugin_map_icon_marker_default");

        bitmap = BitmapFactory.decodeResource(ctx.getResources(), defaultMarkerDrawableId);

        return bitmap;
    }

    public static StateListDrawable bgColorDrawableSelector(Bitmap nomal, Bitmap focus) {

        @SuppressWarnings("deprecation")
        BitmapDrawable nomalBitmap = new BitmapDrawable(nomal);
        @SuppressWarnings("deprecation")
        BitmapDrawable focusBitmap = new BitmapDrawable(focus);
        StateListDrawable selector = new StateListDrawable();
        selector.addState(new int[]{android.R.attr.state_pressed}, focusBitmap);
        selector.addState(new int[]{android.R.attr.state_selected}, focusBitmap);
        selector.addState(new int[]{android.R.attr.state_focused}, focusBitmap);
        selector.addState(new int[]{}, nomalBitmap);
        return selector;
    }

    public static Bitmap getBitMapFromImageUrl(Context ctx, String imgUrl) {
        if (imgUrl == null || imgUrl.length() == 0) {
            return null;
        }

        Bitmap bitmap = null;
        InputStream is = null;
        try {
            if (imgUrl.startsWith(BUtility.F_Widget_RES_SCHEMA)) {
                is = BUtility.getInputStreamByResPath(ctx, imgUrl);
                bitmap = BitmapFactory.decodeStream(is);
            } else if (imgUrl.startsWith(BUtility.F_FILE_SCHEMA)) {
                imgUrl = imgUrl.replace(BUtility.F_FILE_SCHEMA, "");
                bitmap = BitmapFactory.decodeFile(imgUrl);
            } else if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
                try {
                    is = ctx.getAssets().open(imgUrl);
                    if (is != null) {
                        bitmap = BitmapFactory.decodeStream(is);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (imgUrl.startsWith("/")) {
                bitmap = BitmapFactory.decodeFile(imgUrl);
            } else if (imgUrl.startsWith("http://")) {

            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static EBaiduMapMarkerOverlayOptions getMarkerOverlayOpitonsWithJSON(String jsonStr) {

        EBaiduMapMarkerOverlayOptions markerOverlayOptions = null;

        try {

            String markerId = null;
            String lngStr = null;
            String latStr = null;
            String iconPath = null;

            JSONObject makerJsonObject = new JSONObject(jsonStr);

            if (makerJsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ID)) {
                markerId = makerJsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ID);
            } else {
                markerId = String.valueOf(getRandomId());
            }

            if (makerJsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG)) {
                lngStr = makerJsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LNG);
            }

            if (makerJsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT)) {
                latStr = makerJsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_LAT);
            }

            if (makerJsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ICON)) {

                iconPath = makerJsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_ICON);

            }

            markerOverlayOptions = new EBaiduMapMarkerOverlayOptions();

            markerOverlayOptions.setIdStr(markerId);
            markerOverlayOptions.setLngStr(lngStr);
            markerOverlayOptions.setLatStr(latStr);
            markerOverlayOptions.setIconPath(iconPath);

            int yOffset = 0;
            String title = null;
            String subTitle = null;
            String bgImgPath = null;
            boolean isUseYOffset = false;

            if (makerJsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BUBBLE)) {

                String bubbleStr = makerJsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BUBBLE);

                JSONObject bubbleJsonObject = new JSONObject(bubbleStr);

                if (bubbleJsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_TITLE)) {
                    title = bubbleJsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_TITLE);
                }

                if (bubbleJsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_SUBTITLE)) {
                    subTitle = bubbleJsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_SUBTITLE);
                }

                if (bubbleJsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BGIMG)) {
                    bgImgPath = bubbleJsonObject.getString(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_BGIMG);

                }

                if (bubbleJsonObject.has(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_YOFFSET)) {
                    yOffset = bubbleJsonObject.getInt(EBaiduMapUtils.MAP_PARAMS_JSON_KEY_YOFFSET);

                    isUseYOffset = true;
                } else {
                    isUseYOffset = false;
                }

            }

            markerOverlayOptions.setBubbleBgImgPath(bgImgPath);
            markerOverlayOptions.setBubbleSubTitle(subTitle);
            markerOverlayOptions.setBubbleTitle(title);
            markerOverlayOptions.setyOffset(yOffset);
            markerOverlayOptions.setiUseYOffset(isUseYOffset);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return markerOverlayOptions;

    }

    public static EBaiduMapRoutePlanOptions paraseRoutePlanOptions(String msg) {
        EBaiduMapRoutePlanOptions routePlanOptions = null;
        try {
            JSONObject json = new JSONObject(msg);
            routePlanOptions = new EBaiduMapRoutePlanOptions();
            routePlanOptions.setId(json.optString(MAP_PARAMS_JSON_KEY_ID, String.valueOf(getRandomId())));
            routePlanOptions.setType(json.getInt(MAP_PARAMS_JSON_KEY_TYPE));

            JSONObject startJson = json.getJSONObject(MAP_PARAMS_JSON_KEY_START);
            routePlanOptions.setStartCity(startJson.optString(MAP_PARAMS_JSON_KEY_CITY));
            routePlanOptions.setStartNode(parsePlanNode(startJson));

            JSONObject endJson = json.getJSONObject(MAP_PARAMS_JSON_KEY_END);
            routePlanOptions.setEndCity(endJson.optString(MAP_PARAMS_JSON_KEY_CITY));
            routePlanOptions.setEndNode(parsePlanNode(endJson));
        } catch (JSONException e) {
            routePlanOptions = null;
            e.printStackTrace();
        }
        return routePlanOptions;
    }

    private static PlanNode parsePlanNode(JSONObject json) {
        PlanNode planNode = null;
        String cityStr = json.optString(MAP_PARAMS_JSON_KEY_CITY, null);
        String nameStr = json.optString(MAP_PARAMS_JSON_KEY_NAME, null);
        String longitudeStr = json.optString(MAP_PARAMS_JSON_KEY_LNG, null);
        String latitudeStr = json.optString(MAP_PARAMS_JSON_KEY_LAT, null);
        if (longitudeStr != null && latitudeStr != null) {
            try {
                float longitude = Float.parseFloat(longitudeStr);
                float latitude = Float.parseFloat(latitudeStr);
                planNode = PlanNode.withLocation(new LatLng(latitude, longitude));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (cityStr != null && nameStr != null) {
            planNode = PlanNode.withCityNameAndPlaceName(cityStr, nameStr);
        }
        return planNode;
    }

    public static byte[] transStreamToBytes(InputStream is, int buffSize) {
        if (is == null) {
            return null;
        }
        if (buffSize <= 0) {
            throw new IllegalArgumentException("buffSize can not less than zero.....");
        }
        byte[] data = null;
        byte[] buffer = new byte[buffSize];
        int actualSize = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while ((actualSize = is.read(buffer)) != -1) {
                baos.write(buffer, 0, actualSize);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static Bitmap downloadImageFromNetwork(String url) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            URL urlObject = new URL(url);
            HttpURLConnection defaultHttpConnection = null;
            if ("https".equalsIgnoreCase(urlObject.getProtocol())){
                // https
                defaultHttpConnection = Http.getHttpsURLConnection(urlObject);
            }else{
                // http
                defaultHttpConnection = (HttpURLConnection) urlObject.openConnection();
            }
            defaultHttpConnection.setConnectTimeout(15000);
            defaultHttpConnection.setReadTimeout(30000);
            defaultHttpConnection.setRequestMethod("GET");
            defaultHttpConnection.setInstanceFollowRedirects(true);
            defaultHttpConnection.connect();
            int responseCode = defaultHttpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = defaultHttpConnection.getInputStream();
                byte[] data = transStreamToBytes(is, 4096);
                if (data != null) {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static Bitmap getImage(Context ctx, String imgUrl) {
        if (imgUrl == null || imgUrl.length() == 0) {
            return null;
        }
        Bitmap bitmap = null;
        InputStream is = null;
        try {
            if (URLUtil.isNetworkUrl(imgUrl)) {
                bitmap = downloadImageFromNetwork(imgUrl);
            } else {
                if (imgUrl.startsWith(BUtility.F_Widget_RES_SCHEMA)) {
                    is = BUtility.getInputStreamByResPath(ctx, imgUrl);
                    bitmap = BitmapFactory.decodeStream(is);
                } else if (imgUrl.startsWith(BUtility.F_FILE_SCHEMA)) {
                    imgUrl = imgUrl.replace(BUtility.F_FILE_SCHEMA, "");
                    bitmap = BitmapFactory.decodeFile(imgUrl);
                } else if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
                    try {
                        is = ctx.getAssets().open(imgUrl);
                        if (is != null) {
                            bitmap = BitmapFactory.decodeStream(is);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    bitmap = BitmapFactory.decodeFile(imgUrl);
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static EBaiduMapPolylineOptions parseLineInfoJson(String msg) {
        EBaiduMapPolylineOptions lineInfo = null;
        if (msg == null || msg.length() == 0) {
            return lineInfo;
        }
        try {
            lineInfo = new EBaiduMapPolylineOptions();
            JSONObject json = new JSONObject(msg);
            lineInfo.setIdStr(json.optString(MAP_PARAMS_JSON_KEY_ID, String.valueOf(getRandomId())));
            lineInfo.setFillColor(json.getString(MAP_PARAMS_JSON_KEY_FILLCOLOR));
            lineInfo.setLineWidth(json.getString(MAP_PARAMS_JSON_KEY_LINEWIDTH));
            JSONArray property = json.getJSONArray(MAP_PARAMS_JSON_KEY_PROPERTY);
            for (int i = 0; i < property.length(); i++) {
                JSONObject item = property.getJSONObject(i);
                double latitude = item.getDouble(MAP_PARAMS_JSON_KEY_LAT);
                double longitude = item.getDouble(MAP_PARAMS_JSON_KEY_LNG);
                LatLng latLng = new LatLng(latitude, longitude);
                lineInfo.addList(latLng);
            }
            if (json.has(MAP_PARAMS_JSON_KEY_EXTRAINFO)) {
                lineInfo.setExtraStr(json.getString(MAP_PARAMS_JSON_KEY_EXTRAINFO));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_VISIBLE)) {
                lineInfo.setVisibleStr(json.getString(MAP_PARAMS_JSON_KEY_VISIBLE));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_ZINDEX)) {
                lineInfo.setzIndexStr(json.getString(MAP_PARAMS_JSON_KEY_ZINDEX));
            }
        } catch (Exception e) {
            lineInfo = null;
            BDebug.e(MAP_TAG, "parseLineInfoJson() ERROR:" + e.getMessage());
            e.printStackTrace();
        }
        return lineInfo;
    }

    public static EBaiduMapCircleOptions parseCircleInfoJson(String msg) {
        EBaiduMapCircleOptions circleInfo = null;
        if (msg == null || msg.length() == 0) {
            return circleInfo;
        }
        try {
            circleInfo = new EBaiduMapCircleOptions();
            JSONObject json = new JSONObject(msg);
            circleInfo.setIdStr(json.optString(MAP_PARAMS_JSON_KEY_ID, String.valueOf(getRandomId())));
            circleInfo.setFillColor(json.getString(MAP_PARAMS_JSON_KEY_FILLCOLOR));
            circleInfo.setStrokeColor(json.getString(MAP_PARAMS_JSON_KEY_STROKECOLOR));
            circleInfo.setLineWidth(json.getString(MAP_PARAMS_JSON_KEY_LINEWIDTH));
            circleInfo.setRadius(json.getString(MAP_PARAMS_JSON_KEY_RADIUS));
            Double latitude = json.getDouble(MAP_PARAMS_JSON_KEY_LAT);
            Double longitude = json.getDouble(MAP_PARAMS_JSON_KEY_LNG);
            circleInfo.setCenterPoint(latitude, longitude);
            if (json.has(MAP_PARAMS_JSON_KEY_EXTRAINFO)) {
                circleInfo.setExtraStr(json.getString(MAP_PARAMS_JSON_KEY_EXTRAINFO));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_VISIBLE)) {
                circleInfo.setVisibleStr(json.getString(MAP_PARAMS_JSON_KEY_VISIBLE));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_ZINDEX)) {
                circleInfo.setzIndexStr(json.getString(MAP_PARAMS_JSON_KEY_ZINDEX));
            }
        } catch (Exception e) {
            circleInfo = null;
            BDebug.e(MAP_TAG, "parseCircleInfoJson() ERROR:" + e.getMessage());
            e.printStackTrace();
        }
        return circleInfo;
    }

    public static EBaiduMapDotOptions parseDotInfoJson(String msg) {
        EBaiduMapDotOptions dotInfo = null;
        if (msg == null || msg.length() == 0) {
            return dotInfo;
        }
        try {
            dotInfo = new EBaiduMapDotOptions();
            JSONObject json = new JSONObject(msg);
            dotInfo.setIdStr(json.optString(MAP_PARAMS_JSON_KEY_ID, String.valueOf(EBaiduMapUtils.getRandomId())));
            dotInfo.setFillColor(json.getString(MAP_PARAMS_JSON_KEY_FILLCOLOR));
            dotInfo.setRadius(json.getString(MAP_PARAMS_JSON_KEY_RADIUS));
            Double latitude = json.getDouble(MAP_PARAMS_JSON_KEY_LAT);
            Double longitude = json.getDouble(MAP_PARAMS_JSON_KEY_LNG);
            dotInfo.setLatLng(latitude, longitude);
            if (json.has(MAP_PARAMS_JSON_KEY_EXTRAINFO)) {
                dotInfo.setExtraStr(json.getString(MAP_PARAMS_JSON_KEY_EXTRAINFO));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_VISIBLE)) {
                dotInfo.setVisibleStr(json.getString(MAP_PARAMS_JSON_KEY_VISIBLE));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_ZINDEX)) {
                dotInfo.setzIndexStr(json.getString(MAP_PARAMS_JSON_KEY_ZINDEX));
            }

        } catch (Exception e) {
            dotInfo = null;
        }
        return dotInfo;
    }

    public static EBaiduMapPolygonOptions parasePolygonInfoJson(String msg) {
        EBaiduMapPolygonOptions polygonInfo = null;
        if (msg == null || msg.length() == 0) {
            return polygonInfo;
        }
        try {
            polygonInfo = new EBaiduMapPolygonOptions();
            JSONObject json = new JSONObject(msg);
            polygonInfo.setIdStr(json.optString(MAP_PARAMS_JSON_KEY_ID, String.valueOf(getRandomId())));
            polygonInfo.setFillColor(json.getString(MAP_PARAMS_JSON_KEY_FILLCOLOR));
            polygonInfo.setStrokeColor(json.getString(MAP_PARAMS_JSON_KEY_STROKECOLOR));
            polygonInfo.setLineWidth(json.getString(MAP_PARAMS_JSON_KEY_LINEWIDTH));
            JSONArray property = json.getJSONArray(MAP_PARAMS_JSON_KEY_PROPERTY);
            for (int i = 0; i < property.length(); i++) {
                JSONObject item = property.getJSONObject(i);
                Double latitude = item.getDouble(MAP_PARAMS_JSON_KEY_LAT);
                Double longitude = item.getDouble(MAP_PARAMS_JSON_KEY_LNG);
                LatLng latLng = new LatLng(latitude, longitude);
                polygonInfo.addList(latLng);
            }
            if (json.has(MAP_PARAMS_JSON_KEY_EXTRAINFO)) {
                polygonInfo.setExtraStr(json.getString(MAP_PARAMS_JSON_KEY_EXTRAINFO));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_VISIBLE)) {
                polygonInfo.setVisibleStr(json.getString(MAP_PARAMS_JSON_KEY_VISIBLE));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_ZINDEX)) {
                polygonInfo.setzIndexStr(json.getString(MAP_PARAMS_JSON_KEY_ZINDEX));
            }
        } catch (Exception e) {
            polygonInfo = null;
            BDebug.e(MAP_TAG, "parasePolygonInfo() ERROR:" + e.getMessage());
            e.printStackTrace();
        }
        return polygonInfo;
    }

    public static EBaiduMapArcOptions parseArcInfoJson(String msg) {
        EBaiduMapArcOptions arcInfo = null;
        if (msg == null || msg.length() == 0) {
            return arcInfo;
        }
        try {
            arcInfo = new EBaiduMapArcOptions();
            JSONObject json = new JSONObject(msg);
            arcInfo.setIdStr(json.optString(MAP_PARAMS_JSON_KEY_ID, String.valueOf(getRandomId())));
            arcInfo.setStrokeColor(json.getString(MAP_PARAMS_JSON_KEY_STROKECOLOR));
            arcInfo.setLineWidth(json.getString(MAP_PARAMS_JSON_KEY_LINEWIDTH));
            arcInfo.setStart(json.getDouble(MAP_PARAMS_JSON_KEY_START_LATITUDE), json.getDouble(MAP_PARAMS_JSON_KEY_START_LONGITUDE));
            arcInfo.setCenter(json.getDouble(MAP_PARAMS_JSON_KEY_CENTER_LATITUDE), json.getDouble(MAP_PARAMS_JSON_KEY_CENTER_LONGITUDE));
            arcInfo.setEnd(json.getDouble(MAP_PARAMS_JSON_KEY_END_LATITUDE), json.getDouble(MAP_PARAMS_JSON_KEY_END_LONGITUDE));
            if (json.has(MAP_PARAMS_JSON_KEY_EXTRAINFO)) {
                arcInfo.setExtraStr(json.getString(MAP_PARAMS_JSON_KEY_EXTRAINFO));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_VISIBLE)) {
                arcInfo.setVisibleStr(json.getString(MAP_PARAMS_JSON_KEY_VISIBLE));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_ZINDEX)) {
                arcInfo.setzIndexStr(json.getString(MAP_PARAMS_JSON_KEY_ZINDEX));
            }
        } catch (Exception e) {
            arcInfo = null;
            BDebug.e(MAP_TAG, "parseArcInfoJson() ERROR:" + e.getMessage());
            e.printStackTrace();
        }
        return arcInfo;
    }

    public static EBaiduMapGroundOptions parseGroundInfoJson(String msg) {
        EBaiduMapGroundOptions groundInfo = null;
        if (msg == null || msg.length() == 0) {
            return groundInfo;
        }
        try {
            groundInfo = new EBaiduMapGroundOptions();
            JSONObject json = new JSONObject(msg);
            groundInfo.setIdStr(json.optString(MAP_PARAMS_JSON_KEY_ID, String.valueOf(getRandomId())));
            groundInfo.setImageUrl(json.getString(MAP_PARAMS_JSON_KEY_IMAGEURL));
            groundInfo.setTransparency(json.getString(MAP_PARAMS_JSON_KEY_TRANSPARENCY));
            JSONArray property = json.getJSONArray(MAP_PARAMS_JSON_KEY_PROPERTY);
            if (property == null || property.length() > 2) {
                return null;
            }
            for (int i = 0; i < property.length(); i++) {
                JSONObject item = property.getJSONObject(i);
                Double latitude = item.getDouble(MAP_PARAMS_JSON_KEY_LAT);
                Double longitude = item.getDouble(MAP_PARAMS_JSON_KEY_LNG);
                LatLng latLng = new LatLng(latitude, longitude);
                groundInfo.addList(latLng);
            }
            if (json.has(MAP_PARAMS_JSON_KEY_IMAGEWIDTH)) {
                groundInfo.setGroundWidth(json.getString(MAP_PARAMS_JSON_KEY_IMAGEWIDTH));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_IMAGEHEIGHT)) {
                groundInfo.setGroundHeight(json.getString(MAP_PARAMS_JSON_KEY_IMAGEHEIGHT));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_EXTRAINFO)) {
                groundInfo.setExtraStr(json.getString(MAP_PARAMS_JSON_KEY_EXTRAINFO));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_VISIBLE)) {
                groundInfo.setVisibleStr(json.getString(MAP_PARAMS_JSON_KEY_VISIBLE));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_ZINDEX)) {
                groundInfo.setzIndexStr(json.getString(MAP_PARAMS_JSON_KEY_ZINDEX));
            }
        } catch (Exception e) {
            groundInfo = null;
            BDebug.e(MAP_TAG, "paraseGroundInfo() ERROR:" + e.getMessage());
            e.printStackTrace();
        }
        return groundInfo;
    }

    public static EBaiduMapTextOptions paraseTextInfo(String msg) {
        EBaiduMapTextOptions textInfo = null;
        if (msg == null || msg.length() == 0) {
            return textInfo;
        }
        try {
            textInfo = new EBaiduMapTextOptions();
            JSONObject json = new JSONObject(msg);
            textInfo.setIdStr(json.optString(MAP_PARAMS_JSON_KEY_ID, String.valueOf(getRandomId())));
            textInfo.setFontSize(json.getString(MAP_PARAMS_JSON_KEY_FONTSIZE));
            textInfo.setText(json.getString(MAP_PARAMS_JSON_KEY_TEXT));
            textInfo.setLatLng(json.getDouble(MAP_PARAMS_JSON_KEY_LAT), json.getDouble(MAP_PARAMS_JSON_KEY_LNG));
            if (json.has(MAP_PARAMS_JSON_KEY_BGCOLOR)) {
                textInfo.setBgColor(json.getString(MAP_PARAMS_JSON_KEY_BGCOLOR));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_FONTCOLOR)) {
                textInfo.setFontColor(json.getString(MAP_PARAMS_JSON_KEY_FONTCOLOR));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_ROTATE)) {
                textInfo.setRotate(json.getString(MAP_PARAMS_JSON_KEY_ROTATE));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_EXTRAINFO)) {
                textInfo.setExtraStr(json.getString(MAP_PARAMS_JSON_KEY_EXTRAINFO));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_VISIBLE)) {
                textInfo.setVisibleStr(json.getString(MAP_PARAMS_JSON_KEY_VISIBLE));
            }
            if (json.has(MAP_PARAMS_JSON_KEY_ZINDEX)) {
                textInfo.setzIndexStr(json.getString(MAP_PARAMS_JSON_KEY_ZINDEX));
            }
        } catch (Exception e) {
            textInfo = null;
            BDebug.e(MAP_TAG, "paraseTextInfo() ERROR:" + e.getMessage());
            e.printStackTrace();
        }
        return textInfo;
    }
    // public static String getImagePath(Context ctx, String imgUrl) {
    // if (imgUrl == null || imgUrl.length() == 0) {
    // return null;
    // }
    //
    // String imgPath = null;
    //
    // try {
    // if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
    // try {
    // ctx.getAssets().g
    // if (is != null) {
    // bitmap = BitmapFactory.decodeStream(is);
    // }
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // } catch (OutOfMemoryError e) {
    // e.printStackTrace();
    // } finally {
    //
    // }
    // return imgPath;
    // }
    //

    public static int getRandomId() {
        return (int) (Math.random() * 100000);
    }

}
