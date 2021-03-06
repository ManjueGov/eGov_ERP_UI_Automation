<!-- #-------------------------------------------------------------------------------
# eGov suite of products aim to improve the internal efficiency,transparency, 
#    accountability and the service delivery of the government  organizations.
# 
#     Copyright (C) <2015>  eGovernments Foundation
# 
#     The updated version of eGov suite of products as by eGovernments Foundation 
#     is available at http://www.egovernments.org
# 
#     This program is free software: you can redistribute it and/or modify
#     it under the terms of the GNU General Public License as published by
#     the Free Software Foundation, either version 3 of the License, or
#     any later version.
# 
#     This program is distributed in the hope that it will be useful,
#     but WITHOUT ANY WARRANTY; without even the implied warranty of
#     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#     GNU General Public License for more details.
# 
#     You should have received a copy of the GNU General Public License
#     along with this program. If not, see http://www.gnu.org/licenses/ or 
#     http://www.gnu.org/licenses/gpl.html .
# 
#     In addition to the terms of the GPL license to be adhered to in using this
#     program, the following additional terms are to be complied with:
# 
# 	1) All versions of this program, verbatim or modified must carry this 
# 	   Legal Notice.
# 
# 	2) Any misrepresentation of the origin of the material is prohibited. It 
# 	   is required that all modified versions of this material be marked in 
# 	   reasonable ways as different from the original version.
# 
# 	3) This license does not grant any rights to any user of the program 
# 	   with regards to rights under trademark law for use of the trade names 
# 	   or trademarks of eGovernments Foundation.
# 
#   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
#------------------------------------------------------------------------------- -->


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<div class="panel-heading">
	<div class="panel-title">
		<spring:message code="${param.header}"/>
	</div>
</div>

<div class="panel-body">
	<div class="row add-border">
		<div class="col-sm-3 ">
			<spring:message code="lbl.fullname"/>
		</div>
		<div class="col-sm-3 add-margin view-content">
			<c:out value="${reIssue.applicant.name.firstName}"></c:out>&nbsp; &nbsp;
			<c:out value="${reIssue.applicant.name.middleName}"></c:out>&nbsp; &nbsp;
			<c:out value="${reIssue.applicant.name.lastName}"></c:out>
		</div>
		<%-- <div class="col-sm-3 ">
			<spring:message code="lbl.othername"/>
		</div>
		<div class="col-sm-3 add-margin view-content">
			<c:out value="${reIssue.applicant.otherName}" default="NA"/>
		</div> --%>
	</div>

<div class="row add-border">
		<div class="col-sm-3 ">
			<spring:message code="lbl.residence.address"/>
		</div>
		<div class="col-sm-3 add-margin view-content">
			<c:out value="${reIssue.applicant.contactInfo.residenceAddress}" />
		</div>
		<div class="col-sm-3 ">
			<spring:message code="lbl.office.address"/>
		</div>
		<div class="col-sm-3 add-margin view-content">
			<c:out value="${reIssue.applicant.contactInfo.officeAddress}" />
		</div>
	</div>

<div class="row add-border">
		<div class="col-sm-3 ">
			<spring:message code="lbl.phoneno"/>
		</div>
		<div class="col-sm-3 add-margin view-content">
			<c:out value="${reIssue.applicant.contactInfo.mobileNo}" />
		</div>
		<div class="col-sm-3 ">
			<spring:message code="lbl.email"/>
		</div>
		<div class="col-sm-3 add-margin view-content">
			<c:out value="${reIssue.applicant.contactInfo.email}" default="NA"/>
		</div>
</div>

<div class="row add-border">
	<div class="col-sm-3 "><spring:message code="lbl.fee.criteria"/></div>
	<div class="col-sm-3 add-margin view-content"><c:out value="${reIssue.feeCriteria.criteria}" /></div>
	<div class="col-sm-3 "><spring:message code="lbl.fee"/></div>
	<div class="col-sm-3 add-margin view-content"><c:out value="${reIssue.feePaid}" /></div>
</div>
								
</div>
