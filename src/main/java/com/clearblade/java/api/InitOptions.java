package com.clearblade.java.api;

import com.clearblade.java.api.auth.AnonAuth;
import com.clearblade.java.api.auth.Auth;
import com.clearblade.java.api.auth.UserAuth;

/**
 * InitOptions is used for customizing ClearBlade initialization. The following values are used by default:
 *
 *   platformUrl - https://platform.clearblade.com
 *   messagingUrl - tcp://messaging.clearblade.com:1883
 *   email - [EMPTY] (Deprecated, use {@link #auth()} instead)
 *   password - [EMPTY] (Deprecated, use {@link #auth()} instead)
 *   auth - {@link com.clearblade.java.api.auth.AnonAuth}
 *   enableLogging - false
 *   callTimeout - false
 *   allowUntrusted - false
 */
public class InitOptions {

    private String _platformUrl;
    private String _messagingUrl;

    private String _email;
    private String _password;
    private Auth _auth;

    private boolean _enableLogging;
    private int _callTimeout;
    private boolean _allowUntrusted;

    public InitOptions() {
        this._platformUrl = "https://platform.clearblade.com";
        this._messagingUrl = "tcp://messaging.clearblade.com:1883";

        this._email = "";
        this._password = "";
        this._auth = new AnonAuth();

        this._enableLogging = false;
        this._callTimeout = 30000;
        this._allowUntrusted = false;
    }

    // Getters

    public String platformUrl() {
        return this._platformUrl;
    }

    public String messagingUrl() {
        return this._messagingUrl;
    }

    public Auth auth() {
        return this._auth;
    }

    public boolean enableLogging() {
        return this._enableLogging;
    }

    public int callTimeout() {
        return this._callTimeout;
    }

    public boolean allowUntrusted() {
        return this._allowUntrusted;
    }

    // Setters

    public InitOptions setPlatformUrl(String platformUrl) {
        this._platformUrl = platformUrl;
        return this;
    }

    public InitOptions setMessagingUrl(String messagingUrl) {
        this._messagingUrl = messagingUrl;
        return this;
    }

    /**
     * @deprecated use {@link #setAuth(Auth)} with {@link com.clearblade.java.api.auth.UserAuth} instead.
     */
    @Deprecated
    public InitOptions setEmail(String email) {
        this._email = email;
        this._auth = new UserAuth(this._email, this._password);
        return this;
    }

    /**
     * @deprecated use {@link #setAuth(Auth)} with {@link com.clearblade.java.api.auth.UserAuth} instead.
     */
    @Deprecated
    public InitOptions setPassword(String password) {
        this._password = password;
        this._auth = new UserAuth(this._email, this._password);
        return this;
    }

    public InitOptions setAuth(Auth auth) {
        this._auth = auth;
        return this;
    }

    public InitOptions setEnableLogging() {
        this._enableLogging = true;
        return this;
    }

    public InitOptions setEnableLogging(boolean isEnabled) {
        this._enableLogging = isEnabled;
        return this;
    }

    public InitOptions setCallTimeout(int timeout) {
        this._callTimeout = timeout;
        return this;
    }

    public InitOptions setAllowUntrusted() {
        this._allowUntrusted = true;
        return this;
    }

    public InitOptions setAllowUntrusted(boolean allow) {
        this._allowUntrusted = allow;
        return this;
    }
}
