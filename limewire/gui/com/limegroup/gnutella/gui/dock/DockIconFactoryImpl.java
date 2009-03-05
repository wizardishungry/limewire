package com.limegroup.gnutella.gui.dock;

import org.limewire.util.OSUtils;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Creates a DockIcon instance for the target
 * platform.
 *
 */
@Singleton
public class DockIconFactoryImpl implements DockIconFactory
{
    @Inject
    public DockIconFactoryImpl () {
        
    }
    
    public DockIcon createDockIcon () {
        if (OSUtils.isMacOSX())
            return new DockIconMacOSXImpl();
        else
            return new DockIconNoOpImpl();
    }
}
