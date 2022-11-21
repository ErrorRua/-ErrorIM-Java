package com.errorim.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ErrorController {
	@RequestMapping("/error/exthrow")
	public void rethrow(HttpServletRequest request) throws Exception {
		throw (Exception) request.getAttribute("filter.error");
	}
}
