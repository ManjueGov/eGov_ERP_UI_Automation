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

package org.egov.wtms.web.controller.elasticSearch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.infra.admin.master.entity.City;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.elasticsearch.entity.ApplicationIndex;
import org.egov.infra.elasticsearch.entity.es.ApplicationDocument;
import org.egov.wtms.entity.es.ApplicationSearchRequest;
import org.egov.wtms.service.es.ApplicationSearchService;
import org.egov.wtms.utils.WaterTaxUtils;
import org.egov.wtms.utils.constants.WaterTaxConstants;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/elastic/appSearch/")
public class ApplicationSearchController {

    private final ApplicationSearchService applicationSearchService;
    @Autowired
    private WaterTaxUtils waterTaxUtils;

    @Autowired
    private CityService cityService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    public ApplicationSearchController(final ApplicationSearchService applicationSearchService,
            final CityService cityService) {
        this.applicationSearchService = applicationSearchService;
    }

    @RequestMapping(value = "/ajax-moduleTypepopulate", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<ApplicationIndex> getAppConfigs(@ModelAttribute("appConfig") @RequestParam final String appModuleName) {
        final List<ApplicationIndex> applicationIndexList = applicationSearchService
                .findApplicationIndexApplicationTypes(appModuleName);
        return applicationIndexList;
    }

    @ModelAttribute("applicationstatusList")
    public Map<String, String> connectionTypes() {
        return applicationSearchService.getApplicationStatusMap();
    }

    @ModelAttribute(value = "sourceList")
    public List<ApplicationIndex> getSourceList() {
        return applicationSearchService.getSourceList();
    }

    @ModelAttribute
    public ApplicationSearchRequest searchRequest() {
        return new ApplicationSearchRequest();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String newSearchForm(final Model model) {
        model.addAttribute("citizenRole", waterTaxUtils.getCitizenUserRole());
        return "applicationSearch-newForm";
    }

    @ModelAttribute(value = "modulesList")
    public List<ApplicationIndex> findApplicationIndexModules() {
        return applicationSearchService.findApplicationIndexModules();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public List<ApplicationSearchRequest> searchApplication(
            @ModelAttribute final ApplicationSearchRequest searchRequest) {
        final SimpleDateFormat ft = new SimpleDateFormat("dd/MM/yyyy");
        List<ApplicationDocument> applicationDocumentList = new ArrayList<ApplicationDocument>();
        final List<ApplicationSearchRequest> finalResult = new ArrayList<ApplicationSearchRequest>();
        applicationDocumentList = findAllAppicationIndexByFilter(searchRequest);
        for (final ApplicationDocument applicationIndex : applicationDocumentList) {
            final ApplicationSearchRequest customerObj = new ApplicationSearchRequest();
            customerObj.setApplicantName(applicationIndex.getApplicantName());
            customerObj.setConsumerCode(applicationIndex.getConsumerCode());
            customerObj.setApplicationAddress(applicationIndex.getApplicantAddress());
            customerObj.setApplicationNumber(applicationIndex.getApplicationNumber());
            customerObj.setOwnername(applicationIndex.getOwnerName());
            customerObj.setSource(applicationIndex.getChannel());
            customerObj.setApplicationType(applicationIndex.getApplicationType());
            if (applicationIndex.getApplicationDate() != null)
                customerObj.setApplicationCreatedDate(ft.format(applicationIndex.getApplicationDate()));
            customerObj.setUrl(applicationIndex.getUrl());
            customerObj.setApplicationStatus(applicationIndex.getStatus());
            finalResult.add(customerObj);
        }
        return finalResult;

    }

    private BoolQueryBuilder getFilterQuery(final ApplicationSearchRequest searchRequest) {
        final City cityWebsite = cityService.getCityByCode(ApplicationThreadLocals.getCityCode());
        BoolQueryBuilder boolQuery = new BoolQueryBuilder();
        QueryBuilders.boolQuery().filter(QueryBuilders.termQuery("cityName", cityWebsite.getName()));
        if (StringUtils.isNotBlank(searchRequest.getApplicantName()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("applicantName", searchRequest.getApplicantName()));
        if (StringUtils.isNotBlank(searchRequest.getConsumerCode()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("consumerCode", searchRequest.getConsumerCode()));
        if (StringUtils.isNotBlank(searchRequest.getApplicationStatus()))
            if (searchRequest.equals(WaterTaxConstants.APPLICATIONSTATUSOPEN))
                boolQuery = boolQuery.filter(QueryBuilders.matchQuery("isClosed", Integer.toString(1)));
            else if (searchRequest.getApplicationStatus().equals(WaterTaxConstants.APPLICATIONSTATUSCLOSED))
                boolQuery = boolQuery.filter(QueryBuilders.matchQuery("isClosed", Integer.toString(0)));
            else {
                // boolQuery =
                // boolQuery.filter(QueryBuilders.matchQuery("isClosed",
                // searchRequest.getApplicationStatus()));
            }
        if (StringUtils.isNotBlank(searchRequest.getMobileNumber()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("mobileNumber", searchRequest.getMobileNumber()));

        if (StringUtils.isNotBlank(searchRequest.getApplicationNumber()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery("applicationNumber", searchRequest.getApplicationNumber()));

        if (StringUtils.isNotBlank(searchRequest.getApplicationType()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery("applicationType", searchRequest.getApplicationType()));

        if (StringUtils.isNotBlank(searchRequest.getSource()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("channel", searchRequest.getSource()));

        if (StringUtils.isNotBlank(searchRequest.getModuleName()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery("moduleName", searchRequest.getModuleName()));

        if (StringUtils.isNotBlank(searchRequest.getFromDate()) && StringUtils.isNotBlank(searchRequest.getToDate()))
            boolQuery = boolQuery.must(QueryBuilders.rangeQuery("applicationDate").from(searchRequest.getFromDate())
                    .to(searchRequest.getToDate()));

        return boolQuery;
    }

    public List<ApplicationDocument> findAllAppicationIndexByFilter(final ApplicationSearchRequest searchRequest) {

        final BoolQueryBuilder query = getFilterQuery(searchRequest);

        final SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withIndices(WaterTaxConstants.APPLICATION_TAX_INDEX_NAME).withQuery(query).build();

        final List<ApplicationDocument> sampleEntities = elasticsearchTemplate.queryForList(searchQuery,
                ApplicationDocument.class);
        return sampleEntities;
    }

}