package com.example.spicyisland.koan.Tools

import java.security.*
import javax.crypto.*
import javax.crypto.spec.GCMParameterSpec

internal class DeCryptor {

    private var keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore")

    init {
        keyStore.load(null)
    }

    fun decryptData(encryptedData: ByteArray, iv: ByteArray): String {

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, (keyStore.getEntry("myAlias", null) as KeyStore.SecretKeyEntry).secretKey,
                    GCMParameterSpec(128, iv))

        return String(cipher.doFinal(encryptedData), charset("UTF-8"))

    }

}
