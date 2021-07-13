import {Component, OnInit} from '@angular/core';
import {Observable} from "rxjs";
import {UploadFileService} from "../../services/upload-file.service";
import {HttpEventType, HttpResponse} from "@angular/common/http";
import {FileInfo} from "../../models/FileInfo";

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

  fileInfos: FileInfo[];
  nameDisplay: string = "No file selected";

  constructor(private uploadService: UploadFileService) {
  }

  ngOnInit(): void {
    this.updateFiles()
  }

  updateFiles() : void {
    this.uploadService.getFiles().subscribe(files => {
      this.fileInfos = files;
    });
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
          this.uploadService.getFiles().subscribe(files => {
            this.fileInfos = files;
          })
        }
      },
      error => {
        this.progress = 0;
        this.message = "Could not upload the file!";
        this.currentFile = undefined;
      });
    this.selectedFiles = undefined;
  }

  deleteFile(filename: string) {
    this.uploadService.deleteFile(filename).subscribe(
      event => {
        // console.log("File deleted successfully.");
        this.uploadService.getFiles().subscribe(files => {
          this.fileInfos = files;
        });
      },
      error => {
          window.alert("Could not delete the file!");
      });

  }
}
