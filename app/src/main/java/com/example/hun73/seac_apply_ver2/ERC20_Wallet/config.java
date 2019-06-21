package com.example.hun73.seac_apply_ver2.ERC20_Wallet;

import android.util.Log;

public class config
{

    private static final String TAG = "Wallet Test config";

    public static String addressethnode(int node)
    {
        switch (node)
        {
            case 1:
                Log.e(TAG, "addressethnode: case 1");
                return "http://176.74.13.102:18087";

            case 2:
                Log.e(TAG, "addressethnode: case 2");
                // https://ropsten.infura.io/v3/fd57c15053744a1a948594530f47f029
                // http://192.168.0.33:8547
                // https://mainnet.infura.io/avyPSzkHujVHtFtf8xwY
                // https://ropsten.infura.io/v3/fd57c15053744a1a948594530f47f029
                return "https://ropsten.infura.io/v3/fd57c15053744a1a948594530f47f029";

            default:
                Log.e(TAG, "addressethnode: default");
                // https://mainnet.infura.io/avyPSzkHujVHtFtf8xwY
                // https://ropsten.infura.io/v3/fd57c15053744a1a948594530f47f029
                return "ropsten.infura.io/v3/fd57c15053744a1a948594530f47f029";
        }
    }

    public static String addresssmartcontract(int contract)
    {
        switch (contract)
        {
            case 1:
//                return "0x5C456316Da36c1c769FA277cE677CB8F690c5767";
                Log.e(TAG, "addresssmartcontract: case 1");
                return "0x2326d71c03a6d91d10d1d3545db8ca10d1a81daf";

            default:
//                return "0x89205A3A3b2A69De6Dbf7f01ED13B2108B2c43e7";
                Log.e(TAG, "addresssmartcontract: defult");
                return "0x2326d71c03a6d91d10d1d3545db8ca10d1a81daf";
        }
    }

    public static String passwordwallet()
    {
        Log.e(TAG, "passwordwallet()");
        return "";
    }


}
