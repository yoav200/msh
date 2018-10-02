package com.choyo.msh.web;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.choyo.msh.account.Account;
import com.choyo.msh.account.AccountService;
import com.choyo.msh.messages.AccountBean;
import com.choyo.msh.product.ProductService;

@Controller
public class HomeController {

	@Value("${spring.application.name}")
	private String appName;

	@Autowired
	private AccountService accountService;

	@Autowired
	private ProductService productService;
	
	@GetMapping({ "/" })
	public String home(HttpServletRequest request, Model model) {
		model.addAttribute("appName", appName);
		model.addAttribute("account", getAccountBean(request));
		model.addAttribute("products", productService.getAllProducts());
		return "home";
	}

	@GetMapping({ "/terms" })
	public String terms(Model model) {
		model.addAttribute("appName", appName);
		return "terms";
	}

	@GetMapping({ "/privacy" })
	public String privacy(Model model) {
		model.addAttribute("appName", appName);
		return "privacy";
	}

	private AccountBean getAccountBean(HttpServletRequest request) {
		boolean someAuthority = request.isUserInRole(Account.Role.USER.getAuthority());
		Principal principal = request.getUserPrincipal();
		AccountBean accountBean = new AccountBean();
		if (someAuthority && StringUtils.isNotBlank(principal.getName())) {
			accountBean = new AccountBean(accountService.findAccountByEmail(principal.getName()));
		}
		return accountBean;
	}

}