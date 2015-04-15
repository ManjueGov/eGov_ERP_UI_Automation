/**
 * eGov suite of products aim to improve the internal efficiency,transparency, 
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
package org.egov.builder.entities;

import java.lang.reflect.Field;
import java.util.Date;

import org.egov.infra.admin.master.entity.BoundaryType;
import org.egov.infra.admin.master.entity.HierarchyType;
import org.joda.time.DateTime;

public class BoundaryTypeBuilder {

    private final BoundaryType boundaryTypeImpl;

    // use this count where unique names,desciptions etc required
    private static int count;

    public BoundaryTypeBuilder() {
        boundaryTypeImpl = new BoundaryType();
        count++;
    }

    public BoundaryTypeBuilder withUpdatedTime(final Date updatedTime) {
        boundaryTypeImpl.setLastModifiedDate(new DateTime(updatedTime));;
        return this;
    }

    public BoundaryTypeBuilder withId(final Integer id) {
        try {
            final Field idField = boundaryTypeImpl.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(boundaryTypeImpl, id);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public BoundaryTypeBuilder withParentBndryType(final BoundaryType parentBndryType) {
        boundaryTypeImpl.setParent(parentBndryType);
        return this;
    }

    public BoundaryTypeBuilder withName(final String name) {
        boundaryTypeImpl.setName(name);
        return this;
    }

    public BoundaryTypeBuilder withParentName(final String parentName) {
        boundaryTypeImpl.setParentName(parentName);
        return this;
    }

    public BoundaryTypeBuilder withBndryTypeLocal(final String bndryTypeLocal) {
        //boundaryTypeImpl.setBndryTypeLocal(bndryTypeLocal);
        return this;
    }

    public BoundaryTypeBuilder withHierarchy(final Long hierarchy) {
        boundaryTypeImpl.setHierarchy(hierarchy);
        return this;
    }

    public BoundaryTypeBuilder withHeirarchyType(final HierarchyType hierarchyType) {
        boundaryTypeImpl.setHierarchyType(hierarchyType);
        return this;
    }

    public BoundaryTypeBuilder withId(final long id) {
        try {
            final Field idField = boundaryTypeImpl.getClass().getSuperclass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(boundaryTypeImpl, id);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public BoundaryTypeBuilder withDefaults() {
        withId(count);
        if (null == boundaryTypeImpl.getLastModifiedDate())
            withUpdatedTime(new Date());

        if (null == boundaryTypeImpl.getName())
            withName("test-BoundaryType-" + count);

        if (0 == boundaryTypeImpl.getHierarchy())
            withHierarchy(Long.valueOf(count));
        if (null != boundaryTypeImpl.getHierarchyType())
            withHeirarchyType(new HeirarchyTypeBuilder().withDefaults().build());
        return this;
    }

    public BoundaryTypeBuilder withDbDefaults() {
        if (null != boundaryTypeImpl.getLastModifiedDate())
            withUpdatedTime(new Date());

        if (null == boundaryTypeImpl.getName())
            withName("test-BoundaryType-" + count);

        
        if (null == boundaryTypeImpl.getHierarchy())
            withHierarchy(Long.valueOf(count));

        if (null == boundaryTypeImpl.getHierarchyType())
            withHeirarchyType(new HeirarchyTypeBuilder().withDbDefaults().build());

        return this;
    }

    public BoundaryType build() {
        return boundaryTypeImpl;
    }
}