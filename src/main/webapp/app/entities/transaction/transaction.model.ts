import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentType } from 'app/entities/enumerations/payment-type.model';

export interface ITransaction {
  id?: number;
  paymentId?: string | null;
  reservationReference?: string;
  paymentStatus?: PaymentStatus;
  paymentType?: PaymentType;
}

export class Transaction implements ITransaction {
  constructor(
    public id?: number,
    public paymentId?: string | null,
    public reservationReference?: string,
    public paymentStatus?: PaymentStatus,
    public paymentType?: PaymentType
  ) {}
}

export function getTransactionIdentifier(transaction: ITransaction): number | undefined {
  return transaction.id;
}
