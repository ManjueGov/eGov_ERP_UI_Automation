<%@ include file="/includes/taglibs.jsp" %>
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

<script>
	function validateWorkFlowApprover(name,errorDivId) {
		document.getElementById("workFlowAction").value=name;
	    var approverPosId = document.getElementById("approverPositionId");
	    if(approverPosId) {
			var approver = approverPosId.options[approverPosId.selectedIndex].text; 
			document.getElementById("approverName").value= approver.split('~')[0];
		}     
	   return  onSubmit();
	}

	function validateWorkFlowApprover(name) {
		if(!onSubmitValidations()){
			return false;
		}
	    document.getElementById("workFlowAction").value=name;
	    var approverDeptId =document.getElementById("approverDepartment");
	    var approverDesgId = document.getElementById("approverDesignation");
	    var approverPosId = document.getElementById("approverPositionId");
	    var approverComments = document.getElementById("approverComments").value;
	    if(approverPosId && approverPosId.value != -1) {
			var approver = approverPosId.options[approverPosId.selectedIndex].text; 
			document.getElementById("approverName").value= approver.split('~')[0];
		}   
		<s:if test="%{getNextAction()!='END'}">
	    if(name=="Forward" || name=="forward") {
	    	if(approverDeptId && approverDeptId.value == -1){
	    		bootbox.alert("Please Select the Approver Department ");
				return false;
		    } else if(approverDesgId && approverDesgId.value == -1){
		    	bootbox.alert("Please Select the Approver Designation ");
				return false;
		    } else if(approverPosId && approverPosId.value == -1){
		    	bootbox.alert("Please Select the Approver ");
				return false;
		    }  
	    }
	    </s:if>
	    if(name=="Forward" || name=="forward" || name=="approve" || name=="Approve") {
	    	 if (approverComments == null || approverComments == "" || approverComments.trim().length == 0) { 
	    		 bootbox.alert("Please Enter Approver Remarks ");
				return false;
	    	}  
	    }
	    if ((name=="Reject" || name=="reject")) {
	    	if (approverComments == null || approverComments == "" || approverComments.trim().length == 0) {
	    		bootbox.alert("Please Enter Rejection Remarks ");
				return false;
	    	}
		}
	    return  onSubmit();
	}
</script>

<div class="buttonbottom" align="center">
	<s:hidden id="workFlowAction" name="workFlowAction"/>
	<table>
		<tr>
			<td><s:iterator value="validActions" var="name">
					<s:if test="%{name!=''}">
					<td>
						<s:submit type="submit" cssClass="buttonsubmit custom-button" value="%{name}"
							id="%{name}" name="%{name}"
							onclick="return validateWorkFlowApprover('%{name}','jsValidationErrors');" style="margin:0 5px" /></td>
					</s:if>
				</s:iterator> 
				<td><input type="button" name="button2" id="button2" value="Close"
				class="button" onclick="window.close();" style="margin:0 5px"/></td></td>
		</tr>
	</table>
</div>