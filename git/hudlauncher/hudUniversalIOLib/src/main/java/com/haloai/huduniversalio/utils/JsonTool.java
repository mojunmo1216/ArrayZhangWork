/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * @FileName : JsonTool.java
 * @ProjectName : vui_common
 * @PakageName : cn.yunzhisheng.common
 * @Author : Dancindream
 * @CreateDate : 2013-8-12
 */
package com.haloai.huduniversalio.utils;

import java.lang.reflect.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.gson.Gson;
import com.google.gson.JsonNull;


/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-8-12
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-8-12
 * @Modified:
 * 2013-8-12: 实现基本功能
 */
public class JsonTool {
	public static final String TAG = "JsonTool";

	public static JSONObject parseToJSONObject(String json) {
		JSONObject parsedJsonObject = new JSONObject();
		JSONTokener jsonTokener = new JSONTokener(json);

		try {
			Object obj = jsonTokener.nextValue();
			if (obj != null) {
				if (obj instanceof JSONObject) {
					parsedJsonObject = ((JSONObject) obj);
				}
			}
		} catch (Exception ee) {
		}

		return parsedJsonObject;
	}

	public static JSONArray parseToJSONOArray(String json) {
		JSONArray parsedJsonObject = new JSONArray();

		if (json != null && !"".equals(json)) {
			JSONTokener jsonTokener = new JSONTokener(json);

			try {
				Object obj = jsonTokener.nextValue();
				if (obj != null) {
					if (obj instanceof JSONObject) {
						parsedJsonObject.put((JSONObject) obj);
					} else if (obj instanceof JSONArray) {
						parsedJsonObject = (JSONArray) obj;
					}
				}
			} catch (Exception ee) {
			}
		}

		return parsedJsonObject;
	}

	public static JSONObject getJSONObject(JSONObject jsonObj, String name) {
		if (jsonObj != null && jsonObj.has(name)) {
			try {
				return jsonObj.getJSONObject(name);
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static JSONObject getJSONObject(JSONArray jsonArr, int index) {
		if (jsonArr != null) {
			try {
			    int length = jsonArr.length();
			    if(index < length) {
			        return jsonArr.getJSONObject(index);
			    }
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public static String getJsonValue(JSONObject json, String key) {
		if (json != null && json.has(key)) {
			try {
				return json.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static String getJsonValue(JSONObject json, String key, String defValue) {
		if (json != null && json.has(key)) {
			try {
				String value = json.getString(key);
				if (value == null || value.equals("") || value.equals("null")) {
					return defValue;
				}
				return value;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return defValue;
	}

	public static boolean getJsonValue(JSONObject json, String key, boolean defValue) {
		if (json != null && json.has(key)) {
			try {
				return json.getBoolean(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return defValue;
	}

	public static int getJsonValue(JSONObject json, String key, int defValue) {
		if (json != null && json.has(key)) {
			try {
				return json.getInt(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return defValue;
	}
	
	public static double getJsonValue(JSONObject json, String key, double defValue) {
		if (json != null && json.has(key)) {
			try {
				return json.getDouble(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return defValue;
	}

	public static JSONArray getJsonArray(JSONObject jsonObj, String key) {
		if (jsonObj != null && jsonObj.has(key)) {
			try {
				return jsonObj.getJSONArray(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * @MethodName : toJson
	 * @Description : 将对象转为JSON串，此方法能够满足大部分需求
	 * @param src
	 *            :将要被转化的对象
	 * @return :转化后的JSON串
	 */
	public static String toJson(Object src) {
		Gson gson = new Gson();
		if (src == null) {
			return gson.toJson(JsonNull.INSTANCE);
		}
		return gson.toJson(src);
	}

	/**
	 * @MethodName : fromJson
	 * @Description : 用来将JSON串转为对象，但此方法不可用来转带泛型的集合
	 * @param json
	 * @param classOfT
	 * @return
	 */
	public static <T> Object fromJson(String json, Class<T> classOfT) {
		Gson gson = new Gson();
		return gson.fromJson(json, (Type) classOfT);
	}

	/**
	 * @MethodName : fromJson
	 * @Description : 用来将JSON串转为对象，此方法可用来转带泛型的集合，如：Type为 
	 *              new TypeToken<List<T>>(){}.getType()
	 *              ，其它类也可以用此方法调用，就是将List<T>替换为你想要转成的类
	 * @param json
	 * @param typeOfT
	 * @return
	 */
	public static Object fromJson(String json, Type typeOfT) {
		Gson gson = new Gson();
		return gson.fromJson(json, typeOfT);
	}
	
	public static void putJSONObjectData(JSONObject objc, String key, String value) {
		try {
			if (value != null && !"".equals(value)) {
				objc.put(key, value);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void putJSONObjectData(JSONObject objc, String key, Object value) {
		try {
			if (value != null && !"".equals(value)) {
				objc.put(key, value);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
