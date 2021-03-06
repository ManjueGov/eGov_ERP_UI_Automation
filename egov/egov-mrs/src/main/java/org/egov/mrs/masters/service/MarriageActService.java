/* eGov suite of products aim to improve the internal efficiency,transparency,
   accountability and the service delivery of the government  organizations.

    Copyright (C) <2015>  eGovernments Foundation

    The updated version of eGov suite of products as by eGovernments Foundation
    is available at http://www.egovernments.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see http://www.gnu.org/licenses/ or
    http://www.gnu.org/licenses/gpl.html .

    In addition to the terms of the GPL license to be adhered to in using this
    program, the following additional terms are to be complied with:

	1) All versions of this program, verbatim or modified must carry this
	   Legal Notice.

	2) Any misrepresentation of the origin of the material is prohibited. It
	   is required that all modified versions of this material be marked in
	   reasonable ways as different from the original version.

	3) This license does not grant any rights to any user of the program
	   with regards to rights under trademark law for use of the trade names
	   or trademarks of eGovernments Foundation.

  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.mrs.masters.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.mrs.masters.entity.MarriageAct;
import org.egov.mrs.masters.repository.MarriageActRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MarriageActService {
    @Autowired
    private MarriageActRepository marriageActRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    @Transactional
    public void create(final MarriageAct act) {
        marriageActRepository.save(act);
    }

    @Transactional
    public MarriageAct update(final MarriageAct act) {
        return marriageActRepository.save(act);
    }

    public MarriageAct getAct(final Long id) {
        return marriageActRepository.findById(id);
    }

    public List<MarriageAct> getActs() {
        return marriageActRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
    }

    public MarriageAct getProxy(final Long id) {
        return marriageActRepository.getOne(id);
    }

    @SuppressWarnings("unchecked")
    public List<MarriageAct> searchActs(final MarriageAct act) {
        final Criteria criteria = getCurrentSession().createCriteria(
                MarriageAct.class);
        if (null != act.getName())
            criteria.add(Restrictions.ilike("name", act.getName(), MatchMode.ANYWHERE));
        return criteria.list();
    }
}
