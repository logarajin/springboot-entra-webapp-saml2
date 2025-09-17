Go to Microsoft Entra admin center → App registrations → Your app → Certificates & secrets → Upload certificate.
Upload the certificate to your App Registration:

Go to Microsoft Entra admin center → App registrations → Your app → Certificates & secrets → Upload certificate.

Enable token encryption:

Still in the app registration → Token encryption → Assign the uploaded certificate.

Now MS Entra will:

Use the certificate’s public key to encrypt ID tokens and SAML assertions.

Expect the client (your app) to use the private key to decrypt.
