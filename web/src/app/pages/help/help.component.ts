import { Component, OnInit } from '@angular/core';
import {FileInfo} from "../../models/FileInfo";
import {UploadFileService} from "../../services/upload-file.service";

@Component({
  selector: 'app-help',
  templateUrl: './help.component.html',
  styleUrls: ['./help.component.css']
})
export class HelpComponent implements OnInit {

  exampleFiles: FileInfo[];

  constructor(private uploadService: UploadFileService) { }

  ngOnInit(): void {
    this.uploadService.getExampleFiles().subscribe(files => {
      this.exampleFiles = files;
    });
  }
}
