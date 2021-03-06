package spi;

import domain.BankAccess;
import domain.BankAccount;
import domain.BankApi;
import domain.BankApiUser;
import domain.request.*;
import domain.response.*;

import java.util.List;

public interface OnlineBankingService {

    BankApi bankApi();

    boolean externalBankAccountRequired();

    boolean userRegistrationRequired();

    BankApiUser registerUser(String bankingUrl, BankAccess bankAccess, String pin);

    void removeUser(String bankingUrl, BankApiUser bankApiUser);

    ScaMethodsResponse authenticatePsu(String bankingUrl, AuthenticatePsuRequest authenticatePsuRequest);

    LoadAccountInformationResponse loadBankAccounts(String bankingUrl,
                                                    LoadAccountInformationRequest loadAccountInformationRequest);
    void removeBankAccount(String bankingUrl, BankAccount bankAccount, BankApiUser bankApiUser);

    LoadBookingsResponse loadBookings(String bankingUrl, LoadBookingsRequest loadBookingsRequest);

    List<BankAccount> loadBalances(String bankingUrl, LoadBalanceRequest loadBalanceRequest);

    boolean bankSupported(String bankCode);

    boolean bookingsCategorized();

    InitiatePaymentResponse initiatePayment(String bankingUrl, TransactionRequest paymentRequest);

    AuthorisationCodeResponse requestAuthorizationCode(String bankingUrl, TransactionRequest paymentRequest);

    String submitAuthorizationCode(SubmitAuthorizationCodeRequest submitPaymentRequest);

    boolean accountInformationConsentRequired(BankApiUser bankApiUser, String accountReference);

    CreateConsentResponse createAccountInformationConsent(String bankingUrl, CreateConsentRequest startScaRequest);

}
