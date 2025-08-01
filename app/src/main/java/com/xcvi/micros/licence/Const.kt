package com.xcvi.micros.licence

const val PUBKEY_1 = """
    DON'T TRIPLE QUOTE REAL KEY!!!
    
    Monetize → Monetization setup → Licensing (sometimes labeled “Monetization setup / Licensing & in‑app billing”).
    Copy the “Base64-encoded RSA public key”.

    It typically starts with: MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8A...
    and ends with: ...IDAQAB
    """
const val PUBKEY_2 = ""
const val PUBKEY_3 = ""

val LICENSE_SALT  = byteArrayOf(
    -12, 88, 23, -1, 17, 66, -33, 91, 54, -120,
    5, -64, 11, 73, -27, 99, -5, 31, 42, -30
)
