package com.choyo.msh.account;

import org.apache.commons.lang3.StringUtils;
import org.brickred.socialauth.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Service("MshAccountService")
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException(email);
        }

        return new User(
                email,
                randomAlphabetic(8),
                true,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(Account.Role.USER.getAuthority())));
    }

    private Account signup(Profile userProfile) {
        Account account = Account.builder()
                .email(userProfile.getEmail())
                .password(randomAlphabetic(8))
                .firstName(userProfile.getFirstName())
                .lastName(userProfile.getLastName())
                .fullName(userProfile.getFullName())
                .displayName(userProfile.getDisplayName())
                .gender(userProfile.getGender())
                .location(userProfile.getLocation())
                .validatedId(userProfile.getValidatedId())
                .profileImageUrl(userProfile.getProfileImageURL())
                .providerId(userProfile.getProviderId())
                .country(userProfile.getCountry())
                .status(Account.AccountStatus.VERIFY)
                .language(userProfile.getLanguage()).build();
        return accountRepository.save(account);
    }

    public void registerWithProfile(Profile profile) throws EmailAlreadyInUseException, MissingEmailProperty {
        if(StringUtils.isBlank(profile.getEmail())) {
            throw new MissingEmailProperty();
        }
        Account account = accountRepository.findByEmail(profile.getEmail());
        // already exists - check provider
        if (account != null && !account.getProviderId().equals(profile.getProviderId())) {
            throw new EmailAlreadyInUseException(account.getEmail());
        }
        // not exists - create account
        if (account == null) {
            account = signup(profile);
        }
        // set in context
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        account.getEmail(),
                        account.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority(Account.Role.USER.getAuthority()))));
    }

    public Account findAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }
}
