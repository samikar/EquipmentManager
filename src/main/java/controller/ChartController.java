package controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChartController {
	 @RequestMapping("/rest/chartTest")
	    public String hello(@RequestParam(value="name", defaultValue="World") String name) {
	        return "{\"id\":\"hello\"}";
	    }
}
