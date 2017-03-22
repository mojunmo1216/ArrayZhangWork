package com.haloai.huduniversalio.speech;


public interface ISpeechParser {
	/*
	 * 解析结果基准值，继承的接口的返回解析结果值只能在该值上累加
	 */
	public static final int PARSE_RES_CODE_BASE	=	0;
	/*
	 * 解析结果错误：当前会话下的错误语音输入
	 */
	public static final int PARSE_RES_CODE_IMPROPER_SPEECH_INPUT = PARSE_RES_CODE_BASE - 1;
	/*
	 * 解析结果错误：无法识别用户输入的语音
	 */
	public static final int PARSE_RES_CODE_NOT_CLEAR_SPEECH_INPUT = PARSE_RES_CODE_BASE - 2;
	/*
	 * 解析结果错误：内部错误
	 */
	public static final int PARSE_RES_CODE_INTERNAL_ERROR = PARSE_RES_CODE_BASE - 3;

	/*
	 * 根据语音引擎的返回，解析语音结果值
	 * return true-解析成功；false-解析失败，可以通过getLastError获取失败解释
	 */
	public abstract int parseSpeechText(String speechText);
	
//	/*
//	 * 获取最后的解析失败原因，其返回文本可以用来显示或进行语音播报
//	 */
//	public abstract String getResultDescription(int resultCode);
	
}
