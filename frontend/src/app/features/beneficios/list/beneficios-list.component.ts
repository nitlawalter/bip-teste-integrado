import { CommonModule } from '@angular/common';
import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSort, MatSortModule } from '@angular/material/sort';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Subject, takeUntil } from 'rxjs';

import { BeneficiosService } from '../../../core/services/beneficios.service';
import { Beneficio } from '../../../core/models/beneficio.model';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-beneficios-list',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatDialogModule
  ],
  templateUrl: './beneficios-list.component.html',
  styleUrl: './beneficios-list.component.scss'
})
export class BeneficiosListComponent implements OnInit, AfterViewInit, OnDestroy {
  displayedColumns = ['id', 'nome', 'descricao', 'valor', 'ativo', 'acoes'];
  dataSource = new MatTableDataSource<Beneficio>([]);
  loading = false;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly service: BeneficiosService, private readonly router: Router, private readonly dialog: MatDialog) {}

  ngOnInit(): void {
    this.dataSource.filterPredicate = (data: Beneficio, filter: string) => {
      const term = filter.trim().toLowerCase();
      return (
        data.nome?.toLowerCase().includes(term) ||
        (data.descricao ? data.descricao.toLowerCase().includes(term) : false)
      );
    };
    this.load();
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  applyFilter(value: string): void {
    this.dataSource.filter = value.trim().toLowerCase();
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  create(): void {
    this.router.navigate(['/beneficios/new']);
  }

  edit(beneficio: Beneficio): void {
    this.router.navigate(['/beneficios', beneficio.id]);
  }

  transfer(beneficio: Beneficio): void {
    this.router.navigate(['/beneficios/transfer'], { queryParams: { fromId: beneficio.id } });
  }

  delete(beneficio: Beneficio): void {
    const data: ConfirmDialogData = {
      title: 'Remover benefício',
      message: `Tem certeza de que deseja remover "${beneficio.nome}"?`,
      confirmLabel: 'Remover'
    };

    this.dialog
      .open(ConfirmDialogComponent, { data })
      .afterClosed()
      .pipe(takeUntil(this.destroy$))
      .subscribe((confirmed) => {
        if (confirmed) {
          this.service
            .delete(beneficio.id)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => this.load(),
              error: () => {}
            });
        }
      });
  }

  private load(): void {
    this.loading = true;
    this.service
      .list()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (beneficios) => {
          this.dataSource.data = beneficios;
          this.loading = false;
        },
        error: () => {
          this.loading = false;
        }
      });
  }
}
