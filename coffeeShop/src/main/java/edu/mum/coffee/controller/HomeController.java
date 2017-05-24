package edu.mum.coffee.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.mum.coffee.domain.Product;
import edu.mum.coffee.domain.ProductType;
import edu.mum.coffee.domain.Users;
import edu.mum.coffee.service.ProductService;
import edu.mum.coffee.service.UserService;

@Controller
public class HomeController {

	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	@Qualifier("userSignUpValidator")
	private Validator validator;
	
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(value = { "/", "/index", "/home" }, method = RequestMethod.GET)
	public String homePage(HttpServletRequest request,Model model) {
		List<Product> products = productService.getAllProduct();
		Map<ProductType,List<Product>> result = classifyProduct(products);
		model.addAttribute("breakfastProducts", result.get(ProductType.BREAKFAST));
		model.addAttribute("lunchProducts", result.get(ProductType.LUNCH));
		model.addAttribute("dinnerProducts", result.get(ProductType.DINNER));
		return "index";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout, Model model) {

		if (error != null) {
			model.addAttribute("error", "Invalid UserName And PassWord");
		}

		if (logout != null) {
			return "redirect:home";
		}
		return "login";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.GET)
	public String signUp(Model model) {
		model.addAttribute("users", new Users());
		return "signUp";
	}
	
	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String register(@ModelAttribute("users")@Validated Users users, BindingResult result, Model model) {
		if (!result.hasErrors()) {
			userService.save(users);
			return "redirect:/home";
		} else {
			model.addAttribute("users", users);
			return "signUp";
		}
	}

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String accesssDenied(Principal user) {
		return "403";
	}
	
	@RequestMapping(value = "*")
	public String notFoundError(HttpServletRequest request) {
		return "404";
	}
	
	@RequestMapping(value = "/filter", method = RequestMethod.GET)
	public String searchProduct(@RequestParam("info") String query, Model model) {
		List<Product> products = productService.findByTextSearch(query);
		Map<ProductType,List<Product>> result = classifyProduct(products);
		model.addAttribute("breakfastProducts", result.get(ProductType.BREAKFAST));
		model.addAttribute("lunchProducts", result.get(ProductType.LUNCH));
		model.addAttribute("dinnerProducts", result.get(ProductType.DINNER));
		return "home";
	}
	
	private Map<ProductType,List<Product>> classifyProduct(List<Product> products) {
		List<Product> productsBF = new ArrayList<>();
		List<Product> productsLunch = new ArrayList<>();
		List<Product> productsDinner = new ArrayList<>();
		for (Product product : products) {
			if (product.getProductType() == ProductType.BREAKFAST) {
				productsBF.add(product);
			} else if (product.getProductType() == ProductType.LUNCH) {
				productsLunch.add(product);
			} else {
				productsDinner.add(product);
			}
		}
		Map<ProductType, List<Product>> result = new HashMap<>();
		result.put(ProductType.BREAKFAST, productsBF);
		result.put(ProductType.LUNCH, productsLunch);
		result.put(ProductType.DINNER, productsDinner);
		return result;
	}
}
