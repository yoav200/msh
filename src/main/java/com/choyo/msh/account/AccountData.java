package com.choyo.msh.account;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountData {

    private Long id;

    private String firstName;

    private String lastName;

    private String fullName;

    private String gender;

    private String location;

    private String profileImageUrl;

    private String country;

    private String language;

}
