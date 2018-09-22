package com.choyo.msh.web;

import com.choyo.msh.account.Account;
import com.choyo.msh.account.AccountService;
import com.choyo.msh.messages.AccountBean;
import com.choyo.msh.messages.MessagesManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class MessagesController {

    private final AccountService accountService;

    private final MessagesManager messagesManager;

    @Autowired
    public MessagesController(AccountService accountService, MessagesManager messagesManager) {
        this.accountService = accountService;
        this.messagesManager = messagesManager;
    }

    @GetMapping("/messages")
    public String getMessages(HttpServletRequest request, Model model) {
        boolean someAuthority = request.isUserInRole(Account.Role.USER.getAuthority());
        Principal principal = request.getUserPrincipal();
        AccountBean accountBean = new AccountBean();
        if (someAuthority && StringUtils.isNotBlank(principal.getName())) {
            accountBean = new AccountBean(accountService.findAccountByEmail(principal.getName()));
        }

        MessagesManager.AccountMessages messages = MessagesManager.AccountMessages.builder()
                .account(accountBean)
                .messages(messagesManager.getAllMessage()).build();

        model.addAttribute("messages", messages);
        return "fragments/messages :: messageContent";
    }
}
