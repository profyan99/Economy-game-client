package com.example.profy.gamecalculator.util;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;

import java.io.IOException;

public class NfcManager {
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String getNfcCardData(Intent intent) throws IOException {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        boolean auth;
        MifareClassic mfc = MifareClassic.get(tagFromIntent);
        mfc.connect();
        String id;
        auth = mfc.authenticateSectorWithKeyA(0, MifareClassic.KEY_DEFAULT);
        if (auth) {
            id = bytesToHex(mfc.readBlock(mfc.sectorToBlock(0)));
        } else {
            throw new IOException("Auth failed");
        }
        return id;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
