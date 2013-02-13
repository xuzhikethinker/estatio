package org.estatio.dom.lease;

import java.math.BigDecimal;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.Indexable;
import org.estatio.dom.index.IndexationCalculator;
import org.estatio.dom.index.Indices;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optional;

@PersistenceCapable
@Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class LeaseTermForIndexableRent extends LeaseTerm implements Indexable {

    // {{ Index (property)
    private Index index;

    @MemberOrder(sequence = "10", name = "Indexable Rent")
    public Index getIndex() {
        return index;
    }

    public void setIndex(final Index index) {
        this.index = index;
    }

    public List<Index> choicesIndex() {
        return indexService.allIndices();
    }

    // }}

    // {{ IndexationFrequency (property)
    private IndexationFrequency indexationFrequency;

    @MemberOrder(sequence = "11", name = "Indexable Rent")
    public IndexationFrequency getIndexationFrequency() {
        return indexationFrequency;
    }

    public void setIndexationFrequency(final IndexationFrequency indexationFrequency) {
        this.indexationFrequency = indexationFrequency;
    }

    // }}

    // {{ BaseIndexStartDate (property)
    private LocalDate baseIndexStartDate;

    @Persistent
    @MemberOrder(sequence = "12", name = "Indexable Rent")
    public LocalDate getBaseIndexStartDate() {
        return baseIndexStartDate;
    }

    public void setBaseIndexStartDate(final LocalDate baseIndexStartDate) {
        this.baseIndexStartDate = baseIndexStartDate;
    }

    // }}

    // {{ BaseIndexValue (property)
    private BigDecimal baseIndexValue;

    @MemberOrder(sequence = "14", name = "Indexable Rent")
    @Optional
    @Column(scale = 4)
    public BigDecimal getBaseIndexValue() {
        return baseIndexValue;
    }

    public void setBaseIndexValue(final BigDecimal baseIndexValue) {
        this.baseIndexValue = baseIndexValue;
    }

    // }}

    // {{ NextIndexStartDate (property)
    private LocalDate nextIndexStartDate;

    @Persistent
    @MemberOrder(sequence = "15", name = "Indexable Rent")
    public LocalDate getNextIndexStartDate() {
        return nextIndexStartDate;
    }

    public void setNextIndexStartDate(final LocalDate nextIndexStartDate) {
        this.nextIndexStartDate = nextIndexStartDate;
    }

    // }}

    // {{ NextIndexValue (property)
    private BigDecimal nextIndexValue;

    @MemberOrder(sequence = "17", name = "Indexable Rent")
    @Optional
    @Column(scale = 4)
    public BigDecimal getNextIndexValue() {
        return nextIndexValue;
    }

    public void setNextIndexValue(final BigDecimal nextIndexValue) {
        this.nextIndexValue = nextIndexValue;
    }

    // }}

    // {{ ReviewDate (property)
    private LocalDate reviewDate;

    @Persistent
    @Optional
    @MemberOrder(sequence = "18", name = "Indexable Rent")
    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(final LocalDate reviewDate) {
        this.reviewDate = reviewDate;
    }

    // }}

    // {{ EffectiveDate (property)
    private LocalDate effectiveDate;

    @Persistent
    @Optional
    @MemberOrder(sequence = "19", name = "Indexable Rent")
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(final LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    // }}

    // {{ IndexationPercentage (property)
    private BigDecimal indexationPercentage;

    @MemberOrder(sequence = "20", name = "Indexable Rent")
    @Optional
    @Column(scale = 4)
    public BigDecimal getIndexationPercentage() {
        return indexationPercentage;
    }

    public void setIndexationPercentage(final BigDecimal indexationPercentage) {
        this.indexationPercentage = indexationPercentage;
    }

    // }}

    // {{ LevellingPercentage (property)
    private BigDecimal levellingPercentage;

    @MemberOrder(sequence = "21", name = "Indexable Rent")
    @Optional
    public BigDecimal getLevellingPercentage() {
        return levellingPercentage;
    }

    public void setLevellingPercentage(final BigDecimal levellingPercentage) {
        this.levellingPercentage = levellingPercentage;
    }

    // }}

    // {{ LevellingValue (property)
    private BigDecimal levellingValue;

    @MemberOrder(sequence = "22", name = "Indexable Rent")
    @Optional
    @Column(scale = 4)
    public BigDecimal getLevellingValue() {
        return levellingValue;
    }

    public void setLevellingValue(final BigDecimal levellingValue) {
        this.levellingValue = levellingValue;
    }

    // }}

    // {{ BaseValue (property)
    private BigDecimal baseValue;

    @MemberOrder(sequence = "30", name = "Values")
    @Column(scale = 4)
    public BigDecimal getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(final BigDecimal baseValue) {
        this.baseValue = baseValue;
    }

    // }}

    // {{ IndexedValue (property)
    private BigDecimal indexedValue;

    @MemberOrder(sequence = "31", name = "Values")
    @Optional
    @Column(scale = 4)
    public BigDecimal getIndexedValue() {
        return indexedValue;
    }

    public void setIndexedValue(final BigDecimal indexedValue) {
        this.indexedValue = indexedValue;
    }

    // }}

    // {{
    public LeaseTerm verify() {
        IndexationCalculator calculator = new IndexationCalculator(getIndex(), getBaseIndexStartDate(), getNextIndexStartDate(), getBaseValue());
        calculator.calculate(this);
        return this;
    }

    public LeaseTerm approve() {
        setValue(getIndexedValue());
        super.approve();
        return this;
     }

    // }}

    // {{
    public LeaseTermForIndexableRent createNextLeaseTerm() {
        // create new term
        LeaseTermForIndexableRent term = (LeaseTermForIndexableRent) getNextTerm();
        if (getNextTerm() == null) {
            term = (LeaseTermForIndexableRent) leaseTermsService.newLeaseTerm(this.getLeaseItem());
        }
        // new start Date
        LocalDate newStartDate = this.getEndDate() == null ? this.getIndexationFrequency().nextDate(this.getStartDate()) : this.getEndDate().plusDays(1);
        term.setStartDate(newStartDate);
        // index
        term.setIndex(this.getIndex());
        term.setBaseIndexStartDate(this.getNextIndexStartDate());
        term.setNextIndexStartDate(this.getIndexationFrequency().nextDate(this.getNextIndexStartDate()));
        term.setEffectiveDate(this.getIndexationFrequency().nextDate(this.getEffectiveDate()));
        term.setReviewDate(this.getIndexationFrequency().nextDate(this.getReviewDate()));
        // value
        term.setBaseValue(this.getValue());
        // set fields on this term
        this.setNextTerm(term);
        this.setEndDate(term.getStartDate().minusDays(1));
        return term;
    }

    // }}

    // {{ Injected Services
    private LeaseTerms leaseTermsService;

    public void setLeaseTermsService(LeaseTerms leaseTerms) {
        this.leaseTermsService = leaseTerms;
    }

    private Indices indexService;

    public void setIndexService(Indices indexes) {
        this.indexService = indexes;
    }

    // }}

}