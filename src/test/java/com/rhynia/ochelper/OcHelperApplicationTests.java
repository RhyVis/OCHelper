package com.rhynia.ochelper;

import com.rhynia.ochelper.util.Utilities;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class OcHelperApplicationTests {

	@Test
	void contextLoads() {
		final String[] t = {"99999999999999999999999999999999990", "5555", "88888888888"};
		System.out.println("测试组：" + Arrays.toString(t));
		for (String k : t) {
			String c = Utilities.formatStringByteUnlimited(k);
			System.out.println("数字: " + k + "; 单位: " + c + ";");
		}
	}

}
