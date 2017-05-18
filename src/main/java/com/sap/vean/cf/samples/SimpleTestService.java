package com.sap.vean.cf.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sap.vean.cf.samples.model.ResultData;

@Controller
@EnableAutoConfiguration
@RequestMapping("/test")
public class SimpleTestService {

    private static final Logger log = LoggerFactory.getLogger(SimpleTestService.class);

   
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody ResultData getData() {
    	
    	log.debug("Running Service");
    	
    	ResultData rs = new ResultData();
    	rs.setMessage("Cloud Foundry Service on Java using Spring");
    	rs.setSuccess(true);
    	
    	return rs;
     
    }
}
