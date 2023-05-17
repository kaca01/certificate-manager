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
  private fileAsByteArray: any = {};
  private base64: any = {};

  ngOnInit(): void {
  }

  close(): void {
    this.dialogRef.close();
  }

  fileName = '';

  constructor(private http: HttpClient, private dialogRef: MatDialogRef<FileUploadDialogComponent>, private certificateService: CertificateService, 
              private snackBar: MatSnackBar,
    @Inject(MAT_DIALOG_DATA) data: any) {

    }

  checkValidity(): void {
    this.certificateService.checkValidityByCopy(this.fileAsByteArray).subscribe((res) => {
      console.log('ressss');  
      console.log(res);
      if (res) this.openSnackBar("Uploaded certificate is valid!");
      else this.openSnackBar("Uploaded certificate is not valid!");
    }, (error) => {
      console.log(error);
      this.openSnackBar(error.error.message, 5000);
    });

    this.dialogRef.close();
  }

  onFileSelected(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    const files: FileList | null = inputElement.files;

    if (files && files.length > 0) {
      const file: File = files[0];
      const reader: FileReader = new FileReader();
      reader.onloadend = () => {
        const fileContent: ArrayBuffer | null = reader.result as ArrayBuffer;
        if (fileContent) {
          const byteArray = new Uint8Array(fileContent);
          console.log(byteArray);
          // converting from unsigned to signed byte array
          const signedArray: Int8Array = new Int8Array(byteArray.length);
          for (let i = 0; i < byteArray.length; i++) {
            const unsignedValue = byteArray[i];
            signedArray[i] = unsignedValue > 127 ? unsignedValue - 256 : unsignedValue;
          }
          this.fileAsByteArray = signedArray;
          
          // the following block of code is for updating name of file on dialog
          this.fileName = file.name;
          const formData = new FormData();
          formData.append("thumbnail", file);
          const upload$ = this.http.post("/api/thumbnail-upload", formData);
          upload$.subscribe();
        }
      };
      reader.readAsArrayBuffer(file);
    }
  }

  openSnackBar(snackMsg : string, duration: number = 2000) : void {
    this.snackBar.open(snackMsg, "Dismiss", {
      duration: duration
    });
  }

}
