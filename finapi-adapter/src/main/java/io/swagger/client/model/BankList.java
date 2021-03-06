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


package io.swagger.client.model;

import java.util.Objects;
import java.util.Arrays;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.client.model.Bank;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for data of multiple banks
 */
@ApiModel(description = "Container for data of multiple banks")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-05T12:19:21.458Z")
public class BankList {
  @SerializedName("banks")
  private List<Bank> banks = new ArrayList<Bank>();

  public BankList banks(List<Bank> banks) {
    this.banks = banks;
    return this;
  }

  public BankList addBanksItem(Bank banksItem) {
    this.banks.add(banksItem);
    return this;
  }

   /**
   * Banks data
   * @return banks
  **/
  @ApiModelProperty(required = true, value = "Banks data")
  public List<Bank> getBanks() {
    return banks;
  }

  public void setBanks(List<Bank> banks) {
    this.banks = banks;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BankList bankList = (BankList) o;
    return Objects.equals(this.banks, bankList.banks);
  }

  @Override
  public int hashCode() {
    return Objects.hash(banks);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BankList {\n");
    
    sb.append("    banks: ").append(toIndentedString(banks)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

