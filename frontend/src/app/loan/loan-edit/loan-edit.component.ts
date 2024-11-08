import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { LoanService } from '../loan.service';
import { Loan } from '../model/Loan';
import { Client } from 'src/app/client/model/Client';
import { Game } from 'src/app/game/model/Game';
import { ClientService } from 'src/app/client/client.service';
import { GameService } from 'src/app/game/game.service';
import { DialogAlertComponent } from 'src/app/core/dialog-alert/dialog-alert.component';

@Component({
  selector: 'app-loan-edit',
  templateUrl: './loan-edit.component.html',
  styleUrls: ['./loan-edit.component.scss']
})
export class LoanEditComponent implements OnInit {
  loan: Loan = new Loan();
  clients: Client[];
  games: Game[];
  maxLoanDays = 14;

  constructor(
    public dialogRef: MatDialogRef<LoanEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private loanService: LoanService,
    private clientService: ClientService,
    private gameService: GameService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.clientService.getClients().subscribe(clients => this.clients = clients);
    this.gameService.getGames().subscribe(games => this.games = games);
  }

  onSave() {
    if (this.loan.startDate) {
      this.loan.startDate = this.convertToISODate(new Date(this.loan.startDate));
    }
  
    if (this.loan.endDate) {
      this.loan.endDate = this.convertToISODate(new Date(this.loan.endDate));
    }
    
    this.loanService.saveLoan(this.loan).subscribe({
      next: () => this.dialogRef.close(),
      error: (error) => {
        const errorMessage = error.message || 'Ha ocurrido un error inesperado.';
        this.openAlertDialog('Error al guardar préstamo', errorMessage);
      }
    });
  }

  convertToISODate(date: Date): string {
    const offsetMs = date.getTimezoneOffset() * 60 * 1000;
    const adjustedDate = new Date(date.getTime() - offsetMs);
    
    return adjustedDate.toISOString().split('T')[0];
  }

  onClose() {
    this.dialogRef.close();
  }

  openAlertDialog(title: string, description: string): void {
    this.dialog.open(DialogAlertComponent, {
      data: { title: title, description: description }
    });
  }
}
