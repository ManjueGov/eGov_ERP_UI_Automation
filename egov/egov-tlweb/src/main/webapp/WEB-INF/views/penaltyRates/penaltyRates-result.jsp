<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) <2015>  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>

<form:form role="form" action="create" modelAttribute="penaltyForm"
	commandName="penaltyForm" id="penaltyform"
	cssClass="form-horizontal form-groups-bordered">
	<div class="row">
		<div class="form-group" id="penalty">
			<form:select path="licenseAppType" id="licenseAppType"
				cssClass="form-control" cssErrorClass="form-control error"
				required="required">
				<form:option value="">
					<spring:message code="lbl.select" />
				</form:option>
				<form:options items="${licenseAppTypes}" itemValue="id"
					itemLabel="name" />
			</form:select>
		</div>
		<div class="col-sm-12">
			<input type="hidden" name="licenseAppTypeId"
				value="${penaltyForm.licenseAppType.id}" />
			<table class="table table-bordered" id="result">
				<thead>
					<th><spring:message code="lbl.from" /></th>
					<th><spring:message code="lbl.to" /></th>
					<th><spring:message code="lbl.penaltyrate" /></th>
					<th></th>
				</thead>
				<c:choose>
					<c:when test="${not empty penaltyForm.getPenaltyRatesList()}">
						<tbody>
							<c:forEach items="${penaltyForm.penaltyRatesList}"
								var="penaltyRatesList" varStatus="vs">
								<tr id="resultrow${vs.index}">
									<td><input type="hidden"
										name="penaltyRatesList[${vs.index}]" id="penaltyId"
										value="${penaltyRatesList.id}" /> <input type="text"
										name="penaltyRatesList[${vs.index}].fromRange" id="fromRange"
										value="${penaltyRatesList.fromRange}"
										class="form-control fromRange text-right patternvalidation"
										pattern="-?\d*" data-pattern="numerichyphen" maxlength="8"
										readonly="readonly" required="required" /></td>
									<td><input type="text"
										name="penaltyRatesList[${vs.index}].toRange" id="toRange"
										value="${penaltyRatesList.toRange}"
										class="form-control text-right patternvalidation"
										pattern="-?\d*" data-pattern="numerichyphen" maxlength="8"
										onchange="return checkValue(this);" required="required" /></td>
									<td><input type="text"
										name="penaltyRatesList[${vs.index}].rate" id="rate"
										value="${penaltyRatesList.rate}"
										class="form-control text-right patternvalidation"
										data-pattern="number" maxlength="8" required="required" /></td>
									<td><span class="add-padding" id="del-row"
										class="btn btn-primary" onclick="deleteThisRow(this)"><i
											class="fa fa-trash" aria-hidden="true"></i></span></td>
								</tr>
							</c:forEach>
						</tbody>
					</c:when>
					<c:otherwise>
						<tbody>
							<tr id="resultrow0">
								<td><input type="hidden" name="penaltyRatesList[0].id"
									id="penaltyId" /> <input type="text"
									name="penaltyRatesList[0].fromRange" id="fromRange" value="0"
									class="form-control text-right patternvalidation"
									pattern="-?\d*" data-pattern="numerichyphen"
									required="required" /></td>
								<td><input type="text" name="penaltyRatesList[0].toRange"
									id="toRange" class="form-control text-right patternvalidation"
									pattern="-?\d*" data-pattern="numerichyphen"
									onchange="return checkValue(this);" required="required" /></td>
								<td><input type="text" name="penaltyRatesList[0].rate"
									id="rate" class="form-control text-right patternvalidation"
									data-pattern="number" required="required" /></td>
								<td><span class="add-padding" id="del-row"
									class="btn btn-primary" onclick="deleteThisRow(this)"><i
										class="fa fa-trash" aria-hidden="true"></i></span></td>
							</tr>
						<tbody>
					</c:otherwise>
				</c:choose>
			</table>
		</div>
		<div class="col-sm-12 text-center">
			<button type="button" id="add-row" class="btn btn-primary">
				<spring:message code="lbl.add" />
			</button>
			<button type='submit' class='btn btn-primary' id="buttonSubmit">
				<spring:message code="lbl.save" />
			</button>
		</div>
	</div>
</form:form>
<script src="<cdn:url  value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/egi'/>"></script>
<script src="<cdn:url  value='/resources/app/js/penaltyRates.js?rnd=${app_release_no}'/>"></script>