LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE:= libseal
LOCAL_SRC_FILES:= jni.c
include $(BUILD_SHARED_LIBRARY)