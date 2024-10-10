import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { LoanService } from '../loan.service';
import { Loan } from '../model/Loan';
import { Client } from 'src/app/client/model/Client';
import { Game } from 'src/app/game/model/Game';
import { ClientService } from 'src/app/client/client.service';
import { GameService } from 'src/app/game/game.service';

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
    private loanService: LoanService,
    private clientService: ClientService,
    private gameService: GameService
  ) { }

  ngOnInit(): void {
    this.clientService.getClients().subscribe(clients => this.clients = clients);
    this.gameService.getGames().subscribe(games => this.games = games);
  }

  onSave() {
    const startDate = new Date(this.loan.startDate);
    const endDate = new Date(this.loan.endDate);

    if (endDate < startDate) {
      alert('La fecha de fin no puede ser anterior a la fecha de inicio.');
      return;
    }

    const diffTime = Math.abs(endDate.getTime() - startDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24)); 

    if (diffDays > this.maxLoanDays) {
      alert('El periodo de préstamo no puede superar los 14 días.');
      return;
    }

    this.loanService.checkLoanValidity(this.loan).subscribe(isValid => {
      if (!isValid) {
        alert('Este préstamo no es válido debido a conflictos con otros préstamos.');
        return;
      }

      this.loanService.saveLoan(this.loan).subscribe(() => {
        this.dialogRef.close();
      });
    });
  }

  onClose() {
    this.dialogRef.close();
  }
}
