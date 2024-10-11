import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoanPage } from './model/LoanPage';
import { Pageable } from '../core/model/page/Pageable';
import { Loan } from './model/Loan';

@Injectable({
  providedIn: 'root'
})
export class LoanService {

  private apiUrl = 'http://localhost:8080/loan';

  constructor(private http: HttpClient) { }

  getLoans(pageable: Pageable): Observable<LoanPage> {
    return this.http.post<LoanPage>(`${this.apiUrl}/paginated`, pageable);
  }

  getLoansFiltered(title?: string, clientId?: number, gameId?: number, filterDate?: Date): Observable<Loan[]> {
    return this.http.get<Loan[]>(this.composeFindUrl(title, clientId, gameId, filterDate));
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

  private composeFindUrl(title?: string, clientId?: number, gameId?: number, filterDate?: Date): string {
    let params = '';

    if (title != null) {
      params += `title=${title}`;
    }

    if (clientId != null) {
      if (params !== '') params += '&';
      params += `clientId=${clientId}`;
    }

    if (gameId != null) {
      if (params !== '') params += '&';
      params += `gameId=${gameId}`;
    }

    if (filterDate != null) {
      if (params !== '') params += '&';
      params += `filterDate=${filterDate.toISOString()}`;
    }

    let url = this.apiUrl;
    if (params !== '') {
      url += `?${params}`;
    }

    return url;
  }
}
