package com.testswitch_api.testswitchapi

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestswitchApiApplicationTests {

	@Test
	fun contextLoads() {
	}

	@Test
	fun testTrialFunction() {
		assertEquals(8, test(4))
	}
}
