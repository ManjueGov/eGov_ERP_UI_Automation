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

package org.egov.works.elasticsearch.service;

import static org.egov.works.utils.WorksConstants.APPROVED;
import static org.egov.works.utils.WorksConstants.WORKSMILESTONE_DISTNAME_COLUMN_NAME;
import static org.egov.works.utils.WorksConstants.WORKSMILESTONE_ESTIMATEDETAILID_COLUMN_NAME;
import static org.egov.works.utils.WorksConstants.WORKSMILESTONE_LOASTATUS_COLUMN_NAME;
import static org.egov.works.utils.WorksConstants.WORKSMILESTONE_TYPEOFWORKNAME_COLUMN_NAME;
import static org.egov.works.utils.WorksConstants.WORKSMILESTONE_ULBNAME_COLUMN_NAME;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.works.elasticsearch.model.WorksIndexsRequest;
import org.egov.works.elasticsearch.model.WorksMilestoneIndexResponse;
import org.egov.works.elasticsearch.model.WorksTransactionIndex;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

/**
 * @author venki
 */
@Service
public class WorksTransactionIndexService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorksTransactionIndexService.class);

    public static final String WORKSTRANSACTION_INDEX_NAME = "workstransaction";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public List<WorksMilestoneIndexResponse> getWorksTransactionDetails(
            final WorksIndexsRequest worksIndexsRequest) {

        Long startTime, timeTaken;
        final BoolQueryBuilder boolQuery = prepareWhereClause(worksIndexsRequest);
        final SearchQuery searchQuery;
        final List<WorksTransactionIndex> worksTransactionIndexs;
        final List<WorksMilestoneIndexResponse> resultList = new ArrayList<WorksMilestoneIndexResponse>();
        WorksMilestoneIndexResponse wmIndexResponse;
        startTime = System.currentTimeMillis();
        searchQuery = new NativeSearchQueryBuilder().withIndices(WORKSTRANSACTION_INDEX_NAME)
                .withPageable(new PageRequest(0, 1000))
                .withQuery(boolQuery)
                .build();
        worksTransactionIndexs = elasticsearchTemplate.queryForList(searchQuery, WorksTransactionIndex.class);

        for (final WorksTransactionIndex response : worksTransactionIndexs) {
            wmIndexResponse = new WorksMilestoneIndexResponse();
            wmIndexResponse.setLineestimatedetailid(response.getLineestimatedetailid());
            wmIndexResponse.setFund(response.getLineestimatefund());
            wmIndexResponse.setScheme(response.getLineestimatescheme());
            wmIndexResponse.setSubscheme(response.getLineestimatesubscheme());
            wmIndexResponse.setWard(response.getLineestimateboundary());
            wmIndexResponse.setEstimatenumber(response.getEstimatenumber());
            wmIndexResponse.setWin(response.getEstimatewin());
            wmIndexResponse.setNameofthework(response.getNameofthework());
            wmIndexResponse.setContractornamecode(response.getLoanameofagency() + "/" + response.getLoacontractorcode());
            wmIndexResponse.setAgreementnumber(response.getLoanumber());
            wmIndexResponse.setAgreementdate(response.getAgreementdate());
            wmIndexResponse.setWorkstatus(response.getWorkstatus());
            wmIndexResponse.setContractperiod(response.getLoacontractperiod());
            resultList.add(wmIndexResponse);
        }

        timeTaken = System.currentTimeMillis() - startTime;

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Time taken by getWorksTransactionDetails is : " + timeTaken + " (millisecs) ");

        return resultList;
    }

    public void getAggregationResults(final WorksIndexsRequest worksIndexsRequest,
            final WorksMilestoneIndexResponse wfmileresponse,
            final String orderingAggregationName) {

        Long startTime, timeTaken;
        AggregationBuilder aggregation;
        BoolQueryBuilder boolQuery;
        SearchQuery searchQuery;
        Aggregations worksAggr;
        List<Terms.Bucket> resultBuckets;
        StringTerms saggr;
        LongTerms laggr;

        /* orderingAggregationName is the aggregation name by which we have to order the results */

        startTime = System.currentTimeMillis();
        boolQuery = prepareWhereClause(worksIndexsRequest);
        aggregation = AggregationBuilders.terms("by_aggregationField").field(orderingAggregationName).size(1000)
                .subAggregation(AggregationBuilders.sum("totalestimatedcostinlakhs").field("estimatevalue"))
                .subAggregation(AggregationBuilders.sum("totalworkordervalueinlakhs").field("loaamount"))
                .subAggregation(AggregationBuilders.sum("totalbillamountinlakhs").field("loatotalbillamt"))
                .subAggregation(AggregationBuilders.sum("totalpaidamountinlakhs").field("loatotalpaidamt"));

        searchQuery = new NativeSearchQueryBuilder().withIndices(WORKSTRANSACTION_INDEX_NAME)
                .withQuery(boolQuery)
                .withSort(SortBuilders.fieldSort(orderingAggregationName).order(SortOrder.DESC))
                .addAggregation(aggregation).build();

        worksAggr = elasticsearchTemplate.query(searchQuery, response -> response.getAggregations());

        if (!orderingAggregationName.equals(WORKSMILESTONE_ESTIMATEDETAILID_COLUMN_NAME)) {
            saggr = worksAggr.get("by_aggregationField");
            resultBuckets = saggr.getBuckets();
        } else {
            laggr = worksAggr.get("by_aggregationField");
            resultBuckets = laggr.getBuckets();
        }

        for (final Terms.Bucket entry : resultBuckets) {
            wfmileresponse.setReporttype(worksIndexsRequest.getReportType());
            final String fieldName = String.valueOf(entry.getKey());
            wfmileresponse.setName(fieldName);
            final Sum totalEstimatedCostInLakhsAggregation = entry.getAggregations().get("totalestimatedcostinlakhs");
            final Sum totalWorkorderValueInLakhsAggregation = entry.getAggregations().get("totalworkordervalueinlakhs");
            final Sum totalBillAmountInLakhsAggregation = entry.getAggregations().get("totalbillamountinlakhs");
            final Sum totalPaidAmountInLakhsAggregation = entry.getAggregations().get("totalpaidamountinlakhs");
            wfmileresponse.setTotalnoofworks(entry.getDocCount());
            wfmileresponse.setTotalestimatedcostinlakhs(totalEstimatedCostInLakhsAggregation.getValue());
            wfmileresponse.setTotalworkordervalueinlakhs(totalWorkorderValueInLakhsAggregation.getValue());
            wfmileresponse.setTotalbillamountinlakhs(totalBillAmountInLakhsAggregation.getValue());
            wfmileresponse.setTotalpaidamountinlakhs(totalPaidAmountInLakhsAggregation.getValue());
        }

        timeTaken = System.currentTimeMillis() - startTime;

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Time taken for setting values in getAggregationResults() is : " + timeTaken
                    + " (millisecs) ");

    }

    private BoolQueryBuilder prepareWhereClause(final WorksIndexsRequest worksIndexsRequest) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(worksIndexsRequest.getTypeofwork()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery(WORKSMILESTONE_TYPEOFWORKNAME_COLUMN_NAME,
                    worksIndexsRequest.getTypeofwork()));

        if (StringUtils.isNotBlank(worksIndexsRequest.getDistname()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery(WORKSMILESTONE_DISTNAME_COLUMN_NAME,
                    worksIndexsRequest.getDistname()));

        if (StringUtils.isNotBlank(worksIndexsRequest.getUlbname()))
            boolQuery = boolQuery.filter(
                    QueryBuilders.matchQuery(WORKSMILESTONE_ULBNAME_COLUMN_NAME, worksIndexsRequest.getUlbname()));

        if (worksIndexsRequest.getLineestimatedetailid() != null)
            boolQuery.filter(
                    QueryBuilders.matchQuery(WORKSMILESTONE_ESTIMATEDETAILID_COLUMN_NAME,
                            worksIndexsRequest.getLineestimatedetailid()));

        boolQuery.filter(QueryBuilders.matchQuery(WORKSMILESTONE_LOASTATUS_COLUMN_NAME, APPROVED));

        return boolQuery;

    }

}