package com.limegroup.gnutella.gui.swtbrowser;

import java.awt.Canvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * Constructs a native browser painted on a SWT component and draws it on a AWT Canvas component.
 * SWT provides better support for native browser integration than Swing currently does. 
 */
public class SWTBrowser {
    
    /** The {@link Display} we need to hold onto for the SWT. */
    private final Display display;
    
    /** Need to keep this guy around for {@link #start(String)}. */
    private final Shell shell;
    
    /** The actual SWT {@link Browser} instance. */
    private final Browser browser;
 
    public SWTBrowser(Canvas canvas, int width, int height, LocationListener lis) { 
        try {
            display = new Display(); 
            shell = SWT_AWT.new_Shell(display, canvas);
            shell.setLayout(new GridLayout());        
    
            browser = new Browser(shell, SWT.NULL);
            browser.setLayoutData(new GridData(GridData.FILL_BOTH));
            if (lis != null) {
                browser.addLocationListener(lis);
            }
    
            browser.addTitleListener(new TitleListener() {
                public void changed(TitleEvent event) {
                    shell.setText(event.title);
                }
            });
    
            shell.setSize(width, height);
            shell.open();        
        }
        catch(Throwable e) {
            cleanUp();
            throw new IllegalStateException("Browser could not be initialized", e);
        }
    }
    
    /**
     * Call this to start the browser.
     * 
     * @param url initial URL to which we go
     */
    public void start(String url) {
        setUrl(url);

        while (!shell.isDisposed()) {
            try {
                if (!display.readAndDispatch()) {
                    display.sleep(); // If no more entries in event queue
                }                
            }
            catch(ArrayIndexOutOfBoundsException e) {
                //do nothing, a hack for mouse events on IE5? 
                //  happening on a few Win98 and Win2000 so far
            }
            catch(StackOverflowError e ) {
                // do nothing, when blocking cookies on IE6, cnn
                // was crashing the client. This still shows the overflow
                //  message but keeps the client and browser moving
                //
                // This appears to be a bug in the SWT browser itself
                // and has been reported to IBM as it occurs on all their
                // sample code browsers both in swing and as pure swt apps.
            }
        }   
        

        cleanUp();

        //TODO: display needs to be disposed!! not sure how to safely do this using
        //  the SWT_AWT bridge. This is currently a small memory leak here
    }
    
    /**
     * Cleans up SWT components since they must explicitly be disposed of. 
     */
    public void cleanUp() { 
        //explicity remove components
        if( shell != null )
            shell.dispose();
        if( browser != null)
            browser.dispose();
    }

    /**
     * Send to the browser to <code>u</code>.
     * 
     * @param u URL to which we go
     */
    public final void setUrl(final String u) {
        display.asyncExec(new Runnable() {
            public void run() {
                browser.setUrl(u);
            }
        });
    }

    /**
     * Stops the browser.
     */
    public final void stop() {
        display.asyncExec(new Runnable() {
            public void run() {
                browser.stop();
            }
        });
    }

    /**
     * Send the browser back.
     */    
    public final void back() {
        display.asyncExec(new Runnable() {
            public void run() {
                browser.back();
            }
        });
    }

    /**
     * Send the browser forward.
     */ 
    public void forward() {
        display.asyncExec(new Runnable() {
            public void run() {
                browser.forward();
            }
        });
    }

    /**
     * Adds a {@link ProgressListener} to the {@link Browser}.
     * 
     * @param listener given to the {@link Browser}
     * @see Browser#addProgressListener(ProgressListener)
     */
    public void addProgressListener(ProgressListener listener) {
        browser.addProgressListener(listener);
    }

    /**
     * Adds a {@link StatusTextListener} to the {@link Browser}.
     * 
     * @param listener given to the {@link Browser}
     * @see Browser#addStatusTextListener(StatusTextListener)
     */    
    public void addStatusTextListener(StatusTextListener listener) {
        browser.addStatusTextListener(listener);
    }

    /**
     * Returns <code>true</code> if the {@link Browser} can go back.
     * 
     * @return <code>true</code> if the {@link Browser} can go back
     * @see Browser#isBackEnabled()
     */
    public boolean isBackEnabled() {
        return browser.isBackEnabled();
    }

    /**
     * Returns <code>true</code> if the {@link Browser} can go forward
     * 
     * @return <code>true</code> if the {@link Browser} can go forward
     * @see Browser#isForwardEnabled()
     */    
    public boolean isForwardEnabled() {
        return browser.isForwardEnabled();
    }
}
