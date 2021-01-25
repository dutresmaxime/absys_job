package com.absys.test.controller;

import com.absys.test.model.User;
import com.absys.test.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	UserService userService;
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public User register(@RequestBody User user) {
		return userService.createUser(user);
	}

	@GetMapping("/")
	public List<User> findAll() {
		return userService.findAll();
	}

	@GetMapping("/workflow/{userid}")
	public User workflow(@PathVariable("userid") String userId) {
		return userService.workflow(userId);
	}

	@GetMapping("/byJobThenCountry")
	public Object findByJobThenCountry() {
		return userService.findByJobThenCountry();
	}

	@PostMapping("/login")
	public User login(@RequestBody String id) {
		return userService.login(id);
	}
}
