package com.example.sportapp.service;

import android.app.Activity;
import android.app.Application;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class PaypalService {

  private static final String TAG = "Places";

  public static final String CLIENT_KEY =
      "AUg6fzBQGHDalF2tSjh_r3dK3if5FEJSDrdYODqudJ2w97l0KT-j_xs5DkYzxGIREJpxfId3nkibhi09";

  private static PaypalService instance;

  public static PaypalService getInstance(Application application) {
    if (instance == null) {

      instance = new PaypalService();

      CheckoutConfig config =
          new CheckoutConfig(
              application,
              CLIENT_KEY,
              Environment.SANDBOX,
              String.format("%s://paypalpay", "com.example.sportapp"),
              CurrencyCode.PLN,
              UserAction.PAY_NOW,
              new SettingsConfig(true, false));
      PayPalCheckout.setConfig(config);
    }

    return instance;
  }

  private PaypalService() {}
}
