package com.haloai.hud.music;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangrui on 17/3/6.
 */
public class MusicConstants {
    public static final int PAGE_COUNT = 10;//每次批量搜索默认页面大小
    public static final int DEFAUT_INDEX = -1;///当播放的index值为-1 不播放音频

    public static final String ALBUM_KIND = "album";
    public static final String URL = "URL";
    public static final String ID = "id";
    public static final String APPKEY = "app_key";
    public static final String DEVICE_ID = "device_id";
    public static final String IMEI_ID = "imei_id";
    public static final String PACKID = "pack_id";
    public static final String SDK_VERSION = "sdk_version";
    public static final String CLIENT_OS_TYPE = "client_os_type";
    public static final String SIGNATURE = "sig";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String EXPIRES_IN = "expires_in";
    public static final String OPEN_ID = "open_id";
    public static final String UID = "uid";
    public static final String OTP = "otp";
    public static final String APPSECRET = "app_secret";
    public static final String CATEGORY_ID = "category_id";
    public static final String CATEGORY_NAME = "category_name";
    public static final String TYPE = "type";
    public static final String TAG_NAME = "tag_name";
    public static final String PAGE = "page";
    public static final String PAGE_SIZE = "count";
    public static final String ALBUM_ID = "album_id";
    public static final String SORT = "sort";
    public static final String ALBUM_TITLE = "album_title";
    public static final String SEARCH_KEY = "q";
    public static final String RADIOTYPE = "radio_type";
    public static final String PROVINCECODE = "province_code";
    public static final String RADIOID = "radio_id";
    public static final String WEEKDAY = "weekday";
    public static final String TRACK_BASE_URL = "track_base_url";
    public static final String ALBUM_IDS = "ids";
    public static final String TRACK_IDS = "ids";
    public static final String RADIO_IDS = "ids";
    public static final String TOTAL_PAGE = "total_page";
    public static final String PRE_PAGE = "pre_page";
    public static final String TRACK_ID = "track_id";
    public static final String PID = "pid";
    public static final String DEVICE_TYPE = "device_type";
    public static final String BANNER_CONTENT_TYPE = "banner_content_type";
    public static final String COLDBOOT_GENRE = "coldboot_genre";
    public static final String COLDBOOT_SUB_GENRE = "coldboot_sub_genre";
    public static final String COLDBOOT_TAGS = "coldboot_tags";
    public static final String TOP = "top";
    public static final String CALC_DIMENSION = "calc_dimension";
    public static final String LIKE_COUNT = "like_count";
    public static final String AID = "aid";
    public static final String TRACKID = "trackId";
    public static final String ALBUMID = "albumId";
    public static final String CITY_CODE = "city_code";
    public static final String RANK_TYPE = "rank_type";
    public static final String RANK_KEY = "rank_key";
    public static final String RADIO_COUNT = "radio_count";
    public static final String CHANNEL = "channel";
    public static final String APP_VERSION = "app_version";
    public static final String IMAGE_SCALE = "image_scale";
    public static final String CATEGORY = "category";
    public static final String TAG = "tag";
    public static final String ALBUM = "album";
    public static final String BATCHALBUM = "batchalbum";
    public static final String SEARCHALBUM = "searchalbum";
    public static final String TRACK = "track";
    public static final String TRACKHOT = "trackhot";
    public static final String BATCHTRACK = "batchtrack";
    public static final String SEARCHTRACK = "searchtrack";
    public static final String PROGRAM = "program";
    public static final String PROVINCE = "province";
    public static final String RADIO = "radio";
    public static final String SCHEDULE = "schedule";
    public static final String VCATEGORY_ID = "vcategory_id";
    public static final String ICON_SET_ID = "icon_set_id";
    public static final String CUSTOMIZED_TRACKLIST_ID = "customized_tracklist_id";
    public static final String PROGRAM_SCHEDULE_ID = "program_schedule_id";
    public static final String PROGRAM_ID = "program_id";
    public static final String DURATION = "duration";
    public static final String PLAYED_SECS = "played_secs";
    public static final String STARTED_AT = "started_at";
    public static final String LIVE_RECORDS = "live_records";
    public static final String PLAY_TYPE = "play_type";
    public static final String TRACK_RECORDS = "track_records";
    public static final String CONTENT_TYPE = "content_type";
    public static final String DISPLAY_COUNT = "display_count";
    public static final String FILE_ID = "file_id";
    public static final String EP = "ep";
    public static final String SAMPLE_LENGTH = "sample_length";
    public static final String API_VERSION = "api_version";
    public static final String RADIO_CATEGORY_ID = "radio_category_id";


    public static Map<String,String> getMapParmas(){
        Map<String,String> map = new HashMap<String, String>();
        map.put(CALC_DIMENSION,"1");
        map.put(PAGE,"0");
        map.put(PAGE_SIZE,PAGE_COUNT+"");
        return map;
    }
}
