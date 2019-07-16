package com.example.profy.gamecalculator.util;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.widget.Toast;

import java.io.IOException;

import static com.example.profy.gamecalculator.MainActivity.bytesToHex;

public class NfcManager {
    public static String getNfcCardData(Intent intent) throws IOException {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        boolean auth;
        MifareClassic mfc = MifareClassic.get(tagFromIntent);
        String metaInfo = "";
        mfc.connect();
        int type = mfc.getType();
        int sectorCount = mfc.getSectorCount();
        String typeS = "";
        switch (type) {
            case MifareClassic.TYPE_CLASSIC:
                typeS = "TYPE_CLASSIC";
                break;
            case MifareClassic.TYPE_PLUS:
                typeS = "TYPE_PLUS";
                break;
            case MifareClassic.TYPE_PRO:
                typeS = "TYPE_PRO";
                break;
            case MifareClassic.TYPE_UNKNOWN:
                typeS = "TYPE_UNKNOWN";
                break;
        }
        metaInfo += "Card type：" + typeS + "\nwith" + sectorCount + " sectors and "
                + mfc.getBlockCount() + " blocks\nStorage Space: " + mfc.getSize() + "B\n";

        auth = mfc.authenticateSectorWithKeyA(0, MifareClassic.KEY_DEFAULT);
        if (auth) {
            metaInfo += "Sector 0: Verified successfully\n";
            metaInfo += "ID: " + bytesToHex(mfc.readBlock( mfc.sectorToBlock(0)));
        } else {
            metaInfo += "Sector 0: Verified failure";
        }
        return metaInfo;
    }
}
