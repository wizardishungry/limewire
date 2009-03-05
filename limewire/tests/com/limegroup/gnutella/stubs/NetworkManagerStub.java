package com.limegroup.gnutella.stubs;

import java.io.IOException;

import org.limewire.io.NetworkUtils;
import org.limewire.io.SimpleNetworkInstanceUtils;
import org.limewire.listener.EventListener;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.limegroup.gnutella.GUID;
import com.limegroup.gnutella.NetworkManager;
import com.limegroup.gnutella.NetworkManagerEvent;

@Singleton
public class NetworkManagerStub implements NetworkManager {
    
    public static final Module MODULE = new AbstractModule() {
        protected void configure() {
            bind(NetworkManager.class).to(NetworkManagerStub.class);
        }
    };
    
    private boolean acceptedIncomingConnection;
    private byte[] address = new byte[] { 127, 0, 0, 1 };
    private int port = 5555;
    private GUID solicitedGUID = new GUID();
    private boolean canReceiveSolicited;
    private boolean canReceiveUnsolicited;
    private boolean guessCapable;
    private byte[] externalAddress;
    private boolean canDoFWT;
    private GUID udpConnectBackGUI = new GUID();
    private boolean oobCapable;
    private int stableUDPPort = 7777;
    private int fwtVersion;
    private boolean incomingTLS;
    private boolean outgoingTLS;
    private boolean tls;

    public boolean acceptedIncomingConnection() {
        return acceptedIncomingConnection;
    }
    
    public void setAcceptedIncomingConnection(boolean accepted) {
        this.acceptedIncomingConnection = accepted;
    }

    public boolean addressChanged() {
        // TODO Auto-generated method stub
        return false;
    }

    public void externalAddressChanged() {
        
    }

    public boolean canDoFWT() {
        return canDoFWT;
    }
    
    public void setCanDoFWT(boolean canDoFWT) {
        this.canDoFWT = canDoFWT;
    }

    public boolean canReceiveSolicited() {
        return canReceiveSolicited;
    }

    public boolean canReceiveUnsolicited() {
        return canReceiveUnsolicited;
    }

    public void setAddress(byte[] address) {
        this.address = address;
    }
    
    /**
     * Returns whatever is set in {@link #setAddress(byte[])} by default
     * 127.0.0.1.
     */
    public byte[] getAddress() {
        return address;
    }

    public byte[] getExternalAddress() {
        return externalAddress;
    }

    public byte[] getNonForcedAddress() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getNonForcedPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    /**
     * Returns whatever is set by {@link #setPort(int)}, by default 5555.
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns a random but the same GUID across calls.
     */
    public GUID getUDPConnectBackGUID() {
        return udpConnectBackGUI;
    }
    
    public void setUDPConnectBackGUID(GUID guid) {
        udpConnectBackGUI = guid;
    }

    public boolean incomingStatusChanged() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isGUESSCapable() {
        return guessCapable;
    }

    public boolean isIpPortValid() {
        return (NetworkUtils.isValidAddress(getAddress()) &&
                NetworkUtils.isValidPort(getPort()));
    }
    
    public boolean isOOBCapable() {
        return oobCapable;
    }
    
    public void setOOBCapable(boolean capable) {
        oobCapable = capable;
    }

    public void setListeningPort(int port) throws IOException {
        // TODO Auto-generated method stub
    }

    public int getStableUDPPort() {
        return stableUDPPort;
    }
    
    public void setStableUDPPort(int port) {
        stableUDPPort = port;
    }

    /**
     * Returns whatever is set by {@link #setSolicitedGUID(GUID)}, by default
     * a random GUID that does not change.
     */
    public GUID getSolicitedGUID() {
        return solicitedGUID;
    }
    
    public void setSolicitedGUID(GUID solicitedGUID) {
        this.solicitedGUID = solicitedGUID;
    }

    public int supportsFWTVersion() {
        return fwtVersion;
    }
    
    public void setSupportsFWTVersion(int version) {
        fwtVersion = version;
    }

    public void setCanReceiveSolicited(boolean canReceiveSolicited) {
        this.canReceiveSolicited = canReceiveSolicited;
    }


    public void setCanReceiveUnsolicited(boolean canReceiveUnsolicited) {
        this.canReceiveUnsolicited = canReceiveUnsolicited;
    }

    public void setGuessCapable(boolean guessCapable) {
        this.guessCapable = guessCapable;
    }

    public void setExternalAddress(byte[] externalAddress) {
        this.externalAddress = externalAddress;
    }
    
    public boolean isPrivateAddress(byte[] addr) {
        return new SimpleNetworkInstanceUtils().isPrivateAddress(addr);
    }

    public void start(){}
    public void stop(){}
    public void initialize() {}
    
    public String getServiceName() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isTLSSupported() {
        return !tls;
    }

    public boolean isIncomingTLSEnabled() {
        return incomingTLS;
    }

    public boolean isOutgoingTLSEnabled() {
        return outgoingTLS;
    }

    public void setIncomingTLSEnabled(boolean incomingTLS) {
        this.incomingTLS = incomingTLS;
    }

    public void setOutgoingTLSEnabled(boolean outgoingTLS) {
        this.outgoingTLS = outgoingTLS;
    }

    public void setTls(boolean tls) {
        this.tls = tls;
    }   

    public void portChanged() {
        
    }

    public void acceptedIncomingConnectionChanged() {
        
    }

    public void addListener(EventListener<NetworkManagerEvent> listener) {
    }

    public boolean removeListener(EventListener<NetworkManagerEvent> listener) {
        return false;
    }

}
