package com.mediatek.contacts.util;

import com.mediatek.contacts.ContactsFeatureConstants.FeatureOption;

public class VCSUtils {
    private static boolean MTK_VOICE_CONTACT_SEARCH_SUPPORT = com.mediatek.common.featureoption.FeatureOption.MTK_VOICE_CONTACT_SEARCH_SUPPORT;

    /**
     * [VCS] whether VCS feature enabled on this device
     * 
     * @return ture if allowed to enable
     */
    public static boolean isVCSFeatureEnabled() {
        return MTK_VOICE_CONTACT_SEARCH_SUPPORT;
    }

}
