export interface User {
    id: number,
    name: string,
    surname: string,
    phone: string,
    country: string,
    password: string,
    email: string
}

export interface CertificateRequest {
    requestType: string,
    issuer: number,
    certificateType: string,
    subject: number,
    refusalReason: number
}