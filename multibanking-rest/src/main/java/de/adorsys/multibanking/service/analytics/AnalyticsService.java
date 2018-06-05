package de.adorsys.multibanking.service.analytics;

import static de.adorsys.multibanking.service.analytics.SmartanalyticsMapper.toContract;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.adorsys.multibanking.domain.AccountAnalyticsEntity;
import de.adorsys.multibanking.domain.BankAccountEntity;
import de.adorsys.multibanking.domain.ContractEntity;
import de.adorsys.multibanking.service.BankDataService;
import de.adorsys.smartanalytics.api.AnalyticsResult;
import de.adorsys.smartanalytics.api.BookingGroup;

@Service
public class AnalyticsService {
	private final BankDataService bankDataService;
	
    public AnalyticsService(BankDataService bankDataService) {
        this.bankDataService = bankDataService;
    }
    public void saveAccountAnalytics(BankAccountEntity bankAccountEntity, AnalyticsResult categoryResult, LocalDate referenceDate) {
        AccountAnalyticsEntity accountAnalyticsEntity = new AccountAnalyticsEntity();
        accountAnalyticsEntity.setUserId(bankAccountEntity.getUserId());
        accountAnalyticsEntity.setAccountId(bankAccountEntity.getId());
        accountAnalyticsEntity.setAnalyticsDate(referenceDate);

        if(bankAccountEntity.getBankAccountBalance()!=null && bankAccountEntity.getBankAccountBalance().getReadyHbciBalance()!=null)
        	accountAnalyticsEntity.setBalanceCalculated(
                bankAccountEntity.getBankAccountBalance().getReadyHbciBalance()
                        .add(accountAnalyticsEntity.getIncomeNext()).add(accountAnalyticsEntity.getExpensesNext()));
        bankDataService.saveAccountAnalytics(bankAccountEntity.getId(), accountAnalyticsEntity);
    }

    public void identifyAndStoreContracts(String userId, String accountId, AnalyticsResult categoryResult) {
    	if(categoryResult.getBookingGroups()!=null){
	        List<ContractEntity> contractEntities = categoryResult.getBookingGroups()
	                .stream()
	                .filter(BookingGroup::isContract)
	                .map(category -> toContract(userId, accountId, category))
	                .collect(Collectors.toList());
	        bankDataService.saveContracts(accountId, contractEntities);
    	}
    }
}
