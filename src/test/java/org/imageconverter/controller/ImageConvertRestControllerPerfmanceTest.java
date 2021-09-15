package org.imageconverter.controller;

import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Value;

public class ImageConvertRestControllerPerfmanceTest {

    @Value("${local.management.port}")
    private int mgt;
    
    @RepeatedTest(3000) // Jconsole or visualVMS
    public void metho() {
	// TODO Auto-generated method stub

    }

}
