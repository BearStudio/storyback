package com.bearstudio.storyback.service.criteria;

import com.bearstudio.storyback.domain.enumeration.PaymentStatus;
import com.bearstudio.storyback.domain.enumeration.PaymentType;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.bearstudio.storyback.domain.Transaction} entity. This class is used
 * in {@link com.bearstudio.storyback.web.rest.TransactionResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /transactions?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class TransactionCriteria implements Serializable, Criteria {

    /**
     * Class for filtering PaymentStatus
     */
    public static class PaymentStatusFilter extends Filter<PaymentStatus> {

        public PaymentStatusFilter() {}

        public PaymentStatusFilter(PaymentStatusFilter filter) {
            super(filter);
        }

        @Override
        public PaymentStatusFilter copy() {
            return new PaymentStatusFilter(this);
        }
    }

    /**
     * Class for filtering PaymentType
     */
    public static class PaymentTypeFilter extends Filter<PaymentType> {

        public PaymentTypeFilter() {}

        public PaymentTypeFilter(PaymentTypeFilter filter) {
            super(filter);
        }

        @Override
        public PaymentTypeFilter copy() {
            return new PaymentTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter paymentId;

    private StringFilter reservationReference;

    private PaymentStatusFilter paymentStatus;

    private PaymentTypeFilter paymentType;

    private Boolean distinct;

    public TransactionCriteria() {}

    public TransactionCriteria(TransactionCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.paymentId = other.paymentId == null ? null : other.paymentId.copy();
        this.reservationReference = other.reservationReference == null ? null : other.reservationReference.copy();
        this.paymentStatus = other.paymentStatus == null ? null : other.paymentStatus.copy();
        this.paymentType = other.paymentType == null ? null : other.paymentType.copy();
        this.distinct = other.distinct;
    }

    @Override
    public TransactionCriteria copy() {
        return new TransactionCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getPaymentId() {
        return paymentId;
    }

    public StringFilter paymentId() {
        if (paymentId == null) {
            paymentId = new StringFilter();
        }
        return paymentId;
    }

    public void setPaymentId(StringFilter paymentId) {
        this.paymentId = paymentId;
    }

    public StringFilter getReservationReference() {
        return reservationReference;
    }

    public StringFilter reservationReference() {
        if (reservationReference == null) {
            reservationReference = new StringFilter();
        }
        return reservationReference;
    }

    public void setReservationReference(StringFilter reservationReference) {
        this.reservationReference = reservationReference;
    }

    public PaymentStatusFilter getPaymentStatus() {
        return paymentStatus;
    }

    public PaymentStatusFilter paymentStatus() {
        if (paymentStatus == null) {
            paymentStatus = new PaymentStatusFilter();
        }
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatusFilter paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentTypeFilter getPaymentType() {
        return paymentType;
    }

    public PaymentTypeFilter paymentType() {
        if (paymentType == null) {
            paymentType = new PaymentTypeFilter();
        }
        return paymentType;
    }

    public void setPaymentType(PaymentTypeFilter paymentType) {
        this.paymentType = paymentType;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TransactionCriteria that = (TransactionCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(paymentId, that.paymentId) &&
            Objects.equals(reservationReference, that.reservationReference) &&
            Objects.equals(paymentStatus, that.paymentStatus) &&
            Objects.equals(paymentType, that.paymentType) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentId, reservationReference, paymentStatus, paymentType, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (paymentId != null ? "paymentId=" + paymentId + ", " : "") +
            (reservationReference != null ? "reservationReference=" + reservationReference + ", " : "") +
            (paymentStatus != null ? "paymentStatus=" + paymentStatus + ", " : "") +
            (paymentType != null ? "paymentType=" + paymentType + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
