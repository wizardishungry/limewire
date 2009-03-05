package com.limegroup.gnutella.gui.swtbrowser;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.limewire.concurrent.ManagedThread;
import org.limewire.setting.BooleanSetting;
import org.limewire.setting.evt.SettingEvent;
import org.limewire.setting.evt.SettingListener;

import com.limegroup.gnutella.GUID;
import com.limegroup.gnutella.gui.ButtonRow;
import com.limegroup.gnutella.gui.GuiCoreMediator;
import com.limegroup.gnutella.gui.I18n;
import com.limegroup.gnutella.gui.actions.LimeAction;
import com.limegroup.gnutella.settings.SWTBrowserSettings;
import com.limegroup.gnutella.util.LimeWireUtils;

/**
 * The panel that holds the browser for the store.
 */
public final class SWTBrowserPanel extends JPanel implements ComponentListener {
    
    private static final Log LOG = LogFactory.getLog(SWTBrowserPanel.class);
    
    /** The instance of the SWT browser. */
    private volatile SWTBrowser browser;   
    
    /** We'll lazily-init the browser, so keep track with this. */
    private final AtomicBoolean isBrowserCreated = new AtomicBoolean(false);
        
    /**
     * Heavey weight component to paint the web browser on
     */
    private Canvas swingCanvas;
    
    /**
     * Navigation row at the top.
     */
    private final JPanel swingNavigationPanel;
    
    /**
     * Progress panel on the bottom.
     */
    private final JPanel swingProgressPanel;
    
    /**
     * The text pane holding the current address.
     */
    private final JTextField swingAddressField = new JTextField(60);
       
    /**
     * Displays the current status.
     */
    private final JLabel swingStatusText;
    
    /**
     * Displays the progress.
     */
    private final JProgressBar swingProgressBar;
    
    /**
     * <code>true</code> if we're currently drawing.
     */
    private boolean busy;
    
    /**
     * Action for going back.
     */
    private Action swingBackAction;
    
    /**
     * Action for going forward.
     */
    private Action swingForwardAction;

    /**
     * The insets around the {@link Browser}.
     */
    private final EmptyBorder emptyBorder;

    /** Background color for The LimeWire Store. */
    final static Color LWS_BACKGROUND_COLOR = new Color(26, 58, 78, 255);

    private JScrollPane scrollPane;
    
    // panel to display if something goes wrong with browser init
    private JPanel errorPanel;
    
    public SWTBrowserPanel() { 
        super(new BorderLayout());        

        swingStatusText = new JLabel(I18n.tr("Idle"));
        swingProgressBar = new JProgressBar(JProgressBar.HORIZONTAL);
        emptyBorder = new EmptyBorder(5,5,0,5);
        
        // Put a little space to the left of the status message
        final int SPACE = 5;
        swingStatusText.setBorder(new EmptyBorder(0, SPACE, 0, 0));
        
        // Create the main panels
        swingNavigationPanel = makeNavigationPanel();
        swingProgressPanel = makeProgressPanel();
        
        // We need to add the NORTH and SOUTH panels first to ensure
        // that the browser will fit in the panel
        add(swingNavigationPanel, BorderLayout.NORTH);
        add(swingProgressPanel, BorderLayout.SOUTH);

        //set the main background, construct the canvas lazily later to replace it
        this.setBackground(LWS_BACKGROUND_COLOR);
        
        this.addComponentListener(this);
    }
    
    /**
     * Called to lazily-initialize the browser.
     */
    public void createBrowser() { 
        //
        // This will be called every time the browser shows so that
        // we lazily-initialize it
        //
        if (isBrowserCreated.getAndSet(true)) 
            return;
        internalCreateBrowser();
    }

    private String munge(String location) {
        String guid = new GUID(GuiCoreMediator.getApplicationServices().getMyGUID()).toHexString();
        if(location != null) {
            int idx = location.indexOf("guid=" + guid);
            if(idx != -1 && idx != 0) {
                location = location.substring(0, idx-1);
            }
        }
        return location;
    }
    
    /**
     * @see #createBrowser()
     */
    private void internalCreateBrowser() {

        // must recreate the canvas each time, so remove the scrollpane if it exists already
        if( scrollPane != null )
            this.remove(scrollPane);
        if( errorPanel != null )
            this.remove(errorPanel);
        
        // lazyily create the canvas only if the browser will be launched
        // must create this each time to ensure that browser has a valid component to paint on
        swingCanvas = new Canvas();
        swingCanvas.setBackground(LWS_BACKGROUND_COLOR);
        scrollPane = new JScrollPane(swingCanvas, JScrollPane.VERTICAL_SCROLLBAR_NEVER, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        
        if(LOG.isDebugEnabled())
            LOG.debug("Creating browser");
        
        new ManagedThread(new Runnable() {
            public void run() {
                try{
                browser = new SWTBrowser(swingCanvas, getWidth(), getHeight(), new LocationListener() {
                    public void changed(LocationEvent e) {
                        // only load the top level url upon completion 
                        if( e.top ) {
                            final String location = munge(e.location);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {                                
                                    swingAddressField.setText(location);
                                }});
                        }
                    }
                    public void changing(LocationEvent e) {
                        // Nothing
                    }
                });
                
                // Listen for updates
                browser.addProgressListener(new ProgressListener() {
                    
                    public void changed(ProgressEvent event) {
                        final int total   = event.total;
                        if (total == 0) return;
                        final int current = event.current;
                        final int ratio   = current * 100 / total;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                swingProgressBar.setValue(ratio);
                                busy = current != total;
                                if (!busy) {
                                    swingCanvas.repaint();
                                }
                            }});
                        }

                    public void completed(ProgressEvent event) {
                        final boolean isBackEnabled = browser.isBackEnabled();
                        final boolean isForwardEnabled = browser.isForwardEnabled();
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {                                
                              swingBackAction.setEnabled(isBackEnabled);
                              swingForwardAction.setEnabled(isForwardEnabled);
                              swingProgressBar.setValue(0);
                              swingStatusText.setText(I18n.tr("Idle"));
                              busy = false;
                              swingCanvas.repaint();
                            }});
                    }
                  });
                
                  browser.addStatusTextListener(new StatusTextListener() {
                    public void changed(StatusTextEvent event) {
                        final String text = event.text;
                        if (text != null && !text.equals("")) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {                                                                     
                                    swingStatusText.setText(text);
                                }});
                        }
                    }
                  });                        
                
                browser.start(getHomeURL());
                }
                catch(Throwable t) {
                    LOG.error(t);
                    displayErrorInBrowser();
                }
            }
        }, "swtbrowser-thread").start();
    }
    
    
    /**
     * Replaces the browser with an error message to inform the user
     * that the browser will not be opening
     */
    private void displayErrorInBrowser(){
        SwingUtilities.invokeLater( new Runnable(){
            public void run(){
                remove(scrollPane);
                add( getErrorPanel(), BorderLayout.CENTER);
                repaint();
            }
        });
    }
        
    /**
     * Creates a panel to display an error message in when the browser
     * fails to load.
     */
    private JPanel getErrorPanel(){
        if( errorPanel == null ) {
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
                
            errorPanel = new JPanel(new GridBagLayout());
            errorPanel.setBackground(LWS_BACKGROUND_COLOR);
                     
            JLabel errorLabel = new JLabel(I18n.tr("Sorry, the browser could not be loaded."));
            errorLabel.setForeground( Color.red );
            errorLabel.setFont( errorLabel.getFont().deriveFont(24f) );
                     
            errorPanel.add(errorLabel, c);
        }
        return errorPanel;
    }
    
    /**
     * Destroys the current instance of the SWT browser. When removing the browser
     * tab from the view, there's no need to keep the SWT Browser around 
     */
    public void destroyBrowser(){ 
        isBrowserCreated.getAndSet(false);
    }
    
    private JPanel makeNavigationPanel() {

        final JPanel res = new JPanel();
        res.setLayout(new BoxLayout(res, BoxLayout.X_AXIS));
        
        // Add the navigation buttons
        final JPanel buttonRow = makeBrowserButtons();
        res.add(buttonRow);
        
        // The URL field
        res.add(swingAddressField);
        final Action goAction = new AbstractAction(I18n.tr("Go")) {
            public void actionPerformed(ActionEvent e) {
                String url = swingAddressField.getText();
                if (url == null || browser == null)
                    return;
                browser.setUrl(makeNicerURL(url.trim()));
            }
        };
        swingAddressField.addActionListener(goAction);
        final JButton go = new JButton(goAction);
        res.add(go);

        final BooleanSetting setting = SWTBrowserSettings.BROWSER_SHOW_ADDRESS;
        swingAddressField.setVisible(setting.getValue());
        go.setVisible(setting.getValue());
        setting.addSettingListener(new SettingListener() {
            public void settingChanged(final SettingEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        swingAddressField.setVisible(setting.getValue());
                        go.setVisible(setting.getValue());
                    }
                });
            }
        });
        
        return res;
    }
    
    private JPanel makeProgressPanel() {
        final JPanel res = new JPanel(new BorderLayout());
        res.add(swingStatusText, BorderLayout.WEST);
        res.add(swingProgressBar, BorderLayout.EAST);
        return res;
    }
    
    private static String makeNicerURL(String url) {
        if (url == null)
            return "";
        if (url.indexOf("://") == -1)
            url = "http://" + url;
        return url;
    }
      
    private ButtonRow makeBrowserButtons() {
        Action[] as = new Action[] { 
           new BrowserButtonAction(I18n.tr("Back"), "SWT_BROWSER_BACK") {
           public void actionPerformed(ActionEvent e) {
               browserBack();
           }
        }, new BrowserButtonAction(I18n.tr("Stop"), "SWT_BROWSER_STOP") {
            public void actionPerformed(ActionEvent e) {
                browserStop();
            }
        }, new BrowserButtonAction(I18n.tr("Store"), "SWT_BROWSER_HOME") {
            public void actionPerformed(ActionEvent e) {
                browserHome();
            }
        }, new BrowserButtonAction(I18n.tr("Forward"), "SWT_BROWSER_NEXT") {
            public void actionPerformed(ActionEvent e) {
                browserForward();
            }
        }, };
        ButtonRow res = new ButtonRow(as, ButtonRow.X_AXIS, ButtonRow.NO_GLUE);
        swingBackAction = as[0];
        swingForwardAction = as[as.length - 1];
        return res;
    }

    /**
     * Pressing the forward button on the browser.
     */
    protected final void browserForward() {
        if (browser != null)
            browser.forward();
    }

    /**
     * Pressing the back button on the browser.
     */
    protected final void browserBack() {
        if (browser != null)
            browser.back();
    }

    /**
     * Pressing the stop button on the browser.
     */
    protected final void browserStop() {
        if (browser != null)
            browser.stop();
    }
    
    /**
     * Pressing the home button on the browser.
     */
    private void browserHome() {
        setURL(getHomeURL());
    }    
    
    private void setURL(final String u) {
        if (browser != null)
            browser.setUrl(u);
    }

    private String getHomeURL() {
        String url = SWTBrowserSettings.BROWSER_HOME_URL.getValue();
        byte[] guid = GuiCoreMediator.getApplicationServices().getMyGUID();
        return LimeWireUtils.addLWInfoToUrl(url, guid);
    }

    public void componentResized(ComponentEvent e) {
        if( swingCanvas != null ) {
            Insets insets = emptyBorder.getBorderInsets();
            swingCanvas.setSize(this.getWidth() - insets.left - insets.right,  
                    this.getHeight() - 2*swingProgressPanel.getPreferredSize().height - 
                    insets.top - insets.bottom - 5);
        }
    }

    public void componentShown(ComponentEvent e) { }    
    public void componentHidden(ComponentEvent e) {} 
    public void componentMoved(ComponentEvent e) { }


    private static abstract class BrowserButtonAction extends AbstractAction {
        BrowserButtonAction(String shortDescriptionKey, String iconName) {

            // We need to preserve the size, if it's not set then the text
            // field grows taller than we want
            putValue(Action.NAME, "");
            
            putValue(Action.SHORT_DESCRIPTION, I18n.tr(shortDescriptionKey));
            putValue(LimeAction.ICON_NAME, iconName);
        }
    }
    
}
