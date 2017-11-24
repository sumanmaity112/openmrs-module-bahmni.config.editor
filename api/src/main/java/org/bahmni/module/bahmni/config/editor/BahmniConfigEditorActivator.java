package org.bahmni.module.bahmni.config.editor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

public class BahmniConfigEditorActivator extends BaseModuleActivator {
    protected Log log = LogFactory.getLog(getClass());

    public void willRefreshContext() {
        log.info("Refreshing bahmniConfigEditor Module");
    }

    public void contextRefreshed() {
        log.info("bahmniConfigEditor Module refreshed");
    }

    public void willStart() {
        log.info("Starting bahmniConfigEditor Module");
    }

    public void started() {
        log.info("bahmniConfigEditor Module started");
    }

    public void willStop() {
        log.info("Stopping bahmniConfigEditor Module");
    }

    public void stopped() {
        log.info("bahmniConfigEditor Module stopped");
    }
}
