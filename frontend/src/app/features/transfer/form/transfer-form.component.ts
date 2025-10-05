import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Subject, takeUntil } from 'rxjs';

import { BeneficiosService } from '../../../core/services/beneficios.service';
import { Beneficio, TransferRequest } from '../../../core/models/beneficio.model';

@Component({
  selector: 'app-transfer-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    RouterLink
  ],
  templateUrl: './transfer-form.component.html',
  styleUrl: './transfer-form.component.scss'
})
export class TransferFormComponent implements OnInit, OnDestroy {
  private readonly destroy$ = new Subject<void>();

  private readonly fb = inject(FormBuilder);
  private readonly service = inject(BeneficiosService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  private readonly differentAccountsValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
    const fromId = control.get('fromId')?.value;
    const toId = control.get('toId')?.value;
    if (fromId && toId && Number(fromId) === Number(toId)) {
      return { sameAccount: true };
    }
    return null;
  };

  readonly form = this.fb.nonNullable.group(
    {
      fromId: [null as number | null, [Validators.required, Validators.min(1)]],
      toId: [null as number | null, [Validators.required, Validators.min(1)]],
      amount: [null as number | null, [Validators.required, Validators.min(0.01)]]
    },
    { validators: this.differentAccountsValidator }
  );

  saving = false;
  beneficios: Beneficio[] = [];

  ngOnInit(): void {
    // Carrega a lista de benefícios ativos para popular os selects
    this.service
      .list()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (items) => {
          this.beneficios = items.filter((item) => item.ativo !== false);
          this.ensureValidSelections();
        }
      });

    this.route.queryParamMap.pipe(takeUntil(this.destroy$)).subscribe((params) => {
      const fromId = Number(params.get('fromId'));
      if (!Number.isNaN(fromId) && fromId > 0) {
        this.form.patchValue({ fromId });
        this.ensureValidSelections();
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { fromId, toId, amount } = this.form.getRawValue();
    const payload: TransferRequest = {
      fromId: Number(fromId),
      toId: Number(toId),
      amount: Number(amount)
    };

    this.saving = true;
    this.service
      .transfer(payload)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.saving = false;
          this.snackBar.open('Transferência realizada com sucesso!', 'Fechar', { duration: 3000 });
          this.router.navigate(['/beneficios']);
        },
        error: () => {
          this.saving = false;
        }
      });
  }

  private ensureValidSelections(): void {
    const { fromId, toId } = this.form.getRawValue();

    if (fromId && !this.beneficios.some((beneficio) => beneficio.id === fromId)) {
      this.form.patchValue({ fromId: null }, { emitEvent: false });
    }

    if (toId && !this.beneficios.some((beneficio) => beneficio.id === toId)) {
      this.form.patchValue({ toId: null }, { emitEvent: false });
    }
  }
}
