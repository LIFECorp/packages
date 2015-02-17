package com.mediatek.contacts.ext;

public interface IContactPlugin {
    SimPickExtension createSimPickExtension();

    ContactListExtension createContactListExtension();

    ContactDetailExtension createContactDetailExtension();

    ContactAccountExtension createContactAccountExtension();

    QuickContactExtension createQuickContactExtension();
    
    IccCardExtension createIccCardExtension();

    ContactsCallOptionHandlerExtension createContactsCallOptionHandlerExtension();

    ContactsCallOptionHandlerFactoryExtension createContactsCallOptionHandlerFactoryExtension();

    ContactDetailEnhancementExtension createContactDetailEnhancementExtension();

    SimServiceExtension createSimServiceExtension();
    
    ImportExportEnhancementExtension createImportExportEnhancementExtension();

    LabeledEditorExtension createLabeledEditorExtension();
}
