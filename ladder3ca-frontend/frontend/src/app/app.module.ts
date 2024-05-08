import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ModalModule } from 'ngx-bootstrap/modal';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { FileSaverModule } from 'ngx-filesaver';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { TabsModule } from 'ngx-bootstrap/tabs';
import { PaginationModule } from 'ngx-bootstrap/pagination';
import { CollapseModule } from 'ngx-bootstrap/collapse';

import { NgHttpLoaderModule } from 'ng-http-loader';
import { EditorModule, TINYMCE_SCRIPT_SRC } from '@tinymce/tinymce-angular';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { HighlightModule, HIGHLIGHT_OPTIONS } from 'ngx-highlightjs';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { WholePageComponent } from './layout/whole-page/whole-page.component';
import { TopNavComponent } from './layout/top-nav/top-nav.component';
import { SideBarComponent } from './layout/side-bar/side-bar.component';
import { FooterComponent } from './layout/footer/footer.component';
import { HomeComponent } from './page/home/home.component';
import { AnnotateComponent } from './page/annotate/annotate.component';
import { DatabaseComponent } from './page/database/database.component';
import { ErrorModalComponent } from './modal/error-modal/error-modal.component';
import { InfoModalComponent } from './modal/info-modal/info-modal.component';
import { ImprintComponent } from './page/imprint/imprint.component';
import { AnnotationResultComponent } from './page/annotate/annotation-result/annotation-result.component';
import { CreationTaskComponent } from './page/database/creation-task/creation-task.component';
import { AnnotatedTextComponent } from './page/database/annotated-text/annotated-text.component';
import { AnnotationComponent } from './page/database/annotated-text/annotation/annotation.component';
import { ModifierComponent } from './page/database/modifier/modifier.component';
import { SubactComponent } from './page/database/subact/subact.component';
import { PosModelComponent } from './page/database/pos-model/pos-model.component';
import { CreationTaskListComponent } from './page/database/creation-task/creation-task-list/creation-task-list.component';
import { SortHeaderComponent } from './layout/sort-header/sort-header.component';
import { EditCreationTaskModalComponent } from './modal/edit-creation-task-modal/edit-creation-task-modal.component';
import { ConfirmationModalComponent } from './modal/confirmation-modal/confirmation-modal.component';
import { ModifierListComponent } from './page/database/modifier/modifier-list/modifier-list.component';
import { EditModifierModalComponent } from './modal/edit-modifier-modal/edit-modifier-modal.component';
import { EditSubactModalComponent } from './modal/edit-subact-modal/edit-subact-modal.component';
import { SubactListComponent } from './page/database/subact/subact-list/subact-list.component';
import { AnnotatedTextListComponent } from './page/database/annotated-text/annotated-text-list/annotated-text-list.component';
import { EditTextWithMetadataModalComponent } from './modal/edit-text-with-metadata-modal/edit-text-with-metadata-modal.component';
import { NoTextAvailableComponent } from './page/database/annotated-text/no-text-available/no-text-available.component';
import { SelectModifierModalComponent } from './modal/select-modifier-modal/select-modifier-modal.component';
import { SelectSubactModalComponent } from './modal/select-subact-modal/select-subact-modal.component';
import { LoginModalComponent } from './modal/login-modal/login-modal.component';
import { LogoutModalComponent } from './modal/logout-modal/logout-modal.component';
import { AppUserComponent } from './page/app-user/app-user.component';
import { ChangePasswordModalComponent } from './modal/change-password-modal/change-password-modal.component';
import { AppUserListComponent } from './page/app-user/app-user-list/app-user-list.component';
import { AuditLogListComponent } from './page/app-user/audit-log-list/audit-log-list.component';
import { AddUserModalComponent } from './modal/add-user-modal/add-user-modal.component';
import { UpdateUserModalComponent } from './modal/update-user-modal/update-user-modal.component';
import { FindAnnotatedTextComponent } from './page/database/annotated-text/find-annotated-text/find-annotated-text.component';
import { AddSearchClauseModalComponent } from './modal/add-search-clause-modal/add-search-clause-modal.component';
import { OffCanvasComponent } from './layout/off-canvas/off-canvas.component';
import { AboutComponent } from './page/about/about.component';
import { AboutDataCollectionComponent } from './page/about/about-data-collection/about-data-collection.component';
import { AboutTrainingDataOverviewComponent } from './page/about/about-training-data-overview/about-training-data-overview.component';
import { AboutCodingSchemeComponent } from './page/about/about-coding-scheme/about-coding-scheme.component';
import { AboutHowToUseComponent } from './page/about/about-how-to-use/about-how-to-use.component';
import { AboutRelatedPublicationsComponent } from './page/about/about-related-publications/about-related-publications.component';
import { AboutContributorsComponent } from './page/about/about-contributors/about-contributors.component';

@NgModule({
  declarations: [
    AppComponent,
    WholePageComponent,
    TopNavComponent,
    SideBarComponent,
    FooterComponent,
    HomeComponent,
    AnnotateComponent,
    DatabaseComponent,
    ErrorModalComponent,
    InfoModalComponent,
    ImprintComponent,
    AnnotationResultComponent,
    CreationTaskComponent,
    AnnotatedTextComponent,
    AnnotationComponent,
    ModifierComponent,
    SubactComponent,
    PosModelComponent,
    CreationTaskListComponent,
    SortHeaderComponent,
    EditCreationTaskModalComponent,
    ConfirmationModalComponent,
    ModifierListComponent,
    EditModifierModalComponent,
    EditSubactModalComponent,
    SubactListComponent,
    AnnotatedTextListComponent,
    EditTextWithMetadataModalComponent,
    NoTextAvailableComponent,
    SelectModifierModalComponent,
    SelectSubactModalComponent,
    LoginModalComponent,
    LogoutModalComponent,
    AppUserComponent,
    ChangePasswordModalComponent,
    AppUserListComponent,
    AuditLogListComponent,
    AddUserModalComponent,
    UpdateUserModalComponent,
    FindAnnotatedTextComponent,
    AddSearchClauseModalComponent,
    OffCanvasComponent,
    AboutComponent,
    AboutDataCollectionComponent,
    AboutTrainingDataOverviewComponent,
    AboutCodingSchemeComponent,
    AboutHowToUseComponent,
    AboutRelatedPublicationsComponent,
    AboutContributorsComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    EditorModule,
    FileSaverModule,
    ModalModule.forRoot(),
    CollapseModule.forRoot(),
    BsDropdownModule.forRoot(),
    NgHttpLoaderModule.forRoot(),
    // TooltipModule.forRoot(),
    NgbTooltipModule,
    TabsModule.forRoot(),
    PaginationModule.forRoot(),
    FontAwesomeModule,
    HighlightModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
