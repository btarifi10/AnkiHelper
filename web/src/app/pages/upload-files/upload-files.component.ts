import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {UploadFileService} from "../../services/upload-file.service";
import {HttpEventType, HttpResponse} from "@angular/common/http";

@Component({
  selector: 'app-upload-files',
  templateUrl: './upload-files.component.html',
  styleUrls: ['./upload-files.component.css']
})
export class UploadFilesComponent implements OnInit {

  selectedFiles: FileList;
  currentFile: File;
  progress = 0;
  message = '';

  fileInfos: Observable<any>;
  nameDisplay: string = "No file selected";

  constructor(private uploadService: UploadFileService) {
  }

  ngOnInit(): void {
    this.fileInfos = this.uploadService.getFiles();
  }

  selectFile(event): void {
    this.selectedFiles = event.target.files;
  }

  upload(): void {
    this.progress = 0;

    this.currentFile = this.selectedFiles.item(0);

    this.uploadService.upload(this.currentFile).subscribe(
      event => {
        if (event.type === HttpEventType.UploadProgress) {
          this.progress = Math.round(100 * event.loaded / event.total);
        } else if (event instanceof HttpResponse) {
          this.message = event.body.message;
          this.fileInfos = this.uploadService.getFiles();
        }
      },
      error => {
        this.progress = 0;
        this.message = "Could not upload the file!";
        this.currentFile = undefined;
      });
    this.selectedFiles = undefined;
    this.fileInfos = this.uploadService.getFiles();
  }

  deleteFile(filename: string) {
    this.uploadService.deleteFile(filename).subscribe(
      event => {
        console.log("File deleted successfully.");
      },
      error => {
        console.log("Could not delete the file!");
      });

    this.fileInfos = this.uploadService.getFiles();
  }
}
