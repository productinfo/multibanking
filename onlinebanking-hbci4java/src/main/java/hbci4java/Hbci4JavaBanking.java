package hbci4java;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import domain.*;
import domain.request.*;
import domain.response.*;
import exception.InvalidPinException;
import hbci4java.job.*;
import hbci4java.model.HbciCallback;
import hbci4java.model.HbciDialogFactory;
import hbci4java.model.HbciDialogRequest;
import lombok.extern.slf4j.Slf4j;
import org.kapott.hbci.exceptions.HBCI_Exception;
import org.kapott.hbci.manager.BankInfo;
import org.kapott.hbci.manager.HBCIDialog;
import org.kapott.hbci.manager.HBCIUtils;
import org.kapott.hbci.manager.HBCIVersion;
import spi.OnlineBankingService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
public class Hbci4JavaBanking implements OnlineBankingService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public Hbci4JavaBanking() {
        this(null);
    }

    public Hbci4JavaBanking(InputStream customBankConfigInput) {
        try (InputStream inputStream = Optional.ofNullable(customBankConfigInput)
                .orElseGet(this::getDefaultBanksInput)) {
            HBCIUtils.refreshBLZList(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.registerModule(new Jdk8Module());
    }

    private InputStream getDefaultBanksInput() {
        return Optional.ofNullable(HBCIUtils.class.getClassLoader().getResource("blz.properties"))
                .map(url -> {
                    try {
                        return url.openStream();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("blz.properties not exists in classpath"));
    }

    @Override
    public BankApi bankApi() {
        return BankApi.HBCI;
    }

    @Override
    public boolean externalBankAccountRequired() {
        return false;
    }

    @Override
    public boolean userRegistrationRequired() {
        return false;
    }

    @Override
    public BankApiUser registerUser(String bankingUrl, BankAccess bankAccess, String pin) {
        //no registration needed
        return null;
    }

    @Override
    public void removeUser(String bankingUrl, BankApiUser bankApiUser) {
        //not needed
    }

    @Override
    public ScaMethodsResponse authenticatePsu(String bankingUrl, AuthenticatePsuRequest authenticatePsuRequest) {
        BankAccess bankAccess = new BankAccess();
        bankAccess.setBankCode(authenticatePsuRequest.getBankCode());
        bankAccess.setBankLogin(authenticatePsuRequest.getLogin());
        bankAccess.setBankLogin2(authenticatePsuRequest.getCustomerId());

        LoadAccountInformationRequest request = LoadAccountInformationRequest.builder()
                .bankAccess(bankAccess)
                .pin(authenticatePsuRequest.getPin())
                .updateTanTransportTypes(true)
                .build();

        LoadAccountInformationResponse loadAccountInformationResponse = this.loadBankAccounts(bankingUrl, request);

        return ScaMethodsResponse.builder()
                .tanTransportTypes(loadAccountInformationResponse.getBankAccess().getTanTransportTypes().get(BankApi.HBCI))
                .build();
    }

    @Override
    public LoadAccountInformationResponse loadBankAccounts(String bankingUrl,
                                                           LoadAccountInformationRequest request) {
        return loadBankAccounts(bankingUrl, request, null);
    }

    public LoadAccountInformationResponse loadBankAccounts(String bankingUrl,
                                                           LoadAccountInformationRequest request,
                                                           HbciCallback callback) {
        try {
            checkBankExists(request.getBankCode(), bankingUrl);
            return AccountInformationJob.loadBankAccounts(request, callback);
        } catch (HBCI_Exception e) {
            throw handleHbciException(e);
        }
    }

    @Override
    public boolean bookingsCategorized() {
        return false;
    }

    @Override
    public InitiatePaymentResponse initiatePayment(String bankingUrl, TransactionRequest paymentRequest) {
        try {
            checkBankExists(paymentRequest.getBankCode(), bankingUrl);
            TransferJob transferJob = new TransferJob();
            transferJob.requestTransfer(paymentRequest);
            return null;
        } catch (HBCI_Exception e) {
            throw handleHbciException(e);
        }
    }

    @Override
    public AuthorisationCodeResponse requestAuthorizationCode(String bankingUrl,
                                                              TransactionRequest transactionRequest) {
        try {
            checkBankExists(transactionRequest.getBankCode(), bankingUrl);

            ScaRequiredJob scaJob = Optional.ofNullable(transactionRequest.getTransaction())
                    .map(sepaTransaction -> createScaJob(sepaTransaction.getTransactionType()))
                    .orElse(new EmptyJob());

            return scaJob.requestAuthorizationCode(transactionRequest);
        } catch (HBCI_Exception e) {
            throw handleHbciException(e);
        }
    }

    @Override
    public String submitAuthorizationCode(SubmitAuthorizationCodeRequest submitAuthorizationCodeRequest) {
        try {
            ScaRequiredJob scaJob = Optional.ofNullable(submitAuthorizationCodeRequest.getSepaTransaction())
                    .map(sepaTransaction -> createScaJob(sepaTransaction.getTransactionType()))
                    .orElse(new EmptyJob());

            return scaJob.sumbitAuthorizationCode(submitAuthorizationCodeRequest);
        } catch (HBCI_Exception e) {
            throw handleHbciException(e);
        }
    }

    @Override
    public void removeBankAccount(String bankingUrl, BankAccount bankAccount, BankApiUser bankApiUser) {
        //not needed
    }

    @Override
    public LoadBookingsResponse loadBookings(String bankingUrl, LoadBookingsRequest loadBookingsRequest) {
        try {
            checkBankExists(loadBookingsRequest.getBankCode(), bankingUrl);
            return LoadBookingsJob.loadBookings(loadBookingsRequest);
        } catch (HBCI_Exception e) {
            throw handleHbciException(e);
        }
    }

    @Override
    public List<BankAccount> loadBalances(String bankingUrl, LoadBalanceRequest loadBalanceRequest) {
        try {
            checkBankExists(loadBalanceRequest.getBankCode(), bankingUrl);
            return LoadBalanceJob.loadBalances(loadBalanceRequest);
        } catch (HBCI_Exception e) {
            throw handleHbciException(e);
        }
    }

    public HBCIDialog createDialog(String bankingUrl, HbciDialogRequest dialogRequest) {
        try {
            checkBankExists(dialogRequest.getBankCode(), bankingUrl);
            return HbciDialogFactory.createDialog(null, dialogRequest);
        } catch (HBCI_Exception e) {
            throw handleHbciException(e);
        }
    }

    @Override
    public boolean bankSupported(String bankCode) {
        BankInfo bankInfo = HBCIUtils.getBankInfo(bankCode);
        return bankInfo != null && bankInfo.getPinTanVersion() != null;
    }

    @Override
    public boolean accountInformationConsentRequired(BankApiUser bankApiUser, String accountReference) {
        return false;
    }

    @Override
    public CreateConsentResponse createAccountInformationConsent(String bankingUrl,
                                                                 CreateConsentRequest startScaRequest) {
        return null;
    }

    private void checkBankExists(String bankCode, String bankingUrl) {
        Optional.ofNullable(bankingUrl).ifPresent(s -> {
            BankInfo bankInfo = HBCIUtils.getBankInfo(bankCode);
            if (bankInfo == null) {
                bankInfo = new BankInfo();
                bankInfo.setBlz(bankCode);
                bankInfo.setPinTanAddress(s);
                bankInfo.setPinTanVersion(HBCIVersion.HBCI_300);
                HBCIUtils.addBankInfo(bankInfo);
            }
        });
    }

    private ScaRequiredJob createScaJob(AbstractScaTransaction.TransactionType transactionType) {
        switch (transactionType) {
            case SINGLE_PAYMENT:
            case FUTURE_SINGLE_PAYMENT:
                return new SinglePaymentJob();
            case FOREIGN_PAYMENT:
                return new ForeignPaymentJob();
            case BULK_PAYMENT:
            case FUTURE_BULK_PAYMENT:
                return new BulkPaymentJob();
            case STANDING_ORDER:
                return new NewStandingOrderJob();
            case RAW_SEPA:
                return new RawSepaJob();
            case FUTURE_SINGLE_PAYMENT_DELETE:
                return new DeleteFutureSinglePaymentJob();
            case FUTURE_BULK_PAYMENT_DELETE:
                return new DeleteFutureBulkPaymentJob();
            case STANDING_ORDER_DELETE:
                return new DeleteStandingOrderJob();
            case TAN_REQUEST:
            case DEDICATED_CONSENT:
                return new EmptyJob();
            default:
                throw new IllegalArgumentException("invalid transaction type " + transactionType);
        }
    }

    private RuntimeException handleHbciException(HBCI_Exception e) {
        Throwable processException = e;
        while (processException.getCause() != null && !(processException.getCause() instanceof InvalidPinException)) {
            processException = processException.getCause();
        }

        if (processException.getCause() != null && processException.getCause() instanceof InvalidPinException) {
            return (InvalidPinException) processException.getCause();
        }

        return e;
    }
}
