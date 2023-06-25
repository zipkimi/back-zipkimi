package com.zipkimi.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	public void healthTest() throws Exception {
		// this.mockMvc.perform(get("/health"))
		// 	.andDo(print())
		// 	.andExpect(content().string("hello world! :)"));


		int result = (solution(new int[]{1, 2, 7, 6, 4}));
		System.out.println("***********************");
		assertEquals(4, result);
	}

	public int solution(int[] nums) {
		int answer = 0;
		List<Integer> sums = new ArrayList<>();
		for (int i = 0; i < nums.length; i++) {
			// 3개의 숫자를 더한 값을 소수인지 판별한다. (모든 경우의 수) -> 소수면 answer 값에 + 1 한다.
			for (int j = i + 1; j < nums.length; j++) {
				for (int k = j + 1; k < nums.length; k++) {
					int sum = nums[i] + nums[j] + nums[k];
					System.out.println("sum : " + sum);
					System.out.println("isPrimeNumber(sum) : " + isPrimeNumber(sum));
					System.out.println("-------");
					if (isPrimeNumber(sum))
						answer++;
				}
			}
		}
		return answer;
	}

	public boolean isPrimeNumber(int number) {
		for (int i = 2; i <= Math.sqrt(number); i++) {
			if (number % i == 0) {
				return false;
			}
		}
		return true;
	}
}