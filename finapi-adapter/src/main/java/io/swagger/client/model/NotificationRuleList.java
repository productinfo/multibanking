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
import io.swagger.client.model.NotificationRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for data of multiple notification rules
 */
@ApiModel(description = "Container for data of multiple notification rules")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-05T12:19:21.458Z")
public class NotificationRuleList {
  @SerializedName("notificationRules")
  private List<NotificationRule> notificationRules = null;

  public NotificationRuleList notificationRules(List<NotificationRule> notificationRules) {
    this.notificationRules = notificationRules;
    return this;
  }

  public NotificationRuleList addNotificationRulesItem(NotificationRule notificationRulesItem) {
    if (this.notificationRules == null) {
      this.notificationRules = new ArrayList<NotificationRule>();
    }
    this.notificationRules.add(notificationRulesItem);
    return this;
  }

   /**
   * List of notification rules
   * @return notificationRules
  **/
  @ApiModelProperty(value = "List of notification rules")
  public List<NotificationRule> getNotificationRules() {
    return notificationRules;
  }

  public void setNotificationRules(List<NotificationRule> notificationRules) {
    this.notificationRules = notificationRules;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationRuleList notificationRuleList = (NotificationRuleList) o;
    return Objects.equals(this.notificationRules, notificationRuleList.notificationRules);
  }

  @Override
  public int hashCode() {
    return Objects.hash(notificationRules);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationRuleList {\n");
    
    sb.append("    notificationRules: ").append(toIndentedString(notificationRules)).append("\n");
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

