package com.choyo.msh.web;

import com.choyo.msh.account.Account;
import com.choyo.msh.account.AccountData;
import com.choyo.msh.account.AccountService;
import com.choyo.msh.messages.MessagesManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private MessagesManager messagesManager;

    @Autowired
    private AccountService accountService;

    @Value("${spring.application.name}")
    private String appName;


    @GetMapping({"/", "/home"})
    public String home(Model model) {
        AccountData accountData = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Account account = accountService.findAccountByEmail(authentication.getName());
            if (account != null) {
                accountData = AccountData.builder()
                        .id(account.getId())
                        .firstName(account.getFirstName())
                        .lastName(account.getLastName())
                        .fullName(StringUtils.isNotBlank(account.getDisplayName()) ? account.getDisplayName() : account.getFullName())
                        .gender(account.getGender())
                        .location(account.getLocation())
                        .profileImageUrl(account.getProfileImageUrl())
                        .country(account.getCountry())
                        .language(account.getLanguage())
                        .build();

            }
        }

        model.addAttribute("appName", appName);
        model.addAttribute("accountData", accountData);
        model.addAttribute("message", messagesManager.getAllMessage());
        return "home";
    }

}