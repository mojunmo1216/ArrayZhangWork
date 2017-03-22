package com.unisound.framework.preference;

import com.unisound.client.SpeechConstants;

public class ErrorMessage {
	public static final String ASR_TIMEOUT_ERROR = "asr timeout error";
	public static final String MIC_CONFLICT_ERROR = "mic conflict error";
	public static final String NULL_PROTOCAL_ERROR = "null protocal error";
	public static final String PROTOCAL_ERROR = "protocal error";
	public static final String COMPILE_ERROR = "compile error";
	
	public static final int[] TIMEOUT_ERROR = {SpeechConstants.ASR_NET_TIMEOUT};
}
