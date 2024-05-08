import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import {
  AnnotatingRequestDTO,
  AnnotatingResultDTO,
  AppRole,
  AppUserDTO,
  AuditLogDTO,
  CreateUserRequestDTO,
  CreationTaskDTO,
  ModifierAnnotationDTO,
  ModifierDTO,
  PasswordChangeRequestDTO,
  RetrainModelMessagesDTO,
  SearchClause,
  SearchRequest,
  SearchResultDTO,
  SimpleMessageDTO,
  SubactAnnotationDTO,
  SubactDTO,
  TextDTO,
  TextWithMetadataDTO,
  UpdateUserRequestDTO,
} from '../model';

@Injectable({
  providedIn: 'root',
})
export class RestService {
  private readonly PUBLIC_BASE_URL = 'public/api/v1';
  private readonly USER_BASE_URL = 'api/v1';

  constructor(private http: HttpClient) {}

  // Public API

  login(
    username: string,
    password: string
  ): Observable<AppUserDTO | undefined> {
    const authHeaderString = 'Basic ' + window.btoa(username + ':' + password);
    let headers = new HttpHeaders(
      !!username && !!password
        ? {
            Authorization: authHeaderString,
          }
        : {}
    );
    headers = headers.append('X-Requested-With', 'XMLHttpRequest');
    const url = this.PUBLIC_BASE_URL + '/login';
    return this.http
      .post<AppUserDTO>(url, null, { headers: headers })
      .pipe(map((dto) => AppUserDTO.fromDTO(dto)));
  }

  logout(): Observable<any> {
    const url = this.PUBLIC_BASE_URL + '/logout';
    return this.http.post<SimpleMessageDTO>(url, '');
  }

  activeUser(): Observable<AppUserDTO | undefined> {
    const url = this.PUBLIC_BASE_URL + '/login';
    return this.http
      .get<AppUserDTO>(url)
      .pipe(map((dto) => AppUserDTO.fromDTO(dto)));
  }

  listAllCreationTasks(): Observable<CreationTaskDTO[]> {
    const url = this.PUBLIC_BASE_URL + '/creation_task/';
    return this.http
      .get<CreationTaskDTO[]>(url)
      .pipe(map((dtos) => CreationTaskDTO.fromDTOs(dtos)));
  }
  readCreationTask(id: number): Observable<CreationTaskDTO | undefined> {
    const url = this.PUBLIC_BASE_URL + '/creation_task/' + id;
    return this.http
      .get<CreationTaskDTO>(url)
      .pipe(map((dto) => CreationTaskDTO.fromDTO(dto)));
  }

  listAllTexts(): Observable<TextDTO[]> {
    const url = this.PUBLIC_BASE_URL + '/text/';
    return this.http
      .get<TextDTO[]>(url)
      .pipe(map((dtos) => TextDTO.fromDTOs(dtos)));
  }

  getOneText(id: string): Observable<TextWithMetadataDTO | undefined> {
    const url = this.PUBLIC_BASE_URL + '/text/' + id;
    return this.http
      .get<TextWithMetadataDTO>(url)
      .pipe(map((dto) => TextWithMetadataDTO.fromDTO(dto)));
  }

  listAllModifiers(): Observable<ModifierDTO[]> {
    const url = this.PUBLIC_BASE_URL + '/modifier/';
    return this.http
      .get<ModifierDTO[]>(url)
      .pipe(map((dtos) => ModifierDTO.fromDTOs(dtos)));
  }

  getOneModifier(id: number): Observable<ModifierDTO | undefined> {
    const url = this.PUBLIC_BASE_URL + '/modifier/' + id;
    return this.http
      .get<ModifierDTO>(url)
      .pipe(map((dto) => ModifierDTO.fromDTO(dto)));
  }

  listAllSubacts(): Observable<SubactDTO[]> {
    const url = this.PUBLIC_BASE_URL + '/subact/';
    return this.http
      .get<SubactDTO[]>(url)
      .pipe(map((dtos) => SubactDTO.fromDTOs(dtos)));
  }

  getOneSubact(id: number): Observable<SubactDTO | undefined> {
    const url = this.PUBLIC_BASE_URL + '/subact/' + id;
    return this.http
      .get<SubactDTO>(url)
      .pipe(map((dto) => SubactDTO.fromDTO(dto)));
  }

  getModifierAnnotationsByText(
    textId: string
  ): Observable<ModifierAnnotationDTO[]> {
    const url = this.PUBLIC_BASE_URL + '/annotation/' + textId + '/modifier/';
    return this.http
      .get<ModifierAnnotationDTO[]>(url)
      .pipe(map((dtos) => ModifierAnnotationDTO.fromDTOs(dtos)));
  }

  getSubactAnnotationsByText(
    textId: string
  ): Observable<SubactAnnotationDTO[]> {
    const url = this.PUBLIC_BASE_URL + '/annotation/' + textId + '/subact/';
    return this.http
      .get<SubactAnnotationDTO[]>(url)
      .pipe(map((dtos) => SubactAnnotationDTO.fromDTOs(dtos)));
  }

  annotateUseModel(
    request: AnnotatingRequestDTO
  ): Observable<AnnotatingResultDTO | undefined> {
    const url = this.PUBLIC_BASE_URL + '/annotate/';
    return this.http
      .post<AnnotatingResultDTO>(url, request)
      .pipe(map((dto) => AnnotatingResultDTO.fromDTO(dto)));
  }

  // User API

  changePassword(
    userId: number,
    oldPassword: string,
    newPassword: string
  ): Observable<SimpleMessageDTO | undefined> {
    const url = this.USER_BASE_URL + '/user/changePassword';
    const req = new PasswordChangeRequestDTO(userId, oldPassword, newPassword);
    return this.http
      .post<SimpleMessageDTO>(url, req)
      .pipe(map((dto) => SimpleMessageDTO.fromDTO(dto)));
  }

  getLogsByUser(): Observable<AuditLogDTO[]> {
    const url = this.USER_BASE_URL + '/user/logs';
    return this.http
      .get<AuditLogDTO[]>(url)
      .pipe(map((dtos) => AuditLogDTO.fromDTOs(dtos)));
  }

  // Main API

  listLatestTexts(count: number): Observable<TextDTO[]> {
    const url = this.USER_BASE_URL + '/text/latest/' + count;
    return this.http
      .get<TextDTO[]>(url)
      .pipe(map((dtos) => TextDTO.fromDTOs(dtos)));
  }

  updateCreationTask(
    id: number,
    dto: CreationTaskDTO
  ): Observable<CreationTaskDTO | undefined> {
    const url = this.USER_BASE_URL + '/creation_task/' + id;
    return this.http
      .put<CreationTaskDTO>(url, dto)
      .pipe(map((dto) => CreationTaskDTO.fromDTO(dto)));
  }

  createCreationTask(
    dto: CreationTaskDTO
  ): Observable<CreationTaskDTO | undefined> {
    const url = this.USER_BASE_URL + '/creation_task/';
    return this.http
      .post<CreationTaskDTO>(url, dto)
      .pipe(map((dto) => CreationTaskDTO.fromDTO(dto)));
  }

  deleteCreationTask(id: number): Observable<CreationTaskDTO[]> {
    const url = this.USER_BASE_URL + '/creation_task/' + id;
    return this.http
      .delete<CreationTaskDTO[]>(url)
      .pipe(map((dtos) => CreationTaskDTO.fromDTOs(dtos)));
  }

  updateOneText(
    id: string,
    text: TextWithMetadataDTO
  ): Observable<TextWithMetadataDTO | undefined> {
    const url = this.USER_BASE_URL + '/text/' + id;
    return this.http
      .put<TextWithMetadataDTO>(url, text)
      .pipe(map((dto) => TextWithMetadataDTO.fromDTO(dto)));
  }

  addNewText(
    text: TextWithMetadataDTO
  ): Observable<TextWithMetadataDTO | undefined> {
    const url = this.USER_BASE_URL + '/text/';
    return this.http
      .post<TextWithMetadataDTO>(url, text)
      .pipe(map((dto) => TextWithMetadataDTO.fromDTO(dto)));
  }

  deleteText(id: string): Observable<TextDTO[]> {
    const url = this.USER_BASE_URL + '/text/' + id;
    return this.http
      .delete<TextDTO[]>(url)
      .pipe(map((dtos) => TextDTO.fromDTOs(dtos)));
  }

  updateOneModifier(
    id: number,
    dto: ModifierDTO
  ): Observable<ModifierDTO | undefined> {
    const url = this.USER_BASE_URL + '/modifier/' + id;
    return this.http
      .put<ModifierDTO>(url, dto)
      .pipe(map((dto) => ModifierDTO.fromDTO(dto)));
  }

  addNewModifier(dto: ModifierDTO): Observable<ModifierDTO | undefined> {
    const url = this.USER_BASE_URL + '/modifier/';
    return this.http
      .post<ModifierDTO>(url, dto)
      .pipe(map((dto) => ModifierDTO.fromDTO(dto)));
  }

  deleteModifier(id: number): Observable<ModifierDTO[]> {
    const url = this.USER_BASE_URL + '/modifier/' + id;
    return this.http
      .delete<ModifierDTO[]>(url)
      .pipe(map((dtos) => ModifierDTO.fromDTOs(dtos)));
  }

  updateOneSubact(
    id: number,
    dto: SubactDTO
  ): Observable<SubactDTO | undefined> {
    const url = this.USER_BASE_URL + '/subact/' + id;
    return this.http
      .put<SubactDTO>(url, dto)
      .pipe(map((dto) => SubactDTO.fromDTO(dto)));
  }

  addNewSubact(dto: SubactDTO): Observable<SubactDTO | undefined> {
    const url = this.USER_BASE_URL + '/subact/';
    return this.http
      .post<SubactDTO>(url, dto)
      .pipe(map((dto) => SubactDTO.fromDTO(dto)));
  }

  deleteSubact(id: number): Observable<SubactDTO[]> {
    const url = this.USER_BASE_URL + '/subact/' + id;
    return this.http
      .delete<SubactDTO[]>(url)
      .pipe(map((dtos) => SubactDTO.fromDTOs(dtos)));
  }

  updateModifierAnnotationsByText(
    textId: string,
    annotations: ModifierAnnotationDTO[]
  ): Observable<ModifierAnnotationDTO[]> {
    const url = this.USER_BASE_URL + '/annotation/' + textId + '/modifier/';
    return this.http
      .put<ModifierAnnotationDTO[]>(url, annotations)
      .pipe(map((dtos) => ModifierAnnotationDTO.fromDTOs(dtos)));
  }

  updateSubactAnnotationsByText(
    textId: string,
    annotations: SubactAnnotationDTO[]
  ): Observable<SubactAnnotationDTO[]> {
    const url = this.USER_BASE_URL + '/annotation/' + textId + '/subact/';
    return this.http
      .put<SubactAnnotationDTO[]>(url, annotations)
      .pipe(map((dtos) => SubactAnnotationDTO.fromDTOs(dtos)));
  }

  retrainAllModels(): Observable<SimpleMessageDTO | undefined> {
    const url = this.USER_BASE_URL + '/model/retrainAll';
    return this.http
      .post<SimpleMessageDTO>(url, '')
      .pipe(map((dto) => SimpleMessageDTO.fromDTO(dto)));
  }

  retrainTrainingRunning(): Observable<SimpleMessageDTO | undefined> {
    const url = this.USER_BASE_URL + '/model/retrainAll';
    return this.http
      .get<SimpleMessageDTO>(url)
      .pipe(map((dto) => SimpleMessageDTO.fromDTO(dto)));
  }

  retrainModelMessages(): Observable<RetrainModelMessagesDTO | undefined> {
    const url = this.USER_BASE_URL + '/model/retrainAll/messages';
    return this.http
      .get<RetrainModelMessagesDTO>(url)
      .pipe(map((dto) => RetrainModelMessagesDTO.fromDTO(dto)));
  }

  retrainModifierModelByLanguage(
    id: number,
    langCode: string
  ): Observable<SimpleMessageDTO | undefined> {
    const url = this.USER_BASE_URL + '/model/modifier/' + id + '/' + langCode;
    return this.http
      .post<SimpleMessageDTO>(url, '')
      .pipe(map((dto) => SimpleMessageDTO.fromDTO(dto)));
  }

  retrainSubactModelByLanguage(
    id: number,
    langCode: string
  ): Observable<SimpleMessageDTO | undefined> {
    const url = this.USER_BASE_URL + '/model/subact/' + id + '/' + langCode;
    return this.http
      .post<SimpleMessageDTO>(url, '')
      .pipe(map((dto) => SimpleMessageDTO.fromDTO(dto)));
  }

  search(clauses: SearchClause[]): Observable<SearchResultDTO | undefined> {
    const url = this.USER_BASE_URL + '/search';
    const request = new SearchRequest(clauses);
    return this.http
      .post<SearchResultDTO>(url, request)
      .pipe(map((dto) => SearchResultDTO.fromDTO(dto)));
  }

  searchAndDownload(clause: SearchClause[]): void {
    const url = this.USER_BASE_URL + '/corpus/download';
    const request = new SearchRequest(clause);
    this.http
      .post(url, request, { responseType: 'blob' })
      .subscribe((blob) =>
        this.downloadFile(blob, 'ladder_search_result.json', 'application/json')
      );
  }

  searchAndDownloadCsv(clause: SearchClause[]): void {
    const url = this.USER_BASE_URL + '/corpus/download/csv';
    const request = new SearchRequest(clause);
    this.http
      .post(url, request, { responseType: 'blob' })
      .subscribe((blob) =>
        this.downloadFile(blob, 'ladder_search_result.csv', 'text/csv')
      );
  }

  searchAndDownloadTei(clause: SearchClause[]): void {
    const url = this.USER_BASE_URL + '/corpus/download/xmltei';
    const request = new SearchRequest(clause);
    this.http
      .post(url, request, { responseType: 'blob' })
      .subscribe((blob) =>
        this.downloadFile(blob, 'ladder_search_result.xml', 'text/xml')
      );
  }

  downloadFile(data: Blob, filename: string, type: string): void {
    const blob = new Blob([data], { type: type });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    link.dispatchEvent(
      new MouseEvent('click', { bubbles: true, cancelable: true, view: window })
    );
    setTimeout(() => {
      window.URL.revokeObjectURL(url);
      link.remove();
    }, 100);
  }

  // Admin API

  adm_listAllUsers(): Observable<AppUserDTO[]> {
    const url = this.USER_BASE_URL + '/admin/user';
    return this.http
      .get<AppUserDTO[]>(url)
      .pipe(map((dtos) => AppUserDTO.fromDTOs(dtos)));
  }

  adm_createUser(
    emailAddress: string,
    password: string,
    roles: AppRole[]
  ): Observable<SimpleMessageDTO | undefined> {
    const url = this.USER_BASE_URL + '/admin/user';
    const req = new CreateUserRequestDTO(emailAddress, password, roles);
    return this.http
      .post<SimpleMessageDTO>(url, req)
      .pipe(map((dto) => SimpleMessageDTO.fromDTO(dto)));
  }

  adm_updateUser(
    id: number,
    emailAddress: string,
    roles: AppRole[]
  ): Observable<SimpleMessageDTO | undefined> {
    const req = new UpdateUserRequestDTO(id, emailAddress, roles);
    const url = this.USER_BASE_URL + '/admin/user/update';
    return this.http
      .post<SimpleMessageDTO>(url, req)
      .pipe(map((dto) => SimpleMessageDTO.fromDTO(dto)));
  }

  adm_changePassword(
    id: number,
    newPassword: string
  ): Observable<SimpleMessageDTO | undefined> {
    const url = this.USER_BASE_URL + '/admin/user/password';
    const req = new PasswordChangeRequestDTO(id, '', newPassword);
    return this.http
      .post<SimpleMessageDTO>(url, req)
      .pipe(map((dto) => SimpleMessageDTO.fromDTO(dto)));
  }

  adm_getLogs(): Observable<AuditLogDTO[]> {
    const url = this.USER_BASE_URL + '/admin/logs';
    return this.http
      .get<AuditLogDTO[]>(url)
      .pipe(map((dtos) => AuditLogDTO.fromDTOs(dtos)));
  }
}
