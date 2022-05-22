package com.bearstudio.storyback.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bearstudio.storyback.IntegrationTest;
import com.bearstudio.storyback.domain.Transaction;
import com.bearstudio.storyback.domain.enumeration.PaymentStatus;
import com.bearstudio.storyback.domain.enumeration.PaymentType;
import com.bearstudio.storyback.repository.TransactionRepository;
import com.bearstudio.storyback.service.criteria.TransactionCriteria;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionResourceIT {

    private static final String DEFAULT_PAYMENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_PAYMENT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_RESERVATION_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_RESERVATION_REFERENCE = "BBBBBBBBBB";

    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.PENDING;
    private static final PaymentStatus UPDATED_PAYMENT_STATUS = PaymentStatus.CANCELED;

    private static final PaymentType DEFAULT_PAYMENT_TYPE = PaymentType.STRIPE;
    private static final PaymentType UPDATED_PAYMENT_TYPE = PaymentType.STRIPE;

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .paymentId(DEFAULT_PAYMENT_ID)
            .reservationReference(DEFAULT_RESERVATION_REFERENCE)
            .paymentStatus(DEFAULT_PAYMENT_STATUS)
            .paymentType(DEFAULT_PAYMENT_TYPE);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .paymentId(UPDATED_PAYMENT_ID)
            .reservationReference(UPDATED_RESERVATION_REFERENCE)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .paymentType(UPDATED_PAYMENT_TYPE);
        return transaction;
    }

    @BeforeEach
    public void initTest() {
        transaction = createEntity(em);
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        // Create the Transaction
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
        assertThat(testTransaction.getReservationReference()).isEqualTo(DEFAULT_RESERVATION_REFERENCE);
        assertThat(testTransaction.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testTransaction.getPaymentType()).isEqualTo(DEFAULT_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReservationReferenceIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setReservationReference(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setPaymentStatus(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPaymentTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setPaymentType(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentId").value(hasItem(DEFAULT_PAYMENT_ID)))
            .andExpect(jsonPath("$.[*].reservationReference").value(hasItem(DEFAULT_RESERVATION_REFERENCE)))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentType").value(hasItem(DEFAULT_PAYMENT_TYPE.toString())));
    }

    @Test
    @Transactional
    void getTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.paymentId").value(DEFAULT_PAYMENT_ID))
            .andExpect(jsonPath("$.reservationReference").value(DEFAULT_RESERVATION_REFERENCE))
            .andExpect(jsonPath("$.paymentStatus").value(DEFAULT_PAYMENT_STATUS.toString()))
            .andExpect(jsonPath("$.paymentType").value(DEFAULT_PAYMENT_TYPE.toString()));
    }

    @Test
    @Transactional
    void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        Long id = transaction.getId();

        defaultTransactionShouldBeFound("id.equals=" + id);
        defaultTransactionShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentIdIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentId equals to DEFAULT_PAYMENT_ID
        defaultTransactionShouldBeFound("paymentId.equals=" + DEFAULT_PAYMENT_ID);

        // Get all the transactionList where paymentId equals to UPDATED_PAYMENT_ID
        defaultTransactionShouldNotBeFound("paymentId.equals=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentIdIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentId not equals to DEFAULT_PAYMENT_ID
        defaultTransactionShouldNotBeFound("paymentId.notEquals=" + DEFAULT_PAYMENT_ID);

        // Get all the transactionList where paymentId not equals to UPDATED_PAYMENT_ID
        defaultTransactionShouldBeFound("paymentId.notEquals=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentIdIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentId in DEFAULT_PAYMENT_ID or UPDATED_PAYMENT_ID
        defaultTransactionShouldBeFound("paymentId.in=" + DEFAULT_PAYMENT_ID + "," + UPDATED_PAYMENT_ID);

        // Get all the transactionList where paymentId equals to UPDATED_PAYMENT_ID
        defaultTransactionShouldNotBeFound("paymentId.in=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentIdIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentId is not null
        defaultTransactionShouldBeFound("paymentId.specified=true");

        // Get all the transactionList where paymentId is null
        defaultTransactionShouldNotBeFound("paymentId.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentIdContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentId contains DEFAULT_PAYMENT_ID
        defaultTransactionShouldBeFound("paymentId.contains=" + DEFAULT_PAYMENT_ID);

        // Get all the transactionList where paymentId contains UPDATED_PAYMENT_ID
        defaultTransactionShouldNotBeFound("paymentId.contains=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentIdNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentId does not contain DEFAULT_PAYMENT_ID
        defaultTransactionShouldNotBeFound("paymentId.doesNotContain=" + DEFAULT_PAYMENT_ID);

        // Get all the transactionList where paymentId does not contain UPDATED_PAYMENT_ID
        defaultTransactionShouldBeFound("paymentId.doesNotContain=" + UPDATED_PAYMENT_ID);
    }

    @Test
    @Transactional
    void getAllTransactionsByReservationReferenceIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reservationReference equals to DEFAULT_RESERVATION_REFERENCE
        defaultTransactionShouldBeFound("reservationReference.equals=" + DEFAULT_RESERVATION_REFERENCE);

        // Get all the transactionList where reservationReference equals to UPDATED_RESERVATION_REFERENCE
        defaultTransactionShouldNotBeFound("reservationReference.equals=" + UPDATED_RESERVATION_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReservationReferenceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reservationReference not equals to DEFAULT_RESERVATION_REFERENCE
        defaultTransactionShouldNotBeFound("reservationReference.notEquals=" + DEFAULT_RESERVATION_REFERENCE);

        // Get all the transactionList where reservationReference not equals to UPDATED_RESERVATION_REFERENCE
        defaultTransactionShouldBeFound("reservationReference.notEquals=" + UPDATED_RESERVATION_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReservationReferenceIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reservationReference in DEFAULT_RESERVATION_REFERENCE or UPDATED_RESERVATION_REFERENCE
        defaultTransactionShouldBeFound("reservationReference.in=" + DEFAULT_RESERVATION_REFERENCE + "," + UPDATED_RESERVATION_REFERENCE);

        // Get all the transactionList where reservationReference equals to UPDATED_RESERVATION_REFERENCE
        defaultTransactionShouldNotBeFound("reservationReference.in=" + UPDATED_RESERVATION_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReservationReferenceIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reservationReference is not null
        defaultTransactionShouldBeFound("reservationReference.specified=true");

        // Get all the transactionList where reservationReference is null
        defaultTransactionShouldNotBeFound("reservationReference.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByReservationReferenceContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reservationReference contains DEFAULT_RESERVATION_REFERENCE
        defaultTransactionShouldBeFound("reservationReference.contains=" + DEFAULT_RESERVATION_REFERENCE);

        // Get all the transactionList where reservationReference contains UPDATED_RESERVATION_REFERENCE
        defaultTransactionShouldNotBeFound("reservationReference.contains=" + UPDATED_RESERVATION_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByReservationReferenceNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where reservationReference does not contain DEFAULT_RESERVATION_REFERENCE
        defaultTransactionShouldNotBeFound("reservationReference.doesNotContain=" + DEFAULT_RESERVATION_REFERENCE);

        // Get all the transactionList where reservationReference does not contain UPDATED_RESERVATION_REFERENCE
        defaultTransactionShouldBeFound("reservationReference.doesNotContain=" + UPDATED_RESERVATION_REFERENCE);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentStatus equals to DEFAULT_PAYMENT_STATUS
        defaultTransactionShouldBeFound("paymentStatus.equals=" + DEFAULT_PAYMENT_STATUS);

        // Get all the transactionList where paymentStatus equals to UPDATED_PAYMENT_STATUS
        defaultTransactionShouldNotBeFound("paymentStatus.equals=" + UPDATED_PAYMENT_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentStatus not equals to DEFAULT_PAYMENT_STATUS
        defaultTransactionShouldNotBeFound("paymentStatus.notEquals=" + DEFAULT_PAYMENT_STATUS);

        // Get all the transactionList where paymentStatus not equals to UPDATED_PAYMENT_STATUS
        defaultTransactionShouldBeFound("paymentStatus.notEquals=" + UPDATED_PAYMENT_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentStatusIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentStatus in DEFAULT_PAYMENT_STATUS or UPDATED_PAYMENT_STATUS
        defaultTransactionShouldBeFound("paymentStatus.in=" + DEFAULT_PAYMENT_STATUS + "," + UPDATED_PAYMENT_STATUS);

        // Get all the transactionList where paymentStatus equals to UPDATED_PAYMENT_STATUS
        defaultTransactionShouldNotBeFound("paymentStatus.in=" + UPDATED_PAYMENT_STATUS);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentStatus is not null
        defaultTransactionShouldBeFound("paymentStatus.specified=true");

        // Get all the transactionList where paymentStatus is null
        defaultTransactionShouldNotBeFound("paymentStatus.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentType equals to DEFAULT_PAYMENT_TYPE
        defaultTransactionShouldBeFound("paymentType.equals=" + DEFAULT_PAYMENT_TYPE);

        // Get all the transactionList where paymentType equals to UPDATED_PAYMENT_TYPE
        defaultTransactionShouldNotBeFound("paymentType.equals=" + UPDATED_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentType not equals to DEFAULT_PAYMENT_TYPE
        defaultTransactionShouldNotBeFound("paymentType.notEquals=" + DEFAULT_PAYMENT_TYPE);

        // Get all the transactionList where paymentType not equals to UPDATED_PAYMENT_TYPE
        defaultTransactionShouldBeFound("paymentType.notEquals=" + UPDATED_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentTypeIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentType in DEFAULT_PAYMENT_TYPE or UPDATED_PAYMENT_TYPE
        defaultTransactionShouldBeFound("paymentType.in=" + DEFAULT_PAYMENT_TYPE + "," + UPDATED_PAYMENT_TYPE);

        // Get all the transactionList where paymentType equals to UPDATED_PAYMENT_TYPE
        defaultTransactionShouldNotBeFound("paymentType.in=" + UPDATED_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    void getAllTransactionsByPaymentTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where paymentType is not null
        defaultTransactionShouldBeFound("paymentType.specified=true");

        // Get all the transactionList where paymentType is null
        defaultTransactionShouldNotBeFound("paymentType.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionShouldBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].paymentId").value(hasItem(DEFAULT_PAYMENT_ID)))
            .andExpect(jsonPath("$.[*].reservationReference").value(hasItem(DEFAULT_RESERVATION_REFERENCE)))
            .andExpect(jsonPath("$.[*].paymentStatus").value(hasItem(DEFAULT_PAYMENT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].paymentType").value(hasItem(DEFAULT_PAYMENT_TYPE.toString())));

        // Check, that the count call also returns 1
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionShouldNotBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).get();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction
            .paymentId(UPDATED_PAYMENT_ID)
            .reservationReference(UPDATED_RESERVATION_REFERENCE)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .paymentType(UPDATED_PAYMENT_TYPE);

        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testTransaction.getReservationReference()).isEqualTo(UPDATED_RESERVATION_REFERENCE);
        assertThat(testTransaction.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testTransaction.getPaymentType()).isEqualTo(UPDATED_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    void putNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction.reservationReference(UPDATED_RESERVATION_REFERENCE);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getPaymentId()).isEqualTo(DEFAULT_PAYMENT_ID);
        assertThat(testTransaction.getReservationReference()).isEqualTo(UPDATED_RESERVATION_REFERENCE);
        assertThat(testTransaction.getPaymentStatus()).isEqualTo(DEFAULT_PAYMENT_STATUS);
        assertThat(testTransaction.getPaymentType()).isEqualTo(DEFAULT_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .paymentId(UPDATED_PAYMENT_ID)
            .reservationReference(UPDATED_RESERVATION_REFERENCE)
            .paymentStatus(UPDATED_PAYMENT_STATUS)
            .paymentType(UPDATED_PAYMENT_TYPE);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getPaymentId()).isEqualTo(UPDATED_PAYMENT_ID);
        assertThat(testTransaction.getReservationReference()).isEqualTo(UPDATED_RESERVATION_REFERENCE);
        assertThat(testTransaction.getPaymentStatus()).isEqualTo(UPDATED_PAYMENT_STATUS);
        assertThat(testTransaction.getPaymentType()).isEqualTo(UPDATED_PAYMENT_TYPE);
    }

    @Test
    @Transactional
    void patchNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeDelete = transactionRepository.findAll().size();

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, transaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
