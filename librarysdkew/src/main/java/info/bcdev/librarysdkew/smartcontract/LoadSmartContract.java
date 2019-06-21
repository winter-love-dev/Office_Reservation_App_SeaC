package info.bcdev.librarysdkew.smartcontract;

import android.os.AsyncTask;
import android.util.Log;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import info.bcdev.librarysdkew.interfaces.callback.CBLoadSmartContract;

public class LoadSmartContract
{

    private String TAG = "Wallet Test LoadSmartContract";

    private CBLoadSmartContract cbLoadSmartContract;
    private Web3j mWeb3j;
    private Credentials mCredentials;
    private String mSmartContractAddress;
    private BigInteger mGasPrice;
    private BigInteger mGasLimit;

    public LoadSmartContract(Web3j web3j,
                             Credentials credentials,
                             String smartContractAddress,
                             BigInteger gasPrice,
                             BigInteger gasLimit)
    {
        mWeb3j = web3j;
        mCredentials = credentials;
        mSmartContractAddress = smartContractAddress;
        mGasPrice = gasPrice;
        mGasLimit = gasLimit;
    }

    public void LoadToken()
    {
        new Token().execute();
    }

    private class Token extends AsyncTask<Void, Void, Map<String, String>>
    {

        @Override
        protected Map<String, String> doInBackground(Void... voids)
        {
            try
            {
                /**
                 // Загружаем файл кошелька и получаем адрес
                 // Upload the wallet file and get the address
                 */
                String address = mCredentials.getAddress();
                Log.e(TAG, "doInBackground: address: " + address );

                /**
                 // Получаем Баланс
                 // Get balance Ethereum
                 */
                EthGetBalance etherbalance = mWeb3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
                String ethbalance = Convert.fromWei(String.valueOf(etherbalance.getBalance()), Convert.Unit.ETHER).toString();

                Log.e(TAG, "doInBackground: Eth Balance:" + ethbalance);
                System.out.println("Eth Balance: " + ethbalance);

                /**
                 // Загружаем Токен
                 // Download Token
                 */
                Log.e(TAG, "클래스 doInBackground에서 ERC20 토큰 정보를 불러옵니다. ");
                TokenERC20 token = TokenERC20.load(mSmartContractAddress, mWeb3j, mCredentials, mGasPrice, mGasLimit);

                String tokenname = token.name().send();
                Log.e(TAG, "doInBackground: tokenname: " + tokenname);

                String tokensymbol = token.symbol().send();
                Log.e(TAG, "doInBackground: tokensymbol: " + tokensymbol);

                String tokenaddress = token.getContractAddress();
                Log.e(TAG, "doInBackground: tokenaddress: " + tokenaddress);

                BigInteger totalsupply = token.totalSupply().send();
                Log.e(TAG, "doInBackground: token.totalSupply().send(): " + token.totalSupply().send() );
                Log.e(TAG, "doInBackground: totalsupply: BigInteger: " + totalsupply);
                Log.e(TAG, "doInBackground: totalsupply: toString(): " + totalsupply.toString());

                BigInteger tokenbalance = token.balanceOf(address).send();
                Log.e(TAG, "doInBackground: token.balanceOf(address).send(): " + token.balanceOf(address).send() );
                Log.e(TAG, "doInBackground: tokenbalance: BigInteger: " + tokenbalance );
                Log.e(TAG, "doInBackground: tokenbalance: toString(): " + tokenbalance.toString());

                Map<String, String> result = new HashMap<>();
                result.put("tokenname", tokenname);
                result.put("tokensymbol", tokensymbol);
                result.put("tokenaddress", tokenaddress);
                result.put("totalsupply", totalsupply.toString());
                result.put("tokenbalance", tokenbalance.toString());

                Log.e(TAG, "doInBackground: Map에 저장 후 onPostExecute로 값을 옮긴다. 그리고 MainActivity backLoadSmartContract() 메소드로 전달한다");

                return result;
            } catch (Exception ex)
            {
                Log.e(TAG, "doInBackground: Exception ex: " +  ex );
                System.out.println("ERROR:" + ex);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Map<String, String> result)
        {
            super.onPostExecute(result);
            if (result != null)
            {
                cbLoadSmartContract.backLoadSmartContract(result);
                Log.e(TAG, "onPostExecute: cbLoadSmartContract.backLoadSmartContract(result): " +  result);
            }
        }
    }

    public void registerCallBack(CBLoadSmartContract cbLoadSmartContract)
    {
        Log.e(TAG, "registerCallBack: cbLoadSmartContract: " + cbLoadSmartContract );
        this.cbLoadSmartContract = cbLoadSmartContract;
    }
}
