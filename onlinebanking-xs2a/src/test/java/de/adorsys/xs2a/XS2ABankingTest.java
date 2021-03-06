package de.adorsys.xs2a;

import de.adorsys.psd2.client.ApiClient;
import de.adorsys.psd2.client.ApiException;
import de.adorsys.psd2.client.api.PaymentInitiationServicePisApi;
import de.adorsys.psd2.client.model.*;
import de.adorsys.xs2a.error.XS2AClientException;
import de.adorsys.xs2a.executor.ConsentUpdateRequestExecutor;
import de.adorsys.xs2a.executor.ConsentUpdateRequestExecutorTest;
import de.adorsys.xs2a.executor.PaymentUpdateRequestExecutor;
import de.adorsys.xs2a.executor.UpdateRequestExecutor;
import de.adorsys.xs2a.model.ConsentXS2AUpdateRequest;
import de.adorsys.xs2a.model.XS2AUpdateRequest;
import de.adorsys.xs2a.model.Xs2aTanSubmit;
import domain.*;
import domain.request.AuthenticatePsuRequest;
import domain.request.LoadAccountInformationRequest;
import domain.request.SubmitAuthorizationCodeRequest;
import domain.request.TransactionRequest;
import domain.response.AuthorisationCodeResponse;
import domain.response.InitiatePaymentResponse;
import domain.response.LoadAccountInformationResponse;
import domain.response.ScaMethodsResponse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.adorsys.xs2a.XS2ABanking.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class XS2ABankingTest {

    private static final String SCA_NAME_VALUE = "Photo Tan";
    private static final String SCA_AUTHENTICATION_VERSION_VALUE = "v1.0";
    private static final String SCA_EXPLANATION_VALUE = "some explanation";
    private static final String SCA_METHOD_ID_VALUE = "111";
    private static final String BANKING_URL = "bankingUrl";
    private static final String IBAN = "iban";
    private static final String AUTHORISATION_ID = "xs2a-authorisationId";
    private static final String PAYMENT_ID = "paymentId";
    private static final String PSU_ID = "login";
    private static final String CORPORATE_ID = "custId";
    private static final String PIN = "pin";
    public static final String TAN = "tan";

    private XS2ABanking xs2aBanking;

    @Mock
    private ApiClient apiClient;

    @Mock
    private PaymentInitiationServicePisApi paymentInitiationServicePisApi;

    @Mock
    private UpdateRequestExecutor executor;

    @Before
    public void setUp() {
        xs2aBanking = new XS2ABanking() {

            @Override
            ApiClient createApiClient(String bankingUrl) {
                return apiClient;
            }

            @Override
            PaymentInitiationServicePisApi createPaymentInitiationServicePisApi(ApiClient apiClient) {
                return paymentInitiationServicePisApi;
            }

            @Override
            UpdateRequestExecutor createUpdateRequestExecutor(SubmitAuthorizationCodeRequest submitPaymentRequest) {
                return executor;
            }
        };
    }

    @Test
    public void authenticatePsu() throws ApiException {
        AuthenticatePsuRequest request = AuthenticatePsuRequest.builder()
                                                 .bankCode("08098")
                                                 .customerId(CORPORATE_ID)
                                                 .login(PSU_ID)
                                                 .paymentId(PAYMENT_ID)
                                                 .pin(PIN)
                                                 .build();

        StartScaprocessResponse scaProcessResponse = new StartScaprocessResponse();
        Map<String, String> links = new HashMap<>();
        links.put("startAuthorisationWithPsuAuthentication", BANKING_URL + "/" + AUTHORISATION_ID);
        scaProcessResponse.setLinks(links);
        ArgumentCaptor<UpdatePsuAuthentication> psuBodyCaptor = ArgumentCaptor.forClass(UpdatePsuAuthentication.class);

        when(paymentInitiationServicePisApi.startPaymentAuthorisation(eq(SINGLE_PAYMENT_SERVICE), eq(SEPA_CREDIT_TRANSFERS),
                                                                      eq(PAYMENT_ID), any(), eq(PSU_ID), isNull(), isNull(),
                                                                      isNull(), isNull(), isNull(),
                                                                      isNull(), eq(PSU_IP_ADDRESS),
                                                                      isNull(), isNull(),
                                                                      isNull(), isNull(),
                                                                      isNull(), isNull(),
                                                                      isNull(), isNull(), isNull())
        ).thenReturn(scaProcessResponse);

        when(paymentInitiationServicePisApi.updatePaymentPsuData(eq(SINGLE_PAYMENT_SERVICE), eq(SEPA_CREDIT_TRANSFERS), eq(PAYMENT_ID),
                                                                 eq(AUTHORISATION_ID), any(), psuBodyCaptor.capture(),
                                                                 isNull(), isNull(), isNull(),
                                                                 eq(PSU_ID), isNull(), eq(CORPORATE_ID),
                                                                 isNull(), eq(PSU_IP_ADDRESS), isNull(),
                                                                 isNull(), isNull(), isNull(),
                                                                 isNull(), isNull(),
                                                                 isNull(), isNull(), isNull())
        ).thenReturn(buildUpdatePsuDataResponse());

        ScaMethodsResponse response = xs2aBanking.authenticatePsu(BANKING_URL, request);

        verify(paymentInitiationServicePisApi, times(1)).startPaymentAuthorisation(eq(SINGLE_PAYMENT_SERVICE), eq(SEPA_CREDIT_TRANSFERS),
                                                                                   eq(PAYMENT_ID), any(), eq(PSU_ID), isNull(), isNull(),
                                                                                   isNull(), isNull(), isNull(),
                                                                                   isNull(), eq(PSU_IP_ADDRESS),
                                                                                   isNull(), isNull(),
                                                                                   isNull(), isNull(),
                                                                                   isNull(), isNull(),
                                                                                   isNull(), isNull(), isNull());

        verify(paymentInitiationServicePisApi, times(1)).updatePaymentPsuData(eq(SINGLE_PAYMENT_SERVICE), eq(SEPA_CREDIT_TRANSFERS), eq(PAYMENT_ID),
                                                                              eq(AUTHORISATION_ID), any(), psuBodyCaptor.capture(),
                                                                              isNull(), isNull(), isNull(),
                                                                              eq(PSU_ID), isNull(), eq(CORPORATE_ID),
                                                                              isNull(), eq(PSU_IP_ADDRESS), isNull(),
                                                                              isNull(), isNull(), isNull(),
                                                                              isNull(), isNull(),
                                                                              isNull(), isNull(), isNull());

        assertThat(response.getTanTransportTypes()).hasSize(1);
        assertThat(response.getAuthorizationId()).isEqualTo(AUTHORISATION_ID);

        TanTransportType tanTransportType = response.getTanTransportTypes().get(0);

        assertThat(SCA_METHOD_ID_VALUE).isEqualTo(tanTransportType.getId());
        assertThat(SCA_AUTHENTICATION_VERSION_VALUE).isEqualTo(tanTransportType.getMedium());
        assertThat(SCA_NAME_VALUE).isEqualTo(tanTransportType.getName());
        assertThat(SCA_EXPLANATION_VALUE).isEqualTo(tanTransportType.getInputInfo());

        UpdatePsuAuthentication psuAuthentication = psuBodyCaptor.getValue();

        assertThat(psuAuthentication.getPsuData().getPassword()).isEqualTo(PIN);
    }

    private Map<String, Object> buildUpdatePsuDataResponse() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> methods = new ArrayList<>();
        Map<String, String> method = new HashMap<>();
        method.put(SCA_AUTHENTICATION_METHOD_ID, SCA_METHOD_ID_VALUE);
        method.put(SCA_NAME, SCA_NAME_VALUE);
        method.put(SCA_AUTHENTICATION_VERSION, SCA_AUTHENTICATION_VERSION_VALUE);
        method.put(SCA_EXPLANATION, SCA_EXPLANATION_VALUE);
        methods.add(method);
        response.put(SCA_METHODS, methods);
        return response;
    }

    @Test(expected = XS2AClientException.class)
    public void authorisePsuWithException() throws ApiException {
        String paymentId = "paymentId";
        String psuId = "login";
        String custId = "custId";
        String pin = "pin";

        AuthenticatePsuRequest request = AuthenticatePsuRequest.builder()
                                                 .bankCode("08098")
                                                 .customerId(custId)
                                                 .login(psuId)
                                                 .paymentId(paymentId)
                                                 .pin(pin)
                                                 .build();

        when(paymentInitiationServicePisApi.startPaymentAuthorisation(eq(SINGLE_PAYMENT_SERVICE), eq(SEPA_CREDIT_TRANSFERS),
                                                                      eq(paymentId), any(), eq(psuId), isNull(), isNull(),
                                                                      isNull(), isNull(), isNull(),
                                                                      isNull(), eq(PSU_IP_ADDRESS),
                                                                      isNull(), isNull(),
                                                                      isNull(), isNull(),
                                                                      isNull(), isNull(),
                                                                      isNull(), isNull(), isNull())).thenThrow(ApiException.class);

        xs2aBanking.authenticatePsu(BANKING_URL, request);
    }

    @Ignore
    @Test
    public void testLoadBankAccounts() {
        BankAccess bankAccess = new BankAccess();
        bankAccess.setBankLogin(System.getProperty("login"));
        bankAccess.setBankLogin2(System.getProperty("login2"));

        LoadAccountInformationRequest request = LoadAccountInformationRequest.builder()
                                                        .bankAccess(bankAccess)
                                                        .bankCode(System.getProperty("blz"))
                                                        .pin(System.getProperty("pin"))
                                                        .build();

        LoadAccountInformationResponse response = xs2aBanking.loadBankAccounts("http://localhost:8082", request);

    }

    @Test
    public void initiatePayment() throws ApiException {

        SinglePayment transaction = new SinglePayment();
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban(IBAN);
        BankAccess bankAccess = new BankAccess();
        bankAccess.setBankLogin(PSU_ID);
        transaction.setDebtorBankAccount(bankAccount);
        transaction.setAmount(new BigDecimal(1));
        TransactionRequest request = TransactionRequest.builder().bankAccess(bankAccess).authorisationId(AUTHORISATION_ID).pin(PIN).transaction(transaction).build();
        ArgumentCaptor<PaymentInitiationSctJson> initiation = ArgumentCaptor.forClass(PaymentInitiationSctJson.class);

        when(paymentInitiationServicePisApi.initiatePayment(
                initiation.capture(),
                eq(SINGLE_PAYMENT_SERVICE),
                eq(SEPA_CREDIT_TRANSFERS),
                any(),
                eq(PSU_IP_ADDRESS),
                isNull(), isNull(),
                isNull(), eq(PSU_ID),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(), isNull()))
                .thenReturn(buildInitiatePaymentResponse());

        InitiatePaymentResponse response = xs2aBanking.initiatePayment(BANKING_URL, request);

        verify(paymentInitiationServicePisApi, times(1)).initiatePayment(
                initiation.capture(),
                eq(SINGLE_PAYMENT_SERVICE),
                eq(SEPA_CREDIT_TRANSFERS),
                any(),
                eq(PSU_IP_ADDRESS),
                isNull(), isNull(),
                isNull(), eq(PSU_ID),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(), isNull());

        assertThat(response.getPaymentId()).isEqualTo(PAYMENT_ID);
        assertThat(response.getTransactionStatus()).isEqualTo(TransactionStatus.RCVD.name());
        assertThat(response.getLinks()).hasSize(1);
        assertThat(response.getLinks().get("self")).isEqualTo(BANKING_URL);
    }

    @Test(expected = XS2AClientException.class)
    public void initiatePaymentWithApiException() throws ApiException {

        SinglePayment transaction = new SinglePayment();
        BankAccount bankAccount = new BankAccount();
        bankAccount.setIban(IBAN);
        BankAccess bankAccess = new BankAccess();
        bankAccess.setBankLogin(PSU_ID);
        transaction.setDebtorBankAccount(bankAccount);
        transaction.setAmount(new BigDecimal(1));
        TransactionRequest request = TransactionRequest.builder().bankAccess(bankAccess).authorisationId(AUTHORISATION_ID).pin(PIN).transaction(transaction).build();
        ArgumentCaptor<PaymentInitiationSctJson> initiation = ArgumentCaptor.forClass(PaymentInitiationSctJson.class);

        when(paymentInitiationServicePisApi.initiatePayment(
                initiation.capture(),
                eq(SINGLE_PAYMENT_SERVICE),
                eq(SEPA_CREDIT_TRANSFERS),
                any(),
                eq(PSU_IP_ADDRESS),
                isNull(), isNull(),
                isNull(), eq(PSU_ID),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(),
                isNull(), isNull(), isNull()))
                .thenThrow(ApiException.class);

        xs2aBanking.initiatePayment(BANKING_URL, request);
    }

    private Map<String, Object> buildInitiatePaymentResponse() {
        Map<String, Object> map = new HashMap<>();
        map.put("transactionStatus", TransactionStatus.RCVD.name());
        map.put("paymentId", PAYMENT_ID);
        HashMap<String, String> links = new HashMap<>();
        links.put("self", BANKING_URL);
        map.put("_links", links);
        return map;
    }

    @Test
    public void requestAuthorizationCode() throws ApiException {

        String selectedSCAMethodId = "901";
        SinglePayment singlePayment = new SinglePayment();
        singlePayment.setPaymentId(PAYMENT_ID);
        BankAccess bankAccess = new BankAccess();
        bankAccess.setBankLogin(PSU_ID);
        bankAccess.setBankLogin2(CORPORATE_ID);
        TransactionRequest request = TransactionRequest.builder().authorisationId(AUTHORISATION_ID).pin(PIN).transaction(singlePayment).bankAccess(bankAccess).tanTransportType(TanTransportType.builder().id(selectedSCAMethodId).build()).build();

        ArgumentCaptor<SelectPsuAuthenticationMethod> bodyCaptor = ArgumentCaptor.forClass(SelectPsuAuthenticationMethod.class);

        when(paymentInitiationServicePisApi.updatePaymentPsuData(eq(SINGLE_PAYMENT_SERVICE), eq(SEPA_CREDIT_TRANSFERS), eq(PAYMENT_ID),
                                                                 eq(AUTHORISATION_ID), any(), bodyCaptor.capture(),
                                                                 isNull(), isNull(), isNull(),
                                                                 eq(PSU_ID), isNull(), eq(CORPORATE_ID),
                                                                 isNull(), eq(PSU_IP_ADDRESS), isNull(),
                                                                 isNull(), isNull(), isNull(),
                                                                 isNull(), isNull(),
                                                                 isNull(), isNull(), isNull())
        ).thenReturn(buildAuthorisationCodeResponse());

        AuthorisationCodeResponse response = xs2aBanking.requestAuthorizationCode(BANKING_URL, request);

        verify(paymentInitiationServicePisApi, times(1)).updatePaymentPsuData(eq(SINGLE_PAYMENT_SERVICE), eq(SEPA_CREDIT_TRANSFERS), eq(PAYMENT_ID),
                                                                              eq(AUTHORISATION_ID), any(), bodyCaptor.capture(),
                                                                              isNull(), isNull(), isNull(),
                                                                              eq(PSU_ID), isNull(), eq(CORPORATE_ID),
                                                                              isNull(), eq(PSU_IP_ADDRESS), isNull(),
                                                                              isNull(), isNull(), isNull(),
                                                                              isNull(), isNull(),
                                                                              isNull(), isNull(), isNull());

        TanChallenge challenge = response.getChallenge();
        assertThat(challenge.getData()).isEqualTo(CHALLENGE_DATA);
        assertThat(challenge.getFormat()).isEqualTo(CHALLENGE_OTP_FORMAT);
        assertThat(challenge.getTitle()).isEqualTo(CHALLENGE_ADDITIONAL_INFORMATION);

        Xs2aTanSubmit tanSubmit = (Xs2aTanSubmit) response.getTanSubmit();
        assertThat(tanSubmit.getAuthorisationId()).isEqualTo(AUTHORISATION_ID);
        assertThat(tanSubmit.getBankingUrl()).isEqualTo(BANKING_URL);
        assertThat(tanSubmit.getTransactionId()).isEqualTo(PAYMENT_ID);
        assertThat(tanSubmit.getPsuId()).isEqualTo(PSU_ID);
        assertThat(tanSubmit.getPsuCorporateId()).isEqualTo(CORPORATE_ID);

        SelectPsuAuthenticationMethod method = bodyCaptor.getValue();

        assertThat(method.getAuthenticationMethodId()).isEqualTo(selectedSCAMethodId);
    }

    @Test(expected = XS2AClientException.class)
    public void requestAuthorizationCodeWithApiException() throws ApiException {

        String selectedSCAMethodId = "901";
        SinglePayment singlePayment = new SinglePayment();
        singlePayment.setPaymentId(PAYMENT_ID);
        BankAccess bankAccess = new BankAccess();
        bankAccess.setBankLogin(PSU_ID);
        bankAccess.setBankLogin2(CORPORATE_ID);
        TransactionRequest request = TransactionRequest.builder().authorisationId(AUTHORISATION_ID).pin(PIN).transaction(singlePayment).bankAccess(bankAccess).tanTransportType(TanTransportType.builder().id(selectedSCAMethodId).build()).build();

        when(paymentInitiationServicePisApi.updatePaymentPsuData(eq(SINGLE_PAYMENT_SERVICE), eq(SEPA_CREDIT_TRANSFERS), eq(PAYMENT_ID),
                                                                 eq(AUTHORISATION_ID), any(), any(),
                                                                 isNull(), isNull(), isNull(),
                                                                 eq(PSU_ID), isNull(), eq(CORPORATE_ID),
                                                                 isNull(), eq(PSU_IP_ADDRESS), isNull(),
                                                                 isNull(), isNull(), isNull(),
                                                                 isNull(), isNull(),
                                                                 isNull(), isNull(), isNull())
        ).thenThrow(ApiException.class);

        xs2aBanking.requestAuthorizationCode(BANKING_URL, request);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void submitAuthorizationCode() throws ApiException {
        String trxId = "transactionId";
        SubmitAuthorizationCodeRequest submitPaymentRequest = buildSubmitAuthorizationCodeRequest();
        XS2AUpdateRequest updateRequest = mock(XS2AUpdateRequest.class);

        when(executor.buildRequest(submitPaymentRequest)).thenReturn(updateRequest);
        when(executor.execute(updateRequest, apiClient)).thenReturn(trxId);

        String transactionId = xs2aBanking.submitAuthorizationCode(submitPaymentRequest);

        verify(executor, times(1)).buildRequest(submitPaymentRequest);
        verify(executor, times(1)).execute(updateRequest, apiClient);

        assertThat(transactionId).isEqualTo(trxId);
    }

    @Test(expected = XS2AClientException.class)
    public void submitAuthorizationCodeWithException() throws ApiException {
        SubmitAuthorizationCodeRequest submitPaymentRequest = buildSubmitAuthorizationCodeRequest();
        XS2AUpdateRequest updateRequest = mock(XS2AUpdateRequest.class);

        when(executor.buildRequest(submitPaymentRequest)).thenReturn(updateRequest);
        when(executor.execute(updateRequest, apiClient)).thenThrow(ApiException.class);

        xs2aBanking.submitAuthorizationCode(submitPaymentRequest);

        verify(executor, times(1)).buildRequest(submitPaymentRequest);
    }

    @Test
    public void createUpdateRequestExecutor() {
        XS2ABanking banking = new XS2ABanking();
        SubmitAuthorizationCodeRequest submitPaymentRequest = buildSubmitAuthorizationCodeRequest();

        UpdateRequestExecutor executor = banking.createUpdateRequestExecutor(submitPaymentRequest);

        assertThat(executor).isInstanceOf(PaymentUpdateRequestExecutor.class);

        SinglePayment payment = new SinglePayment() {
            @Override
            public TransactionType getTransactionType() {
                return TransactionType.DEDICATED_CONSENT;
            }
        };
        submitPaymentRequest.setSepaTransaction(payment);

        executor = banking.createUpdateRequestExecutor(submitPaymentRequest);

        assertThat(executor).isInstanceOf(ConsentUpdateRequestExecutor.class);

    }

    private SubmitAuthorizationCodeRequest buildSubmitAuthorizationCodeRequest() {
        Xs2aTanSubmit tanSubmit = new Xs2aTanSubmit();
        tanSubmit.setBankingUrl(BANKING_URL);
        SinglePayment payment = new SinglePayment();
        return SubmitAuthorizationCodeRequest.builder().tanSubmit(tanSubmit).sepaTransaction(payment).build();
    }

    private Map<String, Object> buildAuthorisationCodeResponse() {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> challengeMap = new HashMap<>();
        challengeMap.put(CHALLENGE_DATA, CHALLENGE_DATA);
        challengeMap.put(CHALLENGE_OTP_FORMAT, CHALLENGE_OTP_FORMAT);
        challengeMap.put(CHALLENGE_ADDITIONAL_INFORMATION, CHALLENGE_ADDITIONAL_INFORMATION);
        response.put(CHALLENGE, challengeMap);
        response.put("tanSubmit", buildTanSubmit());
        return response;
    }

    private Map<Object, Object> buildTanSubmit() {
        Map<Object, Object> tanSubmit = new HashMap<>();
        tanSubmit.put("bankingUrl", BANKING_URL);
        tanSubmit.put("paymentId", PAYMENT_ID);
        tanSubmit.put("authorisationId", AUTHORISATION_ID);
        tanSubmit.put("psuId", PSU_ID);
        tanSubmit.put("psuCorporateId", CORPORATE_ID);
        return tanSubmit;
    }
}
