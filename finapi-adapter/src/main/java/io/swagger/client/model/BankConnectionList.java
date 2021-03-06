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
import io.swagger.client.model.BankConnection;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for data of multiple bank connections
 */
@ApiModel(description = "Container for data of multiple bank connections")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2019-02-05T12:19:21.458Z")
public class BankConnectionList {
  @SerializedName("connections")
  private List<BankConnection> connections = new ArrayList<BankConnection>();

  public BankConnectionList connections(List<BankConnection> connections) {
    this.connections = connections;
    return this;
  }

  public BankConnectionList addConnectionsItem(BankConnection connectionsItem) {
    this.connections.add(connectionsItem);
    return this;
  }

   /**
   * List of bank connections
   * @return connections
  **/
  @ApiModelProperty(required = true, value = "List of bank connections")
  public List<BankConnection> getConnections() {
    return connections;
  }

  public void setConnections(List<BankConnection> connections) {
    this.connections = connections;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BankConnectionList bankConnectionList = (BankConnectionList) o;
    return Objects.equals(this.connections, bankConnectionList.connections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(connections);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BankConnectionList {\n");
    
    sb.append("    connections: ").append(toIndentedString(connections)).append("\n");
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

