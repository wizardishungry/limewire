package com.limegroup.gnutella.gui.tabs;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.limewire.setting.evt.SettingEvent;
import org.limewire.setting.evt.SettingListener;

import com.limegroup.gnutella.gui.GUIUtils;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.swtbrowser.SWTBrowserPanel;
import com.limegroup.gnutella.settings.ApplicationSettings;
import com.limegroup.gnutella.settings.SWTBrowserSettings;

/**
 * This class constructs the search/download tab, including all UI elements.
 */
public final class SWTBrowserSearchTab extends AbstractTab {
    
    /** visible component. */
    private final SWTBrowserPanel COMPONENT;

    public SWTBrowserSearchTab() {
        super(I18n.tr(GUIUtils.stripAmpersand(SWTBrowserSettings.getTitleSetting().getValue())),
                I18n.tr(SWTBrowserSettings.getTooltipSetting().getValue()),
                "browser_tab");
        
        COMPONENT = new SWTBrowserPanel();
        SettingListener listener = new SettingListener() {
            public void settingChanged(final SettingEvent evt) {
                if (evt.getEventType() == SettingEvent.EventType.VALUE_CHANGED) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (evt.getSetting() == SWTBrowserSettings.getTitleSetting())
                                changeTitle(I18n.tr(GUIUtils.stripAmpersand(SWTBrowserSettings.getTitleSetting().getValue())));
                            else if (evt.getSetting() == SWTBrowserSettings.getTooltipSetting())
                                changeTooltip(I18n.tr(SWTBrowserSettings.getTooltipSetting().getValue()));
                        }
                    });
                }
            }
        };
        SWTBrowserSettings.getTitleSetting().addSettingListener(listener);
        SWTBrowserSettings.getTooltipSetting().addSettingListener(listener);
    }
    
    @Override
    public final void storeState(boolean visible) {
        ApplicationSettings.SWT_BROWSER_VIEW_ENABLED.setValue(visible);
        if( visible )
            COMPONENT.createBrowser();
        else 
            COMPONENT.destroyBrowser();
    }

    @Override
    public final JComponent getComponent() {
        return COMPONENT;
    }
    
    @Override
    public void mouseClicked() { 
        COMPONENT.createBrowser();
    }

}
