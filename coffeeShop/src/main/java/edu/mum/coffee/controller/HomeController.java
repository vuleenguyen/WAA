package edu.mum.coffee.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import edu.mum.coffee.domain.Product;
import edu.mum.coffee.domain.ProductType;
import edu.mum.coffee.service.ProductService;

@Controller
public class HomeController {

	@Autowired
	private ProductService productService;

	@RequestMapping(value = { "/", "/index", "/home" }, method = RequestMethod.GET)
	public String homePage(Model model) {
		List<Product> products = productService.getAllProduct();
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
		model.addAttribute("breakfastProducts", productsBF);
		model.addAttribute("lunchProducts", productsLunch);
		model.addAttribute("dinnerProducts", productsDinner);
		return "home";
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

	@RequestMapping(value = "/403", method = RequestMethod.GET)
	public String accesssDenied(Principal user) {
		return "403";
	}
}
