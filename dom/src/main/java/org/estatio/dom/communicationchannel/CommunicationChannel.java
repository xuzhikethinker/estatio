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
package org.estatio.dom.communicationchannel;

import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Bookmarkable;
import org.apache.isis.applib.annotation.Disabled;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;
import org.apache.isis.applib.annotation.Where;

import org.estatio.dom.EstatioMutableObject;
import org.estatio.dom.JdoColumnLength;
import org.estatio.dom.WithNameGetter;
import org.estatio.dom.WithReferenceGetter;

/**
 * Represents a mechanism for communicating with its {@link CommunicationChannelOwner owner}.
 *
 * <p>
 * This is an abstract entity; concrete subclasses are {@link PostalAddress postal}, {@link PhoneOrFaxNumber phone/fax}
 *  and {@link EmailAddress email}.
 */
@javax.jdo.annotations.PersistenceCapable(identityType=IdentityType.DATASTORE)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy=IdGeneratorStrategy.NATIVE, 
        column="id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER, 
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME, 
        column="discriminator")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReferenceAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.communicationchannel.CommunicationChannel "
                        + "WHERE reference == :reference "
                        + "&& type == :type"),
        @javax.jdo.annotations.Query(
                name = "findByOwner", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.estatio.dom.communicationchannel.CommunicationChannel "
                        + "WHERE owner == :owner ")
})
@Bookmarkable(BookmarkPolicy.AS_CHILD)
public abstract class CommunicationChannel 
        extends EstatioMutableObject<CommunicationChannel> 
        implements WithNameGetter, WithReferenceGetter {

    public CommunicationChannel() {
        // TODO: description is annotated as optional,
        // so it doesn't really make sense for it to be part of the natural sort
        // order
        super("id, type, description");
        //super("type, description");
    }

    // //////////////////////////////////////
    
    @MemberOrder(sequence="2")
    @Hidden(where = Where.OBJECT_FORMS)
    public String getName() {
        return getContainer().titleOf(this);
    }
    
    // //////////////////////////////////////

    private CommunicationChannelOwner owner;

    /**
     * nb: annotated as <tt>@Optional</tt>, but this is a workaround because cannot set 
     * <tt>@Column(allowNulls="false")</tt> for a polymorphic relationship.
     */
    @javax.jdo.annotations.Persistent(
            extensions = {
                    @Extension(vendorName = "datanucleus",
                        key = "mapping-strategy",
                        value = "per-implementation"),
                    @Extension(vendorName = "datanucleus",
                        key = "implementation-classes",
                        value = "org.estatio.dom.party.Party"
                                + ",org.estatio.dom.asset.FixedAsset")
            })
    @javax.jdo.annotations.Columns({
        @javax.jdo.annotations.Column(name="ownerPartyId"),
        @javax.jdo.annotations.Column(name="ownerFixedAssetId")
    })
    @Optional 
    @Hidden(where = Where.PARENTED_TABLES)
    @Disabled
    public CommunicationChannelOwner getOwner() {
        return owner;
    }

    public void setOwner(final CommunicationChannelOwner owner) {
        this.owner = owner;
    }

    // //////////////////////////////////////

    private CommunicationChannelType type;

    @MemberOrder(sequence="1")
    @javax.jdo.annotations.Column(allowsNull="false", length=JdoColumnLength.TYPE_ENUM)
    @Hidden(where=Where.OBJECT_FORMS)
    public CommunicationChannelType getType() {
        return type;
    }

    public void setType(final CommunicationChannelType type) {
        this.type = type;
    }

    // //////////////////////////////////////

    private String reference;

    /**
     * For import purposes only
     */
    @javax.jdo.annotations.Column(allowsNull="true", length=JdoColumnLength.REFERENCE)
    @Hidden
    public String getReference() {
        return reference;
    }

    public void setReference(final String reference) {
        this.reference = reference;
    }


    // //////////////////////////////////////

    private String description;

    @javax.jdo.annotations.Column(length=JdoColumnLength.DESCRIPTION)
    @Hidden(where=Where.ALL_TABLES)
    @Optional
    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    // //////////////////////////////////////

    private boolean legal;

    @MemberOrder(sequence="3")
    public boolean isLegal() {
        return legal;
    }

    public void setLegal(final boolean Legal) {
        this.legal = Legal;
    }

}
