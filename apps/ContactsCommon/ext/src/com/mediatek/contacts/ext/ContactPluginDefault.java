package com.mediatek.contacts.ext;

public class ContactPluginDefault implements IContactPlugin {
    public static final String COMMD_FOR_OP01 = "ExtensionForOP01";
    public static final String COMMD_FOR_OP09 = "ExtensionForOP09";
    public static final String COMMD_FOR_AppGuideExt = "ExtensionForAppGuideExt";
    public static final String COMMD_FOR_RCS = "ExtenstionForRCS";
    public static final String COMMD_FOR_AAS = "ExtensionForAAS";
    public static final String COMMD_FOR_SNE = "ExtensionForSNE";
    public static final String COMMD_FOR_SNS = "ExtensionForSNS";

    public ContactAccountExtension createContactAccountExtension() {
        return new ContactAccountExtension();
    }

    public ContactDetailExtension createContactDetailExtension() {
        return new ContactDetailExtension();
    }

    public ContactListExtension createContactListExtension() {
        return new ContactListExtension();
    }

    public SimPickExtension createSimPickExtension() {
        return new SimPickExtension();
    }

    public QuickContactExtension createQuickContactExtension() {
        return new QuickContactExtension();
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
    public SimServiceExtension createSimServiceExtension() {
        return new SimServiceExtension();
    }
    
    @Override
    public ImportExportEnhancementExtension createImportExportEnhancementExtension() {
        return new ImportExportEnhancementExtension();
    }

    @Override
    public IccCardExtension createIccCardExtension() {
        return new IccCardExtension();
    }

    @Override
    public LabeledEditorExtension createLabeledEditorExtension() {
        return new LabeledEditorExtension();
    }
}
