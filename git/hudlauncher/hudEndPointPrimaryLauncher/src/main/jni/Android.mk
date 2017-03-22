LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := TurnProtocolParser
LOCAL_SRC_FILES :=  cutils.c \
					cld_navi_turnprotocol_CameraInfo.c \
					cld_navi_turnprotocol_GuidanceHightWayInfo.c \
					cld_navi_turnprotocol_GuidanceInfo.c \
					cld_navi_turnprotocol_GuidanceLaneInfo.c \
					cld_navi_turnprotocol_GuidanceLaneInfo_LaneState.c \
					cld_navi_turnprotocol_JVInfo.c \
					cld_navi_turnprotocol_TurnProtocolInfo.c \
					cld_navi_turnprotocol_TurnProtocolParser.c

LOCAL_LDLIBS	:= -llog -ljnigraphics
LOCAL_CFLAGS   := -DHAVE_LINUX_LOCAL_SOCKET_NAMESPACE

include $(BUILD_SHARED_LIBRARY)
