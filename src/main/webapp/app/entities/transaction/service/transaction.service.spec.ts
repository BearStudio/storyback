import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentType } from 'app/entities/enumerations/payment-type.model';
import { ITransaction, Transaction } from '../transaction.model';

import { TransactionService } from './transaction.service';

describe('Transaction Service', () => {
  let service: TransactionService;
  let httpMock: HttpTestingController;
  let elemDefault: ITransaction;
  let expectedResult: ITransaction | ITransaction[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TransactionService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      paymentId: 'AAAAAAA',
      reservationReference: 'AAAAAAA',
      paymentStatus: PaymentStatus.PENDING,
      paymentType: PaymentType.STRIPE,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Transaction', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Transaction()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Transaction', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          paymentId: 'BBBBBB',
          reservationReference: 'BBBBBB',
          paymentStatus: 'BBBBBB',
          paymentType: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Transaction', () => {
      const patchObject = Object.assign(
        {
          paymentId: 'BBBBBB',
          paymentStatus: 'BBBBBB',
          paymentType: 'BBBBBB',
        },
        new Transaction()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Transaction', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          paymentId: 'BBBBBB',
          reservationReference: 'BBBBBB',
          paymentStatus: 'BBBBBB',
          paymentType: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Transaction', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTransactionToCollectionIfMissing', () => {
      it('should add a Transaction to an empty array', () => {
        const transaction: ITransaction = { id: 123 };
        expectedResult = service.addTransactionToCollectionIfMissing([], transaction);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transaction);
      });

      it('should not add a Transaction to an array that contains it', () => {
        const transaction: ITransaction = { id: 123 };
        const transactionCollection: ITransaction[] = [
          {
            ...transaction,
          },
          { id: 456 },
        ];
        expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, transaction);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Transaction to an array that doesn't contain it", () => {
        const transaction: ITransaction = { id: 123 };
        const transactionCollection: ITransaction[] = [{ id: 456 }];
        expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, transaction);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transaction);
      });

      it('should add only unique Transaction to an array', () => {
        const transactionArray: ITransaction[] = [{ id: 123 }, { id: 456 }, { id: 59755 }];
        const transactionCollection: ITransaction[] = [{ id: 123 }];
        expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, ...transactionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const transaction: ITransaction = { id: 123 };
        const transaction2: ITransaction = { id: 456 };
        expectedResult = service.addTransactionToCollectionIfMissing([], transaction, transaction2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transaction);
        expect(expectedResult).toContain(transaction2);
      });

      it('should accept null and undefined values', () => {
        const transaction: ITransaction = { id: 123 };
        expectedResult = service.addTransactionToCollectionIfMissing([], null, transaction, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transaction);
      });

      it('should return initial array if no Transaction is added', () => {
        const transactionCollection: ITransaction[] = [{ id: 123 }];
        expectedResult = service.addTransactionToCollectionIfMissing(transactionCollection, undefined, null);
        expect(expectedResult).toEqual(transactionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
