import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoanPage } from './model/LoanPage';
import { Pageable } from '../core/model/page/Pageable';
import { Loan } from './model/Loan';

@Injectable({
  providedIn: 'root'
})
export class LoanService {

  private apiUrl = 'http://localhost:8080/loan';

  constructor(private http: HttpClient) {}

  getLoans(pageable: Pageable): Observable<LoanPage> {
    return this.http.post<LoanPage>(`${this.apiUrl}/paginated`, { pageable });
  }

  getLoansFiltered(gameId?: number, clientId?: number, searchDate?: string, pageable?: Pageable): Observable<LoanPage> {
    const params = {
      gameId: gameId || null,
      clientId: clientId || null,
      searchDate: searchDate || null,
      pageable: pageable || { pageNumber: 0, pageSize: 5, sort: [{ property: 'id', direction: 'ASC' }] }
    };
    return this.http.post<LoanPage>(`${this.apiUrl}/filtered`, params);
  }

  saveLoan(loan: Loan): Observable<Loan> {
    if (loan.id) {
      return this.http.put<Loan>(`${this.apiUrl}/${loan.id}`, loan);
    } else {
      return this.http.post<Loan>(this.apiUrl, loan);
    }
  }

  deleteLoan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  checkLoanValidity(loan: Loan): Observable<boolean> {
    return this.http.post<boolean>(`${this.apiUrl}/validate`, loan);
  }
}
