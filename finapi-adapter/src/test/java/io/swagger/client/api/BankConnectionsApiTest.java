/*
 * finAPI RESTful Services
 * finAPI RESTful Services
 *
 * OpenAPI spec version: v1.64.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.client.api;

import io.swagger.client.ApiException;
import io.swagger.client.model.BadCredentialsError;
import io.swagger.client.model.BankConnection;
import io.swagger.client.model.BankConnectionList;
import io.swagger.client.model.EditBankConnectionParams;
import io.swagger.client.model.ErrorMessage;
import io.swagger.client.model.IdentifierList;
import io.swagger.client.model.ImportBankConnectionParams;
import io.swagger.client.model.UpdateBankConnectionParams;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for BankConnectionsApi
 */
@Ignore
public class BankConnectionsApiTest {

    private final BankConnectionsApi api = new BankConnectionsApi();

    
    /**
     * Delete all bank connections
     *
     * Delete all bank connections of the user that is authorized by the access_token. Must pass the user&#39;s access_token.&lt;br/&gt;&lt;br/&gt;Notes: &lt;br/&gt;- All notification rules that are connected to any specific bank connection will get deleted as well. &lt;br/&gt;- If at least one bank connection is busy (currently in the process of import, update, or transactions categorization), then this service will perform no action at all.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void deleteAllBankConnectionsTest() throws ApiException {
        IdentifierList response = api.deleteAllBankConnections();

        // TODO: test validations
    }
    
    /**
     * Delete a bank connection
     *
     * Delete a single bank connection of the user that is authorized by the access_token, including all of its accounts and their transactions and balance data. Must pass the connection&#39;s identifier and the user&#39;s access_token.&lt;br/&gt;&lt;br/&gt;Notes: &lt;br/&gt;- All notification rules that are connected to the bank connection will get adjusted so that they no longer have this connection listed. Notification rules that are connected to just this bank connection (and no other connection) will get deleted altogether. &lt;br/&gt;- A bank connection cannot get deleted while it is in the process of import, update, or transactions categorization.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void deleteBankConnectionTest() throws ApiException {
        Long id = null;
        api.deleteBankConnection(id);

        // TODO: test validations
    }
    
    /**
     * Edit a bank connection
     *
     * Change the stored authentication credentials (banking user ID, banking customer ID, and banking PIN), or other fields of the bank connection. Must pass the connection&#39;s identifier and the user&#39;s access_token.&lt;br/&gt;&lt;br/&gt;Note that a bank connection&#39;s credentials cannot be changed while it is in the process of import, update, or transactions categorization.&lt;br/&gt;&lt;br/&gt;NOTE: Depending on your license, this service may respond with HTTP code 451, containing an error message with a identifier of web form in it. In addition to that the response will also have included a &#39;Location&#39; header, which contains the URL to the web form. In this case, you must forward your user to finAPI&#39;s web form. For a detailed explanation of the Web Form Flow, please refer to this article: &lt;a href&#x3D;&#39;https://finapi.zendesk.com/hc/en-us/articles/360002596391&#39; target&#x3D;&#39;_blank&#39;&gt;finAPI&#39;s Web Form Flow&lt;/a&gt;
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void editBankConnectionTest() throws ApiException {
        Long id = null;
        EditBankConnectionParams body = null;
        BankConnection response = api.editBankConnection(id, body);

        // TODO: test validations
    }
    
    /**
     * Get all bank connections
     *
     * Get bank connections of the user that is authorized by the access_token. Must pass the user&#39;s access_token. You can set optional search criteria to get only those bank connections that you are interested in. If you do not specify any search criteria, then this service functions as a &#39;get all&#39; service.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getAllBankConnectionsTest() throws ApiException {
        List<Long> ids = null;
        BankConnectionList response = api.getAllBankConnections(ids);

        // TODO: test validations
    }
    
    /**
     * Get a bank connection
     *
     * Get a single bank connection of the user that is authorized by the access_token. Must pass the connection&#39;s identifier and the user&#39;s access_token.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getBankConnectionTest() throws ApiException {
        Long id = null;
        BankConnection response = api.getBankConnection(id);

        // TODO: test validations
    }
    
    /**
     * Get multiple bank connections
     *
     * Get a list of multiple bank connections of the user that is authorized by the access_token. Must pass the connections&#39; identifiers and the user&#39;s access_token. Connections whose identifiers do not exist or do not relate to the authorized user will not be contained in the result (If this applies to all of the given identifiers, then the result will be an empty list). WARNING: This service is deprecated and will be removed at some point. If you want to get multiple bank connections, please instead use the service &#39;Get all bank connections&#39; and pass a comma-separated list of identifiers as a parameter &#39;ids&#39;.
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void getMultipleBankConnectionsTest() throws ApiException {
        List<Long> ids = null;
        BankConnectionList response = api.getMultipleBankConnections(ids);

        // TODO: test validations
    }
    
    /**
     * Import a new bank connection
     *
     * Imports a new bank connection for a specific user. Must pass the connection credentials and the user&#39;s access_token. All bank accounts will be downloaded and imported with their current balances, transactions and supported two-step-procedures (note that the amount of available transactions may vary between banks, e.g. some banks deliver all transactions from the past year, others only deliver the transactions from the past three months). The balance and transactions download process runs asynchronously, so this service may return before all balances and transactions have been imported. Also, all downloaded transactions will be categorized by a separate background process that runs asynchronously too. To check the status of the balance and transactions download process as well as the background categorization process, see the status flags that are returned by the GET /bankConnections/&lt;id&gt; service.&lt;br/&gt;&lt;br/&gt;Note that some banks may require a multi-step authentication, in which case the service will respond with HTTP code 510 and an error message containing a challenge for the user from the bank. You must display the challenge message to the user, and then retry the service call, passing the user&#39;s answer to the bank&#39;s challenge in the &#39;challengeResponse&#39; field.&lt;br/&gt;&lt;br/&gt;You can also import a \&quot;demo connection\&quot; which contains a single bank account with some pre-defined transactions. To import the demo connection, you need to pass the identifier of the \&quot;finAPI Test Bank\&quot;. In case of demo connection import, any other fields besides the bank identifier can remain unset. The bankingUserId, bankingCustomerId, bankingPin, and storePin fields will be stored if you pass them, however they will not be regarded when updating the demo connection (in other words: It doesn&#39;t matter what credentials you choose for the demo connection). Note however that if you want to import the demo connection multiple times for the same user, you must use a different bankingUserId and/or bankingCustomerId for each of the imports. Also note that the skipPositionsDownload flag is ignored for the demo bank connection, i.e. when importing the demo bank connection, you will always get the transactions for its account. You can enable multi-step authentication for the demo bank connection by setting the bank connection name to \&quot;MSA\&quot;.&lt;br/&gt;&lt;br/&gt;&lt;b&gt;For a more in-depth understanding of the import process, please also read this article on our Dev Portal: &lt;a href&#x3D;&#39;https://finapi.zendesk.com/hc/en-us/articles/115000296607-Import-Update-of-Bank-Connections-Accounts&#39; target&#x3D;&#39;_blank&#39;&gt;Import &amp; Update of Bank Connections / Accounts&lt;/a&gt;&lt;/b&gt;&lt;br/&gt;&lt;br/&gt;NOTE: Depending on your license, this service may respond with HTTP code 451, containing an error message with a identifier of web form in it. In addition to that the response will also have included a &#39;Location&#39; header, which contains the URL to the web form. In this case, you must forward your user to finAPI&#39;s web form. For a detailed explanation of the Web Form Flow, please refer to this article: &lt;a href&#x3D;&#39;https://finapi.zendesk.com/hc/en-us/articles/360002596391&#39; target&#x3D;&#39;_blank&#39;&gt;finAPI&#39;s Web Form Flow&lt;/a&gt;
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void importBankConnectionTest() throws ApiException {
        ImportBankConnectionParams body = null;
        BankConnection response = api.importBankConnection(body);

        // TODO: test validations
    }
    
    /**
     * Update a bank connection
     *
     * Update an existing bank connection of the user that is authorized by the access_token. Downloads and imports the current account balances and new transactions. Must pass the connection&#39;s identifier and the user&#39;s access_token. For more information about the processes of authentication, data download and transactions categorization, see POST /bankConnections/import. Note that supported two-step-procedures are updated as well. It may unset the current default two-step-procedure of the given bank connection (but only if this procedure is not supported anymore by the bank). You can also update the \&quot;demo connection\&quot; (in this case, the fields &#39;bankingPin&#39;, &#39;importNewAccounts&#39;, and &#39;skipPositionsDownload&#39; will be ignored).&lt;br/&gt;&lt;br/&gt;Note that you cannot trigger an update of a bank connection as long as there is still a previously triggered update running.&lt;br/&gt;&lt;br/&gt;&lt;b&gt;For a more in-depth understanding of the update process, please also read this article on our Dev Portal: &lt;a href&#x3D;&#39;https://finapi.zendesk.com/hc/en-us/articles/115000296607-Import-Update-of-Bank-Connections-Accounts&#39; target&#x3D;&#39;_blank&#39;&gt;Import &amp; Update of Bank Connections / Accounts&lt;/a&gt;&lt;/b&gt;&lt;br/&gt;&lt;br/&gt;NOTE: Depending on your license, this service may respond with HTTP code 451, containing an error message with a identifier of web form in it. In addition to that the response will also have included a &#39;Location&#39; header, which contains the URL to the web form. In this case, you must forward your user to finAPI&#39;s web form. For a detailed explanation of the Web Form Flow, please refer to this article: &lt;a href&#x3D;&#39;https://finapi.zendesk.com/hc/en-us/articles/360002596391&#39; target&#x3D;&#39;_blank&#39;&gt;finAPI&#39;s Web Form Flow&lt;/a&gt;
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void updateBankConnectionTest() throws ApiException {
        UpdateBankConnectionParams body = null;
        BankConnection response = api.updateBankConnection(body);

        // TODO: test validations
    }
    
}
