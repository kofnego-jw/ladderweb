import { Component, OnInit } from '@angular/core';
import { AuditLogDTO } from 'src/app/model';
import { AppService } from 'src/app/service/app.service';

@Component({
  selector: 'app-audit-log-list',
  templateUrl: './audit-log-list.component.html',
  styleUrls: ['./audit-log-list.component.scss'],
})
export class AuditLogListComponent implements OnInit {
  logs: AuditLogDTO[] = [];

  constructor(private app: AppService) {}

  ngOnInit(): void {
    this.loadLogs();
  }

  loadLogs(): void {
    this.app.adm_getLogs().subscribe((logs) => this.setLogs(logs));
  }

  setLogs(logs: AuditLogDTO[]): void {
    this.logs = logs;
  }
}
