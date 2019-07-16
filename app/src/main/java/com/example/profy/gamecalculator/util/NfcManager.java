package com.example.profy.gamecalculator.util;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;

import java.io.IOException;

import static com.example.profy.gamecalculator.MainActivity.bytesToHex;

public class NfcManager {
    public static String getNfcCardData(Intent intent) throws IOException {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        boolean auth;
        MifareClassic mfc = MifareClassic.get(tagFromIntent);
        mfc.connect();
        String id = "";
        auth = mfc.authenticateSectorWithKeyA(0, MifareClassic.KEY_DEFAULT);
        if (auth) {
            id = bytesToHex(mfc.readBlock(mfc.sectorToBlock(0)));
        } else {
            throw new IOException("Auth failed");
        }
        return id;
    }
}
