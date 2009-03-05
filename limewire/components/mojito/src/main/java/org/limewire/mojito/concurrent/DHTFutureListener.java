/*
 * Mojito Distributed Hash Table (Mojito DHT)
 * Copyright (C) 2006-2007 LimeWire LLC
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.limewire.mojito.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;


/**
 * The listener interface for receiving DHTFuture notifications.
 * <p>
 * Note: There is no guarantee any of the methods is called if the
 * task does not finish.
 * </p>
 */
public interface DHTFutureListener<T> {
    
    /**
     * Called if a task finished with the given result
     */
    public void handleFutureSuccess(T result);
    
    /**
     * Called if a task finished with an ExecutionException
     */
    public void handleExecutionException(ExecutionException e);
    
    /**
     * Called if a task was cancelled
     */
    public void handleCancellationException(CancellationException e);
    
    /**
     * Called if a task was interrupted
     */
    public void handleInterruptedException(InterruptedException e);
}
