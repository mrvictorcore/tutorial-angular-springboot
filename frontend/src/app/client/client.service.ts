import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from './model/Client';

@Injectable({
  providedIn: 'root'
})
export class ClientService {

  private apiUrl = 'http://localhost:8080/client';

  constructor(
    private http: HttpClient
  ) { }

  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.apiUrl);
  }

  saveClient(client: Client): Observable<Client> {
    let url = this.apiUrl;
    if (client.id != null) url += '/' + client.id;

    return this.http.put<Client>(url, client);
  }

  deleteClient(idClient: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${idClient}`);
  }
}
