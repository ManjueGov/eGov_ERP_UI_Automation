package org.egov.council.web.controller;

import static org.egov.council.utils.constants.CouncilConstants.CHECK_BUDGET;
import static org.egov.council.utils.constants.CouncilConstants.IMPLEMENTATIONSTATUS;
import static org.egov.council.utils.constants.CouncilConstants.IMPLEMENTATION_STATUS_FINISHED;
import static org.egov.council.utils.constants.CouncilConstants.MODULE_FULLNAME;
import static org.egov.council.utils.constants.CouncilConstants.REVENUE_HIERARCHY_TYPE;
import static org.egov.council.utils.constants.CouncilConstants.WARD;
import static org.egov.infra.web.utils.WebUtils.toJSON;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.council.autonumber.PreambleNumberGenerator;
import org.egov.council.entity.CouncilPreamble;
import org.egov.council.entity.enums.PreambleType;
import org.egov.council.service.CouncilPreambleService;
import org.egov.council.service.CouncilThirdPartyService;
import org.egov.council.utils.constants.CouncilConstants;
import org.egov.council.web.adaptor.CouncilPreambleJsonAdaptor;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.admin.master.service.BoundaryService;
import org.egov.infra.admin.master.service.DepartmentService;
import org.egov.infra.filestore.service.FileStoreService;
import org.egov.infra.utils.FileStoreUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/councilpreamble")
public class CouncilPreambleController extends GenericWorkFlowController {
	private final static String COUNCILPREAMBLE_NEW = "councilpreamble-new";
	private final static String COUNCILPREAMBLE_RESULT = "councilpreamble-result";
	private final static String COUNCILPREAMBLE_EDIT = "councilpreamble-edit";
	private final static String COUNCILPREAMBLE_VIEW = "councilpreamble-view";
	private final static String COUNCILPREAMBLE_SEARCH = "councilpreamble-search";
	private final static String COUNCILPREAMBLE_UPDATE_STATUS = "councilpreamble-update-status";
	private final static String COMMONERRORPAGE = "common-error-page";
	@Autowired
	private CouncilPreambleService councilPreambleService;
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private DepartmentService departmentService;

	@Autowired
	private EgwStatusHibernateDAO egwStatusHibernateDAO;

	@Autowired
	private AutonumberServiceBeanResolver autonumberServiceBeanResolver;

	@Autowired
	private CouncilThirdPartyService councilThirdPartyService;

	@Qualifier("fileStoreService")
	protected @Autowired FileStoreService fileStoreService;

	protected @Autowired FileStoreUtils fileStoreUtils;
	@Autowired
	private BoundaryService boundaryService;

	@Autowired
	private AppConfigValueService appConfigValueService;

	private static final Logger LOGGER = Logger
			.getLogger(CouncilPreambleController.class);

	public @ModelAttribute("departments") List<Department> getDepartmentList() {
		return departmentService.getAllDepartments();
	}

	public @ModelAttribute("wards") List<Boundary> getWardsList() {
		return boundaryService
				.getActiveBoundariesByBndryTypeNameAndHierarchyTypeName(WARD,
						REVENUE_HIERARCHY_TYPE);
	}

	public @ModelAttribute("URL") String getAppConfigValues() {
		List<AppConfigValues> appConfigValue = appConfigValueService
				.getConfigValuesByModuleAndKey(MODULE_FULLNAME, CHECK_BUDGET);
		if (appConfigValue != null && appConfigValue.size() > 0)
			return appConfigValueService
					.getConfigValuesByModuleAndKey(MODULE_FULLNAME,
							CHECK_BUDGET).get(0).getValue();
		return "";
	}

	public @ModelAttribute("implementationStatus") List<EgwStatus> getImplementationStatusList() {
		return egwStatusHibernateDAO.getStatusByModule(IMPLEMENTATIONSTATUS);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newForm(final Model model) {
		CouncilPreamble councilPreamble = new CouncilPreamble();
		councilPreamble.setType(PreambleType.GENERAL);
		model.addAttribute("councilPreamble", councilPreamble);
		prepareWorkFlowOnLoad(model, councilPreamble);
		model.addAttribute("currentState", "NEW");

		return COUNCILPREAMBLE_NEW;
	}

	private void prepareWorkFlowOnLoad(final Model model,
			CouncilPreamble councilPreamble) {
		WorkflowContainer workFlowContainer = new WorkflowContainer();
		prepareWorkflow(model, councilPreamble, workFlowContainer);
		model.addAttribute("stateType", councilPreamble.getClass()
				.getSimpleName());
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String create(
			@Valid @ModelAttribute final CouncilPreamble councilPreamble,
			final BindingResult errors,
			@RequestParam final MultipartFile attachments, final Model model,
			final HttpServletRequest request,
			final RedirectAttributes redirectAttrs,
			@RequestParam String workFlowAction) {
		if (errors.hasErrors()) {
			prepareWorkFlowOnLoad(model, councilPreamble);
			return COUNCILPREAMBLE_NEW;
		}

		if (attachments != null && attachments.getSize() > 0) {
			try {
				councilPreamble.setFilestoreid(fileStoreService.store(
						attachments.getInputStream(),
						attachments.getOriginalFilename(),
						attachments.getContentType(),
						CouncilConstants.MODULE_NAME));
			} catch (IOException e) {
				LOGGER.error("Error in loading documents" + e.getMessage(), e);
			}
		}
		PreambleNumberGenerator preamblenumbergenerator = autonumberServiceBeanResolver
				.getAutoNumberServiceFor(PreambleNumberGenerator.class);
		councilPreamble.setPreambleNumber(preamblenumbergenerator
				.getNextNumber(councilPreamble));
		councilPreamble.setStatus(egwStatusHibernateDAO
				.getStatusByModuleAndCode(CouncilConstants.PREAMBLE_MODULENAME,
						CouncilConstants.PREAMBLE_STATUS_CREATED));
		councilPreamble.setType(PreambleType.GENERAL);

		Long approvalPosition = 0l;
		String approvalComment = "";
		String approverName = "";
		String nextDesignation = "";
		if (request.getParameter("approvalComent") != null)
			approvalComment = request.getParameter("approvalComent");
		if (request.getParameter("workFlowAction") != null)
			workFlowAction = request.getParameter("workFlowAction");
		if (request.getParameter("approverName") != null)
			approverName = request.getParameter("approverName");
		if (request.getParameter("nextDesignation") != null)
			nextDesignation = request.getParameter("nextDesignation");
		if (request.getParameter("approvalPosition") != null
				&& !request.getParameter("approvalPosition").isEmpty())
			approvalPosition = Long.valueOf(request
					.getParameter("approvalPosition"));

		councilPreambleService.create(councilPreamble, approvalPosition,
				approvalComment, workFlowAction);

		String message = messageSource.getMessage("msg.councilPreamble.create",
				new String[] {
						approverName.concat("~").concat(nextDesignation),
						councilPreamble.getPreambleNumber() }, null);
		redirectAttrs.addFlashAttribute("message", message);
		return "redirect:/councilpreamble/result/" + councilPreamble.getId();
	}

	@RequestMapping(value = "/downloadfile/{fileStoreId}")
	public void download(@PathVariable final String fileStoreId,
			final HttpServletResponse response) throws IOException {
		fileStoreUtils.fetchFileAndWriteToStream(fileStoreId,
				CouncilConstants.MODULE_NAME, false, response);
	}

	@RequestMapping(value = "/result/{id}", method = RequestMethod.GET)
	public String result(@PathVariable("id") final Long id, Model model) {
		CouncilPreamble councilPreamble = councilPreambleService.findOne(id);
		model.addAttribute("councilPreamble", councilPreamble);
		model.addAttribute("applicationHistory",
				councilThirdPartyService.getHistory(councilPreamble));
		prepareWorkFlowOnLoad(model, councilPreamble);
		return COUNCILPREAMBLE_RESULT;
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String update(
			@Valid @ModelAttribute final CouncilPreamble councilPreamble,
			final Model model, @RequestParam final MultipartFile attachments,
			final BindingResult errors, final HttpServletRequest request,
			final RedirectAttributes redirectAttrs,
			@RequestParam String workFlowAction) {
		if (errors.hasErrors()) {
			prepareWorkFlowOnLoad(model, councilPreamble);
			model.addAttribute("currentState", councilPreamble
					.getCurrentState().getValue());
			return COUNCILPREAMBLE_EDIT;
		}
		if (attachments != null && attachments.getSize() > 0) {
			try {
				councilPreamble.setFilestoreid(fileStoreService.store(
						attachments.getInputStream(),
						attachments.getOriginalFilename(),
						attachments.getContentType(),
						CouncilConstants.MODULE_NAME));
			} catch (IOException e) {
				LOGGER.error(
						"Error in loading Employee photo" + e.getMessage(), e);
			}
		}

		Long approvalPosition = 0l;
		String approvalComment = StringUtils.EMPTY;
		String message = StringUtils.EMPTY;
		if (request.getParameter("approvalComent") != null)
			approvalComment = request.getParameter("approvalComent");
		if (request.getParameter("workFlowAction") != null)
			workFlowAction = request.getParameter("workFlowAction");
		if (request.getParameter("approvalPosition") != null
				&& !request.getParameter("approvalPosition").isEmpty())
			approvalPosition = Long.valueOf(request
					.getParameter("approvalPosition"));

		councilPreambleService.update(councilPreamble, approvalPosition,
				approvalComment, workFlowAction);
		if (null != workFlowAction) {
			if (CouncilConstants.WF_STATE_REJECT
					.equalsIgnoreCase(workFlowAction)) {
				message = getMessage("msg.councilPreamble.reject",
						councilPreamble);
			} else if (CouncilConstants.WF_APPROVE_BUTTON
					.equalsIgnoreCase(workFlowAction)) {
				message = getMessage("msg.councilPreamble.success",
						councilPreamble);
			} else if (CouncilConstants.WF_FORWARD_BUTTON
					.equalsIgnoreCase(workFlowAction)) {
				message = getMessage("msg.councilPreamble.forward",
						councilPreamble);
			} else if (CouncilConstants.WF_PROVIDE_INFO_BUTTON
					.equalsIgnoreCase(workFlowAction)) {
				message = getMessage("msg.councilPreamble.moreInfo",
						councilPreamble);
			}
			redirectAttrs.addFlashAttribute("message", message);
		}
		return "redirect:/councilpreamble/result/" + councilPreamble.getId();
	}

	@RequestMapping(value = "/updateimplimentaionstatus/{id}", method = RequestMethod.GET)
	public String updateStatus(@PathVariable("id") final Long id, final Model model,
			final HttpServletResponse response) throws IOException {
		CouncilPreamble councilPreamble = councilPreambleService.findOne(id);
		if(null!=councilPreamble.getImplementationStatus() && IMPLEMENTATION_STATUS_FINISHED.equals(councilPreamble.getImplementationStatus().getCode())){
            model.addAttribute("message", "msg.councilPreamble.alreadyfinished");
        return COMMONERRORPAGE;
        }
		model.addAttribute("councilPreamble", councilPreamble);
		model.addAttribute("applicationHistory",
				councilThirdPartyService.getHistory(councilPreamble));
		return COUNCILPREAMBLE_UPDATE_STATUS;
	}
	@RequestMapping(value = "/updateimplimentaionstatus", method = RequestMethod.POST)
	public String updateImplementationStatus(
			@Valid @ModelAttribute final CouncilPreamble councilPreamble,
			final Model model, final BindingResult errors,
			final HttpServletRequest request,
			final RedirectAttributes redirectAttrs) {
		
		if(councilPreamble.getImplementationStatus().getCode()!=null){
		councilPreambleService.updateImplementationStatus(councilPreamble);
		}
		redirectAttrs.addFlashAttribute("message",messageSource.getMessage("msg.councilPreamble.update", null, null));
		return "redirect:/councilpreamble/result/" + councilPreamble.getId();
	}

	private String getMessage(String messageLabel,
			final CouncilPreamble councilPreamble) {
		String message;
		message = messageSource.getMessage(messageLabel,
				new String[] { councilPreamble.getPreambleNumber() }, null);
		return message;
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String edit(@PathVariable("id") final Long id, final Model model,
			final HttpServletResponse response) throws IOException {
		CouncilPreamble councilPreamble = councilPreambleService.findOne(id);
		WorkflowContainer workFlowContainer = new WorkflowContainer();
		prepareWorkflow(model, councilPreamble, workFlowContainer);
		model.addAttribute("stateType", councilPreamble.getClass()
				.getSimpleName());
		model.addAttribute("currentState", councilPreamble.getCurrentState()
				.getValue());
		model.addAttribute("councilPreamble", councilPreamble);
		model.addAttribute("applicationHistory",
				councilThirdPartyService.getHistory(councilPreamble));

		if (councilPreamble.getStatus().getCode()
				.equals("PREAMBLEAPPROVEDFORMOM")) {
			return COUNCILPREAMBLE_VIEW;
		}
			return COUNCILPREAMBLE_EDIT;
	
	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable("id") final Long id, Model model) {
		CouncilPreamble councilPreamble = councilPreambleService.findOne(id);
		model.addAttribute("councilPreamble", councilPreamble);
		model.addAttribute("applicationHistory",
				councilThirdPartyService.getHistory(councilPreamble));

		return COUNCILPREAMBLE_VIEW;
	}

	@RequestMapping(value = "/search/{mode}", method = RequestMethod.GET)
	public String search(@PathVariable("mode") final String mode, Model model) {
		model.addAttribute("councilPreamble", new CouncilPreamble());
		return COUNCILPREAMBLE_SEARCH;

	}

	@RequestMapping(value = "/ajaxsearch/{mode}", method = RequestMethod.POST, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String ajaxsearch(
			@PathVariable("mode") final String mode, Model model,
			@ModelAttribute final CouncilPreamble councilPreamble) {
		List<CouncilPreamble> searchResultList;

		if (mode.equalsIgnoreCase("edit")) {
			searchResultList = councilPreambleService
					.searchFinalizedPreamble(councilPreamble);
		} else {
			searchResultList = councilPreambleService.search(councilPreamble);
		}
		final String prambleJsonData = new StringBuilder("{\"data\":")
				.append(toJSON(searchResultList, CouncilPreamble.class,
						CouncilPreambleJsonAdaptor.class)).append("}")
				.toString();

		return prambleJsonData;
	}

}
