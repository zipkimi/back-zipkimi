package com.safeinterior.board.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc

class TestControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	public void health() throws Exception {
		this.mockMvc.perform(get("/health/promise"))
			.andDo(print())
			.andExpect(content().string("hello promise world! :)"));
	}
}