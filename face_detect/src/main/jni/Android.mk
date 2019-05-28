LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := jniCalculator

LOCAL_SRC_FILES := Calculator.cpp

LOCAL_LDLIBS := -llog -landroid

include $(BUILD_SHARED_LIBRARY)