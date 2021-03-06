package com.recon.web;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recon.entity.TrainingDetails;
import com.recon.service.TrainingService;
import com.recon.service.UserService;
import com.recon.util.CustomErrorType;

import javassist.NotFoundException;

@CrossOrigin
@RestController
@RequestMapping("/training")
@PropertySource("classpath:errorcodes.properties")
public class TrainingRestController {

	private Logger logger = LoggerFactory.getLogger("myresume");

	@Autowired
	private TrainingService trainingService;

	@Autowired
	private UserService userservice;

	@Value("${unauthorizedDeleteErrorCode}")
	private String unauthorizedDeleteErrorCode;

	@Value("${unauthorizedDeleteErrorMessage}")
	private String unauthorizedDeleteErrorMessage;

	@Value("${invalidDataErrorCode}")
	private String invalidDataErrorCode;

	@Value("${invalidDataErrorMessage}")
	private String invalidDataErrorMessage;

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public List<TrainingDetails> getTrainingDetails(@RequestParam(value = "username") String username) {
		logger.debug("inside get training details for {}", username);
		return trainingService.getTrainingDetailsByUser(username);

	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<?> addTrainingDetails(@RequestBody TrainingDetails trainingdetails) {
		try {
			logger.info("Inside addTrainingDetails");
			trainingService.addTrainingDetails(trainingdetails);
			return new ResponseEntity<>(trainingdetails, HttpStatus.CREATED);
		} catch (RuntimeException e) {
			logger.debug(e.getMessage());
			return new ResponseEntity<>(new CustomErrorType(invalidDataErrorCode, invalidDataErrorMessage),
					HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public ResponseEntity<?> updateTrainingDetails(@RequestBody TrainingDetails training) {
		logger.info("inside update training details");
		try {
			return new ResponseEntity<>(trainingService.updateTrainingDetails(training), HttpStatus.CREATED);
		} catch (RuntimeException | NotFoundException e) {
			logger.debug(e.getMessage());
			return new ResponseEntity<>(new CustomErrorType(invalidDataErrorCode, invalidDataErrorMessage),
					HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "delete/{trainingId}", method = RequestMethod.GET)
	public ResponseEntity<?> removeEducationdetails(@PathVariable("trainingId") Long trainingId,
			HttpServletResponse response) {
		logger.debug("inside remove training: {}", trainingId);
		String result = null;
		if (trainingService.findbyTrainingIDandUsername(trainingId,
				userservice.getCurrentUser().getUserName()) == null) {
			return new ResponseEntity<>(
					new CustomErrorType(unauthorizedDeleteErrorCode, unauthorizedDeleteErrorMessage),
					HttpStatus.UNAUTHORIZED);
		}
		try {
			trainingService.removeTrainingDetails(trainingId);
			result = "{\"deleted\":true}";
			return ResponseEntity.status(HttpStatus.OK).body(result);
		} catch (RuntimeException e) {
			logger.debug("EXCEPTION: {}", e.getMessage());
			result = "{\"deleted\":false}";
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
		}

	}
}
