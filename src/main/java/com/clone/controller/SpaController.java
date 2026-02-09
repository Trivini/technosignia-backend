package com.clone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

  @RequestMapping(value = {
      "/",
      "/admin/**",
      "/jobs/**",
      "/student/**",
      "/about",
      "/courses/**",
      "/placement"
  })
  public String forward() {
    return "forward:/index.html";
  }
}

