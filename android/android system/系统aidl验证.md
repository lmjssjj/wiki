```
    mBinder.getCallingUid()
    public boolean verifyAuthenticity(int uid) {
        String[] packageNames = this.getPackageManager().getPackagesForUid(uid);
        for (String packageName : packageNames) {
            Log.v("lmjssjj", "" + packageName);
            try {
                Signature[] sigs = getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES).signatures;
                for (Signature sig : sigs) {

                    //get the raw certificate into input stream
                    final byte[] rawCert = sig.toByteArray();
                    InputStream certStream = new ByteArrayInputStream(rawCert);

                    //Read the X.509 Certificate into certBytes
                    final CertificateFactory certFactory = CertificateFactory.getInstance("X509");
                    final X509Certificate x509Cert = (X509Certificate) certFactory.generateCertificate(certStream);
                    byte[] certBytes = x509Cert.getEncoded();

                    //get the fixed SHA-1 cert
                    MessageDigest md = MessageDigest.getInstance("SHA-1");
                    md.update(certBytes);
                    byte[] certThumbprint = md.digest();

                    //cert in hex format
                    String compareHash = bytesToHex(certThumbprint);

                    Log.v("lmjssjj", compareHash);
                }
            } catch (Exception e) {
                Log.e("lmjssjj", "Exception reading client data!" + e);
            }
        }
        return true;
    }

    private static String bytesToHex(byte[] inputBytes) {
        final StringBuilder sb = new StringBuilder();
        for (byte b : inputBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
```

