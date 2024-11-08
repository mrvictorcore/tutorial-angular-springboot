import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { ClientService } from '../client.service';
import { Client } from '../model/Client';
import { DialogAlertComponent } from 'src/app/core/dialog-alert/dialog-alert.component';

@Component({
  selector: 'app-client-edit',
  templateUrl: './client-edit.component.html',
  styleUrls: ['./client-edit.component.scss']
})
export class ClientEditComponent implements OnInit {

  client: Client;

  constructor(
    public dialogRef: MatDialogRef<ClientEditComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private clientService: ClientService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    if (this.data.client != null) {
      this.client = Object.assign({}, this.data.client);
    }
    else {
      this.client = new Client();
    }
  }

  onSave(): void {
    this.clientService.saveClient(this.client).subscribe({
      next: () => this.dialogRef.close(),
      error: (err) => {
        if (err) {
          this.openAlertDialog('Error al guardar cliente', 'El nombre del cliente ya está en uso. Por favor, elige otro.');
        }
      }
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }

  openAlertDialog(title: string, description: string): void {
    this.dialog.open(DialogAlertComponent, {
      data: { title: title, description: description }
    });
  }
}
