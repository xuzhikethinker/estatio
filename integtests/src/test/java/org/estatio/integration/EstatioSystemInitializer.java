/*
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
package org.estatio.integration;

import org.apache.log4j.Level;

import org.apache.isis.applib.services.queryresultscache.QueryResultsCache;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.commons.config.IsisConfigurationDefault;
import org.apache.isis.core.integtestsupport.IsisSystemForTest;
import org.apache.isis.core.runtime.services.memento.MementoServiceDefault;
import org.apache.isis.core.runtime.services.xmlsnapshot.XmlSnapshotServiceDefault;
import org.apache.isis.core.wrapper.WrapperFactoryDefault;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusObjectStore;
import org.apache.isis.objectstore.jdo.datanucleus.DataNucleusPersistenceMechanismInstaller;
import org.apache.isis.objectstore.jdo.datanucleus.service.eventbus.EventBusServiceJdo;
import org.apache.isis.objectstore.jdo.datanucleus.service.support.IsisJdoSupportImpl;

import org.estatio.api.Api;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannelTypes;
import org.estatio.dom.agreement.AgreementRoleCommunicationChannels;
import org.estatio.dom.agreement.AgreementRoleTypes;
import org.estatio.dom.agreement.AgreementRoles;
import org.estatio.dom.agreement.AgreementTypes;
import org.estatio.dom.agreement.Agreements;
import org.estatio.dom.asset.FixedAssetRoles;
import org.estatio.dom.asset.FixedAssets;
import org.estatio.dom.asset.Properties;
import org.estatio.dom.asset.financial.FixedAssetFinancialAccounts;
import org.estatio.dom.asset.registration.contributed.FixedAssetRegistrationContributions;
import org.estatio.dom.charge.ChargeGroups;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.communicationchannel.CommunicationChannelContributions;
import org.estatio.dom.communicationchannel.CommunicationChannels;
import org.estatio.dom.communicationchannel.EmailAddresses;
import org.estatio.dom.communicationchannel.PhoneOrFaxNumbers;
import org.estatio.dom.communicationchannel.PostalAddresses;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.event.Events;
import org.estatio.dom.financial.BankMandates;
import org.estatio.dom.financial.FinancialAccounts;
import org.estatio.dom.financial.contributed.FinancialAccountContributions;
import org.estatio.dom.geography.Countries;
import org.estatio.dom.geography.StateContributions;
import org.estatio.dom.geography.States;
import org.estatio.dom.index.IndexBases;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.IndexationService;
import org.estatio.dom.index.Indices;
import org.estatio.dom.invoice.InvoiceItems;
import org.estatio.dom.invoice.InvoiceNumeratorContributions;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForInvoiceRun;
import org.estatio.dom.invoice.viewmodel.InvoiceSummariesForPropertyDueDate;
import org.estatio.dom.lease.LeaseItems;
import org.estatio.dom.lease.LeaseTerms;
import org.estatio.dom.lease.LeaseTypes;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.Occupancies;
import org.estatio.dom.lease.UnitsForLease;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.lease.tags.Activities;
import org.estatio.dom.lease.tags.Brands;
import org.estatio.dom.lease.tags.Sectors;
import org.estatio.dom.lease.tags.UnitSizes;
import org.estatio.dom.numerator.Numerators;
import org.estatio.dom.party.Organisations;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Persons;
import org.estatio.dom.tax.TaxRates;
import org.estatio.dom.tax.Taxes;
import org.estatio.fixture.EstatioRefDataObjectsFixture;
import org.estatio.services.bookmarks.BookmarkServiceForEstatio;
import org.estatio.services.clock.ClockService;
import org.estatio.services.links.LinkContributions;
import org.estatio.services.links.Links;
import org.estatio.services.settings.ApplicationSettingsServiceForEstatio;
import org.estatio.services.settings.EstatioSettingsService;

/**
 * Holds an instance of an {@link IsisSystemForTest} as a {@link ThreadLocal} on
 * the current thread, initialized with Estatio's domain services and with
 * {@link EstatioRefDataObjectsFixture reference data fixture}.
 */
public class EstatioSystemInitializer {

    private EstatioSystemInitializer() {
    }

    public static IsisSystemForTest initIsft() {
        IsisSystemForTest isft = IsisSystemForTest.getElseNull();
        if (isft == null) {
            isft = new EstatioIntegTestBuilder().build().setUpSystem();
            IsisSystemForTest.set(isft);
        }
        return isft;
    }

    private static class EstatioIntegTestBuilder extends IsisSystemForTest.Builder {

        private EstatioIntegTestBuilder() {
            withFixtures(new EstatioRefDataObjectsFixture());
            withLoggingAt(Level.INFO);
            with(testConfiguration());
            with(new DataNucleusPersistenceMechanismInstaller());
            withServices(
                    new WrapperFactoryDefault(),
                    new Countries(),
                    new InvoiceSummariesForPropertyDueDate(),
                    new States(),
                    new StateContributions(),
                    new Currencies(),
                    new Indices(),
                    new IndexBases(),
                    new IndexValues(),
                    new FixedAssets(),
                    new FixedAssetFinancialAccounts(),
                    new Properties(),
                    new FixedAssetRoles(),
                    new UnitsForLease(),
                    new Parties(),
                    new Persons(),
                    new Organisations(),
                    new Agreements(),
                    new AgreementTypes(),
                    new AgreementRoles(),
                    new AgreementRoleCommunicationChannels(),
                    new AgreementRoleCommunicationChannelTypes(),
                    new AgreementRoleTypes(),
                    new BankMandates(),
                    new Leases(),
                    new LeaseTerms(),
                    new LeaseItems(),
                    new LeaseTypes(),
                    new Occupancies(),
                    new Invoices(),
                    new InvoiceNumeratorContributions(),
                    new InvoiceItems(),
                    new InvoiceItemsForLease(),
                    new IndexationService(),
                    new CommunicationChannels(),
                    new CommunicationChannelContributions(),
                    new PostalAddresses(),
                    new EmailAddresses(),
                    new PhoneOrFaxNumbers(),
                    new Taxes(),
                    new TaxRates(),
                    new Events(),
                    new UnitSizes(),
                    new Sectors(),
                    new Activities(),
                    new Brands(),
                    new BookmarkServiceForEstatio(),
                    new XmlSnapshotServiceDefault(),
                    new MementoServiceDefault(),
                    new Charges(),
                    new ChargeGroups(),
                    new FinancialAccounts(),
                    new Numerators(),
                    new ClockService(),
                    new Api(),
                    new IsisJdoSupportImpl(),
                    new InvoiceCalculationService(),
                    new InvoiceSummariesForInvoiceRun(),
                    new ApplicationSettingsServiceForEstatio(),
                    new EstatioSettingsService(),
                    new FinancialAccountContributions(),
                    new FixedAssetRegistrationContributions(),
                    new EventBusServiceJdo(),
                    new Links(),
                    new LinkContributions(),
                    new QueryResultsCache());
        }

        private IsisConfiguration testConfiguration() {
            final IsisConfigurationDefault testConfiguration = new IsisConfigurationDefault();

            testConfiguration.add("isis.persistor.datanucleus.RegisterEntities.packagePrefix", "org.estatio");

            testConfiguration.add(DataNucleusObjectStore.INSTALL_FIXTURES_KEY, "true");

            // uncomment to use log4jdbc instead
            // testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName",
            // "net.sf.log4jdbc.DriverSpy");

            // testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionURL",
            // "jdbc:sqlserver://localhost:1433;instance=.;databaseName=estatio");
            // testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionDriverName",
            // "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionUserName",
            // "estatio");
            // testConfiguration.add("isis.persistor.datanucleus.impl.javax.jdo.option.ConnectionPassword",
            // "estatio");

            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.defaultInheritanceStrategy", "TABLE_PER_CLASS");

            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.cache.level2.type", "none");
            // TODO: this is a (temporary?) work-around for
            // NumeratorIntegrationTest failing if do a find prior to create and
            // then a find;
            // believe that the second find fails to work due to original find
            // caching an incorrect query compilation plan
            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.query.compilation.cached", "false");

            testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.identifier.case", "PreserveCase");

            // adding this is meant to be all that is required for
            // across-the-board multi-tenancy support
            // however, it causes DN to throw a NullPointerException...
            // testConfiguration.add("isis.persistor.datanucleus.impl.datanucleus.tenantId","DEV1");

            return testConfiguration;
        }
    }

}