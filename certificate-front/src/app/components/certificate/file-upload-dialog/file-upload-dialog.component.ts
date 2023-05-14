import { HttpClient } from '@angular/common/http';
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CertificateService } from 'src/app/service/certificate.service';

@Component({
  selector: 'app-file-upload-dialog',
  templateUrl: './file-upload-dialog.component.html',
  styleUrls: ['./file-upload-dialog.component.css']
})
export class FileUploadDialogComponent implements OnInit {

  ngOnInit(): void {
  }

  close(): void {

  }

  fileName = '';

  constructor(private http: HttpClient, private dialogRef: MatDialogRef<FileUploadDialogComponent>, private certificateService: CertificateService, 
              private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) data: any) {

    }

  checkValidity(): void {
    console.log(this.fileName);
  }

  onFileSelected(event: any) {

    const file:File = event.target.files[0];

    if (file) {

        this.fileName = file.name;

        const formData = new FormData();

        formData.append("thumbnail", file);

        const upload$ = this.http.post("/api/thumbnail-upload", formData);

        upload$.subscribe();
    }
}

}
