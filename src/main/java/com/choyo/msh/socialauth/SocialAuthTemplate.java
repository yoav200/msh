package com.choyo.msh.socialauth;

import org.brickred.socialauth.SocialAuthManager;

import java.io.Serializable;

public class SocialAuthTemplate implements Serializable {

    private static final long serialVersionUID = -1015105736894158333L;

    private SocialAuthManager socialAuthManager;

    public SocialAuthTemplate() {
    }

    public SocialAuthManager getSocialAuthManager() {
        return this.socialAuthManager;
    }

    public void setSocialAuthManager(SocialAuthManager socialAuthManager) {
        this.socialAuthManager = socialAuthManager;
    }
}