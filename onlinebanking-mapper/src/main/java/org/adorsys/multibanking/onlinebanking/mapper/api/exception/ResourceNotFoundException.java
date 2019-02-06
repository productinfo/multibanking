package org.adorsys.multibanking.onlinebanking.mapper.api.exception;


public class ResourceNotFoundException extends ParametrizedMessageException {
	private static final long serialVersionUID = -1836646959951727323L;
	public static final String MESSAGE_KEY = "resource.not.found";
	public static final String RENDERED_MESSAGE_KEY = "Resource [{0}] with id [{1}] not found.";
	public static final String MESSAGE_DOC = MESSAGE_KEY + ": Resource with provided id does not exist";
	public ResourceNotFoundException(Class<?> resourceClazz, String businessKey) {
        super(String.format(RENDERED_MESSAGE_KEY, resourceClazz.getSimpleName(), businessKey));
        this.addParam("0_ressource", resourceClazz.getSimpleName());
        this.addParam("1_businessKey", businessKey);
    }
}
