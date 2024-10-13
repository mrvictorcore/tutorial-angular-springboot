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

  constructor(private http: HttpClient) { }

  getLoans(pageable: Pageable): Observable<LoanPage> {
    return this.http.post<LoanPage>(`${this.apiUrl}/paginated`, { pageable: pageable });
  }

  getLoansFiltered(title?: string, clientId?: number, searchDate?: string): Observable<Loan[]> {
    let params = new HttpParams();
    if (title) params = params.set('title', title);
    if (clientId) params = params.set('clientId', clientId.toString());
    if (searchDate) params = params.set('searchDate', searchDate);

    return this.http.get<Loan[]>(`${this.apiUrl}/filtered`, { params });
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
