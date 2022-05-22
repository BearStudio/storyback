import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITransaction, Transaction } from '../transaction.model';
import { TransactionService } from '../service/transaction.service';
import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';
import { PaymentType } from 'app/entities/enumerations/payment-type.model';

@Component({
  selector: 'jhi-transaction-update',
  templateUrl: './transaction-update.component.html',
})
export class TransactionUpdateComponent implements OnInit {
  isSaving = false;
  paymentStatusValues = Object.keys(PaymentStatus);
  paymentTypeValues = Object.keys(PaymentType);

  editForm = this.fb.group({
    id: [],
    paymentId: [],
    reservationReference: [null, [Validators.required]],
    paymentStatus: [null, [Validators.required]],
    paymentType: [null, [Validators.required]],
  });

  constructor(protected transactionService: TransactionService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transaction }) => {
      this.updateForm(transaction);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transaction = this.createFromForm();
    if (transaction.id !== undefined) {
      this.subscribeToSaveResponse(this.transactionService.update(transaction));
    } else {
      this.subscribeToSaveResponse(this.transactionService.create(transaction));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransaction>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(transaction: ITransaction): void {
    this.editForm.patchValue({
      id: transaction.id,
      paymentId: transaction.paymentId,
      reservationReference: transaction.reservationReference,
      paymentStatus: transaction.paymentStatus,
      paymentType: transaction.paymentType,
    });
  }

  protected createFromForm(): ITransaction {
    return {
      ...new Transaction(),
      id: this.editForm.get(['id'])!.value,
      paymentId: this.editForm.get(['paymentId'])!.value,
      reservationReference: this.editForm.get(['reservationReference'])!.value,
      paymentStatus: this.editForm.get(['paymentStatus'])!.value,
      paymentType: this.editForm.get(['paymentType'])!.value,
    };
  }
}
