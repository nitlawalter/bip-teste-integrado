import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { EMPTY, Subject, filter, switchMap, takeUntil } from 'rxjs';

import { BeneficiosService } from '../../../core/services/beneficios.service';
import { BeneficioRequest } from '../../../core/models/beneficio.model';

@Component({
  selector: 'app-beneficios-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatIconModule
  ],
  templateUrl: './beneficios-form.component.html',
  styleUrl: './beneficios-form.component.scss'
})
export class BeneficiosFormComponent implements OnInit, OnDestroy {
  private readonly destroy$ = new Subject<void>();

  private readonly fb = inject(FormBuilder);
  private readonly service = inject(BeneficiosService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  readonly form = this.fb.nonNullable.group({
    nome: ['', Validators.required],
    descricao: [''],
    valor: [0, [Validators.required, Validators.min(0)]],
    ativo: [true]
  });

  loading = false;
  saving = false;
  isEdit = false;
  title = 'Novo Benefício';

  private currentId?: number;

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        takeUntil(this.destroy$),
        filter((params) => params.has('id')),
        switchMap((params) => {
          const id = Number(params.get('id'));
          if (Number.isNaN(id)) {
            return EMPTY;
          }
          this.isEdit = true;
          this.title = 'Editar Benefício';
          this.currentId = id;
          this.loading = true;
          return this.service.get(id);
        })
      )
      .subscribe({
        next: (beneficio) => {
          this.form.patchValue({
            nome: beneficio.nome,
            descricao: beneficio.descricao ?? '',
            valor: beneficio.valor,
            ativo: beneficio.ativo
          });
          this.loading = false;
        },
        error: () => {
          this.loading = false;
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

    const raw = this.form.getRawValue();
    const payload: BeneficioRequest = {
      nome: raw.nome,
      descricao: raw.descricao || undefined,
      valor: Number(raw.valor),
      ativo: raw.ativo ?? true
    };

    this.saving = true;

    const request$ = this.isEdit && this.currentId != null
      ? this.service.update(this.currentId, payload)
      : this.service.create(payload);

    request$.pipe(takeUntil(this.destroy$)).subscribe({
      next: () => {
        this.saving = false;
        this.snackBar.open('Benefício salvo com sucesso!', 'Fechar', { duration: 3000 });
        this.router.navigate(['/beneficios']);
      },
      error: () => {
        this.saving = false;
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/beneficios']);
  }
}
