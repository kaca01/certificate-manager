import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from '../infrastructure/app-routing.module';
import { AppComponent } from './app.component';
import { WelcomePageComponent } from './components/welcome-page/welcome-page.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from 'src/infrastructure/material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { LoginComponent } from './components/login/login.component';
import { CertificateComponent } from './components/certificate/certificate.component';
import { CertificateRequestComponent } from './components/certificate-request/certificate-request.component';
import { AuthService } from './service/auth.service';

import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './components/interceptor/TokenInterceptor';
import { ApiService } from './service/api.service';
import { UserService } from './service/user.service';
import { ConfigService } from './service/config.service';
import { CertificateService } from './service/certificate.service';
import { NavigationComponent } from './components/navigation/navigation.component';
import { RegistrationComponent } from './components/registration/registration.component';
import { RequestsComponent } from './components/requests/requests.component';

@NgModule({
  declarations: [
    AppComponent,
    WelcomePageComponent,
    LoginComponent,
    CertificateComponent,
    CertificateRequestComponent,
    NavigationComponent,
    RegistrationComponent,
    RequestsComponent,
  ],
  imports: [
    FormsModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    ReactiveFormsModule,
    HttpClientModule,
  ],
  providers: [
  {
    provide: HTTP_INTERCEPTORS,
    useClass: TokenInterceptor,
    multi: true
  },
    AuthService,
    ApiService,
    UserService,
    ConfigService,
    CertificateService
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}

