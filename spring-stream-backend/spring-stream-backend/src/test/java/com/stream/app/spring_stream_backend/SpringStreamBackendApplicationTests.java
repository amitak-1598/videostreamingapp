package com.stream.app.spring_stream_backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.stream.app.services.VideoService;

@SpringBootTest
class SpringStreamBackendApplicationTests {

	@Autowired
	VideoService videoservice;
	
	@Test
	void contextLoads() {
		videoservice.processVideo("4392e646-3668-48a4-a626-9286e72baa1c");
	}

}
