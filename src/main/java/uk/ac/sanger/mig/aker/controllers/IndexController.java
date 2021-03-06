package uk.ac.sanger.mig.aker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import uk.ac.sanger.mig.aker.messages.Order;
import uk.ac.sanger.mig.aker.services.WorkOrderService;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author pi1
 * @since February 2015
 */
@Controller
public class IndexController {

	@Autowired
	private WorkOrderService workOrderService;

	@RequestMapping("/")
	public String greeting() {

		return "main/index";
	}

	@RequestMapping("/rabbit")
	@ResponseBody
	public String rabbit() throws InterruptedException, JsonProcessingException {

		final Order test = new Order("Hello from RabbitMQ!");
		final Order test2 = new Order("Hello from RabbitMQ! 2");
		final Order test3 = new Order("Hello from RabbitMQ! 3");

		workOrderService.sendOrder(test);
		workOrderService.sendOrder(test2);
		workOrderService.sendOrder(test3);

		return "Rabbit";
	}

	@RequestMapping("/login")
	public String login() {
		return "login";
	}

}
