package com.choyo.msh.messages;

import com.choyo.msh.account.Account;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class AccountBean {

    private Long id;

    private String firstName;

    private String lastName;

    private String displayName;

    private String profileImageUrl;

    public AccountBean() {
        this.id = -1L;
        this.firstName = "Unknown";
        this.lastName = "Friend";
        this.displayName = "Unknown Friend";
        this.profileImageUrl = "";
    }
    public AccountBean(Account account) {
        this.id = account.getId();
        this.firstName = account.getFirstName();
        this.lastName = account.getLastName();
        this.displayName = resolveDisplayName(account);
        this.profileImageUrl = account.getProfileImageUrl();
    }

    private String resolveDisplayName(Account account) {
        if (StringUtils.isNotBlank(account.getDisplayName())) {
            return account.getDisplayName();
        } else if (StringUtils.isNotBlank(account.getFullName())) {
            return account.getFullName();
        } else {
            return account.getFirstName() + " " + account.getLastName();
        }
    }
}
