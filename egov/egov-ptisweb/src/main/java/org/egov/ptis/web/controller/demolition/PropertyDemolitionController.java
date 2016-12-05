/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.ptis.web.controller.demolition;

import org.egov.commons.Installment;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.utils.DateUtils;
import org.egov.ptis.client.util.PropertyTaxUtil;
import org.egov.ptis.constants.PropertyTaxConstants;
import org.egov.ptis.domain.dao.demand.PtDemandDao;
import org.egov.ptis.domain.dao.property.BasicPropertyDAO;
import org.egov.ptis.domain.entity.property.BasicProperty;
import org.egov.ptis.domain.entity.property.Property;
import org.egov.ptis.domain.entity.property.PropertyImpl;
import org.egov.ptis.domain.service.demolition.PropertyDemolitionService;
import org.egov.ptis.exceptions.TaxCalculatorExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import static org.egov.ptis.constants.PropertyTaxConstants.APPLICATION_TYPE_DEMOLITION;
import static org.egov.ptis.constants.PropertyTaxConstants.ARR_COLL_STR;
import static org.egov.ptis.constants.PropertyTaxConstants.ARR_DMD_STR;
import static org.egov.ptis.constants.PropertyTaxConstants.CURR_FIRSTHALF_COLL_STR;
import static org.egov.ptis.constants.PropertyTaxConstants.CURR_FIRSTHALF_DMD_STR;
import static org.egov.ptis.constants.PropertyTaxConstants.DEMOLITION;
import static org.egov.ptis.constants.PropertyTaxConstants.PROPERTY_VALIDATION;
import static org.egov.ptis.constants.PropertyTaxConstants.STATUS_WORKFLOW;
import static org.egov.ptis.constants.PropertyTaxConstants.TARGET_TAX_DUES;
import static org.egov.ptis.constants.PropertyTaxConstants.TARGET_WORKFLOW_ERROR;
import static org.egov.ptis.constants.PropertyTaxConstants.CURR_SECONDHALF_DMD_STR;
import static org.egov.ptis.constants.PropertyTaxConstants.CURR_SECONDHALF_COLL_STR;

@Controller
@RequestMapping(value = "/property/demolition/{assessmentNo}")
public class PropertyDemolitionController extends GenericWorkFlowController {

    protected static final String COMMON_FORM = "commonForm";
    protected static final String DEMOLITION_FORM = "demolition-form";
    protected static final String DEMOLITION_SUCCESS = "demolition-success";

    @Autowired
    private BasicPropertyDAO basicPropertyDAO;

    @Autowired
    private PtDemandDao ptDemandDAO;

    @Autowired
    private PropertyTaxUtil propertyTaxUtil;

    @Autowired
    private PropertyDemolitionService propertyDemolitionService;
    BasicProperty basicProperty;
    PropertyImpl propertyImpl = new PropertyImpl();
    PropertyImpl oldProperty;

    @ModelAttribute
    public Property propertyModel(@PathVariable String assessmentNo) {
        basicProperty = basicPropertyDAO.getBasicPropertyByPropertyID(assessmentNo);
        if (null != basicProperty) {
            oldProperty = basicProperty.getActiveProperty();
            propertyImpl = (PropertyImpl) basicProperty.getActiveProperty().createPropertyclone();
        }
        return propertyImpl;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String newForm(final Model model, @PathVariable String assessmentNo) {
        if (null != basicProperty && basicProperty.isUnderWorkflow()) {
            model.addAttribute("wfPendingMsg", "Could not do " + APPLICATION_TYPE_DEMOLITION
                    + " now, property is undergoing some work flow.");
            return TARGET_WORKFLOW_ERROR;
        }
        boolean hasChildPropertyUnderWorkflow = propertyTaxUtil.checkForParentUsedInBifurcation(basicProperty.getUpicNo());
        if(hasChildPropertyUnderWorkflow){
        	model.addAttribute("errorMsg", "Cannot proceed as this property is used in Bifurcation, which is under workflow");
            return PROPERTY_VALIDATION;
        }
        final Map<String, BigDecimal> propertyTaxDetails = ptDemandDAO.getDemandCollMap(basicProperty
                .getActiveProperty());
        Map<String, Installment> installmentMap = propertyTaxUtil.getInstallmentsForCurrYear(new Date());
        Installment installmentFirstHalf = installmentMap.get(PropertyTaxConstants.CURRENTYEAR_FIRST_HALF);
        BigDecimal currentPropertyTax = BigDecimal.ZERO;
        BigDecimal currentPropertyTaxDue = BigDecimal.ZERO;
        BigDecimal arrearPropertyTaxDue = BigDecimal.ZERO;
        BigDecimal currentFirstHalfTaxDue = BigDecimal.ZERO;
        if (DateUtils.between(new Date(), installmentFirstHalf.getFromDate(), installmentFirstHalf.getToDate())) {
            currentPropertyTax = propertyTaxDetails.get(CURR_FIRSTHALF_DMD_STR);
            currentPropertyTaxDue = propertyTaxDetails.get(CURR_FIRSTHALF_DMD_STR)
                    .subtract(propertyTaxDetails.get(CURR_FIRSTHALF_COLL_STR));
            arrearPropertyTaxDue = propertyTaxDetails.get(ARR_DMD_STR).subtract(propertyTaxDetails.get(ARR_COLL_STR));
        } else {
            currentPropertyTax = propertyTaxDetails.get(CURR_SECONDHALF_DMD_STR);
            currentPropertyTaxDue = propertyTaxDetails.get(CURR_SECONDHALF_DMD_STR)
                    .subtract(propertyTaxDetails.get(CURR_SECONDHALF_COLL_STR));
            currentFirstHalfTaxDue = propertyTaxDetails.get(CURR_FIRSTHALF_DMD_STR)
                    .subtract(propertyTaxDetails.get(CURR_FIRSTHALF_COLL_STR));
            arrearPropertyTaxDue = propertyTaxDetails.get(ARR_DMD_STR).subtract(propertyTaxDetails.get(ARR_COLL_STR))
                    .add(currentFirstHalfTaxDue);
        }
        model.addAttribute("currentPropertyTax", currentPropertyTax);
        model.addAttribute("currentPropertyTaxDue", currentPropertyTaxDue);
        model.addAttribute("arrearPropertyTaxDue", arrearPropertyTaxDue);
        if (arrearPropertyTaxDue.compareTo(BigDecimal.ZERO) > 0) {
            model.addAttribute("taxDuesErrorMsg", "Please clear property tax due for property demolition ");
            return TARGET_TAX_DUES;
        }
        propertyDemolitionService.addModelAttributes(model, basicProperty);
        model.addAttribute("stateType", propertyImpl.getClass().getSimpleName());
        prepareWorkflow(model, propertyImpl, new WorkflowContainer());
        return DEMOLITION_FORM;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public String demoltionFormSubmit(@ModelAttribute Property property, final BindingResult errors,
            final RedirectAttributes redirectAttrs, final Model model, final HttpServletRequest request,
            @RequestParam String workFlowAction) throws TaxCalculatorExeption {

        propertyDemolitionService.validateProperty(property, errors, request);

        if (errors.hasErrors()) {
            prepareWorkflow(model, (PropertyImpl) property, new WorkflowContainer());
            model.addAttribute("stateType", property.getClass().getSimpleName());
            propertyDemolitionService.addModelAttributes(model, basicProperty);
            return DEMOLITION_FORM;
        } else {

            final Character status = STATUS_WORKFLOW;
            Long approvalPosition = 0l;
            String approvalComent = "";

            if (request.getParameter("approvalComent") != null)
                approvalComent = request.getParameter("approvalComent");
            if (request.getParameter("workFlowAction") != null)
                workFlowAction = request.getParameter("workFlowAction");
            if (request.getParameter("approvalPosition") != null && !request.getParameter("approvalPosition").isEmpty())
                approvalPosition = Long.valueOf(request.getParameter("approvalPosition"));

            propertyDemolitionService.saveProperty(oldProperty, property, status, approvalComent, workFlowAction,
                    approvalPosition, DEMOLITION);

            model.addAttribute(
                    "successMessage",
                    "Property demolition data saved successfully in the system and forwarded to "
                            + propertyTaxUtil.getApproverUserName(approvalPosition));
            return DEMOLITION_SUCCESS;
        }
    }

    public BasicProperty getBasicProperty() {
        return basicProperty;
    }

    public void setBasicProperty(BasicProperty basicProperty) {
        this.basicProperty = basicProperty;
    }

}
