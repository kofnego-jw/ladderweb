import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './page/home/home.component';
import { AnnotateComponent } from './page/annotate/annotate.component';
import { DatabaseComponent } from './page/database/database.component';
import { ImprintComponent } from './page/imprint/imprint.component';
import { CreationTaskComponent } from './page/database/creation-task/creation-task.component';
import { AnnotatedTextComponent } from './page/database/annotated-text/annotated-text.component';
import { AnnotationComponent } from './page/database/annotated-text/annotation/annotation.component';
import { ModifierComponent } from './page/database/modifier/modifier.component';
import { SubactComponent } from './page/database/subact/subact.component';
import { PosModelComponent } from './page/database/pos-model/pos-model.component';
import { NoTextAvailableComponent } from './page/database/annotated-text/no-text-available/no-text-available.component';
import { AppUserComponent } from './page/app-user/app-user.component';
import { AppUserListComponent } from './page/app-user/app-user-list/app-user-list.component';
import { AuditLogListComponent } from './page/app-user/audit-log-list/audit-log-list.component';
import { AboutComponent } from './page/about/about.component';
import { AboutDataCollectionComponent } from './page/about/about-data-collection/about-data-collection.component';
import { AboutTrainingDataOverviewComponent } from './page/about/about-training-data-overview/about-training-data-overview.component';
import { AboutCodingSchemeComponent } from './page/about/about-coding-scheme/about-coding-scheme.component';
import { AboutHowToUseComponent } from './page/about/about-how-to-use/about-how-to-use.component';
import { AboutRelatedPublicationsComponent } from './page/about/about-related-publications/about-related-publications.component';
import { AboutContributorsComponent } from './page/about/about-contributors/about-contributors.component';
const routes: Routes = [
  { path: 'home', component: HomeComponent, title: 'LadderWeb - Home' },
  {
    path: 'about',
    component: AboutComponent,
    title: 'LadderWeb - About',
    children: [
      {
        path: 'data-collection',
        component: AboutDataCollectionComponent,
        title: 'LadderWeb - About - Data Collection',
      },
      {
        path: 'training-data-overview',
        component: AboutTrainingDataOverviewComponent,
        title: 'LadderWeb - About - Training Data Overview',
      },
      {
        path: 'coding-scheme',
        component: AboutCodingSchemeComponent,
        title: 'LadderWeb - About - Coding Scheme',
      },
      {
        path: 'how-to-use',
        component: AboutHowToUseComponent,
        title: 'LadderWeb - About - How to Use',
      },
      {
        path: 'related-publications',
        component: AboutRelatedPublicationsComponent,
        title: 'LadderWeb - About - Related Publications',
      },
      {
        path: 'contributors',
        component: AboutContributorsComponent,
        title: 'LadderWeb - About - Contributors',
      },
      { path: '**', redirectTo: '/about/data-collection' },
    ],
  },
  {
    path: 'annotate',
    component: AnnotateComponent,
    title: 'LadderWeb - Annotate Text',
  },
  {
    path: 'database',
    component: DatabaseComponent,
    title: 'LadderWeb - Manage Data',
    children: [
      {
        path: 'creation-task',
        component: CreationTaskComponent,
        title: 'LadderWeb - Manage Data - Creation Tasks',
      },
      {
        path: 'text',
        component: AnnotatedTextComponent,
        title: 'LadderWeb - Manage Data - Texts and Annotations',
        children: [
          { path: 'annotation', component: AnnotationComponent },
          { path: '**', component: NoTextAvailableComponent },
        ],
      },
      {
        path: 'modifier',
        component: ModifierComponent,
        title: 'LadderWeb - Manage Data - Modifiers',
      },
      {
        path: 'subact',
        component: SubactComponent,
        title: 'LadderWeb - Manage Data - Subacts',
      },
      {
        path: 'model',
        component: PosModelComponent,
        title: 'LadderWeb - Manage Data - Models',
      },
      { path: '**', redirectTo: '/database/text' },
    ],
  },
  {
    path: 'admin',
    component: AppUserComponent,
    title: 'LadderWeb - App Administration',
    children: [
      { path: 'user', component: AppUserListComponent },
      { path: 'log', component: AuditLogListComponent },
    ],
  },
  {
    path: 'imprint',
    component: ImprintComponent,
    title: 'LadderWeb - Imprint and Data Protection',
  },

  { path: '**', redirectTo: '/annotate' },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      useHash: true,
      scrollPositionRestoration: 'enabled',
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
