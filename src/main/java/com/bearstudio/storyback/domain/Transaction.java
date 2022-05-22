package com.bearstudio.storyback.domain;

import com.bearstudio.storyback.domain.enumeration.PaymentStatus;
import com.bearstudio.storyback.domain.enumeration.PaymentType;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "payment_id")
    private String paymentId;

    @NotNull
    @Column(name = "reservation_reference", nullable = false)
    private String reservationReference;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPaymentId() {
        return this.paymentId;
    }

    public Transaction paymentId(String paymentId) {
        this.setPaymentId(paymentId);
        return this;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getReservationReference() {
        return this.reservationReference;
    }

    public Transaction reservationReference(String reservationReference) {
        this.setReservationReference(reservationReference);
        return this;
    }

    public void setReservationReference(String reservationReference) {
        this.reservationReference = reservationReference;
    }

    public PaymentStatus getPaymentStatus() {
        return this.paymentStatus;
    }

    public Transaction paymentStatus(PaymentStatus paymentStatus) {
        this.setPaymentStatus(paymentStatus);
        return this;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentType getPaymentType() {
        return this.paymentType;
    }

    public Transaction paymentType(PaymentType paymentType) {
        this.setPaymentType(paymentType);
        return this;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", paymentId='" + getPaymentId() + "'" +
            ", reservationReference='" + getReservationReference() + "'" +
            ", paymentStatus='" + getPaymentStatus() + "'" +
            ", paymentType='" + getPaymentType() + "'" +
            "}";
    }
}
