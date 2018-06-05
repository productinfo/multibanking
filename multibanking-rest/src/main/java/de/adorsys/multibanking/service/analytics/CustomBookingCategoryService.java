package de.adorsys.multibanking.service.analytics;

import java.util.List;

import org.adorsys.docusafe.business.DocumentSafeService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.adorsys.multibanking.auth.UserContext;
import de.adorsys.multibanking.auth.UserObjectPersistenceService;
import de.adorsys.multibanking.domain.CustomCategoryEntity;
import de.adorsys.multibanking.service.helper.BookingCategoryServiceTemplate;

/**
 * TODO Reset rules provider after every change.
 * 
 * @author fpo 2018-03-24 04:34
 *
 */
@Service
public class CustomBookingCategoryService extends BookingCategoryServiceTemplate<CustomCategoryEntity>{
    private UserObjectPersistenceService uos;
	public CustomBookingCategoryService(UserContext userContext, ObjectMapper objectMapper, DocumentSafeService documentSafeService) {
        this.uos = new UserObjectPersistenceService(userContext, objectMapper, documentSafeService);
    }
	@Override
	protected UserObjectPersistenceService cbs() {
	    return uos;
	}

    protected TypeReference<List<CustomCategoryEntity>> listType(){
		return new TypeReference<List<CustomCategoryEntity>>() {};
	}

}
