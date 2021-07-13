import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";
import {FileInfo} from "../models/FileInfo";

@Injectable({
  providedIn: 'root'
})
export class UploadFileService {

  private baseUrl = '/api';

  constructor(private http: HttpClient) { }

  upload(file : File): Observable<HttpEvent<any>> {
    const formData: FormData = new FormData();

    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.baseUrl}/upload`,formData, {
    reportProgress: true,
    responseType: 'json'
    });

    return this.http.request(req);
  }

  deleteFile(filename: string): Observable<HttpEvent<any>> {

    const req = new HttpRequest('DELETE', `${this.baseUrl}/?filename=${filename}`, {
      reportProgress: true,
      responseType: 'json'
    });

    return this.http.request(req);
  }

  getFiles(): Observable<FileInfo[]> {
    return this.http.get<FileInfo[]>(`${this.baseUrl}/files`);
  }

  getExampleFiles(): Observable<FileInfo[]> {
    return this.http.get<FileInfo[]>(`${this.baseUrl}/exampleFiles`);
  }
}
