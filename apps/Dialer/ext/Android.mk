LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES := mediatek-framework
LOCAL_JAVA_LIBRARIES += telephony-common

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src) \
                   ../src/com/mediatek/dialer/PhoneCallDetailsEx.java \
                   ../src/com/mediatek/dialer/calllogex/ContactInfoEx.java \
                   ../src/com/mediatek/dialer/calllogex/CallLogQueryEx.java \
                   ../../ContactsCommon/src/com/android/contacts/common/util/UriUtils.java

LOCAL_MODULE := com.mediatek.dialer.ext

LOCAL_STATIC_JAVA_LIBRARIES := com.android.phone.shared \
                               CellConnUtil
LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := proguard.flags

include $(BUILD_STATIC_JAVA_LIBRARY)

