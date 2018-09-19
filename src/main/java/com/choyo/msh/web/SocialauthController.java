package com.choyo.msh.web;

import com.choyo.msh.account.AccountService;
import com.choyo.msh.messages.MessageBean;
import com.choyo.msh.messages.MessagesManager;
import com.choyo.msh.socialauth.SocialAuthTemplate;
import com.choyo.msh.socialauth.SocialauthControllerConfig;
import lombok.extern.slf4j.Slf4j;
import org.brickred.socialauth.AuthProvider;
import org.brickred.socialauth.SocialAuthManager;
import org.brickred.socialauth.util.SocialAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Slf4j
@Controller
@RequestMapping({"/socialauth"})
public class SocialauthController {

    @Autowired
    private SocialauthControllerConfig config;

    @Autowired
    private SocialAuthTemplate socialAuthTemplate;

    @Autowired
    private SocialAuthManager socialAuthManager;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessagesManager messagesManager;

    // ===========  signin request

    @RequestMapping(params = {"id"})
    public String connect(@RequestParam("id") String providerId, HttpServletRequest request) throws Exception {
        log.debug("Getting Authentication URL for :" + providerId);
        String callbackURL = config.getBaseCallbackUrl() + request.getServletPath();
        String url = this.socialAuthManager.getAuthenticationUrl(providerId, callbackURL);
        if (callbackURL.equals(url)) {
            url = config.getSuccessPageUrl();
            this.socialAuthManager.connect(new HashMap<>());
        }
        this.socialAuthTemplate.setSocialAuthManager(this.socialAuthManager);
        return "redirect:" + url;
    }

    // ===========  approve callback

    @RequestMapping(params = {"oauth_token"})
    public String oauthCallback(HttpServletRequest request) {
        return this.approveCallback(request);
    }

    @RequestMapping(params = {"code"})
    public String oauth2Callback(HttpServletRequest request) {
        return this.approveCallback(request);
    }

    @RequestMapping(params = {"openid.claimed_id"})
    public String openidCallback(HttpServletRequest request) {
        return this.approveCallback(request);
    }

    // ===========  Cancel callback

    @RequestMapping(params = {"error", "error_reason"})
    public String fbCancel(@RequestParam("error_reason") String error) {
        log.warn("Facebook send an error : " + error);
        return cancelCallback(error);
    }

    @RequestMapping(params = {"error_code", "error_message"})
    public String fbCancel2(@RequestParam("error_code") String errorCode, @RequestParam("error_message") String error) {
        log.warn("Facebook send an error_code : " + errorCode + ", message: " + error);
        return cancelCallback(error);
    }

    @RequestMapping(params = {"openid.mode=cancel"})
    public String googleCancel(@RequestParam("openid.mode") String error) {
        log.warn("Google send an error : " + error);
        return cancelCallback(error);
    }


    private String approveCallback(HttpServletRequest request) {
        SocialAuthManager m = this.socialAuthTemplate.getSocialAuthManager();
        if (m != null) {
            try {
                AuthProvider provider = m.connect(SocialAuthUtil.getRequestParametersMap(request));
                log.debug("Connected Provider : " + provider.getProviderId());
                accountService.registerWithProfile(provider.getUserProfile());
                messagesManager.addMessage(MessageBean.builder()
                        .type(MessageBean.MessageType.SIGIN)
                        .message("Welcome to the Game!")
                        .build());
            } catch (Exception e) {
                log.error("error during signin", e);
                messagesManager.addMessage(MessageBean.builder()
                        .type(MessageBean.MessageType.ERROR)
                        .message("Signin fail with the following error: " + e.getMessage())
                        .build());
            }
        } else {
            log.warn("Unable to connect provider because SocialAuthManager object is null.");
        }
        return "redirect:/" + config.getSuccessPageUrl();
    }


    private String cancelCallback(String error) {
        messagesManager.addMessage(MessageBean.builder()
                .type(MessageBean.MessageType.ERROR)
                .message("Siginin fail: " + error)
                .build());
        return "redirect:/" + config.getAccessDeniedPageUrl();
    }
}
