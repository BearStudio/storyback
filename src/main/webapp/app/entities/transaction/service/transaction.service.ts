import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITransaction, getTransactionIdentifier } from '../transaction.model';

export type EntityResponseType = HttpResponse<ITransaction>;
export type EntityArrayResponseType = HttpResponse<ITransaction[]>;

@Injectable({ providedIn: 'root' })
export class TransactionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/transactions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(transaction: ITransaction): Observable<EntityResponseType> {
    return this.http.post<ITransaction>(this.resourceUrl, transaction, { observe: 'response' });
  }

  update(transaction: ITransaction): Observable<EntityResponseType> {
    return this.http.put<ITransaction>(`${this.resourceUrl}/${getTransactionIdentifier(transaction) as number}`, transaction, {
      observe: 'response',
    });
  }

  partialUpdate(transaction: ITransaction): Observable<EntityResponseType> {
    return this.http.patch<ITransaction>(`${this.resourceUrl}/${getTransactionIdentifier(transaction) as number}`, transaction, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITransaction[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTransactionToCollectionIfMissing(
    transactionCollection: ITransaction[],
    ...transactionsToCheck: (ITransaction | null | undefined)[]
  ): ITransaction[] {
    const transactions: ITransaction[] = transactionsToCheck.filter(isPresent);
    if (transactions.length > 0) {
      const transactionCollectionIdentifiers = transactionCollection.map(transactionItem => getTransactionIdentifier(transactionItem)!);
      const transactionsToAdd = transactions.filter(transactionItem => {
        const transactionIdentifier = getTransactionIdentifier(transactionItem);
        if (transactionIdentifier == null || transactionCollectionIdentifiers.includes(transactionIdentifier)) {
          return false;
        }
        transactionCollectionIdentifiers.push(transactionIdentifier);
        return true;
      });
      return [...transactionsToAdd, ...transactionCollection];
    }
    return transactionCollection;
  }
}
