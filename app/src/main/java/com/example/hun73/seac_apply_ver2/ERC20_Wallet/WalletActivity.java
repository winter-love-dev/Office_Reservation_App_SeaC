package com.example.hun73.seac_apply_ver2.ERC20_Wallet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.hun73.seac_apply_ver2.R;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import androidx.appcompat.app.AppCompatActivity;

import info.bcdev.librarysdkew.GetCredentials;
import info.bcdev.librarysdkew.interfaces.callback.CBBip44;
import info.bcdev.librarysdkew.interfaces.callback.CBGetCredential;
import info.bcdev.librarysdkew.interfaces.callback.CBLoadSmartContract;
import info.bcdev.librarysdkew.interfaces.callback.CBSendingEther;
import info.bcdev.librarysdkew.interfaces.callback.CBSendingToken;
import info.bcdev.librarysdkew.smartcontract.LoadSmartContract;
import info.bcdev.librarysdkew.utils.InfoDialog;
import info.bcdev.librarysdkew.utils.ToastMsg;
import info.bcdev.librarysdkew.utils.qr.Generate;
import info.bcdev.librarysdkew.utils.qr.ScanIntegrator;
import info.bcdev.librarysdkew.wallet.Balance;
import info.bcdev.librarysdkew.wallet.SendingEther;
import info.bcdev.librarysdkew.wallet.SendingToken;
import info.bcdev.librarysdkew.wallet.generate.Bip44;
import info.bcdev.librarysdkew.web3j.Initiate;

/**
 * @author Dmitry Markelov
 * Telegram group: https://t.me/joinchat/D62dXAwO6kkm8hjlJTR9VA
 * <p>
 * Copyright (C) 2010 The Android Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p>
 * Если есть вопросы, отвечу в телеграме
 * If you have any questions, I will answer the telegram
 * <p>
 * Russian:
 * Пример включает следующие функции:
 * - Получаем адрес кошелька
 * - Получаем баланс Eth
 * - Получаем баланс Токена
 * - Получаем название Токена
 * - Получаем символ Токена
 * - Получаем адрес Контракта Токена
 * - Получаем общее количество выпущеных Токенов
 * <p>
 * <p>
 * English:
 * The example includes the following functions:
 * - Get address wallet
 * - Get balance Eth
 * - Get balance Token
 * - Get Name Token
 * - Get Symbol Token
 * - Get contract Token address
 * - Get supply Token
 */

public class WalletActivity extends AppCompatActivity implements CBGetCredential, CBLoadSmartContract, CBBip44, CBSendingEther, CBSendingToken
{
    private static final String TAG = "Wallet Test WalletActivity";

    //
    private String mNodeUrl = config.addressethnode(2);

    private String mPasswordwallet = config.passwordwallet();

    private String mSmartcontract = config.addresssmartcontract(1);

    TextView ethaddress, ethbalance, tokenname, tokensymbol, tokensupply, tokenaddress, tokenbalance, tokensymbolbalance, seedcode;
    TextView tv_gas_limit, tv_gas_price, tv_fee;
    EditText sendtoaddress, sendtokenvalue, sendethervalue;

    ImageView qr_small, qr_big;

    final Context context = this;

    IntentIntegrator qrScan;

    private Web3j mWeb3j;

    private File keydir;

    private Credentials mCredentials;

    private InfoDialog mInfoDialog;

    private BigInteger mGasPrice;

    private BigInteger mGasLimit;

    private SendingEther sendingEther;

    private SendingToken sendingToken;

    private ToastMsg toastMsg;

    //
    private LinearLayout wallet_card_1, wallet_card_2;


    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Log.e(TAG, "onCreate: config 클래스에서 노드 주소와 스마트 컨트랙트 정보를 가져와서 앱 실행됨");

        mInfoDialog = new InfoDialog(this);

        // 이더리움 주소와 잔액
        ethaddress = (TextView) findViewById(R.id.ethaddress); // Your Ether Address
        ethbalance = (TextView) findViewById(R.id.ethbalance); // Your Ether Balance

        // todo: 이 지갑에 연결된 토큰 정보
        // 토큰 이름
        tokenname = (TextView) findViewById(R.id.tokenname); // Token Name

        // 토큰 심벌
        tokensymbol = (TextView) findViewById(R.id.tokensymbol); // Token Symbol

        // 토큰 공급량
        tokensupply = (TextView) findViewById(R.id.tokensupply); // Token Supply

        // 토큰 주소
        tokenaddress = (TextView) findViewById(R.id.tokenaddress); // Token Address

        // 보유중인 토큰 잔액
        tokenbalance = (TextView) findViewById(R.id.tokenbalance); // Token Balance

        // 이게 뭐지? 토큰 심볼 잔액...? 현재 쓰이는 곳 없음
        tokensymbolbalance = (TextView) findViewById(R.id.tokensymbolbalance);

        // 토큰 배포 코드. 배포중인 주소 코드
        seedcode = (TextView) findViewById(R.id.seedcode);

        // 이더리움 또는 토큰을 보낼 주소
        sendtoaddress = (EditText) findViewById(R.id.sendtoaddress); // Address for sending ether or token

        // 전송할 토큰 수량
        sendtokenvalue = (EditText) findViewById(R.id.SendTokenValue); // Ammount token for sending

        // 전송할 이더리움 수량
        sendethervalue = (EditText) findViewById(R.id.SendEthValue); // Ammount ether for sending

        // todo: 이더리움 혹은 토큰 전송 영역.
        // 수량 입력 후 전송버튼 클릭하는 영역
        wallet_card_1 = findViewById(R.id.wallet_card_1);

        // 전송중임을 알리는 로딩 영역
        wallet_card_2 = findViewById(R.id.wallet_card_2);

        // qr코드
        qr_small = (ImageView) findViewById(R.id.qr_small);

        // todo: qr코드 스캐너 실행 버튼
        // 상대방 지갑 주소를 스캔후 아래 qr코드 입력 영역에 담는다.
        qrScan = new IntentIntegrator(this);

        // 수수료 제한 걸기
        tv_gas_limit = (TextView) findViewById(R.id.tv_gas_limit);

        // 지불할 수수료 선택하기
        tv_gas_price = (TextView) findViewById(R.id.tv_gas_price);

        //
        tv_fee = (TextView) findViewById(R.id.tv_fee);

        // todo: 수량을 입력하는 드래그 바 생성 (SeekBar 생성)
        // 토큰수량 제한하기
        final SeekBar sb_gas_limit = (SeekBar) findViewById(R.id.sb_gas_limit);
        sb_gas_limit.setOnSeekBarChangeListener(seekBarChangeListenerGL);

        // 토큰 금액 입력하기
        final SeekBar sb_gas_price = (SeekBar) findViewById(R.id.sb_gas_price);
        sb_gas_price.setOnSeekBarChangeListener(seekBarChangeListenerGP);

        //
        GetFee();

        getWeb3j();

        toastMsg = new ToastMsg();

//        keydir = this.getFilesDir("/keystore/");
        keydir = this.getFilesDir();
        Log.e(TAG, "onCreate: keydir: " + keydir.toString() );

        File[] listfiles = keydir.listFiles();
        Log.e(TAG, "onCreate: listfiles: " + listfiles.length);

        if (listfiles.length == 0)
        {
            Log.e(TAG, "onCreate: CreateWallet 실행됨");
            CreateWallet();
        } else
        {
            Log.e(TAG, "onCreate: getCredentials");
            getCredentials(keydir);
//            Log.e(TAG, "onCreate: CreateWallet 실행됨");
//            CreateWallet();
        }
    }

    @SuppressLint("LongLogTag")
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.SendEther:
                sendEther();
                break;
            case R.id.SendToken:
                sendToken();
                break;
            case R.id.qr_small:
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.qr_view);
                qr_big = (ImageView) dialog.findViewById(R.id.qr_big);
                qr_big.setImageBitmap(new Generate().Get(getEthAddress(), 900, 900));
                dialog.show();
                break;
            case R.id.qrScan:
                new ScanIntegrator(this).startScan();
                break;
        }
    }

    /* Create Wallet */
    // todo: 이더리움 주소와 보유중인 잔액 불러오기
    @SuppressLint("LongLogTag")
    @Override
    public void backGeneration(Map<String, String> result, Credentials credentials)
    {
        mCredentials = credentials;
        setEthAddress(result.get("address"));
        setEthBalance(getEthBalance());
        setSeed(result.get(seedcode));
        Log.e(TAG, "backGeneration: result.get(seedcode): " + result.get(seedcode) );
        new SaveWallet(keydir, mCredentials, mPasswordwallet).execute();
        mInfoDialog.Dismiss();
    }

    // todo: 지갑 생성하기
    // 지갑이 저장된 파일로 접근했을 때, 지갑이 존재하지 않으면 새로 생성함.
    @SuppressLint("LongLogTag")
    private void CreateWallet()
    {
        Log.e(TAG, "CreateWallet: CreateWallet()");
        Bip44 bip44 = new Bip44();
        bip44.registerCallBack(this);
        bip44.execute(mPasswordwallet);
        mInfoDialog.Get("Wallet generation", "Please wait few seconds");
    }

    // todo: 토큰 배포 코드(주소). 배포중인 주소 세팅하기
    @SuppressLint("LongLogTag")
    private void setSeed(String seed)
    {
        seedcode.setText(seed);
    }
    /* End Create Wallet*/

    /* Get Web3j*/
    @SuppressLint("LongLogTag")
    private void getWeb3j()
    {
        Log.e(TAG, "getWeb3j: mNodeUrl: " + mNodeUrl);
        new Initiate(mNodeUrl);
        mWeb3j = Initiate.sWeb3jInstance;
    }

    /* Get Credentials */
    // todo: 지갑이 저장된 폴더로 접근하기
    @SuppressLint("LongLogTag")
    private void getCredentials(File keydir)
    {
        Log.e(TAG, "getCredentials: 실행됨");

        File[] listfiles = keydir.listFiles();
        try
        {
            mInfoDialog.Get("Load Wallet", "Please wait few seconds");
            Log.e(TAG, "getCredentials: try: 지갑 정보 불러오기 대기중");

            GetCredentials getCredentials = new GetCredentials();
            getCredentials.registerCallBack(this);
            getCredentials.FromFile(listfiles[0].getAbsolutePath(), mPasswordwallet);
        } catch (IOException e)
        {
            Log.e(TAG, "getCredentials: IOException e: " + e.toString() );
            e.printStackTrace();
        } catch (CipherException e)
        {
            Log.e(TAG, "getCredentials: CipherException e: " + e.toString() );
            e.printStackTrace();
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    public void backLoadCredential(Credentials credentials)
    {
        mCredentials = credentials;
        mInfoDialog.Dismiss();
        LoadWallet();
    }
    /* End Get Credentials */

    @SuppressLint("LongLogTag")
    private void LoadWallet()
    {
        setEthAddress(getEthAddress());
        Log.e(TAG, "getEthAddress: " + getEthAddress());

        setEthBalance(getEthBalance());
        Log.e(TAG, "getEthBalance: " + getEthBalance());

        GetTokenInfo();
    }

    /* Get Address Ethereum */
    @SuppressLint("LongLogTag")
    private String getEthAddress()
    {
        Log.e(TAG, "getEthAddress: " + mCredentials.getAddress());
        return mCredentials.getAddress();
    }

    /* Set Address Ethereum */
    @SuppressLint("LongLogTag")
    private void setEthAddress(String address)
    {
        Log.e(TAG, "setEthAddress: ");
        ethaddress.setText(address);
        qr_small.setImageBitmap(new Generate().Get(address, 800, 800));
    }

    @SuppressLint("LongLogTag")
    private String getToAddress()
    {
        Log.e(TAG, "getToAddress: " + sendtoaddress.getText().toString());
        return sendtoaddress.getText().toString();
    }

    @SuppressLint("LongLogTag")
    private void setToAddress(String toAddress)
    {
        Log.e(TAG, "setToAddress: toAddress: " + toAddress);
        sendtoaddress.setText(toAddress);
    }

    /* Get Balance */
    @SuppressLint("LongLogTag")
    private String getEthBalance()
    {
        try
        {
            Log.e(TAG, "getEthBalance: getEthBalance()");
            Log.e(TAG, "getEthBalance: mWeb3j: " + mWeb3j);
            Log.e(TAG, "getEthBalance: getEthAddress()).getInEther(): " + new Balance(mWeb3j, getEthAddress()).getInEther().toString());
            return new Balance(mWeb3j, getEthAddress()).getInEther().toString();
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /* Get Send Ammount */
    @SuppressLint("LongLogTag")
    private String getSendEtherAmmount()
    {
        Log.e(TAG, "getSendEtherAmmount: " + sendethervalue.getText().toString());
        return sendethervalue.getText().toString();
    }

    // todo: 토큰 전송 시작
    @SuppressLint("LongLogTag")
    private String getSendTokenAmmount()
    {
        Log.e(TAG, "getSendTokenAmmount: " + sendtokenvalue.getText().toString());

        LottieAnimationView uploadWorkSpace_lottie = (LottieAnimationView) findViewById(R.id.Lottie_token_send); // 로띠 에니메이션 위치
        uploadWorkSpace_lottie.setAnimation("send_2.json");
        uploadWorkSpace_lottie.setRepeatCount(100);
        uploadWorkSpace_lottie.playAnimation();
        wallet_card_1.setVisibility(View.GONE);
        wallet_card_2.setVisibility(View.VISIBLE);

        return sendtokenvalue.getText().toString();
    }

    /* Set Balance */
    @SuppressLint("LongLogTag")
    private void setEthBalance(String ethBalance)
    {
        Log.e(TAG, "setEthBalance: ");
        ethbalance.setText(ethBalance);
    }

    // todo: 토큰 금액 불러오기
    @SuppressLint("LongLogTag")
    public void GetFee()
    {
        // 토큰 수량
        setGasPrice(getGasPrice());
        Log.e(TAG, "GetFee: getGasPrice()");

        // 토큰 수량제한
        setGasLimit(getGasLimit());
        Log.e(TAG, "GetFee: getGasLimit()");

        // Decimal: 소수 & Big Decimal: 큰 십진수
        // Big Decimal에 관한 설명은 해당 링크에서 - https://jsonobject.tistory.com/466
        // BigDecimal은 숫자를 정밀하게 저장하고 표현할 수 있는 함수이다
        BigDecimal fee = BigDecimal.valueOf(mGasPrice.doubleValue() * mGasLimit.doubleValue());
        Log.e(TAG, "GetFee: BigDecimal fee: " + fee.toString());

        BigDecimal feeresult = Convert.fromWei(fee.toString(), Convert.Unit.ETHER);
        tv_fee.setText(feeresult.toPlainString() + " ETH");
        Log.e(TAG, "GetFee: BigDecimal feeresult: " + feeresult.toPlainString() + " ETH");
    }

    // todo: 토큰 금액 조회결과 보기
    @SuppressLint("LongLogTag")
    private String getGasPrice()
    {
        Log.e(TAG, "getGasPrice: 토큰 금액 조회결과 보기");
        return tv_gas_price.getText().toString();
    }

    // todo: 토큰 금액 조회하기
    @SuppressLint("LongLogTag")
    private void setGasPrice(String gasPrice)
    {
        Log.e(TAG, "getGasPrice: 토큰 금액 조회하기");
        mGasPrice = Convert.toWei(gasPrice, Convert.Unit.GWEI).toBigInteger();
    }


    // todo: 토큰 수량 제한결과 보여주기
    @SuppressLint("LongLogTag")
    private String getGasLimit()
    {
        Log.e(TAG, "getGasLimit: 토큰 수량 제한결과 보여주기");
        return tv_gas_limit.getText().toString();
    }

    // todo: 토큰 수량 제한하기
    @SuppressLint("LongLogTag")
    private void setGasLimit(String gasLimit)
    {
        Log.e(TAG, "setGasLimit: 토큰 수량 제한하기");
        mGasLimit = BigInteger.valueOf(Long.valueOf(gasLimit));
    }

    /*Get Token Info*/
    // todo: 토큰 정보 불러오기
    @SuppressLint("LongLogTag")
    private void GetTokenInfo()
    {
        Log.e(TAG, "GetTokenInfo: 토큰 정보 불러오기");

        LoadSmartContract loadSmartContract = new LoadSmartContract(mWeb3j, mCredentials, mSmartcontract, mGasPrice, mGasLimit);
        loadSmartContract.registerCallBack(this);
        loadSmartContract.LoadToken();

        Log.e(TAG, "GetTokenInfo: loadSmartContract: Smart Contract 정보 불러오기 (토큰 정보, 토큰 금액 등)");

        Log.e(TAG, "GetTokenInfo: mWeb3j: " + mWeb3j);
        Log.e(TAG, "GetTokenInfo: mCredentials: " + mCredentials);
        Log.e(TAG, "GetTokenInfo: mSmartcontract: " + mSmartcontract);
        Log.e(TAG, "GetTokenInfo: mGasPrice" + mGasPrice);
        Log.e(TAG, "GetTokenInfo: mGasLimit: " + mGasLimit);
    }

    /* Get Token*/
    @SuppressLint("LongLogTag")
    @Override
    public void backLoadSmartContract(Map<String, String> result)
    {
        Log.e(TAG, "backLoadSmartContract: 실행됨");

//        String tokenbalance = result.get("tokenbalance");
//        String TokenBalance = tokenbalance.substring(13);


        // todo: 액티비티에 표시할 토큰 보유잔액 불러오기 (해당 메소드에서 값을 세팅함. 아래도 마찬가지로 값을 불러와서 각자 해당하는 메소드에 세팅됨)
        setTokenBalance(result.get("tokenbalance"));
        Log.e(TAG, "backLoadSmartContract: setTokenBalance: " + result.get("tokenbalance"));

        // todo: 액티비티에 표시할 토큰 이름 불러오기
        Log.e(TAG, "backLoadSmartContract: setTokenName: " + result.get("tokenname"));
        setTokenName(result.get("tokenname"));

        // todo: 액티비티에 표시할 토큰 심볼 불러오기
        Log.e(TAG, "backLoadSmartContract: setTokenSymbol: " + result.get("tokensymbol"));
        setTokenSymbol(result.get("tokensymbol"));

        // todo: 액티비티에 표시할 토큰 주소 불러오기
        Log.e(TAG, "backLoadSmartContract: setTokenAddress: " + result.get("tokenaddress"));
        setTokenAddress(result.get("tokenaddress"));

        // todo: 액티비티에 표시할 토큰 총 공급량 불러오기
        setTokenSupply(result.get("totalsupply"));
        Log.e(TAG, "backLoadSmartContract: setTokenSupply: "  + result.get("totalsupply"));

        Log.e(TAG, "backLoadSmartContract 끝");
    }

    // 금액 소수점 표시하기
    public static String toNumFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    // todo: 화면에 토큰 잔액 표시하기 (위에 backLoadSmartContract() 메소드에서 값을 불러와서 세팅함.)
    @SuppressLint("LongLogTag")
    private void setTokenBalance(String value)
    {
        Log.e(TAG, "setTokenBalance: 토큰 잔금 세팅하기");
        Log.e(TAG, "setTokenBalance: " + value);
        tokenbalance.setText(value);
    }

    // todo: 화면에 토큰 이름표시하기
    @SuppressLint("LongLogTag")
    private void setTokenName(String value)
    {
        Log.e(TAG, "setTokenName: 토큰 이름 세팅하기");
        Log.e(TAG, "setTokenName: " + value);
        tokenname.setText(value);
    }

    // todo: 화면에 토큰 심볼 표시하기
    @SuppressLint("LongLogTag")
    private void setTokenSymbol(String value)
    {
        Log.e(TAG, "setTokenName: 토큰 심벌 세팅하기");
        Log.e(TAG, "setTokenSymbol: " + value);
        tokensymbol.setText(value);
    }

    // todo: 화면에 토큰 총 공급량 표시하기
    @SuppressLint("LongLogTag")
    private void setTokenSupply(String value)
    {
        Log.e(TAG, "setTokenSupply: 토큰 총 공급량 세팅하기");
        Log.e(TAG, "setTokenSupply: " + value);
        tokensupply.setText(value);
    }

    // todo: 화면에 토큰 주소 표시하기
    @SuppressLint("LongLogTag")
    private void setTokenAddress(String value)
    {
        Log.e(TAG, "setTokenAddress: 토큰 주소 세팅하기");
        Log.e(TAG, "setTokenAddress: " + value);
        tokenaddress.setText(value);
    }
    /* End Get Token*/

    /* Sending */
    // todo: 이더리움 전송하기
    @SuppressLint("LongLogTag")
    private void sendEther()
    {
        Log.e(TAG, "sendEther: 이더리움 전송하기");
        sendingEther = new SendingEther(mWeb3j,
                mCredentials,
                getGasPrice(),
                getGasLimit());
        sendingEther.registerCallBack(this);
        sendingEther.Send(getToAddress(), getSendEtherAmmount());
    }

    // todo: 이더리움 전송 완료 후 감소한 이더리움 보유개수를 표시하기 위해 지갑 정보를 새로 불러온다.
    @SuppressLint("LongLogTag")
    @Override
    public void backSendEthereum(EthSendTransaction result)
    {
        Log.e(TAG, "backSendEthereum: 이더리움 전송 완료");
        toastMsg.Long(this, result.getTransactionHash());

        Log.e(TAG, "backSendEthereum: 이더리움 전송 완료 후 지갑 정보 새로 불러오기");
        LoadWallet();
    }

    // todo: 토큰 전송하기
    @SuppressLint("LongLogTag")
    private void sendToken()
    {
        Log.e(TAG, "sendToken: 토큰 전송하기");
        sendingToken = new SendingToken(mWeb3j,
                mCredentials,
                getGasPrice(),
                getGasLimit());
        sendingToken.registerCallBackToken(this);
        sendingToken.Send(mSmartcontract, getToAddress(), getSendTokenAmmount());
    }

    // todo: 토큰 전송 완료 후 감소한 토큰 보유개수를 표시하기 위해 지갑 정보를 새로 불러온다.
    @SuppressLint("LongLogTag")
    @Override
    public void backSendToken(TransactionReceipt result)
    {
        Log.e(TAG, "backSendToken: 토큰전송 완료");
        wallet_card_1.setVisibility(View.VISIBLE);
        wallet_card_2.setVisibility(View.GONE);

        toastMsg.Long(this, /*result.getTransactionHash()*/ "전송 완료");
        sendtoaddress.setText(null);
        sendtokenvalue.setText("0");
        sendethervalue.setText("0");
        Log.e(TAG, "backSendToken: " + result.getTransactionHash());

        Log.e(TAG, "backSendEthereum: 토큰 전송 완료 후 지갑 정보 새로 불러오기");
        LoadWallet();
    }
    /* End Sending */

    /* QR Scan */
    @SuppressLint("LongLogTag")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.e(TAG, "onActivityResult: onActivityResult 로 값이 넘어옴");

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                toastMsg.Short(this, "Result Not Found");
            } else
            {
                Log.e(TAG, "onActivityResult: result: QR코드 스캐너에서 상대방 지갑 주소가 넘어옴. " + result.getContents());

                String resultt = result.getContents();
                String resulttt = resultt.replace("ethereum:", "");

                // todo: 토큰을 전송할 상대방의 지갑 주소를 세팅한다.
                setToAddress(resulttt);

                // 상대방 토큰 주소정보 표시
//                toastMsg.Short(this, result.getContents());
            }
        } else {super.onActivityResult(requestCode, resultCode, data);}
    }
    /* End Q Scan */

    /* SeekBar Listener */
    @SuppressLint("LongLogTag")
    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerGL = new SeekBar.OnSeekBarChangeListener()
    {
        // todo: 지불할 수수료 값을 제한하는 seekbar
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            Log.e(TAG, "onProgressChanged: GL SeekBar 변화 감지");
            // 숫자로 조절할 수 있음.
            GetGasLimit(String.valueOf(seekBar.getProgress() * 1000 + 42000));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
        }
    };

    @SuppressLint("LongLogTag")
    private SeekBar.OnSeekBarChangeListener seekBarChangeListenerGP = new SeekBar.OnSeekBarChangeListener()
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            // todo: 지불할 수수료 값을 선택하는 seekbar.
            Log.e(TAG, "onProgressChanged: GP SeekBar 변화감지");
            GetGasPrice(String.valueOf(seekBar.getProgress() + 12));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
        }
    };

    @SuppressLint("LongLogTag")
    public void GetGasLimit(String value)
    {
        // 지불할 수수료 제한
        Log.e(TAG, "GetGasLimit: seekbar에서 제한한 토큰수량 표시하기");
        tv_gas_limit.setText(value);
        GetFee();

        Log.e(TAG, "GetGasLimit: 해당 값을 알기 위해 GetFee() 메소드 실행");
    }

    @SuppressLint("LongLogTag")
    public void GetGasPrice(String value)
    {
        // 지불할 수수료
        Log.e(TAG, "GetGasLimit: seekbar에서 선택한 토큰 금액 표시하기");
        tv_gas_price.setText(value);
        GetFee();

        Log.e(TAG, "GetGasLimit: 해당 값을 알기 위해 GetFee() 메소드 실행");
    }


    /* End SeekBar Listener */
}
