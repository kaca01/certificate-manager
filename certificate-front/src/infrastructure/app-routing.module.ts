import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WelcomePageComponent } from '../app/components/welcome-page/welcome-page.component';
import { LoginComponent } from 'src/app/components/login/login.component';
import { CertificateComponent } from 'src/app/components/certificate/certificate.component';
import { RegistrationComponent } from 'src/app/components/registration/registration.component';
import { RequestsComponent } from 'src/app/components/requests/requests.component';


const routes: Routes = [
  { path: 'welcome-page', component: WelcomePageComponent},
  { path: 'login', component: LoginComponent},
  { path: 'certificate', component: CertificateComponent},
  { path: 'registration', component: RegistrationComponent},
  { path: 'requests', component: RequestsComponent},
  { path: '', redirectTo: '/welcome-page', pathMatch: 'full' },
  { path: '**', component: WelcomePageComponent },
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
