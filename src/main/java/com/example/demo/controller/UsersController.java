package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entities.Song;
import com.example.demo.entities.Users;
import com.example.demo.services.SongService;
import com.example.demo.services.UsersService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UsersController {
	@Autowired
	UsersService service;
	@Autowired
	SongService songService;
	@PostMapping("/register")
	public String addUsers(@ModelAttribute Users user) {
		boolean userStatus=service.emailExists(user.getEmail());
		if(userStatus==false) {
			service.addUsers(user);
			System.out.println("User added.");
		}
		else {
			System.out.println("user already exists");
		}
		return "home";
	}

	@PostMapping("/validate")
	public String validate(@RequestParam("email") String email,@RequestParam("password") String password,HttpSession session,Model model) {
		if(service.emailExists(email)) {
			if(service.validateUser(email,password)==true) {
				String role=service.getRole(email);
				
				session.setAttribute("email", email);
				
				if(role.equals("admin")) {
					return "adminHome";
				}else {
					Users u =service.getUser(email);
					if(u.isPremium()==false) {
					return "customerHome";
					}else {
						List<Song> songsList = songService.fetchAllSongs();
						model.addAttribute("songs", songsList);
						return "customerHomePremium";
					}
				}
			}
		}
		else {
			return "login";
		}
		return "login";
	}
	
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		
		session.invalidate();
		
		return "login";
	}
	
	
}
