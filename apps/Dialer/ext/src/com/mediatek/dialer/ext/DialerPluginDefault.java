package com.mediatek.dialer.ext;

public class DialerPluginDefault implements IDialerPlugin {
    public static final String COMMD_FOR_OP01 = "ExtensionForOP01";
    public static final String COMMD_FOR_OP09 = "ExtensionForOP09";
    public static final String COMMD_FOR_RCS = "ExtenstionForRCS";
    public static final String COMMD_FOR_AAS = "ExtensionForAAS";
    public static final String COMMD_FOR_SNE = "ExtensionForSNE";
    public static final String COMMD_FOR_SNS = "ExtensionForSNS";
    public static final String COMMD_FOR_AppGuideExt = "ExtensionForAppGuideExt";

    public CallDetailExtension createCallDetailExtension() {
        return new CallDetailExtension();
    }

    public CallListExtension createCallListExtension() {
        return new CallListExtension();
    }

    public ContactAccountExtension createContactAccountExtension() {
        return new ContactAccountExtension();
    }

    public DialPadExtension createDialPadExtension() {
        return new DialPadExtension();
    }

    public DialtactsExtension createDialtactsExtension() {
        return new DialtactsExtension();
    }

    public SpeedDialExtension createSpeedDialExtension() {
        return new SpeedDialExtension();
    }

    
    public ContactDetailEnhancementExtension createContactDetailEnhancementExtension() {
        return new ContactDetailEnhancementExtension();
    }

    @Override
    public ContactsCallOptionHandlerExtension createContactsCallOptionHandlerExtension() {
        return new ContactsCallOptionHandlerExtension();
    }

    @Override
    public ContactsCallOptionHandlerFactoryExtension createContactsCallOptionHandlerFactoryExtension() {
        return new ContactsCallOptionHandlerFactoryExtension();
    }

    @Override
    public CallLogAdapterExtension createCallLogAdapterExtension() {
        return new CallLogAdapterExtension();
    }

    @Override
    public CallDetailHistoryAdapterExtension createCallDetailHistoryAdapterExtension() {
        return new CallDetailHistoryAdapterExtension();
    }

    @Override
    public CallLogSearchResultActivityExtension createCallLogSearchResultActivityExtension() {
        return new CallLogSearchResultActivityExtension();
    }

    @Override
    public DialerSearchAdapterExtension createDialerSearchAdapterExtension() {
        return new DialerSearchAdapterExtension();
    }

    @Override
    public CallLogSimInfoHelperExtension createCallLogSimInfoHelperExtension() {
        return new CallLogSimInfoHelperExtension();
    }
}
