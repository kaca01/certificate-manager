import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WelcomePageComponent } from '../app/components/welcome-page/welcome-page.component';
import { LoginComponent } from 'src/app/components/login/login.component';
import { CertificateComponent } from 'src/app/components/certificate/certificate.component';

const routes: Routes = [
  { path: 'welcome-page', component: WelcomePageComponent},
  { path: 'login', component: LoginComponent},
  { path: 'certificate', component: CertificateComponent},
  { path: '', redirectTo: '/welcome-page', pathMatch: 'full' },
  { path: '**', component: WelcomePageComponent },
]


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
