import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Pageable } from 'src/app/core/model/page/Pageable';
import { LoanEditComponent } from '../loan-edit/loan-edit.component';
import { DialogConfirmationComponent } from 'src/app/core/dialog-confirmation/dialog-confirmation.component';
import { LoanService } from '../loan.service';
import { Loan } from '../model/Loan';
import { Client } from 'src/app/client/model/Client';
import { Game } from 'src/app/game/model/Game';
import { GameService } from 'src/app/game/game.service';
import { ClientService } from 'src/app/client/client.service';

@Component({
  selector: 'app-loan-list',
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.scss']
})
export class LoanListComponent implements OnInit {
  clients: Client[];
  games: Game[];
  loans: Loan[];

  filterTitle: string;
  filterClient: Client;
  filterGame: Game;
  filterDate: Date;

  pageNumber: number = 0;
  pageSize: number = 5;
  totalElements: number = 0;

  dataSource = new MatTableDataSource<Loan>();
  displayedColumns: string[] = ['id', 'game', 'client', 'startDate', 'endDate', 'action'];

  constructor(
    private loanService: LoanService,
    private gameService: GameService,
    private clientService: ClientService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadPage();
    this.gameService.getGames().subscribe(games => this.games = games);
    this.clientService.getClients().subscribe(clients => this.clients = clients);
  }

  onCleanFilter(): void {
    this.filterGame = null;
    this.filterClient = null;
    this.filterDate = null;
    this.loadPage();
  }

  onSearch(): void {
    this.loadPage();
  }

  loadPage(event?: PageEvent) {
    let pageable: Pageable = {
      pageNumber: this.pageNumber,
      pageSize: this.pageSize,
      sort: [{
        property: 'id',
        direction: 'ASC'
      }]
    };

    if (event != null) {
      pageable.pageSize = event.pageSize;
      pageable.pageNumber = event.pageIndex;
    }

    if (this.filterGame || this.filterClient || this.filterDate) {
      let gameId = this.filterGame ? this.filterGame.id : null;
      let clientId = this.filterClient ? this.filterClient.id : null;
      let searchDate = this.filterDate ? this.filterDate.toISOString().split('T')[0] : null;

      this.loanService.getLoansFiltered(gameId, clientId, searchDate, pageable).subscribe(data => {
        this.dataSource.data = data.content;
        this.pageNumber = data.pageable.pageNumber;
        this.pageSize = data.pageable.pageSize;
        this.totalElements = data.totalElements;
      });

    } else {
      this.loanService.getLoans(pageable).subscribe(data => {
        this.dataSource.data = data.content;
        this.pageNumber = data.pageable.pageNumber;
        this.pageSize = data.pageable.pageSize;
        this.totalElements = data.totalElements;
      });
    }
  }

  createLoan() {
    const dialogRef = this.dialog.open(LoanEditComponent, { data: {} });
    dialogRef.afterClosed().subscribe(() => this.ngOnInit());
  }

  editLoan(loan: Loan) {
    const dialogRef = this.dialog.open(LoanEditComponent, { data: { loan } });
    dialogRef.afterClosed().subscribe(() => this.ngOnInit());
  }

  deleteLoan(loan: Loan) {
    const dialogRef = this.dialog.open(DialogConfirmationComponent, {
      data: { title: "Eliminar préstamo", description: "¿Desea eliminar el préstamo?" }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loanService.deleteLoan(loan.id).subscribe(() => this.ngOnInit());
      }
    });
  }
}
