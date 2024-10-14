import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Pageable } from '../core/model/page/Pageable';
import { Author } from './model/Author';
import { AuthorPage } from './model/AuthorPage';

@Injectable({
  providedIn: 'root'
})
export class AuthorService {

  private apiUrl = 'http://localhost:8080/author';

  constructor(
    private http: HttpClient
  ) { }

  getAuthors(pageable: Pageable): Observable<AuthorPage> {
    return this.http.post<AuthorPage>(this.apiUrl, {pageable:pageable});
  }

  saveAuthor(author: Author): Observable<void> {

    let url = this.apiUrl;
    if (author.id != null) url += '/'+author.id;

    return this.http.put<void>(url, author);
  }

  deleteAuthor(idAuthor : number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idAuthor}`);
  }    

  getAllAuthors(): Observable<Author[]> {
    return this.http.get<Author[]>(this.apiUrl);
  }

}
