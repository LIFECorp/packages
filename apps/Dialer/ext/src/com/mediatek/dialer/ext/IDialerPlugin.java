package com.mediatek.dialer.ext;

public interface IDialerPlugin {
    SpeedDialExtension createSpeedDialExtension();

    DialtactsExtension createDialtactsExtension();

    DialPadExtension createDialPadExtension();

    ContactAccountExtension createContactAccountExtension();

    CallListExtension createCallListExtension();

    CallDetailExtension createCallDetailExtension();

    ContactsCallOptionHandlerExtension createContactsCallOptionHandlerExtension();

    ContactsCallOptionHandlerFactoryExtension createContactsCallOptionHandlerFactoryExtension();

    CallLogAdapterExtension createCallLogAdapterExtension();

    CallDetailHistoryAdapterExtension createCallDetailHistoryAdapterExtension();

    DialerSearchAdapterExtension createDialerSearchAdapterExtension();

    CallLogSearchResultActivityExtension createCallLogSearchResultActivityExtension();

    ContactDetailEnhancementExtension createContactDetailEnhancementExtension();

    CallLogSimInfoHelperExtension createCallLogSimInfoHelperExtension();
}
