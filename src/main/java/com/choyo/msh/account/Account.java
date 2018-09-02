package com.choyo.msh.account;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.choyo.msh.account.Account.AccountStatus.PENDING;

@Entity
@Table(name = "account")
@Builder
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@Getter
@ToString
@EqualsAndHashCode
public class Account {

    enum AccountStatus {
        PENDING, VERIFY, DELETED
    }

    public enum Role implements GrantedAuthority {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN");

        private String authority;

        Role(String authority) {
            this.authority = authority;
        }

        @Override
        public String getAuthority() {
            return this.authority;
        }

        public static List<String> supportedRolesAsString() {
            return Stream.of(Role.values()).map(Role::getAuthority).collect(Collectors.toList());
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String password;

    @Column(unique = true)
    private String email;

    private String firstName;

    private String lastName;

    private String fullName;

    private String displayName;

    private String gender;

    private String location;

    private String validatedId;

    private String profileImageUrl;

    private String providerId;

    private String country;

    private String language;

    @Enumerated(EnumType.STRING)
    private AccountStatus status;
}
