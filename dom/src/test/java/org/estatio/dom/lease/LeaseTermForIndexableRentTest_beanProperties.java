/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom.lease;

import org.junit.Test;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.index.Index;

public class LeaseTermForIndexableRentTest_beanProperties extends AbstractBeanPropertiesTest {

	@Test
	public void test() {
	    newPojoTester()
	        .withFixture(pojos(LeaseItem.class))
	        .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
	        .withFixture(pojos(Index.class))
            .withFixture(statii())
	        .exercise(new LeaseTermForIndexableRent());
	}

	
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static FixtureDatumFactory<LeaseTermStatus> statii() {
        return new FixtureDatumFactory(LeaseTermStatus.class, (Object[])LeaseTermStatus.values());
    }

}
