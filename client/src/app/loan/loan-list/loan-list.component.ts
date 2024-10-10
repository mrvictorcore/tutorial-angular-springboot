import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Loan } from '../model/Loan';
import { LoanService } from '../loan.service';
import { MatDialog } from '@angular/material/dialog';
import { LoanEditComponent } from '../loan-edit/loan-edit.component';
import { DialogConfirmationComponent } from 'src/app/core/dialog-confirmation/dialog-confirmation.component';
import { PageEvent } from '@angular/material/paginator';
import { Pageable } from 'src/app/core/model/page/Pageable';
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
  loans: Loan[];
  games: Game[];

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
  ) { }

  ngOnInit(): void {
    this.loadPage();

    this.gameService.getGames().subscribe(games => this.games = games);

    this.clientService.getClients().subscribe(clients => this.clients = clients);
  }

  onCleanFilter(): void {
    this.filterTitle = null;
    this.filterClient = null;
    this.filterDate = null;
    this.onSearch();
  }

  onSearch(): void {
    let title = this.filterTitle;
    let clientId = this.filterClient != null ? this.filterClient.id : null;
    let gameId = this.filterGame != null ? this.filterGame.id : null;
    let filterDate = this.filterDate;

    this.loanService.getLoansFiltered(title, clientId, gameId, filterDate).subscribe(
      loans => this.loans = loans
    );
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

    this.loanService.getLoans(pageable).subscribe(data => {
      this.dataSource.data = data.content;
      this.pageNumber = data.pageable.pageNumber;
      this.pageSize = data.pageable.pageSize;
      this.totalElements = data.totalElements;
    });
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
