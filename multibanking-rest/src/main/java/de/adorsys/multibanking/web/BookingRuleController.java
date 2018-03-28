package de.adorsys.multibanking.web;

import java.io.IOException;
import java.util.List;

import org.adorsys.docusafe.business.types.complex.DSDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.domain.CustomRuleEntity;
import de.adorsys.multibanking.domain.RuleEntity;
import de.adorsys.multibanking.exception.InvalidRulesException;
import de.adorsys.multibanking.service.CustomBookingRuleService;
import de.adorsys.multibanking.service.SystemBookingRuleService;
import de.adorsys.multibanking.web.common.BaseController;

/**
 * @author alexg on 07.02.17.
 * @author fpo 2018-03-20 11:47
 */
@UserResource
@RestController
@RequestMapping(path = "api/v1/analytics/rules")
public class BookingRuleController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(BookingRuleController.class);
    private static final ObjectMapper YAML_OBJECT_MAPPER = yamlObjectMapper();

    @Autowired
    private CustomBookingRuleService bookingRuleService;
    @Autowired
    private SystemBookingRuleService systemBookingRuleService;

    @RequestMapping(method = RequestMethod.POST)
    public HttpEntity<Void> createRule(@RequestBody CustomRuleEntity ruleEntity) {
        bookingRuleService.createOrUpdateCustomRule(ruleEntity);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/custom", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> getCustomRules() {
        DSDocument dsDocument = bookingRuleService.getCustomBookingRules();
    	return loadBytesForWeb(dsDocument);
    }

    @RequestMapping(value = "/static", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<ByteArrayResource> getStaticRules() {
        DSDocument dsDocument = systemBookingRuleService.getStaticBookingRules();
    	return loadBytesForWeb(dsDocument);
    }

    @RequestMapping(value = "/custom/{ruleId}", method = RequestMethod.PUT)
    public HttpEntity<Void> updateCustomRule(@PathVariable String ruleId, @RequestBody CustomRuleEntity ruleEntity) {
    	ruleEntity.setId(ruleId);
    	bookingRuleService.createOrUpdateCustomRule(ruleEntity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/static/{ruleId}", method = RequestMethod.PUT)
    public HttpEntity<Void> updateRule(@PathVariable String ruleId, @RequestBody RuleEntity ruleEntity) {
    	ruleEntity.setId(ruleId);
    	systemBookingRuleService.createOrUpdateStaticRule(ruleEntity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/custom", method = RequestMethod.PUT)
    public HttpEntity<Void> createOrUpdateCustomRules(@RequestBody List<CustomRuleEntity> ruleEntities) {
    	bookingRuleService.createOrUpdateCustomRules(ruleEntities);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/static", method = RequestMethod.PUT)
    public HttpEntity<Void> createOrUpdateStaticRules(@RequestBody List<RuleEntity> ruleEntities) {
    	systemBookingRuleService.createOrUpdateStaticRules(ruleEntities);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(path = "/custom/upload", method = RequestMethod.PUT, consumes=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<?> uploadReplaceCustomRules(@RequestParam MultipartFile rulesFile) {
    	
        if (!rulesFile.isEmpty())return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("File is empty");

        try {
            List<CustomRuleEntity> rulesEntities = YAML_OBJECT_MAPPER.readValue(rulesFile.getInputStream(), new TypeReference<List<CustomRuleEntity>>() {});
            bookingRuleService.replceCustomRules(rulesEntities);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            throw new InvalidRulesException(e.getMessage());
        }
    }

    @RequestMapping(path = "/static/upload", method = RequestMethod.PUT, consumes=MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public HttpEntity<?> uploadReplaceStaticRules(@RequestParam MultipartFile rulesFile) {
    	
        if (!rulesFile.isEmpty())return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("File is empty");

        try {
            List<RuleEntity> rulesEntities = YAML_OBJECT_MAPPER.readValue(rulesFile.getInputStream(), new TypeReference<List<RuleEntity>>() {});
            systemBookingRuleService.replceStaticRules(rulesEntities);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            throw new InvalidRulesException(e.getMessage());
        }
    }

    @RequestMapping(value = "/custom/{ruleId}", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteCustomRule(@PathVariable String ruleId) {
        bookingRuleService.deleteCustomRule(ruleId);
        log.info("Rule [{}] deleted.", ruleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @RequestMapping(value = "/static/{ruleId}", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteStaticRule(@PathVariable String ruleId) {
    	systemBookingRuleService.deleteStaticRule(ruleId);
        log.info("Rule [{}] deleted.", ruleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/custom", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteCustomRules(@PathVariable List<String> ruleIds) {
        bookingRuleService.deleteCustomRules(ruleIds);
        log.info("Rule [{}] deleted.", ruleIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @RequestMapping(value = "/static", method = RequestMethod.DELETE)
    public HttpEntity<Void> deleteStaticRules(@PathVariable List<String> ruleIds) {
    	systemBookingRuleService.deleteStaticRules(ruleIds);
        log.info("Rule [{}] deleted.", ruleIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}