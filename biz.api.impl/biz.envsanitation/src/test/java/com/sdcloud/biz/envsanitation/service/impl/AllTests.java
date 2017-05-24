package com.sdcloud.biz.envsanitation.service.impl;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(Suite.class)
@ContextConfiguration("classpath:test-bean-context.xml")
@SuiteClasses({	
//	AssetCarServciceImplTest.class,
	AssetDustbinServciceImplTest.class
	})
public class AllTests {

}
