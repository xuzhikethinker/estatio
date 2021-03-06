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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import javax.validation.Path.ReturnValueNode;

import com.google.common.collect.Lists;

import org.hamcrest.Matchers;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.agreement.Agreement;
import org.estatio.dom.agreement.AgreementRole;
import org.estatio.dom.agreement.AgreementRoleType;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.agreement.AgreementType;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.financial.BankAccount;
import org.estatio.dom.financial.BankMandate;
import org.estatio.dom.financial.BankMandates;
import org.estatio.dom.financial.FinancialAccount;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.FinancialConstants;
import org.estatio.dom.party.Party;
import org.estatio.dom.party.PartyForTesting;
import org.estatio.services.clock.ClockService;

public class LeaseTest_newMandate {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Mock
    private AgreementRoleTypes mockAgreementRoleTypes;
    @Mock
    private AgreementRoles mockAgreementRoles;
    @Mock
    private AgreementTypes mockAgreementTypes;
    @Mock
    private FinancialAccounts mockFinancialAccounts;
    @Mock
    private ClockService mockClockService;

    @Mock
    private DomainObjectContainer mockContainer;

    private BankMandates bankMandates;

    private Lease lease;

    @Mock
    private Agreements agreements;

    private BankMandate bankMandate;

    private BankAccount bankAccount;
    private BankAccount someOtherBankAccount;

    private Party tenant;
    private Party landlord;

    private AgreementRoleType landlordAgreementRoleType;
    private AgreementRoleType tenantAgreementRoleType;
    private AgreementRoleType creditorAgreementRoleType;
    private AgreementRoleType debtorAgreementRoleType;

    private AgreementRole tenantAgreementRole;
    private AgreementRole landlordAgreementRole;

    private AgreementType bankMandateAgreementType;

    private LocalDate startDate;
    private LocalDate endDate;

    @Before
    public void setUp() throws Exception {

        tenantAgreementRoleType = new AgreementRoleType();
        tenantAgreementRoleType.setTitle(LeaseConstants.ART_LANDLORD);
        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).findByTitle(LeaseConstants.ART_LANDLORD);
                will(returnValue(landlordAgreementRoleType));
            }
        });

        tenantAgreementRoleType = new AgreementRoleType();
        tenantAgreementRoleType.setTitle(LeaseConstants.ART_TENANT);
        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).findByTitle(LeaseConstants.ART_TENANT);
                will(returnValue(tenantAgreementRoleType));
            }
        });

        debtorAgreementRoleType = new AgreementRoleType();
        debtorAgreementRoleType.setTitle(FinancialConstants.ART_DEBTOR);
        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).findByTitle(FinancialConstants.ART_DEBTOR);
                will(returnValue(debtorAgreementRoleType));
            }
        });
        context.checking(new Expectations() {
            {
                allowing(agreements).findAgreementByReference("MANDATEREF");
                will(returnValue(null));
            }
        });

        creditorAgreementRoleType = new AgreementRoleType();
        creditorAgreementRoleType.setTitle(FinancialConstants.ART_CREDITOR);
        context.checking(new Expectations() {
            {
                allowing(mockAgreementRoleTypes).findByTitle(FinancialConstants.ART_CREDITOR);
                will(returnValue(creditorAgreementRoleType));
            }
        });

        bankMandateAgreementType = new AgreementType();
        bankMandateAgreementType.setTitle(FinancialConstants.AT_MANDATE);
        context.checking(new Expectations() {
            {
                allowing(mockAgreementTypes).find(FinancialConstants.AT_MANDATE);
                will(returnValue(bankMandateAgreementType));
            }
        });

        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(new LocalDate(2013, 4, 2)));
            }
        });

        tenant = new PartyForTesting();
        tenantAgreementRole = new AgreementRole();
        tenantAgreementRole.setParty(tenant);
        tenantAgreementRole.setType(tenantAgreementRoleType);
        tenantAgreementRole.injectClockService(mockClockService);

        landlord = new PartyForTesting();
        landlordAgreementRole = new AgreementRole();
        landlordAgreementRole.setParty(landlord);
        landlordAgreementRole.setType(landlordAgreementRoleType);
        landlordAgreementRole.injectClockService(mockClockService);

        bankMandate = new BankMandate();
        bankMandate.setContainer(mockContainer);

        bankAccount = new BankAccount();
        bankAccount.setReference("REF1");
        someOtherBankAccount = new BankAccount();

        startDate = new LocalDate(2013, 4, 1);
        endDate = new LocalDate(2013, 5, 2);

        // a mini integration test, since using the real BankMandates impl
        bankMandates = new BankMandates();
        bankMandates.setContainer(mockContainer);
        bankMandates.injectAgreementTypes(mockAgreementTypes);
        bankMandates.injectAgreementRoleTypes(mockAgreementRoleTypes);

        // the main class under test
        lease = new Lease();
        lease.injectAgreementRoleTypes(mockAgreementRoleTypes);
        lease.injectAgreementRoles(mockAgreementRoles);
        lease.injectAgreementTypes(mockAgreementTypes);
        lease.injectFinancialAccounts(mockFinancialAccounts);
        lease.injectBankMandates(bankMandates);
        lease.setContainer(mockContainer);
        lease.injectClockService(mockClockService);
        lease.injectAgreements(agreements);

    }

    @Test
    public void whenSecondaryPartyIsUnknown_isDisabled() {

        // given
        assertThat(lease.getRoles(), Matchers.empty());

        // when
        final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate);

        // then
        assertThat(disabledReason, is("Could not determine the tenant (secondary party) of this lease"));
    }

    @Test
    public void whenSecondaryPartyIsKnownButNotCurrent_isDisabled() {

        // given
        tenantAgreementRole.setAgreement(lease);
        lease.getRoles().add(tenantAgreementRole);

        tenantAgreementRole.setEndDate(new LocalDate(2013, 4, 1));

        // when
        final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate);

        // then
        assertThat(disabledReason, is(not(nullValue())));

        // and when/then
        // (defaultXxx wouldn't get called, but for coverage...)
        assertThat(lease.default0PaidBy(), is(nullValue()));
    }

    @Test
    public void whenSecondaryPartyIsKnownButNoBankAccounts_isDisabled() {

        // given
        tenantAgreementRole.setAgreement(lease);
        lease.getRoles().add(tenantAgreementRole);

        context.checking(new Expectations() {
            {
                oneOf(mockFinancialAccounts).findBankAccountsByOwner(tenant);
                will(returnValue(Collections.emptyList()));
            }
        });

        // when, then
        final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate);
        assertThat(disabledReason, is(not(nullValue())));
    }

    @Test
    public void whenSecondaryPartyIsKnownAndHasBankAccounts_canInvoke() {

        // given
        tenantAgreementRole.setAgreement(lease);
        lease.getRoles().add(tenantAgreementRole);
        lease.getRoles().add(landlordAgreementRole);

        context.checking(new Expectations() {
            {
                allowing(mockFinancialAccounts).findBankAccountsByOwner(tenant);
                will(returnValue(Lists.newArrayList(bankAccount)));
            }
        });

        // when/then
        final String disabledReason = lease.disableNewMandate(bankAccount, "MANDATEREF", startDate, endDate);
        assertThat(disabledReason, is(nullValue()));

        // and when/then
        final List<BankAccount> bankAccounts = lease.choices0NewMandate();
        assertThat(bankAccounts, Matchers.contains(bankAccount));

        // and when/then
        final BankAccount defaultBankAccount = lease.default0NewMandate();
        assertThat(defaultBankAccount, is(bankAccount));

        // and when/then
        final String validateReason = lease.validateNewMandate(defaultBankAccount, "MANDATEREF", startDate, endDate);
        assertThat(validateReason, is(nullValue()));

        // and given
        assertThat(lease.getPaidBy(), is(nullValue()));
        final AgreementRole newBankMandateAgreementRoleForCreditor = new AgreementRole();
        final AgreementRole newBankMandateAgreementRoleForDebtor = new AgreementRole();

        context.checking(new Expectations() {
            {
                oneOf(mockContainer).newTransientInstance(BankMandate.class);
                will(returnValue(bankMandate));

                oneOf(mockContainer).persistIfNotAlready(bankMandate);

                oneOf(mockContainer).newTransientInstance(AgreementRole.class);
                will(returnValue(newBankMandateAgreementRoleForCreditor));

                oneOf(mockContainer).newTransientInstance(AgreementRole.class);
                will(returnValue(newBankMandateAgreementRoleForDebtor));

                oneOf(mockContainer).persistIfNotAlready(newBankMandateAgreementRoleForCreditor);

                oneOf(mockContainer).persistIfNotAlready(newBankMandateAgreementRoleForDebtor);
            }
        });

        // when
        final Lease returned = lease.newMandate(defaultBankAccount, "MANDATEREF", startDate, endDate);

        // then
        assertThat(returned, is(lease));

        assertThat(lease.getPaidBy(), is(bankMandate));
        assertThat(bankMandate.getType(), is(bankMandateAgreementType));
        assertThat(bankMandate.getBankAccount(), is((FinancialAccount) bankAccount));
        assertThat(bankMandate.getStartDate(), is(startDate));
        assertThat(bankMandate.getEndDate(), is(endDate));
        assertThat(bankMandate.getReference(), is("MANDATEREF"));

        assertThat(newBankMandateAgreementRoleForCreditor.getAgreement(), is((Agreement) bankMandate));
        assertThat(newBankMandateAgreementRoleForCreditor.getParty(), is(landlord));
        assertThat(newBankMandateAgreementRoleForDebtor.getAgreement(), is((Agreement) bankMandate));
        assertThat(newBankMandateAgreementRoleForDebtor.getParty(), is(tenant));
    }

    @Test
    public void whenPrereqs_validateWithIncorrectBankAccount() {

        // given
        tenantAgreementRole.setAgreement(lease);
        lease.getRoles().add(tenantAgreementRole);

        context.checking(new Expectations() {
            {
                oneOf(mockFinancialAccounts).findBankAccountsByOwner(tenant);
                will(returnValue(Lists.newArrayList(bankAccount)));
            }
        });

        // when/then
        final String validateReason = lease.validateNewMandate(someOtherBankAccount, "MANDATEREF", startDate, endDate);
        assertThat(validateReason, is("Bank account is not owned by this lease's tenant"));
    }

}
