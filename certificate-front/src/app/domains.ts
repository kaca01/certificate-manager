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
    issuer: string,
    certificateType: string,
    subject: number,
    refusalReason: number
}