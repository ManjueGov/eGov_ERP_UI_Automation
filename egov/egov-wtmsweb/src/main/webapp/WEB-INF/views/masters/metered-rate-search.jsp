<%--
  ~ eGov suite of products aim to improve the internal efficiency,transparency,
  ~ accountability and the service delivery of the government  organizations.
  ~
  ~  Copyright (C) 2017  eGovernments Foundation
  ~
  ~  The updated version of eGov suite of products as by eGovernments Foundation
  ~  is available at http://www.egovernments.org
  ~
  ~  This program is free software: you can redistribute it and/or modify
  ~  it under the terms of the GNU General Public License as published by
  ~  the Free Software Foundation, either version 3 of the License, or
  ~  any later version.
  ~
  ~  This program is distributed in the hope that it will be useful,
  ~  but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~  GNU General Public License for more details.
  ~
  ~  You should have received a copy of the GNU General Public License
  ~  along with this program. If not, see http://www.gnu.org/licenses/ or
  ~  http://www.gnu.org/licenses/gpl.html .
  ~
  ~  In addition to the terms of the GPL license to be adhered to in using this
  ~  program, the following additional terms are to be complied with:
  ~
  ~      1) All versions of this program, verbatim or modified must carry this
  ~         Legal Notice.
  ~
  ~      2) Any misrepresentation of the origin of the material is prohibited. It
  ~         is required that all modified versions of this material be marked in
  ~         reasonable ways as different from the original version.
  ~
  ~      3) This license does not grant any rights to any user of the program
  ~         with regards to rights under trademark law for use of the trade names
  ~         or trademarks of eGovernments Foundation.
  ~
  ~  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  --%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>

<form:form method="post" action="/wtms/masters/metered-rate-search/" class="form-horizontal form-groups-bordered" modelAttribute="meteredRates" id="meteredRatesSearchform">
	<div class="panel panel-primary" data-collapsed="0">
		<div class="panel-heading">
			<c:if test="${not empty message}">
				<div role="alert">${message}</div>
			</c:if>
		</div>
		<div class="panel-body custom-form">
			<div class="form-group">
				<label class="col-sm-5 control-label text-right"><spring:message code="lbl.slabname"/><span class="mandatory"></span></label>
				<div class="col-sm-3 add-margin">
					<form:select path="slabName" data-first-option="false" cssClass="form-control" required="required">
						<form:option value=""><spring:message code="lbl.select"/></form:option>
						<form:options items="${slabNameList}" itemValue="slabName" itemLabel="slabName"/>
					</form:select>
					<form:errors path="slabName" cssClass="add-margin error-msg"/>
				</div>
			</div>
			<br/>
				<div class="text-center">
					<button type="button" class="btn btn-primary" id="search"><spring:message code="lbl.search"/></button>
					<a href="javascript:void(0)" class="btn btn-default" onclick="self.close()" ><spring:message code="lbl.close"/></a>
				</div>
		
	</div>
	</div>
	<c:if test="${mode=='view'}">
		<table class="table table-bordered table-hover multiheadertbl tblmeteredrate" id="view-meter-rates-table">
			<thead>
				<tr>
					<th><spring:message code="lbl.rateamount"/></th>
					<th><spring:message code="lbl.flatamount"/></th>
					<th><spring:message code="lbl.isrecursive"/></th>
					<th><spring:message code="lbl.recursivefactor"/></th>
					<th><spring:message code="lbl.recursiveamount"/></th>
					<th><spring:message code="lbl.effective.fromdate"/></th>
					<th><spring:message code="lbl.effective.todate"/></th>
				</tr>
			</thead>
			<tbody>
			<c:choose>
					<c:when test = "${meteredRates.ratesDetail.size()==0}">
					<tr id="meteredRateRow">
						<td colspan="7">
							<spring:message code="lbl.no.records.found"></spring:message>
						</td>
					</tr>
					</c:when>
					<c:otherwise>
						<c:forEach items="${meteredRates.ratesDetail}" var="ratesDetailValue" varStatus="counter">
						<tr id="meteredRateRow">
							<td>
								<form:input class="form-control" path="ratesDetail[${counter.index}].rateAmount" id="ratesDetail[${counter.index}].rateAmount" value="${ratesDetailValue.rateAmount}" readonly="true"/>
							</td>
							<td>
								<form:input class="form-control" path="ratesDetail[${counter.index}].flatAmount" id="ratesDetail[${counter.index}].flatAmount" value="${ratesDetailValue.flatAmount}" readonly="true"/>
							</td>
							<td>
								<form:hidden path="ratesDetail[${counter.index}].recursive" value="${ratesDetailValue.recursive}" id="ratesDetail[${counter.index}].recursive" class=" row text-right" />
								<c:choose>
									<c:when test="${ratesDetailValue.recursive=='true'}">
										<input type="checkbox" name="ratesDetail[${counter.index}].recursive" value="${ratesDetailValue.recursive}" onchange="changeRecursive(this);" id="ratesDetail[${counter.index}].recursive" disabled="true" checked="checked" class="row text-right" />
									</c:when>
									<c:otherwise>
										<input type="checkbox" name="ratesDetail[${counter.index}].recursive" value="${ratesDetailValue.recursive}" onchange="changeRecursive(this);" id="ratesDetail[${counter.index}].recursive" disabled="true" class="row text-right" />
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<form:input path="ratesDetail[${counter.index}].recursiveFactor" class="form-control patternvalidation" data-pattern="decimalvalue" id="ratesDetail[${counter.index}].recursiveFactor" readonly="true"/>
							</td>
							<td>
								<form:input path="ratesDetail[${counter.index}].recursiveAmount" class="form-control patternvalidation" data-pattern="decimalvalue" id="ratesDetail[${counter.index}].recursiveAmount" readonly="true"/>
							</td>
							<td>
								<form:input path="ratesDetail[${counter.index}].fromDate" id="ratesDetail[${counter.index}].fromDate" class="form-control datepicker" disabled="true"/>
							</td>
							<td>
								<form:input path="ratesDetail[${counter.index}].toDate" id="ratesDetail[${counter.index}].toDate" class="form-control datepicker" data-date-start-date="0d"  maxlength="100" disabled="true"/>
							</td>
						</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
				
			</tbody>
			<t:footer>
			</t:footer>
		</table> 
	
	</c:if>
		<table class="table table-bordered table-hover multiheadertbl" id="resultTable"></table>
</form:form>

<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/egi'/>" />
<link rel="stylesheet"
	href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/egi'/>">
<link rel="stylesheet"
	href="<cdn:url  value='/resources/global/js/jquery/plugins/datatables/responsive/css/datatables.responsive.css' context='/egi'/>">
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/jquery.dataTables.min.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.tableTools.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/TableTools.min.js' context='/egi'/>"></script>
<script type="text/javascript"
	src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/egi'/>"></script>
<script src="<cdn:url value='/resources/js/app/metered-rates-master.js?rnd=${app_release_no}'/>"></script>
