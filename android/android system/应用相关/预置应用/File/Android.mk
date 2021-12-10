LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE       := device_owner_2
LOCAL_MODULE_TAGS  := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_SRC_FILES    := device_owner_2.xml
LOCAL_MODULE_SUFFIX=.xml
LOCAL_MODULE_PATH  := $(TARGET_OUT_DATA)/system  #放到/data/system 目录下
include $(BUILD_PREBUILT)

include $(CLEAR_VARS)
LOCAL_MODULE       := device_policies
LOCAL_MODULE_TAGS  := optional
LOCAL_MODULE_CLASS := ETC
LOCAL_SRC_FILES    := device_policies.xml
LOCAL_MODULE_SUFFIX=.xml
LOCAL_MODULE_PATH  := $(TARGET_OUT_DATA)/system #放到/data/system 目录下

include $(BUILD_PREBUILT)