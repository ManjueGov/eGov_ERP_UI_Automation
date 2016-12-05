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
import static org.egov.works.utils.WorksConstants.WORKSMILESTONE_MILESTONESTATUS_COLUMN_NAME;
import static org.egov.works.utils.WorksConstants.WORKSMILESTONE_TYPEOFWORKNAME_COLUMN_NAME;
import static org.egov.works.utils.WorksConstants.WORKSMILESTONE_ULBCODE_COLUMN_NAME;
import static org.egov.works.utils.WorksConstants.WORKSMILESTONE_ULBNAME_COLUMN_NAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.works.elasticsearch.model.WorksIndexsRequest;
import org.egov.works.elasticsearch.model.WorksMilestoneIndexResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

/**
 * @author venki
 */

@Service
public class WorksMilestoneIndexService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorksMilestoneIndexService.class);

    public static final String WORKSMILESTONE_INDEX_NAME = "worksmilestone";
    public static final String WORKSTRANSACTION_INDEX_NAME = "workstransaction";

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    public List<WorksMilestoneIndexResponse> getAggregationResults(final WorksIndexsRequest worksIndexsRequest,
            final String orderingAggregationName) {

        Long startTime, timeTaken;
        AggregationBuilder aggregation;
        BoolQueryBuilder boolQuery;
        SearchQuery searchQuery;
        Aggregations worksAggr;
        final List<WorksMilestoneIndexResponse> finalResponses = new ArrayList<>();
        final List<WorksMilestoneIndexResponse> worksTransactionResponses = new ArrayList<>();
        final List<WorksMilestoneIndexResponse> worksMilestoneResponses = new ArrayList<>();
        final Map<String, WorksMilestoneIndexResponse> worksMilestoneResponseMap = new HashMap<String, WorksMilestoneIndexResponse>();
        WorksMilestoneIndexResponse wmIndexResponse;
        List<Terms.Bucket> resultBuckets;
        StringTerms saggr;
        LongTerms laggr;

        /* orderingAggregationName is the aggregation name by which we have to order the results */

        startTime = System.currentTimeMillis();
        boolQuery = prepareWhereClauseForTransaction(worksIndexsRequest);
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
            wmIndexResponse = new WorksMilestoneIndexResponse();
            wmIndexResponse.setReporttype(worksIndexsRequest.getReportType());
            final String fieldName = String.valueOf(entry.getKey());
            wmIndexResponse.setName(fieldName);
            final Sum totalEstimatedCostInLakhsAggregation = entry.getAggregations().get("totalestimatedcostinlakhs");
            final Sum totalWorkorderValueInLakhsAggregation = entry.getAggregations().get("totalworkordervalueinlakhs");
            final Sum totalBillAmountInLakhsAggregation = entry.getAggregations().get("totalbillamountinlakhs");
            final Sum totalPaidAmountInLakhsAggregation = entry.getAggregations().get("totalpaidamountinlakhs");
            wmIndexResponse.setTotalnoofworks(entry.getDocCount());
            wmIndexResponse.setTotalestimatedcostinlakhs(totalEstimatedCostInLakhsAggregation.getValue());
            wmIndexResponse.setTotalworkordervalueinlakhs(totalWorkorderValueInLakhsAggregation.getValue());
            wmIndexResponse.setTotalbillamountinlakhs(totalBillAmountInLakhsAggregation.getValue());
            wmIndexResponse.setTotalpaidamountinlakhs(totalPaidAmountInLakhsAggregation.getValue());
            worksTransactionResponses.add(wmIndexResponse);
        }

        boolQuery = prepareWhereClauseForMilestone(worksIndexsRequest);

        aggregation = AggregationBuilders.terms("by_aggregationField").field(orderingAggregationName).size(1000)
                .subAggregation(AggregationBuilders.avg("jan01to15actual").field("jan01to15actual"))
                .subAggregation(AggregationBuilders.avg("jan01to15target").field("jan01to15target"))
                .subAggregation(AggregationBuilders.avg("jan16to31actual").field("jan16to31actual"))
                .subAggregation(AggregationBuilders.avg("jan16to31target").field("jan16to31target"))
                .subAggregation(AggregationBuilders.avg("feb01to15actual").field("feb01to15actual"))
                .subAggregation(AggregationBuilders.avg("feb01to15target").field("feb01to15target"))
                .subAggregation(AggregationBuilders.avg("feb16to28or29actual").field("feb16to28or29actual"))
                .subAggregation(AggregationBuilders.avg("feb16to28or29target").field("feb16to28or29target"))
                .subAggregation(AggregationBuilders.avg("mar01to15actual").field("mar01to15actual"))
                .subAggregation(AggregationBuilders.avg("mar01to15target").field("mar01to15target"))
                .subAggregation(AggregationBuilders.avg("mar16to31actual").field("mar16to31actual"))
                .subAggregation(AggregationBuilders.avg("mar16to31target").field("mar16to31target"))
                .subAggregation(AggregationBuilders.avg("apr01to15actual").field("apr01to15actual"))
                .subAggregation(AggregationBuilders.avg("apr01to15target").field("apr01to15target"))
                .subAggregation(AggregationBuilders.avg("apr16to30actual").field("apr16to30actual"))
                .subAggregation(AggregationBuilders.avg("apr16to30target").field("apr16to30target"))
                .subAggregation(AggregationBuilders.avg("may01to15actual").field("may01to15actual"))
                .subAggregation(AggregationBuilders.avg("may01to15target").field("may01to15target"))
                .subAggregation(AggregationBuilders.avg("may16to31actual").field("may16to31actual"))
                .subAggregation(AggregationBuilders.avg("may16to31target").field("may16to31target"))
                .subAggregation(AggregationBuilders.avg("jun01to15actual").field("jun01to15actual"))
                .subAggregation(AggregationBuilders.avg("jun01to15target").field("jun01to15target"))
                .subAggregation(AggregationBuilders.avg("jun16to30actual").field("jun16to30actual"))
                .subAggregation(AggregationBuilders.avg("jun16to30target").field("jun16to30target"))
                .subAggregation(AggregationBuilders.avg("jul01to15actual").field("jul01to15actual"))
                .subAggregation(AggregationBuilders.avg("jul01to15target").field("jul01to15target"))
                .subAggregation(AggregationBuilders.avg("jul16to31actual").field("jul16to31actual"))
                .subAggregation(AggregationBuilders.avg("jul16to31target").field("jul16to31target"))
                .subAggregation(AggregationBuilders.avg("aug01to15actual").field("aug01to15actual"))
                .subAggregation(AggregationBuilders.avg("aug01to15target").field("aug01to15target"))
                .subAggregation(AggregationBuilders.avg("aug16to31actual").field("aug16to31actual"))
                .subAggregation(AggregationBuilders.avg("aug16to31target").field("aug16to31target"))
                .subAggregation(AggregationBuilders.avg("sep01to15actual").field("sep01to15actual"))
                .subAggregation(AggregationBuilders.avg("sep01to15target").field("sep01to15target"))
                .subAggregation(AggregationBuilders.avg("sep16to30actual").field("sep16to30actual"))
                .subAggregation(AggregationBuilders.avg("sep16to30target").field("sep16to30target"))
                .subAggregation(AggregationBuilders.avg("oct01to15actual").field("oct01to15actual"))
                .subAggregation(AggregationBuilders.avg("oct01to15target").field("oct01to15target"))
                .subAggregation(AggregationBuilders.avg("oct16to31actual").field("oct16to31actual"))
                .subAggregation(AggregationBuilders.avg("oct16to31target").field("oct16to31target"))
                .subAggregation(AggregationBuilders.avg("nov01to15actual").field("nov01to15actual"))
                .subAggregation(AggregationBuilders.avg("nov01to15target").field("nov01to15target"))
                .subAggregation(AggregationBuilders.avg("nov16to30actual").field("nov16to30actual"))
                .subAggregation(AggregationBuilders.avg("nov16to30target").field("nov16to30target"))
                .subAggregation(AggregationBuilders.avg("dec01to15actual").field("dec01to15actual"))
                .subAggregation(AggregationBuilders.avg("dec01to15target").field("dec01to15target"))
                .subAggregation(AggregationBuilders.avg("dec16to31actual").field("dec16to31actual"))
                .subAggregation(AggregationBuilders.avg("dec16to31target").field("dec16to31target"));

        searchQuery = new NativeSearchQueryBuilder().withIndices(WORKSMILESTONE_INDEX_NAME)
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
            wmIndexResponse = new WorksMilestoneIndexResponse();
            wmIndexResponse.setReporttype(worksIndexsRequest.getReportType());
            final String fieldName = String.valueOf(entry.getKey());
            wmIndexResponse.setName(fieldName);
            final Avg jan01to15actual = entry.getAggregations().get("jan01to15actual");
            final Avg jan01to15target = entry.getAggregations().get("jan01to15target");
            final Avg jan16to31actual = entry.getAggregations().get("jan16to31actual");
            final Avg jan16to31target = entry.getAggregations().get("jan16to31target");
            final Avg feb01to15actual = entry.getAggregations().get("feb01to15actual");
            final Avg feb01to15target = entry.getAggregations().get("feb01to15target");
            final Avg feb16to28or29actual = entry.getAggregations().get("feb16to28or29actual");
            final Avg feb16to28or29target = entry.getAggregations().get("feb16to28or29target");
            final Avg mar01to15actual = entry.getAggregations().get("mar01to15actual");
            final Avg mar01to15target = entry.getAggregations().get("mar01to15target");
            final Avg mar16to31actual = entry.getAggregations().get("mar16to31actual");
            final Avg mar16to31target = entry.getAggregations().get("mar16to31target");
            final Avg apr01to15actual = entry.getAggregations().get("apr01to15actual");
            final Avg apr01to15target = entry.getAggregations().get("apr01to15target");
            final Avg apr16to30actual = entry.getAggregations().get("apr16to30actual");
            final Avg apr16to30target = entry.getAggregations().get("apr16to30target");
            final Avg may01to15actual = entry.getAggregations().get("may01to15actual");
            final Avg may01to15target = entry.getAggregations().get("may01to15target");
            final Avg may16to31actual = entry.getAggregations().get("may16to31actual");
            final Avg may16to31target = entry.getAggregations().get("may16to31target");
            final Avg jun01to15actual = entry.getAggregations().get("jun01to15actual");
            final Avg jun01to15target = entry.getAggregations().get("jun01to15target");
            final Avg jun16to30actual = entry.getAggregations().get("jun16to30actual");
            final Avg jun16to30target = entry.getAggregations().get("jun16to30target");
            final Avg jul01to15actual = entry.getAggregations().get("jul01to15actual");
            final Avg jul01to15target = entry.getAggregations().get("jul01to15target");
            final Avg jul16to31actual = entry.getAggregations().get("jul16to31actual");
            final Avg jul16to31target = entry.getAggregations().get("jul16to31target");
            final Avg aug01to15actual = entry.getAggregations().get("aug01to15actual");
            final Avg aug01to15target = entry.getAggregations().get("aug01to15target");
            final Avg aug16to31actual = entry.getAggregations().get("aug16to31actual");
            final Avg aug16to31target = entry.getAggregations().get("aug16to31target");
            final Avg sep01to15actual = entry.getAggregations().get("sep01to15actual");
            final Avg sep01to15target = entry.getAggregations().get("sep01to15target");
            final Avg sep16to30actual = entry.getAggregations().get("sep16to30actual");
            final Avg sep16to30target = entry.getAggregations().get("sep16to30target");
            final Avg oct01to15actual = entry.getAggregations().get("oct01to15actual");
            final Avg oct01to15target = entry.getAggregations().get("oct01to15target");
            final Avg oct16to31actual = entry.getAggregations().get("oct16to31actual");
            final Avg oct16to31target = entry.getAggregations().get("oct16to31target");
            final Avg nov01to15actual = entry.getAggregations().get("nov01to15actual");
            final Avg nov01to15target = entry.getAggregations().get("nov01to15target");
            final Avg nov16to30actual = entry.getAggregations().get("nov16to30actual");
            final Avg nov16to30target = entry.getAggregations().get("nov16to30target");
            final Avg dec01to15actual = entry.getAggregations().get("dec01to15actual");
            final Avg dec01to15target = entry.getAggregations().get("dec01to15target");
            final Avg dec16to31actual = entry.getAggregations().get("dec16to31actual");
            final Avg dec16to31target = entry.getAggregations().get("dec16to31target");
            wmIndexResponse.setTotalnoofworks(entry.getDocCount());
            wmIndexResponse.setJan01to15actual(jan01to15actual.getValue());
            wmIndexResponse.setJan01to15target(jan01to15target.getValue());
            wmIndexResponse.setJan16to31actual(jan16to31actual.getValue());
            wmIndexResponse.setJan16to31target(jan16to31target.getValue());
            wmIndexResponse.setFeb01to15actual(feb01to15actual.getValue());
            wmIndexResponse.setFeb01to15target(feb01to15target.getValue());
            wmIndexResponse.setFeb16to28or29actual(feb16to28or29actual.getValue());
            wmIndexResponse.setFeb16to28or29target(feb16to28or29target.getValue());
            wmIndexResponse.setMar01to15actual(mar01to15actual.getValue());
            wmIndexResponse.setMar01to15target(mar01to15target.getValue());
            wmIndexResponse.setMar16to31actual(mar16to31actual.getValue());
            wmIndexResponse.setMar16to31target(mar16to31target.getValue());
            wmIndexResponse.setApr01to15actual(apr01to15actual.getValue());
            wmIndexResponse.setApr01to15target(apr01to15target.getValue());
            wmIndexResponse.setApr16to30actual(apr16to30actual.getValue());
            wmIndexResponse.setApr16to30target(apr16to30target.getValue());
            wmIndexResponse.setMay01to15actual(may01to15actual.getValue());
            wmIndexResponse.setMay01to15target(may01to15target.getValue());
            wmIndexResponse.setMay16to31actual(may16to31actual.getValue());
            wmIndexResponse.setMay16to31target(may16to31target.getValue());
            wmIndexResponse.setJun01to15actual(jun01to15actual.getValue());
            wmIndexResponse.setJun01to15target(jun01to15target.getValue());
            wmIndexResponse.setJun16to30actual(jun16to30actual.getValue());
            wmIndexResponse.setJun16to30target(jun16to30target.getValue());
            wmIndexResponse.setJul01to15actual(jul01to15actual.getValue());
            wmIndexResponse.setJul01to15target(jul01to15target.getValue());
            wmIndexResponse.setJul16to31actual(jul16to31actual.getValue());
            wmIndexResponse.setJul16to31target(jul16to31target.getValue());
            wmIndexResponse.setAug01to15actual(aug01to15actual.getValue());
            wmIndexResponse.setAug01to15target(aug01to15target.getValue());
            wmIndexResponse.setAug16to31actual(aug16to31actual.getValue());
            wmIndexResponse.setAug16to31target(aug16to31target.getValue());
            wmIndexResponse.setSep01to15actual(sep01to15actual.getValue());
            wmIndexResponse.setSep01to15target(sep01to15target.getValue());
            wmIndexResponse.setSep16to30actual(sep16to30actual.getValue());
            wmIndexResponse.setSep16to30target(sep16to30target.getValue());
            wmIndexResponse.setOct01to15actual(oct01to15actual.getValue());
            wmIndexResponse.setOct01to15target(oct01to15target.getValue());
            wmIndexResponse.setOct16to31actual(oct16to31actual.getValue());
            wmIndexResponse.setOct16to31target(oct16to31target.getValue());
            wmIndexResponse.setNov01to15actual(nov01to15actual.getValue());
            wmIndexResponse.setNov01to15target(nov01to15target.getValue());
            wmIndexResponse.setNov16to30actual(nov16to30actual.getValue());
            wmIndexResponse.setNov16to30target(nov16to30target.getValue());
            wmIndexResponse.setDec01to15actual(dec01to15actual.getValue());
            wmIndexResponse.setDec01to15target(dec01to15target.getValue());
            wmIndexResponse.setDec16to31actual(dec16to31actual.getValue());
            wmIndexResponse.setDec16to31target(dec16to31target.getValue());
            worksMilestoneResponses.add(wmIndexResponse);
        }

        prepareResponseMap(worksIndexsRequest, worksMilestoneResponses, worksMilestoneResponseMap);
        prepareResultList(worksIndexsRequest, worksTransactionResponses, worksMilestoneResponseMap, finalResponses);

        timeTaken = System.currentTimeMillis() - startTime;

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Time taken for setting values in getAggregationResults() is : " + timeTaken
                    + " (millisecs) ");

        return finalResponses;

    }

    public void getAggregationResultsForUlb(final WorksIndexsRequest worksIndexsRequest,
            final WorksMilestoneIndexResponse wmIndexResponse, final String orderingAggregationName) {

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

        boolQuery = prepareWhereClauseForMilestone(worksIndexsRequest);

        aggregation = AggregationBuilders.terms("by_aggregationField").field(orderingAggregationName).size(1000)
                .subAggregation(AggregationBuilders.avg("jan01to15actual").field("jan01to15actual"))
                .subAggregation(AggregationBuilders.avg("jan01to15target").field("jan01to15target"))
                .subAggregation(AggregationBuilders.avg("jan16to31actual").field("jan16to31actual"))
                .subAggregation(AggregationBuilders.avg("jan16to31target").field("jan16to31target"))
                .subAggregation(AggregationBuilders.avg("feb01to15actual").field("feb01to15actual"))
                .subAggregation(AggregationBuilders.avg("feb01to15target").field("feb01to15target"))
                .subAggregation(AggregationBuilders.avg("feb16to28or29actual").field("feb16to28or29actual"))
                .subAggregation(AggregationBuilders.avg("feb16to28or29target").field("feb16to28or29target"))
                .subAggregation(AggregationBuilders.avg("mar01to15actual").field("mar01to15actual"))
                .subAggregation(AggregationBuilders.avg("mar01to15target").field("mar01to15target"))
                .subAggregation(AggregationBuilders.avg("mar16to31actual").field("mar16to31actual"))
                .subAggregation(AggregationBuilders.avg("mar16to31target").field("mar16to31target"))
                .subAggregation(AggregationBuilders.avg("apr01to15actual").field("apr01to15actual"))
                .subAggregation(AggregationBuilders.avg("apr01to15target").field("apr01to15target"))
                .subAggregation(AggregationBuilders.avg("apr16to30actual").field("apr16to30actual"))
                .subAggregation(AggregationBuilders.avg("apr16to30target").field("apr16to30target"))
                .subAggregation(AggregationBuilders.avg("may01to15actual").field("may01to15actual"))
                .subAggregation(AggregationBuilders.avg("may01to15target").field("may01to15target"))
                .subAggregation(AggregationBuilders.avg("may16to31actual").field("may16to31actual"))
                .subAggregation(AggregationBuilders.avg("may16to31target").field("may16to31target"))
                .subAggregation(AggregationBuilders.avg("jun01to15actual").field("jun01to15actual"))
                .subAggregation(AggregationBuilders.avg("jun01to15target").field("jun01to15target"))
                .subAggregation(AggregationBuilders.avg("jun16to30actual").field("jun16to30actual"))
                .subAggregation(AggregationBuilders.avg("jun16to30target").field("jun16to30target"))
                .subAggregation(AggregationBuilders.avg("jul01to15actual").field("jul01to15actual"))
                .subAggregation(AggregationBuilders.avg("jul01to15target").field("jul01to15target"))
                .subAggregation(AggregationBuilders.avg("jul16to31actual").field("jul16to31actual"))
                .subAggregation(AggregationBuilders.avg("jul16to31target").field("jul16to31target"))
                .subAggregation(AggregationBuilders.avg("aug01to15actual").field("aug01to15actual"))
                .subAggregation(AggregationBuilders.avg("aug01to15target").field("aug01to15target"))
                .subAggregation(AggregationBuilders.avg("aug16to31actual").field("aug16to31actual"))
                .subAggregation(AggregationBuilders.avg("aug16to31target").field("aug16to31target"))
                .subAggregation(AggregationBuilders.avg("sep01to15actual").field("sep01to15actual"))
                .subAggregation(AggregationBuilders.avg("sep01to15target").field("sep01to15target"))
                .subAggregation(AggregationBuilders.avg("sep16to30actual").field("sep16to30actual"))
                .subAggregation(AggregationBuilders.avg("sep16to30target").field("sep16to30target"))
                .subAggregation(AggregationBuilders.avg("oct01to15actual").field("oct01to15actual"))
                .subAggregation(AggregationBuilders.avg("oct01to15target").field("oct01to15target"))
                .subAggregation(AggregationBuilders.avg("oct16to31actual").field("oct16to31actual"))
                .subAggregation(AggregationBuilders.avg("oct16to31target").field("oct16to31target"))
                .subAggregation(AggregationBuilders.avg("nov01to15actual").field("nov01to15actual"))
                .subAggregation(AggregationBuilders.avg("nov01to15target").field("nov01to15target"))
                .subAggregation(AggregationBuilders.avg("nov16to30actual").field("nov16to30actual"))
                .subAggregation(AggregationBuilders.avg("nov16to30target").field("nov16to30target"))
                .subAggregation(AggregationBuilders.avg("dec01to15actual").field("dec01to15actual"))
                .subAggregation(AggregationBuilders.avg("dec01to15target").field("dec01to15target"))
                .subAggregation(AggregationBuilders.avg("dec16to31actual").field("dec16to31actual"))
                .subAggregation(AggregationBuilders.avg("dec16to31target").field("dec16to31target"));

        searchQuery = new NativeSearchQueryBuilder().withIndices(WORKSMILESTONE_INDEX_NAME)
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
            final Avg jan01to15actual = entry.getAggregations().get("jan01to15actual");
            final Avg jan01to15target = entry.getAggregations().get("jan01to15target");
            final Avg jan16to31actual = entry.getAggregations().get("jan16to31actual");
            final Avg jan16to31target = entry.getAggregations().get("jan16to31target");
            final Avg feb01to15actual = entry.getAggregations().get("feb01to15actual");
            final Avg feb01to15target = entry.getAggregations().get("feb01to15target");
            final Avg feb16to28or29actual = entry.getAggregations().get("feb16to28or29actual");
            final Avg feb16to28or29target = entry.getAggregations().get("feb16to28or29target");
            final Avg mar01to15actual = entry.getAggregations().get("mar01to15actual");
            final Avg mar01to15target = entry.getAggregations().get("mar01to15target");
            final Avg mar16to31actual = entry.getAggregations().get("mar16to31actual");
            final Avg mar16to31target = entry.getAggregations().get("mar16to31target");
            final Avg apr01to15actual = entry.getAggregations().get("apr01to15actual");
            final Avg apr01to15target = entry.getAggregations().get("apr01to15target");
            final Avg apr16to30actual = entry.getAggregations().get("apr16to30actual");
            final Avg apr16to30target = entry.getAggregations().get("apr16to30target");
            final Avg may01to15actual = entry.getAggregations().get("may01to15actual");
            final Avg may01to15target = entry.getAggregations().get("may01to15target");
            final Avg may16to31actual = entry.getAggregations().get("may16to31actual");
            final Avg may16to31target = entry.getAggregations().get("may16to31target");
            final Avg jun01to15actual = entry.getAggregations().get("jun01to15actual");
            final Avg jun01to15target = entry.getAggregations().get("jun01to15target");
            final Avg jun16to30actual = entry.getAggregations().get("jun16to30actual");
            final Avg jun16to30target = entry.getAggregations().get("jun16to30target");
            final Avg jul01to15actual = entry.getAggregations().get("jul01to15actual");
            final Avg jul01to15target = entry.getAggregations().get("jul01to15target");
            final Avg jul16to31actual = entry.getAggregations().get("jul16to31actual");
            final Avg jul16to31target = entry.getAggregations().get("jul16to31target");
            final Avg aug01to15actual = entry.getAggregations().get("aug01to15actual");
            final Avg aug01to15target = entry.getAggregations().get("aug01to15target");
            final Avg aug16to31actual = entry.getAggregations().get("aug16to31actual");
            final Avg aug16to31target = entry.getAggregations().get("aug16to31target");
            final Avg sep01to15actual = entry.getAggregations().get("sep01to15actual");
            final Avg sep01to15target = entry.getAggregations().get("sep01to15target");
            final Avg sep16to30actual = entry.getAggregations().get("sep16to30actual");
            final Avg sep16to30target = entry.getAggregations().get("sep16to30target");
            final Avg oct01to15actual = entry.getAggregations().get("oct01to15actual");
            final Avg oct01to15target = entry.getAggregations().get("oct01to15target");
            final Avg oct16to31actual = entry.getAggregations().get("oct16to31actual");
            final Avg oct16to31target = entry.getAggregations().get("oct16to31target");
            final Avg nov01to15actual = entry.getAggregations().get("nov01to15actual");
            final Avg nov01to15target = entry.getAggregations().get("nov01to15target");
            final Avg nov16to30actual = entry.getAggregations().get("nov16to30actual");
            final Avg nov16to30target = entry.getAggregations().get("nov16to30target");
            final Avg dec01to15actual = entry.getAggregations().get("dec01to15actual");
            final Avg dec01to15target = entry.getAggregations().get("dec01to15target");
            final Avg dec16to31actual = entry.getAggregations().get("dec16to31actual");
            final Avg dec16to31target = entry.getAggregations().get("dec16to31target");
            wmIndexResponse.setTotalnoofworks(entry.getDocCount());
            wmIndexResponse.setJan01to15actual(jan01to15actual.getValue());
            wmIndexResponse.setJan01to15target(jan01to15target.getValue());
            wmIndexResponse.setJan16to31actual(jan16to31actual.getValue());
            wmIndexResponse.setJan16to31target(jan16to31target.getValue());
            wmIndexResponse.setFeb01to15actual(feb01to15actual.getValue());
            wmIndexResponse.setFeb01to15target(feb01to15target.getValue());
            wmIndexResponse.setFeb16to28or29actual(feb16to28or29actual.getValue());
            wmIndexResponse.setFeb16to28or29target(feb16to28or29target.getValue());
            wmIndexResponse.setMar01to15actual(mar01to15actual.getValue());
            wmIndexResponse.setMar01to15target(mar01to15target.getValue());
            wmIndexResponse.setMar16to31actual(mar16to31actual.getValue());
            wmIndexResponse.setMar16to31target(mar16to31target.getValue());
            wmIndexResponse.setApr01to15actual(apr01to15actual.getValue());
            wmIndexResponse.setApr01to15target(apr01to15target.getValue());
            wmIndexResponse.setApr16to30actual(apr16to30actual.getValue());
            wmIndexResponse.setApr16to30target(apr16to30target.getValue());
            wmIndexResponse.setMay01to15actual(may01to15actual.getValue());
            wmIndexResponse.setMay01to15target(may01to15target.getValue());
            wmIndexResponse.setMay16to31actual(may16to31actual.getValue());
            wmIndexResponse.setMay16to31target(may16to31target.getValue());
            wmIndexResponse.setJun01to15actual(jun01to15actual.getValue());
            wmIndexResponse.setJun01to15target(jun01to15target.getValue());
            wmIndexResponse.setJun16to30actual(jun16to30actual.getValue());
            wmIndexResponse.setJun16to30target(jun16to30target.getValue());
            wmIndexResponse.setJul01to15actual(jul01to15actual.getValue());
            wmIndexResponse.setJul01to15target(jul01to15target.getValue());
            wmIndexResponse.setJul16to31actual(jul16to31actual.getValue());
            wmIndexResponse.setJul16to31target(jul16to31target.getValue());
            wmIndexResponse.setAug01to15actual(aug01to15actual.getValue());
            wmIndexResponse.setAug01to15target(aug01to15target.getValue());
            wmIndexResponse.setAug16to31actual(aug16to31actual.getValue());
            wmIndexResponse.setAug16to31target(aug16to31target.getValue());
            wmIndexResponse.setSep01to15actual(sep01to15actual.getValue());
            wmIndexResponse.setSep01to15target(sep01to15target.getValue());
            wmIndexResponse.setSep16to30actual(sep16to30actual.getValue());
            wmIndexResponse.setSep16to30target(sep16to30target.getValue());
            wmIndexResponse.setOct01to15actual(oct01to15actual.getValue());
            wmIndexResponse.setOct01to15target(oct01to15target.getValue());
            wmIndexResponse.setOct16to31actual(oct16to31actual.getValue());
            wmIndexResponse.setOct16to31target(oct16to31target.getValue());
            wmIndexResponse.setNov01to15actual(nov01to15actual.getValue());
            wmIndexResponse.setNov01to15target(nov01to15target.getValue());
            wmIndexResponse.setNov16to30actual(nov16to30actual.getValue());
            wmIndexResponse.setNov16to30target(nov16to30target.getValue());
            wmIndexResponse.setDec01to15actual(dec01to15actual.getValue());
            wmIndexResponse.setDec01to15target(dec01to15target.getValue());
            wmIndexResponse.setDec16to31actual(dec16to31actual.getValue());
            wmIndexResponse.setDec16to31target(dec16to31target.getValue());
        }

        timeTaken = System.currentTimeMillis() - startTime;

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Time taken for setting values in getAggregationResultsForUlb() is : " + timeTaken
                    + " (millisecs) ");

    }

    private void prepareResultList(final WorksIndexsRequest worksIndexsRequest,
            final List<WorksMilestoneIndexResponse> worksTransactionResponses,
            final Map<String, WorksMilestoneIndexResponse> worksMilestoneResponseMap,
            final List<WorksMilestoneIndexResponse> finalResponses) {
        WorksMilestoneIndexResponse finalResponse;
        for (final WorksMilestoneIndexResponse transactionResponse : worksTransactionResponses) {
            if (worksMilestoneResponseMap.get(prepareGroupingKey(worksIndexsRequest, transactionResponse)) != null) {
                final WorksMilestoneIndexResponse milestoneResponse = worksMilestoneResponseMap
                        .get(prepareGroupingKey(worksIndexsRequest, transactionResponse));
                finalResponse = new WorksMilestoneIndexResponse();
                finalResponse = milestoneResponse;
                finalResponse.setTotalbillamountinlakhs(transactionResponse.getTotalbillamountinlakhs());
                finalResponse.setTotalestimatedcostinlakhs(transactionResponse.getTotalestimatedcostinlakhs());
                finalResponse.setTotalpaidamountinlakhs(transactionResponse.getTotalpaidamountinlakhs());
                finalResponse.setTotalworkordervalueinlakhs(transactionResponse.getTotalworkordervalueinlakhs());
                finalResponse.setMilestonenotcreatedcount(
                        transactionResponse.getTotalnoofworks() - milestoneResponse.getTotalnoofworks());
                finalResponse.setTotalnoofworks(transactionResponse.getTotalnoofworks());
            } else {
                finalResponse = new WorksMilestoneIndexResponse();
                finalResponse = transactionResponse;
                finalResponse.setMilestonenotcreatedcount(transactionResponse.getTotalnoofworks());
            }
            finalResponses.add(finalResponse);

        }

    }

    private void prepareResponseMap(final WorksIndexsRequest worksIndexsRequest,
            final List<WorksMilestoneIndexResponse> worksTransactionResponses,
            final Map<String, WorksMilestoneIndexResponse> worksTransactionResponseMap) {

        for (final WorksMilestoneIndexResponse response : worksTransactionResponses)
            worksTransactionResponseMap.put(prepareGroupingKey(worksIndexsRequest, response), response);

    }

    private String prepareGroupingKey(final WorksIndexsRequest worksMilestoneIndexRequest,
            final WorksMilestoneIndexResponse response) {

        String groupingKey = "";

        if (StringUtils.isNotBlank(worksMilestoneIndexRequest.getTypeofwork()))
            groupingKey = groupingKey + worksMilestoneIndexRequest.getTypeofwork();

        if (StringUtils.isNotBlank(worksMilestoneIndexRequest.getDistname()))
            groupingKey = groupingKey + worksMilestoneIndexRequest.getDistname();

        if (StringUtils.isNotBlank(worksMilestoneIndexRequest.getUlbname()))
            groupingKey = groupingKey + worksMilestoneIndexRequest.getUlbname();

        if (worksMilestoneIndexRequest.getUlbcodes() != null && !worksMilestoneIndexRequest.getUlbcodes().isEmpty())
            for (final String ulbcode : worksMilestoneIndexRequest.getUlbcodes())
                groupingKey = groupingKey + ulbcode;
        groupingKey = groupingKey + response.getName();

        return groupingKey;
    }

    private BoolQueryBuilder prepareWhereClauseForMilestone(final WorksIndexsRequest worksIndexsRequest) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(worksIndexsRequest.getTypeofwork()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery(WORKSMILESTONE_TYPEOFWORKNAME_COLUMN_NAME,
                    worksIndexsRequest.getTypeofwork()));

        if (StringUtils.isNotBlank(worksIndexsRequest.getDistname()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery(WORKSMILESTONE_DISTNAME_COLUMN_NAME, worksIndexsRequest.getDistname()));

        if (StringUtils.isNotBlank(worksIndexsRequest.getUlbname()))
            boolQuery = boolQuery
                    .filter(QueryBuilders.matchQuery(WORKSMILESTONE_ULBNAME_COLUMN_NAME, worksIndexsRequest.getUlbname()));

        if (worksIndexsRequest.getUlbcodes() != null && !worksIndexsRequest.getUlbcodes().isEmpty())
            boolQuery.filter(QueryBuilders.termsQuery(WORKSMILESTONE_ULBCODE_COLUMN_NAME, worksIndexsRequest.getUlbcodes()));

        if (worksIndexsRequest.getLineestimatedetailid() != null)
            boolQuery.filter(QueryBuilders.matchQuery(WORKSMILESTONE_ESTIMATEDETAILID_COLUMN_NAME,
                    worksIndexsRequest.getLineestimatedetailid()));

        boolQuery.filter(QueryBuilders.matchQuery(WORKSMILESTONE_MILESTONESTATUS_COLUMN_NAME, APPROVED));

        return boolQuery;
    }

    private BoolQueryBuilder prepareWhereClauseForTransaction(final WorksIndexsRequest worksIndexsRequest) {

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        if (StringUtils.isNotBlank(worksIndexsRequest.getTypeofwork()))
            boolQuery = boolQuery.filter(QueryBuilders.matchQuery(WORKSMILESTONE_TYPEOFWORKNAME_COLUMN_NAME,
                    worksIndexsRequest.getTypeofwork()));

        if (StringUtils.isNotBlank(worksIndexsRequest.getDistname()))
            boolQuery = boolQuery.filter(
                    QueryBuilders.matchQuery(WORKSMILESTONE_DISTNAME_COLUMN_NAME, worksIndexsRequest.getDistname()));

        if (StringUtils.isNotBlank(worksIndexsRequest.getUlbname()))
            boolQuery = boolQuery.filter(
                    QueryBuilders.matchQuery(WORKSMILESTONE_ULBNAME_COLUMN_NAME, worksIndexsRequest.getUlbname()));

        if (worksIndexsRequest.getUlbcodes() != null && !worksIndexsRequest.getUlbcodes().isEmpty())
            boolQuery.filter(
                    QueryBuilders.termsQuery(WORKSMILESTONE_ULBCODE_COLUMN_NAME, worksIndexsRequest.getUlbcodes()));

        if (worksIndexsRequest.getLineestimatedetailid() != null)
            boolQuery.filter(QueryBuilders.matchQuery(WORKSMILESTONE_ESTIMATEDETAILID_COLUMN_NAME,
                    worksIndexsRequest.getLineestimatedetailid()));

        boolQuery.filter(QueryBuilders.matchQuery(WORKSMILESTONE_LOASTATUS_COLUMN_NAME, APPROVED));

        return boolQuery;
    }

}