package com.limegroup.gnutella.gui.options.panes;

import java.io.IOException;

import javax.swing.JCheckBox;

import org.limewire.i18n.I18nMarker;

import com.limegroup.gnutella.gui.GUIMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.LabeledComponent;
import com.limegroup.gnutella.settings.StatusBarSettings;

/**
 * This class defines the panel in the options window that allows the user
 * to change whether the LWS indicator is shown in the status bar.
 */
//2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class StatusBarLWSPaneItem extends AbstractPaneItem {

    public final static String TITLE = I18n.tr("LimeWire Store Indicator");
    
    public final static String LABEL = I18n.tr("You can display your LimeWire Store status in the status bar.");

    /**
     * Constant for the key of the locale-specific <tt>String</tt> for whether 
     * the LWS status should be displayed in the status bar.
     */
    private final String CHECK_BOX_LABEL = 
        I18nMarker.marktr("Show LimeWire Store Indicator:");

    private final JCheckBox CHECK_BOX = new JCheckBox();

    /**
	 * The constructor constructs all of the elements of this
	 * <tt>AbstractPaneItem</tt>.
	 * 
	 * @param key
	 *            the key for this <tt>AbstractPaneItem</tt> that the
	 *            superclass uses to generate locale-specific keys
	 */
	public StatusBarLWSPaneItem() {
	    super(TITLE, LABEL);
	    
		LabeledComponent comp = new LabeledComponent(CHECK_BOX_LABEL,
				CHECK_BOX, LabeledComponent.LEFT_GLUE, LabeledComponent.LEFT);
		add(comp.getComponent());
	}

    /**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
     *
     * Sets the options for the fields in this <tt>PaneItem</tt> when the 
     * window is shown.
     */
    @Override
    public void initOptions() {
        CHECK_BOX.setSelected(StatusBarSettings.LWS_DISPLAY_ENABLED.getValue());
    }

    /**
     * Defines the abstract method in <tt>AbstractPaneItem</tt>.<p>
     *
     * Applies the options currently set in this window, displaying an
     * error message to the user if a setting could not be applied.
     *
     * @throws IOException if the options could not be applied for some reason
     */
    @Override
    public boolean applyOptions() throws IOException {
        if (!isDirty())
            return false;
        
        StatusBarSettings.LWS_DISPLAY_ENABLED.setValue(CHECK_BOX.isSelected());
        GUIMediator.instance().getStatusLine().refresh();
        return false;
    }
    
    public boolean isDirty() {
        return StatusBarSettings.LWS_DISPLAY_ENABLED.getValue() != CHECK_BOX.isSelected();
    }
}
